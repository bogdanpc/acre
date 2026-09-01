package dev.containers;

import java.util.List;

/**
 * Apple container, as reported by {@code container ls --all --format json}.
 *
 * @param id            the container name
 * @param image         the image reference, for example {@code docker.io/library/postgres:18-alpine}
 * @param state         {@code running}, {@code stopped}, ...
 * @param address       the IP of the first attached network, empty while the container is stopped
 * @param cpus          the number of CPUs given to the container
 * @param memoryInBytes the memory given to the container
 * @param details       the fields the table has no room for, shown on the detail page
 */
public record Container(String id, String image, String state, String address, int cpus, long memoryInBytes,
                        Details details) {

    private static final long MEGABYTE = 1024L * 1024L;

    public record Mount(String volume, String destination, String source) {
    }

    public record Details(String platform, String hostname, String user, String command, List<String> ports,
                          String runtime, boolean nestedVirtualization, boolean rosetta,
                          String created, String started, List<Mount> mounts) {

        public static final Details EMPTY =
                new Details("", "", "", "", List.of(), "", false, false, "", "", List.of());

        public Details {
            ports = List.copyOf(ports);
            mounts = List.copyOf(mounts);
        }
    }

    public Container(String id, String image, String state, String address, int cpus, long memoryInBytes) {
        this(id, image, state, address, cpus, memoryInBytes, Details.EMPTY);
    }

    /**
     * @return the memory in megabytes
     */
    public String memory() {
        return memoryInBytes / MEGABYTE + " MB";
    }
}
