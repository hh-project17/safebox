package ru.hh.safebox.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.safebox.util.Util;
import ru.hh.safebox.web.Response;

import java.io.IOException;
import java.nio.file.*;

public class Sandbox {

    private static final Logger LOG = LoggerFactory.getLogger(Sandbox.class);

    private final RunConfig runConfig;

    public Sandbox(RunConfig runConfig) {
        this.runConfig = runConfig;

        LOG.info("Created sandbox with parameters [\n{}\n]", runConfig);
    }

    public Response run() {
        prepareForCodeExecution();

        Response response = new DockerCmdBuilder(runConfig)
                .startNewContainer()
                .exec(String.format("/sharedDir/run.sh %s %s %d %s",
                        runConfig.getProgrammingLang().getCompiler(),
                        runConfig.getProgrammingLang().getFileName(),
                        runConfig.getRam(),
                        runConfig.getProgrammingLang().getRunner()))
                .finishAndRemoveContainer();

        LOG.info("Produced response {}", response);

        Util.deleteDirectory(runConfig.getSharedDir());
        return response;
    }

    private void prepareForCodeExecution() {
        try {
            Files.createDirectories(runConfig.getSharedDir());

            createCodeFileInSharedDir();

            createUserInputFileInSharedDir();

            copyScriptsToSharedDirectory();

        } catch (IOException e) {
            LOG.error("Error while preparing for code execution", e);
        }

    }

    private void createCodeFileInSharedDir() throws IOException {
        Files.write(runConfig.getSharedDir().resolve(runConfig.getProgrammingLang().getFileName()),
                runConfig.getCode().getBytes(),
                StandardOpenOption.CREATE_NEW);
    }

    private void createUserInputFileInSharedDir() throws IOException {
        Files.write(runConfig.getSharedDir().resolve("args"),
                runConfig.getUserInput().getBytes(),
                StandardOpenOption.CREATE_NEW);
    }

    private void copyScriptsToSharedDirectory() throws IOException {
        Files.list(Paths.get("src/main/resources/scripts/"))
                .forEach(f -> {
                    try {
                        Files.copy(f, runConfig.getSharedDir().resolve(f.getFileName()), StandardCopyOption.COPY_ATTRIBUTES);
                    } catch (IOException e) {
                        LOG.error("Can't copy file {} to dir {}", f, runConfig.getSharedDir(), e);
                    }
                });
    }

}
