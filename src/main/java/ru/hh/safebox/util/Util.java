package ru.hh.safebox.util;

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

    public static Response executeCommand(String command, long timeout) {
        StringBuilder output = new StringBuilder();
        StringBuilder err = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor(timeout, TimeUnit.MILLISECONDS);

            try (BufferedReader stdOutReader =
                         new BufferedReader(new InputStreamReader(p.getInputStream()));
                 BufferedReader stdErrReader =
                         new BufferedReader(new InputStreamReader(p.getErrorStream()))) {

                String line;
                while ((line = stdOutReader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }

                while ((line = stdErrReader.readLine()) != null) {
                    err.append(line).append(System.lineSeparator());
                }

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); //todo log
        }

        return new Response(output.toString(), err.toString());
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
            e.printStackTrace();//todo log this!
        }

    }

}
