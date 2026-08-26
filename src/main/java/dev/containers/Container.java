package dev.containers;

/**
 * Apple container, as reported by {@code container ls --all --format json}.
 *
 * @param id             the container name
 * @param image          the image reference, for example {@code docker.io/library/postgres:18-alpine}
 * @param state          {@code running}, {@code stopped}, ...
 * @param address        the IP of the first attached network, empty while the container is stopped
 * @param cpus           the number of CPUs given to the container
 * @param memoryInBytes  the memory given to the container
 */
public record Container(String id, String image, String state, String address, int cpus, long memoryInBytes) {

    private static final long MEGABYTE = 1024L * 1024L;

    /** @return the memory in megabytes, the same unit the CLI prints */
    public String memory() {
        return memoryInBytes / MEGABYTE + " MB";
    }
}
