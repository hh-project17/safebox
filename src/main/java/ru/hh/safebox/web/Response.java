package ru.hh.safebox.web;

public class Response {

    private final String stdOut;
    private final String stdErr;

    public Response(String stdOut, String stdErr) {
        this.stdOut = stdOut;
        this.stdErr = stdErr;
    }

    public String getStdOut() {
        return stdOut;
    }

    public String getStdErr() {
        return stdErr;
    }

    @Override
    public String toString() {
        return "Response{" +
                "stdOut='" + stdOut + '\'' +
                ", stdErr='" + stdErr + '\'' +
                '}';
    }

}
