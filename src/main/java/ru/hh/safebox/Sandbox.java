package ru.hh.safebox;

import ru.hh.safebox.config.Settings;
import ru.hh.safebox.util.Util;

public class Sandbox {

    private final Settings settings;

    private final Integer compilerType;
    private final String code;
    private final String userInput;

    public Sandbox(Settings settings, Integer compilerType, String code, String userInput) {
        this.settings = settings;

        this.compilerType = compilerType;
        this.code = code;
        this.userInput = userInput;
    }

    public String run() {
        return Util.executeCommand("docker-do-something", settings.timeout());
    }

}
