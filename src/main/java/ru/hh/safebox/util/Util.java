package ru.hh.safebox.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.safebox.web.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

public class Util {

    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    public static Response executeCommand(String command, long timeout) {
        LOG.info("Command '{}' called", command);
        StringBuilder output = new StringBuilder();
        StringBuilder err = new StringBuilder();
        boolean finished = false;
        try {
            Process p = Runtime.getRuntime().exec(command);
            if (timeout == -1) {
                p.waitFor();
                finished = true;
            } else {
                finished = p.waitFor(timeout, TimeUnit.MILLISECONDS);
            }

            try (BufferedReader stdOutReader =
                         new BufferedReader(new InputStreamReader(p.getInputStream()));
                 BufferedReader stdErrReader =
                         new BufferedReader(new InputStreamReader(p.getErrorStream()))) {

                String line;
                while (finished && (line = stdOutReader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }

                while (finished && (line = stdErrReader.readLine()) != null) {
                    err.append(line).append(System.lineSeparator());
                }

            }
        } catch (IOException | InterruptedException e) {
            LOG.error("Error while executing command = {}", command, e);
        }

        if (!finished) {
            err.append("TimeOuted");
        }

        return new Response(output.toString().trim(), err.toString().trim());
    }

    public static Response executeCommand(String command) {
        return executeCommand(command, -1);
    }

    public static void deleteDirectory(Path tempDir) {
        try {
            Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            LOG.error("Can't delete something in dir {}", tempDir, e);
        }
        LOG.info("Directory {} deleted", tempDir);
    }

}
