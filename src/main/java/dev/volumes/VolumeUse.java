package dev.volumes;

import dev.containers.Container;

import java.util.List;

public record VolumeUse(String container, String address, String hostname, String destination) {

    public static List<VolumeUse> of(List<Container> containers, Volume volume) {
        return containers.stream()
                .flatMap(container -> container.details().mounts().stream()
                        .filter(mount -> mounts(mount, volume))
                        .map(mount -> new VolumeUse(
                                container.id(),
                                container.address(),
                                container.details().hostname(),
                                mount.destination())))
                .toList();
    }

    private static boolean mounts(Container.Mount mount, Volume volume) {
        return !mount.volume().isEmpty() && mount.volume().equals(volume.name());
    }
}
