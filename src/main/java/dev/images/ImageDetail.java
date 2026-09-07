package dev.images;

import java.util.List;

public record ImageDetail(ContainerImage image, List<ImageUse> uses) {
}
