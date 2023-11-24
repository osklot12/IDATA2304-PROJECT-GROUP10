package no.ntnu.network.message.serialize.tool;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.ByteSerializableFnst;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableString;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * JUnit testing for the {@code DataTypeConverter} class.
 */
public class DataTypeConverterTest {
    /**
     * Tests that the method getSerializableFnst() converts the map as expected.
     */
    @Test
    public void testGetSerializableFnst() {
        Map<Integer, DeviceClass> fnst = new HashMap<>();
        fnst.put(3, DeviceClass.S3);
        fnst.put(6, DeviceClass.A1);

        ByteSerializableFnst serializableFnst = DataTypeConverter.getSerializableFnst(fnst);

        fnst.forEach((key, value) -> {
            ByteSerializableInteger serializableKey = new ByteSerializableInteger(key);
            ByteSerializableString deviceClassString = new ByteSerializableString(value.name());

            assertEquals(deviceClassString, serializableFnst.get(serializableKey));
        });
        assertEquals(2, serializableFnst.size());
    }

    /**
     * Tests that the method getFnst() converts the serializable fnst as expected.
     */
    @Test
    public void testGetFnst() {
        ByteSerializableFnst serializableFnst = new ByteSerializableFnst();
        serializableFnst.put(new ByteSerializableInteger(3), new ByteSerializableString("S3"));
        serializableFnst.put(new ByteSerializableInteger(6), new ByteSerializableString("A1"));

        Map<Integer, DeviceClass> fnst = DataTypeConverter.getFnst(serializableFnst);

        serializableFnst.forEach((key, value) -> {
            int nodeAddress = key.getInteger();
            DeviceClass deviceClass = DeviceClass.getDeviceClass(value.getString());

            assertEquals(deviceClass, fnst.get(nodeAddress));
        });
        assertEquals(2, fnst.size());
    }


}