package ru.hh.safebox.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.safebox.util.Util;
import ru.hh.safebox.web.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;

import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;

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
        copyScriptFromResources("java.sh", runConfig.getSharedDir(),
                OWNER_EXECUTE, GROUP_EXECUTE, OTHERS_EXECUTE);
        copyScriptFromResources("run.sh", runConfig.getSharedDir(),
                OWNER_EXECUTE, GROUP_EXECUTE, OTHERS_EXECUTE);
    }

    private void copyScriptFromResources(String scriptName, Path sharedDir, PosixFilePermission... perms) throws IOException {
        try (InputStream resourceStream = Sandbox.class
                .getClassLoader()
                .getResourceAsStream("scripts/" + scriptName);

             FileOutputStream fos = new FileOutputStream(sharedDir.resolve(scriptName).toFile())) {

            byte[] buf = new byte[2048];
            int r;
            while ((r = resourceStream.read(buf)) != -1) {
                fos.write(buf, 0, r);

            }

            Files.setPosixFilePermissions(sharedDir.resolve(scriptName), new HashSet<>(Arrays.asList(perms)));
        }
    }

}
