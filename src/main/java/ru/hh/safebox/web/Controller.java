package ru.hh.safebox.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hh.safebox.Sandbox;
import ru.hh.safebox.config.Settings;

@RestController
public class Controller {

    @Autowired
    private Settings settings;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/compile")
    public String compile(@RequestParam Integer compilerType,
                        @RequestParam String code,
                        @RequestParam String userInput) {

        Sandbox box = new Sandbox(settings, compilerType, code, userInput);
        return box.run();
    }

}
