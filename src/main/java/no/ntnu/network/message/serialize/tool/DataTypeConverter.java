package no.ntnu.network.message.serialize.tool;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A utility class for converting data structures into corresponding serializable structures,
 * and vice versa. The class encapsulates the logic for converting data structures that needs to be serialized.
 */
public class DataTypeConverter {
    /**
     * Does not allow for creation of instances of DataTypeConverter.
     */
    private DataTypeConverter(){}

    /**
     * Converts a set of integers to a serializable format.
     *
     * @param integerSet the set of integers to convert
     * @return the corresponding serializable integer set
     */
    public static ByteSerializableSet<ByteSerializableInteger> getSerializableSetOfIntegers(Set<Integer> integerSet) {
        if (integerSet == null) {
            throw new IllegalArgumentException("Cannot convert set of integers, because integerSet is null.");
        }

        ByteSerializableSet<ByteSerializableInteger> serializableSet = new ByteSerializableSet<>();
        integerSet.forEach(integer -> serializableSet.add(new ByteSerializableInteger(integer)));

        return serializableSet;
    }

    /**
     * Converts a set of serializable integers to a set of integers.
     *
     * @param serializableSet the serializable integers to convert
     * @return a set of integers
     */
    public static Set<Integer> getSetOfIntegers(ByteSerializableSet<ByteSerializableInteger> serializableSet) {
        if (serializableSet == null) {
            throw new IllegalArgumentException("Cannot convert serializable set of integers, because serializableSet is null.");
        }

        Set<Integer> integerSet = new HashSet<>();
        serializableSet.forEach(serializableInteger -> integerSet.add(serializableInteger.getInteger()));

        return integerSet;
    }

    /**
     * Converts an FNST to a corresponding serializable format.
     *
     * @param fnst the fnst to convert
     * @return the corresponding serializable map
     */
    public static ByteSerializableFnst getSerializableFnst(Map<Integer, DeviceClass> fnst) {
        if (fnst == null) {
            throw new IllegalArgumentException("Cannot convert FNST, because FNST is null.");
        }

        ByteSerializableFnst serializableFnst = new ByteSerializableFnst();
        fnst.forEach((key, value) -> {
            ByteSerializableInteger serializableAddress = new ByteSerializableInteger(key);
            ByteSerializableString serializableDeviceClass = new ByteSerializableString(value.name());
            serializableFnst.put(serializableAddress, serializableDeviceClass);
        });

        return serializableFnst;
    }

    /**
     * Converts a serializable FNST to a corresponding regular map.
     *
     * @param serializableFnst the serializable fnst
     * @return the corresponding map
     */
    public static Map<Integer, DeviceClass> getFnst(ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> serializableFnst) {
        if (serializableFnst == null) {
            throw new IllegalArgumentException("Cannot convert FNST, because serializableFnst is null.");
        }

        Map<Integer, DeviceClass> fnst = new HashMap<>();
        serializableFnst.forEach((key, value) -> {
            int address = key.getInteger();
            DeviceClass deviceClass = DeviceClass.getDeviceClass(value.getString());
            fnst.put(address, deviceClass);
        });

        return fnst;
    }

    /**
     * Converts a FNSM to a corresponding serializable format.
     *
     * @param fnsm the field node status map to convert
     * @return the corresponding serializable map
     */
    public static ByteSerializableFnsm getSerializableFnsm(Map<Integer, Integer> fnsm) {
        if (fnsm == null) {
            throw new IllegalArgumentException("Cannot convert FNSM, because fnsm is null.");
        }

        ByteSerializableFnsm serializableFnsm = new ByteSerializableFnsm();
        fnsm.forEach((key, value) -> serializableFnsm.put(new ByteSerializableInteger(key), new ByteSerializableInteger(value)));

        return serializableFnsm;
    }

    /**
     * Converts a serializable FNSM to a corresponding map.
     *
     * @param serializableFnsm the serializable fnsm to convert
     * @return the corresponding map
     */
    public static Map<Integer, Integer> getFnsm(ByteSerializableMap<ByteSerializableInteger, ByteSerializableInteger> serializableFnsm) {
        if (serializableFnsm == null) {
            throw new IllegalArgumentException("Cannot convert serializable FNSM, because serializableFnsm is null.");
        }

        Map<Integer, Integer> fnsm = new HashMap<>();
        serializableFnsm.forEach((key, value) -> fnsm.put(key.getInteger(), value.getInteger()));

        return fnsm;
    }

    /**
     * Converts a field node pool to a serializable format.
     *
     * @param fieldNodePool the field node pool to convert
     * @return the serializable field node pool
     */
    public static ByteSerializableFieldNodePool getSerializableFieldNodePool(Map<Integer, String> fieldNodePool) {
        if (fieldNodePool == null) {
            throw new IllegalArgumentException("Cannot convert field node pool, because fieldNodePool is null.");
        }

        ByteSerializableFieldNodePool serializableFieldNodePool = new ByteSerializableFieldNodePool();
        fieldNodePool.forEach((key, value) -> {
            ByteSerializableInteger serializableAddress = new ByteSerializableInteger(key);
            ByteSerializableString serializableName = new ByteSerializableString(value);

            serializableFieldNodePool.put(serializableAddress, serializableName);
        });

        return serializableFieldNodePool;
    }

    /**
     * Converts a serializable field node pool to a corresponding map.
     *
     * @param serializableFieldNodePool the serializable field node pool to convert
     * @return the corresponding map
     */
    public static Map<Integer, String> getFieldNodePool(ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> serializableFieldNodePool) {
        if (serializableFieldNodePool == null) {
            throw new IllegalArgumentException("Cannot convert serializable field node pool, because serializableFieldNodePool is null.");
        }

        Map<Integer, String> fieldNodePool = new HashMap<>();
        serializableFieldNodePool.forEach((key, value) -> {
            int address = key.getInteger();
            String name = value.getString();

            fieldNodePool.put(address, name);
        });

        return fieldNodePool;
    }

    /**
     * Converts a compatibility list to a serializable format.
     *
     * @param compatibilityList the compatibility list to convert
     * @return the serializable compatibility list
     */
    public static ByteSerializableSet<ByteSerializableString> getSerializableCompatibilityList(Set<DeviceClass> compatibilityList) {
        if (compatibilityList == null) {
            throw new IllegalArgumentException("Cannot convert compatibility list, because compatibilityList is null.");
        }

        ByteSerializableSet<ByteSerializableString> result = new ByteSerializableSet<>();
        compatibilityList.forEach(deviceClass -> result.add(new ByteSerializableString(deviceClass.name())));

        return result;
    }

    /**
     * Converts a serializable set of serializable strings to a compatibility list.
     *
     * @param serializableSet the serializable set to convert
     * @return a corresponding compatibility list
     */
    public static Set<DeviceClass> getCompatibilityList(ByteSerializableSet<ByteSerializableString> serializableSet) {
        if (serializableSet == null) {
            throw new IllegalArgumentException("Cannot convert serializable set, because serializableSet is null.");
        }

        Set<DeviceClass> result = new HashSet<>();
        serializableSet.forEach(string -> result.add(DeviceClass.getDeviceClass(string.getString())));

        return result;
    }
}
