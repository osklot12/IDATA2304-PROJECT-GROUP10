package no.ntnu.network.connectionservice.sensordatarouter;

import no.ntnu.network.connectionservice.ConnectionService;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.sensordataprocess.UdpSensorDataSink;
import no.ntnu.tools.logger.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * A connection service that Routes received sensor data to specified destinations.
 */
public class UdpSensorDataRouter implements ConnectionService {
    private final UdpSensorDataSink sensorDataSink;
    private final Set<SensorDataDestination> destinations;
    private Thread routingThread;
    private volatile boolean running;

    /**
     * Creates a new UdpSensorDataRouter.
     *
     * @param sensorDataSink the sensor data sink to receive sensor data from
     */
    public UdpSensorDataRouter(UdpSensorDataSink sensorDataSink) {
        this.sensorDataSink = sensorDataSink;
        this.destinations = new HashSet<>();
    }

    @Override
    public void start() {
        if (running) {
            throw new IllegalStateException("Cannot start the service, because it is already running.");
        }

        running = true;
        routingThread = new Thread(this::startRoutingSensorData);
        routingThread.start();
    }

    /**
     * Starts routing received sensor data messages to the destinations.
     */
    private void startRoutingSensorData() {
        while (running) {
            try {
                SensorDataMessage message = sensorDataSink.receiveNextMessage();
                if (message != null) {
                    destinations.forEach(destination -> destination.receiveSensorData(message));
                }
            } catch (IOException e) {
                Logger.error("Could not receive sensor data message: " + e.getMessage());
            }
        }
    }

    @Override
    public void stop() {
        if (!running) {
            throw new IllegalStateException("Cannot stop the service, because it is not running.");
        }

        running = false;
        routingThread.interrupt();
    }

    /**
     * Adds a destination for routed sensor data messages.
     *
     * @param destination the destination to add
     */
    public void addDestination(SensorDataDestination destination) {
        destinations.add(destination);
    }

    /**
     * Removes a destination for routed sensor data messages.
     *
     * @param destination the destination to remove
     */
    public void removeDestination(SensorDataDestination destination) {
        destinations.remove(destination);
    }

    /**
     * Returns the local port number on which the sensor data is received.
     *
     * @return the local port number
     */
    public int getLocalPortNumber() {
        return sensorDataSink.getPortNumber();
    }
}
