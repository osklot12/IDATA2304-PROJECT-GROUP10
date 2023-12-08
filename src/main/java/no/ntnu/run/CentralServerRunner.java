package no.ntnu.run;

import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.tools.logger.SystemOutLogger;

public class CentralServerRunner {
    public static final String IP_ADDRESS = "localhost";
    public static void main(String[] args) {
        CentralServer server = new CentralServer();
        server.addLogger(new SystemOutLogger());
        server.run();
    }
}
