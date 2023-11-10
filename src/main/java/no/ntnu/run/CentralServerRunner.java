package no.ntnu.run;

import no.ntnu.network.centralserver.CentralServer;

public class CentralServerRunner {
    public static void main(String[] args) {
        CentralServer server = new CentralServer();
        server.run();
    }
}
