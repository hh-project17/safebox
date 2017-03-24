package ru.hh.safebox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class SpringConfig {

    @Bean
    public Settings settings() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream("src/main/resources/settings.properties")) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace(); //todo log
        }
        return new Settings(props);
    }

}
