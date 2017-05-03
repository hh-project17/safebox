package ru.hh.safebox.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.safebox.util.Util;
import ru.hh.safebox.web.Response;

public class DockerCmdBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    private final static String DOCKER = "docker";
    private final static String WAITING_LOOP = "sleep infinity";

    private RunConfig config;

    private String containerId;

    private StringBuilder stdOut = new StringBuilder();
    private StringBuilder stdErr = new StringBuilder();

    private boolean correct = true;

    public DockerCmdBuilder(RunConfig config) {
        this.config = config;
    }

    public DockerCmdBuilder startNewContainer() {
        Response response = Util.executeCommand(String.format("%s create -v %s:/sharedDir %s %s",
                DOCKER, config.getSharedDir().toAbsolutePath(), config.getImage(), WAITING_LOOP));
        containerId = response.getStdOut();

        response = Util.executeCommand(DOCKER + " start " + containerId);
        stdErr.append(response.getStdErr());
        if (stdErr.length() > 0) {
            LOG.error("Error while trying to start docker = {}", stdErr.toString());
            correct = false;
        }
        return this;
    }

    public DockerCmdBuilder exec(String cmd) {
        Response response = Util.executeCommand(DOCKER + " exec " + containerId + " " + cmd, config.getTimeout());
        stdOut.append(response.getStdOut());
        stdErr.append(response.getStdErr());
        return this;
    }

    public Response finish() {
        if (!correct) {
            stdOut  = new StringBuilder("Internal Error");
        }
        return new Response(stdOut.toString(), stdErr.toString());
    }

    public Response finishAndRemoveContainer() {
        Util.executeCommand(DOCKER + " kill " + containerId);
        Util.executeCommand(DOCKER + " rm " + containerId);
        return finish();
    }

}
