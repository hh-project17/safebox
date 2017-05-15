package ru.hh.safebox.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hh.safebox.app.RunConfig;
import ru.hh.safebox.app.Sandbox;
import ru.hh.safebox.config.Settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class CompileController {

    @Autowired
    private Settings settings;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/compile")
    public Response compile(@RequestParam Integer compilerType,
                            @RequestParam String code,
                            @RequestParam(required = false) String userInput,
                            @RequestParam(required = false) Long timeout,
                            @RequestParam(required = false) Integer ram) {

        RunConfig runConfig = new RunConfig.Builder(compilerType, code)
                .setUserInput(userInput != null ? userInput : "")
                .setSharedDir(getTempDir())
                .setImage(settings.imageName)
                .setTimeout(timeout != null ? timeout : settings.defaultTimeout)
                .setRam(ram != null ? ram : settings.defaultRam)
                .build();

        Sandbox box = new Sandbox(runConfig);
        return box.run();
    }

    private Path getTempDir() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return Paths.get("temp")
                .resolve(String.valueOf(random.nextDouble())
                        .replace(".", ""));
    }

}
