package no.ntnu.network.message.sensordata;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.ByteSerializableDouble;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A sensor data message containing Single-Double-Unit data.
 */
public class SduSensorDataMessage extends SensorDataMessage {
    private final double data;

    /**
     * Creates a new SDUSensorDataMessage.
     *
     * @param clientNodeAddress the address of the client that sent the data
     * @param sensorAddress     the address of the sensor that captured the data
     * @param data              the sdu data captured
     */
    public SduSensorDataMessage(int clientNodeAddress, int sensorAddress, double data) {
        super(clientNodeAddress, sensorAddress);

        this.data = data;
    }

    @Override
    protected byte[] getDataBytes(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitDouble(new ByteSerializableDouble(data));
    }

    @Override
    public void extractData(SensorDataReceiver receiver) {
        receiver.receiveSduData(getClientNodeAddress(), getSensorAddress(), data);
    }

    @Override
    public String toString() {
        return "SDU data captured by sensor " + getSensorAddress() + ": " + data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SduSensorDataMessage s)) {
            return false;
        }

        return super.equals(s) && data == s.data;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + Double.hashCode(data);

        return result;
    }
}
