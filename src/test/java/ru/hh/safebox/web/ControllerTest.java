package ru.hh.safebox.web;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.hh.safebox.config.Settings;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(Controller.class)
public class ControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Settings settings;

    @Before
    public void setUp() {
        settings.timeout = 100_000L;
        settings.imageName = "sandbox";
    }

    @Test
    public void compileJavaCode() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "0")
                .param("code", "class test {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        System.out.println(\"hello java\");\n" +
                        "    }\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"stdOut\":\"hello java\",\"stdErr\":\"\"}"));
    }

    @Test
    public void compileJavaCodeWithArgs() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "0")
                .param("code", "import java.util.Scanner;\n" +
                        "public class qwe {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        Scanner r = new Scanner(System.in);\n" +
                        "        System.out.println(r.nextInt());\n" +
                        "    }\n" +
                        "}")
                .param("userInput", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"stdOut\":\"10\",\"stdErr\":\"\"}"));
    }

    @Test
    public void compilePythonCode() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "1")
                .param("code", "print 'man'\n" +
                        "print 'this is python'"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("man\\nthis is python")));
    }

    @Ignore//todo не работает пока, поправить
    @Test
    public void compilePythonCodeWithArgs() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "0")
                .param("code", "a = raw_input('Please enter something:')\n" +
                        "print a")
                .param("userInput", "777"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("777")));
    }

}
