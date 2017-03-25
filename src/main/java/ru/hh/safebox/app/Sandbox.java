package ru.hh.safebox.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.safebox.config.Settings;
import ru.hh.safebox.util.Util;
import ru.hh.safebox.web.Response;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ThreadLocalRandom;

public class Sandbox {

    private static final Logger LOG = LoggerFactory.getLogger(Sandbox.class);

    private final Settings settings;
    private final Path tempDir;

    private final Language language;
    private final String code;
    private final String userInput;

    public Sandbox(Settings settings, Integer compilerType, String code, String userInput) {
        this.settings = settings;
        this.tempDir = getTempDir();

        this.language = Language.values()[compilerType];
        this.code = code;
        this.userInput = userInput;
        LOG.info("Created sandbox with parameters [compiler : {}; code : {}; userInput : {}; tempDir : {}]",
                language, code, userInput, tempDir);
    }

    public Response run() {
        prepareForCodeExecution();

        Response response = Util.executeCommand(String.format("docker run --rm -v %s:/sharedDir %s /sharedDir/run.sh %s %s %s",
                tempDir.toAbsolutePath(), settings.imageName, language.getCompiler(), language.getFileName(), language.getRunner()),
                settings.timeout);
        LOG.info("Produced response {}", response);

        Util.deleteDirectory(tempDir);
        return response;
    }

    private void prepareForCodeExecution() {
        try {
            Files.createDirectories(tempDir);

            Files.write(tempDir.resolve(language.getFileName()),
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

}
