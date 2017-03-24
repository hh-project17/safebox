package ru.hh.safebox.config;

import java.util.Properties;

public class Settings {

    private Properties settings;

    public Settings(Properties settings) {
        this.settings = settings;
    }

    public long timeout() {
        return Long.parseLong(settings.getProperty("timeout"));
    }

}
