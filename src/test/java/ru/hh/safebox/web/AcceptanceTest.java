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
import org.springframework.web.util.NestedServletException;
import ru.hh.safebox.config.Settings;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CompileController.class)
public class AcceptanceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Settings settings;

    @Before
    public void setUp() {
        settings.defaultRam = 900;
        settings.defaultTimeout = 100_000L;
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
                        "public    class qwe {\n" +
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
    public void compilePython2Code() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "1")
                .param("code", "print 'man'\n" +
                        "print 'this is python'"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("man\\nthis is python")));
    }

    @Test
    public void compilePython2CodeWithArgs() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "1")
                .param("code", "a = raw_input('Please enter something:')\n" +
                        "print a")
                .param("userInput", "777"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("777")));
    }

    @Test
    public void compilePython3Code() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "2")
                .param("code", "print('man')\n" +
                        "print('this is python')"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("man\\nthis is python")));
    }

    @Test(expected = NestedServletException.class)
    public void shouldThrowExceptionWhenPostedWrongCompilerType() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "-1")
                .param("code", "lala"));
    }

    @Test
    public void shouldResponseWithErrorAfterCompilingPython3Code() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "2")
                .param("code", "print 'hi'"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SyntaxError: invalid syntax")));
    }

    @Test
    public void shouldReturn400IfSpecifiedNotAllAtributes() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "2"))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void shouldReturn404IfWrongUrlTemplate() throws Exception {
        this.mvc.perform(post("/compilerzz")
                .param("compilerType", "2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn405IfGetRequestPerformed() throws Exception {
        this.mvc.perform(get("/compile")
                .param("compilerType", "2"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void shouldEndWithTimeOut() throws Exception {
        settings.defaultTimeout = 1000L;
        this.mvc.perform(post("/compile")
                .param("compilerType", "0")
                .param("code", "class time {\n" +
                        "    public static void main(String[] args) throws InterruptedException {\n" +
                        "        Thread.sleep(5_000);\n" +
                        "    }\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"stdErr\":\"TimeOuted\"}")));

    }

    @Test
    public void shouldEndWithOutOfMemmory() throws Exception {
        settings.defaultRam = 4;
        settings.defaultTimeout = 10000L;
        this.mvc.perform(post("/compile")
                .param("compilerType", "0")
                .param("code",
                        "\n" +
                                "import java.util.concurrent.ThreadLocalRandom;" +
                                "    import java.util.LinkedList;\n" +
                                "import java.util.List;\n" +
                                "class test {" +
                                "    public static void main(String[] args) {\n" +
                                "        List<List<String>> listOfLists = new LinkedList<>();\n" +
                                "        List<String> list = new LinkedList<>();\n" +
                                "        while (true){\n" +
                                "            list.add(ThreadLocalRandom.current().nextDouble() + \"\");\n" +
                                "            listOfLists.add(list);\n" +
                                "if (listOfLists.size() > 100000) break;" +
                                "        }\n" +
                                "    }}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"stdErr\":\"TimeOuted\"}")));
    }

    @Test
    public void outOfMemmoryPython() throws Exception {
        this.mvc.perform(post("/compile")
                .param("compilerType", "1")
                .param("code", "print 'man'\n" +
                        "x = [i**2 for i in range(20000000)]"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("man")));
    }

}
