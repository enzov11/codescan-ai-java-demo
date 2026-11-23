package demo;

import java.io.IOException;

public class PullRequestCommandExample {
    public Process pingHost(String host) throws IOException {
        return Runtime.getRuntime().exec("ping " + host);
    }
}
