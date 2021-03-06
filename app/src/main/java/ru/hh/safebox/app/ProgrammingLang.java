package ru.hh.safebox.app;

public enum ProgrammingLang {

    JAVA("javac", "/sharedDir/java.sh", "code.java"),
    PYTHON2("python2", "", "code.py"),
    PYTHON3("python3", "", "code.py");

    private final String compiler;
    private final String runner; //for Java only
    private final String fileName;

    ProgrammingLang(String compiler, String runner, String fileName) {
        this.compiler = compiler;
        this.runner = runner;
        this.fileName = fileName;
    }

    public String getCompiler() {
        return compiler;
    }

    public String getRunner() {
        return runner;
    }

    public String getFileName() {
        return fileName;
    }

}
