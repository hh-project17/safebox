package ru.hh.safebox.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hh.safebox.app.Sandbox;
import ru.hh.safebox.app.DockerConfig;
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

        DockerConfig config = new DockerConfig.Builder()
                .setImage(settings.imageName)
                .setSharedDir(getTempDir())
                .setTimeout(timeout == null ? settings.defaultTimeout : timeout)
                .setRam(ram == null ? settings.defaultRam : ram)
                .build();

        Sandbox box = new Sandbox(config, compilerType, code, userInput);
        return box.run();
    }

    private Path getTempDir() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return Paths.get("temp")
                .resolve(String.valueOf(random.nextDouble())
                        .replace(".", ""));
    }

}
