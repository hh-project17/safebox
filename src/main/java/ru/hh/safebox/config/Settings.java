package ru.hh.safebox.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Settings {

    @Value("${timeout}")
    public Long timeout;

    @Value("${imageName}")
    public String imageName;

}
