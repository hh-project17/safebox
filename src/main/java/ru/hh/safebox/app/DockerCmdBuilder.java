package ru.hh.safebox.app;

import ru.hh.safebox.util.Util;
import ru.hh.safebox.web.Response;

public class DockerCmdBuilder {

    private final static String DOCKER = "docker";
    private final static String WAITING_LOOP = "sleep infinity";

    private RunConfig config;

    private String containerId;

    private StringBuilder stdOut = new StringBuilder();
    private StringBuilder stdErr = new StringBuilder();

    public DockerCmdBuilder(RunConfig config) {
        this.config = config;
    }

    public DockerCmdBuilder createContainer() {
        Response response = Util.executeCommand(String.format("%s create -v %s:/sharedDir -m %dm %s %s",
                DOCKER, config.getSharedDir().toAbsolutePath(), config.getRam(), config.getImage(), WAITING_LOOP));
        containerId = response.getStdOut();
        Util.executeCommand(DOCKER + " start " + containerId);
        return this;
    }

    public DockerCmdBuilder exec(String cmd) {
        Response response = Util.executeCommand(DOCKER + " exec " + containerId + " " + cmd, config.getTimeout());
        stdOut.append(response.getStdOut());
        stdErr.append(response.getStdErr());
        return this;
    }

    public Response finish() {
        return new Response(stdOut.toString(), stdErr.toString());
    }

    public Response finishAndRemoveContainer() {
        Util.executeCommand(DOCKER + " kill " + containerId);
        Util.executeCommand(DOCKER + " rm " + containerId);
        return new Response(stdOut.toString(), stdErr.toString());
    }

    public Response runSingleCommandAndRemove(String cmd) {
        return Util.executeCommand(String.format("%s run --rm -v %s:/sharedDir %s %s",
                DOCKER, config.getSharedDir().toAbsolutePath(), config.getImage(), cmd),
                config.getTimeout());
    }
}
