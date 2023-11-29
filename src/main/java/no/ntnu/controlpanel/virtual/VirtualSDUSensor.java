package no.ntnu.controlpanel.virtual;

import javafx.beans.Observable;
import no.ntnu.broker.VirtualSDUDataBroker;
import no.ntnu.fieldnode.device.DeviceClass;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * A representation of an SDU sensor, used for storing data for the sensor it represents.
 */
public class VirtualSDUSensor extends VirtualDevice {
    private final VirtualSDUDataBroker dataBroker;
    private final ArrayDeque<Double> dataBuffer;

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

        this.dataBroker = new VirtualSDUDataBroker();
        this.dataBuffer = new ArrayDeque<>();
    }

    /**
     * Adds new SDU data to the sensor buffer.
     *
     * @param data sdu data to add
     */
    public void addSensorData(double data) {
        dataBuffer.add(data);
    }

    /**
     * Adds a listener.
     *
     * @param listener listener to add
     * @return true if successfully added
     */
    public boolean addListener(VirtualSDUSensorListener listener) {
        return dataBroker.addSubscriber(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener listener to remove
     * @return true if successfully removed
     */
    public boolean removeListener(VirtualSDUSensorListener listener) {
        return dataBroker.removeSubscriber(listener);
    }
}
