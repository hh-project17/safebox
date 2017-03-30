package ru.hh.safebox.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.safebox.config.Settings;
import ru.hh.safebox.util.Util;
import ru.hh.safebox.web.Response;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Sandbox {

    private static final Logger LOG = LoggerFactory.getLogger(Sandbox.class);

    private final Settings settings;
    private final ProgrammingLang programmingLang;
    private final String code;
    private final String userInput;

    private final Path tempDir;

    public Sandbox(Settings settings, Integer compilerType, String code, String userInput) {
        this.settings = settings;
        this.programmingLang = getProgrammingLang(compilerType);
        this.code = code;
        this.userInput = userInput;

        this.tempDir = getTempDir();

        LOG.info("Created sandbox with parameters [compiler : {}; code : {}; userInput : {}; tempDir : {}]",
                programmingLang, code, userInput, tempDir);
    }

    public Response run() {
        prepareForCodeExecution();

        Response response = new DockerCmdBuilder(settings.imageName, tempDir.toAbsolutePath())
                .startNewContainer()
                .exec(String.format("/sharedDir/run.sh %s %s %s",
                        programmingLang.getCompiler(), programmingLang.getFileName(), programmingLang.getRunner()),
                        settings.timeout)
                .finishAndKill();

        LOG.info("Produced response {}", response);

        Util.deleteDirectory(tempDir);
        return response;
    }

    private void prepareForCodeExecution() {
        try {
            Files.createDirectories(tempDir);

            Files.write(tempDir.resolve(programmingLang.getFileName()),
                    //to avoid compilation error (publicClassName != fileName)
                    code.replace("public class", "class").getBytes(),
                    StandardOpenOption.CREATE_NEW);

            Files.write(tempDir.resolve("args"),
                    (userInput != null ? userInput : "").getBytes(),
                    StandardOpenOption.CREATE_NEW);

            copyScriptsToSharedDirectory();

        } catch (IOException e) {
            LOG.error("Error while preparing for code execution", e);
        }

    }

    private void copyScriptsToSharedDirectory() throws IOException {
        Files.list(Paths.get("src/main/resources/scripts/"))
                .forEach(f -> {
                    try {
                        Files.copy(f, tempDir.resolve(f.getFileName()), StandardCopyOption.COPY_ATTRIBUTES);
                    } catch (IOException e) {
                        LOG.error("Can't copy file {} to dir {}", f, tempDir, e);
                    }
                });
    }

    private Path getTempDir() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return Paths.get("temp")
                .resolve(String.valueOf(random.nextDouble())
                        .replace(".", ""));
    }

    private ProgrammingLang getProgrammingLang(Integer compilerType) {
        try {
            return ProgrammingLang.values()[compilerType];
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Not supported language. " +
                    "You can choose from " + Arrays.asList(ProgrammingLang.values()), e);
        }
    }

}
