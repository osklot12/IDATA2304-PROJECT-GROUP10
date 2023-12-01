package no.ntnu.network.sensordataprocess;

import no.ntnu.network.DataCommAgent;

import java.io.IOException;

/**
 * Provides data communication agents for a network entity using UDP.
 */
public interface UdpDataCommAgentProvider {
    /**
     * Establishes and returns a data communication agent, which can be used to send sensor data.
     *
     * @param portNumber the port number for the remote UDP process
     * @return the data communication agent
     */
    DataCommAgent getDataCommAgent(int portNumber) throws IOException;
}
