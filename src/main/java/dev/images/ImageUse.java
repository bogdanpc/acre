package dev.images;

import dev.containers.Container;

import java.util.List;

public record ImageUse(String container, String address, String hostname) {

    public static List<ImageUse> of(List<Container> containers, ContainerImage image) {
        return containers.stream()
                .filter(container -> container.image().equals(image.reference()))
                .map(container -> new ImageUse(
                        container.id(),
                        container.address(),
                        container.details().hostname()))
                .toList();
    }
}
