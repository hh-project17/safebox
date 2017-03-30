package ru.hh.safebox.app;

import ru.hh.safebox.util.Util;
import ru.hh.safebox.web.Response;

import java.nio.file.Path;

public class DockerCmdBuilder {

    private final static String DOCKER = "docker";
    private final static String WAITING_LOOP = "sleep infinity";

    private String image;
    private Path volume;

    private String containerId;
    private StringBuilder stdOut = new StringBuilder();
    private StringBuilder stdErr = new StringBuilder();

    public DockerCmdBuilder(String image, Path volume) {
        this.image = image;
        this.volume = volume;
    }

    public DockerCmdBuilder startNewContainer() {
        Response response = Util.executeCommand(String.format("%s create -v %s:/sharedDir %s %s",
                DOCKER, volume, image, WAITING_LOOP));
        containerId = response.getStdOut();
        Util.executeCommand(DOCKER + " start " + containerId);
        return this;
    }

    public DockerCmdBuilder exec(String cmd) {
        Response response = Util.executeCommand(DOCKER + " exec " + containerId + " " + cmd);
        stdOut.append(response.getStdOut());
        stdErr.append(response.getStdErr());
        return this;
    }

    public DockerCmdBuilder exec(String cmd, long timeout) {
        Response response = Util.executeCommand(DOCKER + " exec " + containerId + " " + cmd, timeout);
        stdOut.append(response.getStdOut());
        stdErr.append(response.getStdErr());
        return this;
    }

    public Response finish() {
        return new Response(stdOut.toString(), stdErr.toString());
    }

    public Response finishAndKill() {
        Util.executeCommand(DOCKER + " kill " + containerId);
        Util.executeCommand(DOCKER + " rm " + containerId);
        return new Response(stdOut.toString(), stdErr.toString());
    }

}
