package no.ntnu.network.message.deserialize.component;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.sensordata.SduSensorDataMessage;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.util.EnumMap;

/**
 * A deserializer for deserialization of sensor data messages in NOFSP.
 */
public class NofspSensorDataDeserializer extends NofspDeserializer {
    private final EnumMap<DeviceClass, MessageDeserializationMethod> deserializationMethods;
    private final DeviceLookupTable lookupTable;

    /**
     * A functional interface defining the methods used for deserializing sensor data messages.
     */
    @FunctionalInterface
    private interface MessageDeserializationMethod {
        SensorDataMessage deserialize(int clientNodeAddress, int sensorAddress, Tlv tlv) throws IOException;
    }

    /**
     * Creates a new NofspSensorDataDeserializer.
     *
     * @param lookupTable the device lookup table used for finding device classes
     */
    public NofspSensorDataDeserializer(DeviceLookupTable lookupTable) {
        if (lookupTable == null) {
            throw new IllegalArgumentException("Cannot create NofspSensorDataDeserializer, because lookupTable is" +
                    "null.");
        }

        this.deserializationMethods = new EnumMap<>(DeviceClass.class);
        initializeDeserializationMap();
        this.lookupTable = lookupTable;
    }

    /**
     * Deserializes a sensor data message TLV.
     *
     * @param tlv the TLV to deserialize
     * @return the deserialized sensor data message
     * @throws IOException thrown if an I/O exception occurs
     */
    public SensorDataMessage deserializeMessage(Tlv tlv) throws IOException {
        if (tlv == null) {
            throw new IllegalArgumentException("Cannot deserialize sensor data message, because tlv is null.");
        }

        SensorDataMessage result = null;

        // creates a tlv reader to read the sensor data message fields
        TlvReader tlvReader = new TlvReader(tlv.valueField(), tlv.getFrame());

        // first field is the client node address
        int clientNodeAddress = getRegularInt(tlvReader.readNextTlv());

        // second field is the sensor address
        int sensorAddress = getRegularInt(tlvReader.readNextTlv());

        // third field is the sensor data
        Tlv dataTlv = tlvReader.readNextTlv();

        // checks the class for the given sensor
        DeviceClass sensorClass = lookupTable.lookup(clientNodeAddress, sensorAddress);
        if (sensorClass != null) {
            MessageDeserializationMethod method = deserializationMethods.get(sensorClass);

            if (method != null) {
                result = method.deserialize(clientNodeAddress, sensorAddress, dataTlv);
            } else {
                throw new IOException("Cannot deserialize sensor message, because no deserialization method exits" +
                        " for sensor of class " + sensorClass.name());
            }
        } else {
            throw new IOException("Cannot deserialize sensor message, because sensor was not identified.");
        }

        return result;
    }

    /**
     * Initializes the map for mapping device classes to deserialization methods.
     */
    private void initializeDeserializationMap() {
        deserializationMethods.put(DeviceClass.S1, this::getSduSensorDataMessage);
        deserializationMethods.put(DeviceClass.S2, this::getSduSensorDataMessage);
        deserializationMethods.put(DeviceClass.S3, this::getSduSensorDataMessage);
    }

    /**
     * Deserializes an {@code SduSensorDataMessage}.
     *
     * @param clientNodeAddress the address of the client node
     * @param sensorAddress the address of the sensor
     * @param dataTlv a tlv holding the sensor data
     * @return the deserialized sdu sensor data message
     */
    private SduSensorDataMessage getSduSensorDataMessage(int clientNodeAddress, int sensorAddress, Tlv dataTlv) {
        SduSensorDataMessage message = null;

        // deserializes the sdu data
        double data = getRegularDouble(dataTlv);

        message = new SduSensorDataMessage(clientNodeAddress, sensorAddress, data);

        return message;
    }
}
