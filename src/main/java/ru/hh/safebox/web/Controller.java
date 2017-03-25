package ru.hh.safebox.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hh.safebox.app.Sandbox;
import ru.hh.safebox.config.Settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class Controller {

    @Autowired
    private Settings settings;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/compile")
    public Response compile(@RequestParam Integer compilerType,
                            @RequestParam String code,
                            @RequestParam(required = false) String userInput) {

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Path tempDir = Paths.get("temp").resolve(String.valueOf(random.nextDouble()).replace(".", ""));

        Sandbox box = new Sandbox(settings, tempDir, compilerType, code, userInput);
        return box.run();
    }

}
