package demo;

import java.io.IOException;
import java.util.Set;

public class UnsafeCommandExample {
    private static final Set<String> ALLOWED_HOSTS = Set.of("localhost", "127.0.0.1");

    public Process pingHost(String host) throws IOException {
        if (!ALLOWED_HOSTS.contains(host)) {
            throw new IllegalArgumentException("Host is not allowed");
        }
        return new ProcessBuilder("ping", host).start();
    }
}
