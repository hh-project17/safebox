package ru.hh.safebox.app;

import java.nio.file.Path;
import java.util.Arrays;

import static ru.hh.safebox.app.ProgrammingLang.JAVA;

public class RunConfig {

    private final ProgrammingLang programmingLang;
    private final String code;
    private final String userInput;

    private final String image;
    private final Path sharedDir;
    private final Long timeout;
    private final Integer ram;

    private RunConfig(Builder builder) {
        this.programmingLang = builder.progLanguage;
        this.code = builder.code;
        this.userInput = builder.userInput;

        this.image = builder.image;
        this.sharedDir = builder.sharedDir;
        this.timeout = builder.timeout;
        this.ram = builder.ram;
    }

    public String getImage() {
        return image;
    }

    public Path getSharedDir() {
        return sharedDir;
    }

    public Long getTimeout() {
        return timeout;
    }

    public Integer getRam() {
        return ram;
    }

    public ProgrammingLang getProgrammingLang() {
        return programmingLang;
    }

    public String getCode() {
        return code;
    }

    public String getUserInput() {
        return userInput;
    }

    public static class Builder {

        private final ProgrammingLang progLanguage;
        private String code;
        private String userInput;

        private String image;
        private Long timeout;
        private Integer ram;
        private Path sharedDir;

        public Builder(Integer progLanguage, String code) {
            this.progLanguage = getProgrammingLang(progLanguage);
            this.code = code;
        }

        public Builder setImage(String image) {
            this.image = image;
            return this;
        }

        public Builder setTimeout(Long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setRam(Integer ram) {
            this.ram = ram;
            return this;
        }

        /**
         * In docker it also known as "Volume"
         */
        public Builder setSharedDir(Path sharedDir) {
            this.sharedDir = sharedDir;
            return this;
        }

        public Builder setUserInput(String userInput) {
            this.userInput = userInput;
            return this;
        }

        public RunConfig build() {
            modifyCode();
            return new RunConfig(this);
        }

        private void modifyCode() {
            if (progLanguage == JAVA) {
                //to avoid compilation error (publicClassName != fileName)
                code = code.replaceAll("public\\s+class", "class");
                return;
            }
            String heapLimit = "import resource\n" +
                    "rsrc = resource.RLIMIT_DATA\n" +
                    "soft, hard = resource.getrlimit(rsrc)\n" +
                    "resource.setrlimit(rsrc, (1048576 * " + ram + ", hard))\n";
            code = heapLimit + code;
        }

        private ProgrammingLang getProgrammingLang(Integer compilerType) {
            try {
                return ProgrammingLang.values()[compilerType];
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Not supported language. " +
                        "You can choose from " + Arrays.asList(ProgrammingLang.values()), e);
            }
        }
    }

    @Override
    public String toString() {
        return "\nRunConfig {" +
                "\nprogrammingLang=" + programmingLang +
                ", \ncode='" + code + '\'' +
                ", \nuserInput='" + userInput + '\'' +
                ", \nimage='" + image + '\'' +
                ", \nsharedDir=" + sharedDir +
                ", \ntimeout=" + timeout +
                ", \nram=" + ram +
                "\n}";
    }
}
