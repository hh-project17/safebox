package ru.hh.safebox.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.hh.safebox.app.Sandbox;
import ru.hh.safebox.config.Settings;

@RestController
public class CompileController {

    @Autowired
    private Settings settings;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/compile")
    public Response compile(@RequestParam Integer compilerType,
                            @RequestParam String code,
                            @RequestParam(required = false) String userInput) {

        Sandbox box = new Sandbox(settings, compilerType, code, userInput);
        return box.run();
    }

}
