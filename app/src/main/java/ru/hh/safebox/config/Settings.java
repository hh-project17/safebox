package ru.hh.safebox.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Settings {

    @Value("${imageName}")
    public String imageName;

    @Value("${defaultTimeout}")
    public Long defaultTimeout;

    @Value("${defaultRam}")
    public Integer defaultRam;

}
