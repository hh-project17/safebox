package ru.hh.safebox.app;

import ru.hh.safebox.config.Settings;
import ru.hh.safebox.util.Util;
import ru.hh.safebox.web.Response;

import java.io.IOException;
import java.nio.file.*;

public class Sandbox {

    private final Settings settings;
    private final Path tempDir;

    private final Language language;
    private final String code;
    private final String userInput;

    public Sandbox(Settings settings, Path tempDir, Integer compilerType, String code, String userInput) {
        this.settings = settings;
        this.tempDir = tempDir;

        this.language = Language.values()[compilerType];
        this.code = code;
        this.userInput = userInput;
    }

    public Response run() {
        prepareForCodeExecution();

        Response response = Util.executeCommand(String.format("docker run --rm -v %s:/sharedDir %s /sharedDir/run.sh %s %s %s",
                tempDir.toAbsolutePath(), settings.imageName, language.getCompiler(), language.getFileName(), language.getRunner()),
                settings.timeout);

        Util.deleteDirectory(tempDir);
        return response;
    }

    private void prepareForCodeExecution() {
        try {
            Files.createDirectories(tempDir);

            Files.write(tempDir.resolve(language.getFileName()),
                    (code.replace("public class", "class") + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE_NEW);

            Files.write(tempDir.resolve("args"),
                    (userInput != null ? userInput : "").getBytes(),
                    StandardOpenOption.CREATE_NEW);

            copyScriptsToSharedDirectory();
        } catch (IOException e) {
            e.printStackTrace(); //todo log
        }

    }

    private void copyScriptsToSharedDirectory() throws IOException {
        Files.list(Paths.get("src/main/resources/scripts/"))
                .forEach(f -> {
                    try {
                        Files.copy(f, tempDir.resolve(f.getFileName()), StandardCopyOption.COPY_ATTRIBUTES);
                    } catch (IOException e) {
                        e.printStackTrace(); //todo log this
                    }
                });
    }

}
