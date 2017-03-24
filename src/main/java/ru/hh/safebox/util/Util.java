package ru.hh.safebox.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class Util {

    public static String executeCommand(String command, long timeout) {
        StringBuilder output = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor(timeout, TimeUnit.MILLISECONDS);

            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(p.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); //todo log
        }

        return output.toString();
    }

}
