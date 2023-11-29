package no.ntnu.network;

import java.io.IOException;

/**
 * Provides data communication agents for a remote network entity, making it possible to push sensor data to
 * this entity. Network entities that are able to create private sensor data sinks naturally implements this
 * interface.
 */
public interface DataCommAgentProvider {
    /**
     * Establishes a data communication agent, which can be used to send sensor data.
     * The method requires that the port number for the sensor data sink has been communicated.
     *
     * @param portNumber the port number of the sensor data sink
     * @return the data communication agent
     */
    DataCommAgent getDataCommAgent(int portNumber) throws IOException;
}
