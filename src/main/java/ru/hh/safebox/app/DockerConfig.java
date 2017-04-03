package ru.hh.safebox.app;

import java.nio.file.Path;

public class DockerConfig {

    private final String image;
    private final Path sharedDir;
    private final Long timeout;
    private final Integer ram;

    private DockerConfig(Builder builder) {
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

    public static class Builder {

        private String image;
        private Long timeout;
        private Integer ram;
        private Path sharedDir;

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
         *
         */
        public Builder setSharedDir(Path sharedDir) {
            this.sharedDir = sharedDir;
            return this;
        }

        public DockerConfig build() {
            return new DockerConfig(this);
        }
    }
}
