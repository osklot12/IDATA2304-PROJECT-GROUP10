package no.ntnu.run;

import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.tools.logger.SystemOutLogger;

/**
 * Runner for the central server.
 */
public class CentralServerRunner {
    /**
     * The main starting point for the central server.
     *
     * @param args console line arguments
     */
    public static void main(String[] args) {
        CentralServer server = new CentralServer();
        server.addLogger(new SystemOutLogger());
        server.run();
    }
}
