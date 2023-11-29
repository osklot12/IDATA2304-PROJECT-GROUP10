package no.ntnu.network.message.sensordata;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A message containing data captured by a sensor.
 */
public abstract class SensorDataMessage implements ByteSerializable {
    private final int clientNodeAddress;
    private final int sensorAddress;

    /**
     * Creates a new SensorDataMessage.
     *
     * @param clientNodeAddress the address of the client that sent the data
     * @param sensorAddress the address of the sensor that captured the data
     */
    protected SensorDataMessage(int clientNodeAddress, int sensorAddress) {
        this.clientNodeAddress = clientNodeAddress;
        this.sensorAddress = sensorAddress;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitSensorDataMessage(this, getDataBytes(visitor));
    }

    /**
     * Returns the sensor data in bytes.
     *
     * @param visitor the serializer visitor
     * @return sensor data in bytes
     */
    protected abstract byte[] getDataBytes(ByteSerializerVisitor visitor) throws SerializationException;

    /**
     * Returns the address of the client that sent the data.
     *
     * @return client node address
     */
    public int getClientNodeAddress() {
        return clientNodeAddress;
    }

    /**
     * Returns the address of the client that sent the data, in a serializable format.
     *
     * @return serializable client node address
     */
    public ByteSerializableInteger getSerializableClientNodeAddress() {
        return new ByteSerializableInteger(clientNodeAddress);
    }

    /**
     * Returns the address of the sensor that captured the data.
     *
     * @return sensor address
     */
    public int getSensorAddress() {
        return sensorAddress;
    }

    /**
     * Returns the address of the sensor that captured the data, in a serializable format.
     *
     * @return serializable sensor address
     */
    public ByteSerializableInteger getSerializableSensorAddress() {
        return new ByteSerializableInteger(sensorAddress);
    }

    /**
     * Extracts the contained data to a receiver.
     *
     * @param receiver the receiver of the data
     */
    public abstract void extractData(SensorDataReceiver receiver);

    @Override
    public String toString() {
        return "sensor data captured by sensor " + sensorAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SensorDataMessage s)) {
            return false;
        }

        return clientNodeAddress == s.clientNodeAddress && sensorAddress == s.sensorAddress;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + clientNodeAddress;
        result = result * 31 + sensorAddress;

        return result;
    }
}
