package no.ntnu.controlpanel.virtual.sensor;

import no.ntnu.controlpanel.virtual.VirtualDevice;
import no.ntnu.fieldnode.device.DeviceClass;

import java.time.LocalTime;
import java.util.*;

/**
 * A representation of an SDU sensor, used for storing data for the sensor it represents.
 */
public class VirtualSDUSensor extends VirtualDevice {
    private final ArrayDeque<Double> dataBuffer;
    private final Map<Integer, VirtualSDUSensorListener> listeners;

    /**
     * Creates a new VirtualSDUSensor.
     *
     * @param deviceClass the class of device
     * @param bufferSize the size of the data buffer, must be a positive integer
     */
    public VirtualSDUSensor(DeviceClass deviceClass, int bufferSize) {
        super(deviceClass);
        if (bufferSize < 1) {
            throw new IllegalArgumentException("Cannot create VirtualSDUSensor, because buffer size must be a positive" +
                    " integer");
        }

        this.dataBuffer = new ArrayDeque<>();
        this.listeners = new HashMap<>();
    }

    /**
     * Returns the next available data.
     * The method will remove the data is returns.
     *
     * @return the next available data, null if no more data exists
     */
    public Double pollNextAvailableData() {
        return dataBuffer.poll();
    }

    /**
     * Adds new SDU data to the sensor buffer.
     *
     * @param data sdu data to add
     */
    public void addSensorData(double data) {
        dataBuffer.add(data);
        listeners.forEach((address, listener) -> listener.newSduData(address));
    }


    /**
     * Adds a listener to listen for the event of new SDU data being stored.
     * Each listener is associated with an address, and this address is then used when notifying the listener
     * about the event.
     *
     * @param address the address for the relation
     * @param listener the listener to add
     */
    public void addListener(int address, VirtualSDUSensorListener listener) {
        listeners.put(address, listener);
    }
}
