package ru.hh.safebox.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.safebox.util.Util;
import ru.hh.safebox.web.Response;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

public class Sandbox {

    private static final Logger LOG = LoggerFactory.getLogger(Sandbox.class);

    private final DockerConfig config;
    private final ProgrammingLang programmingLang;
    private final String code;
    private final String userInput;

    public Sandbox(DockerConfig config, Integer compilerType, String code, String userInput) {
        this.config = config;
        this.programmingLang = getProgrammingLang(compilerType);
        this.code = code;
        this.userInput = userInput;

        LOG.info("Created sandbox with parameters [compiler : {}; code : {}; userInput : {}; config : {}]",
                programmingLang, code, userInput, config);
    }

    public Response run() {
        prepareForCodeExecution();

        Response response = new DockerCmdBuilder(config).runSingleCommandAndRemove(String.format("/sharedDir/run.sh %s %s %s",
                programmingLang.getCompiler(), programmingLang.getFileName(), programmingLang.getRunner()));
//                )
//                .createContainer()
//                .exec(String.format("/sharedDir/run.sh %s %s %s",
//                        programmingLang.getCompiler(), programmingLang.getFileName(), programmingLang.getRunner()))
//                .finishAndRemoveContainer();

        LOG.info("Produced response {}", response);

        Util.deleteDirectory(config.getSharedDir());
        return response;
    }

    private void prepareForCodeExecution() {
        try {
            Files.createDirectories(config.getSharedDir());

            createCodeFileInSharedDir();

            createUserInputFileInSharedDir();

            copyScriptsToSharedDirectory();

        } catch (IOException e) {
            LOG.error("Error while preparing for code execution", e);
        }

    }

    private void createCodeFileInSharedDir() throws IOException {
        Files.write(config.getSharedDir().resolve(programmingLang.getFileName()),
                //to avoid compilation error (publicClassName != fileName)
                programmingLang == ProgrammingLang.JAVA
                        ? code.replaceAll("public\\s+class", "class").getBytes()
                        :
                        (
                                "import resource" + System.lineSeparator()
                        + "rsrc = resource.RLIMIT_DATA" + System.lineSeparator()
                        + "soft, hard = resource.getrlimit(rsrc)" + System.lineSeparator()
                        + "resource.setrlimit(resource.RLIMIT_DATA, (1024, hard))"
                        + System.lineSeparator() +
//                        )
//                                        (
                                        code).getBytes(),
                StandardOpenOption.CREATE_NEW);
    }

    private void createUserInputFileInSharedDir() throws IOException {
        Files.write(config.getSharedDir().resolve("args"),
                (userInput != null ? userInput : "").getBytes(),
                StandardOpenOption.CREATE_NEW);
    }

    private void copyScriptsToSharedDirectory() throws IOException {
        Files.list(Paths.get("src/main/resources/scripts/"))
                .forEach(f -> {
                    try {
                        Files.copy(f, config.getSharedDir().resolve(f.getFileName()), StandardCopyOption.COPY_ATTRIBUTES);
                    } catch (IOException e) {
                        LOG.error("Can't copy file {} to dir {}", f, config.getSharedDir(), e);
                    }
                });
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
