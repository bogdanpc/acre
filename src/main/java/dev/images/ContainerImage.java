package dev.images;

import java.util.List;

public record ContainerImage(
        String reference,
        String mediaType,
        String digest,
        long indexSize,
        List<Configuration> configurations) {

    public record Configuration(
            String platform,
            long size,
            String entrypoint,
            String cmd,
            String workingDir,
            List<Variable> environment) {
    }

    public record Variable(String name, String value) {
    }

    public String name() {
        var path = nameAndTag();
        var tag = path.lastIndexOf(':');
        return tag < 0 ? path : path.substring(0, tag);
    }

    public String tag() {
        var path = nameAndTag();
        var tag = path.lastIndexOf(':');
        return tag < 0 ? "" : path.substring(tag + 1);
    }

    public long size() {
        return configurations.stream().mapToLong(Configuration::size).sum();
    }

    private String nameAndTag() {
        return reference.substring(reference.lastIndexOf('/') + 1);
    }
}
