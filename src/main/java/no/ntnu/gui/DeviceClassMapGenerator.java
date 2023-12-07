package no.ntnu.gui;

import no.ntnu.fieldnode.device.DeviceClass;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A class generating maps containing information about device classes.
 */
public class DeviceClassMapGenerator {
    /**
     * Does not allow creating instances of the class.
     */
    private DeviceClassMapGenerator() {}

    /**
     * Returns a map containing the sensor domain for each sensor device class.
     *
     * @return sensor domain map
     */
    public static Map<DeviceClass, String> getSensorDomainMap() {
        EnumMap<DeviceClass, String> sensorDomains = new EnumMap<>(DeviceClass.class);

        sensorDomains.put(DeviceClass.S1, "Temperature");
        sensorDomains.put(DeviceClass.S2, "Humidity");
        sensorDomains.put(DeviceClass.S3, "Luminosity");

        return sensorDomains;
    }

    /**
     * Returns a map containing the SI unit for each sdu sensor device class.
     *
     * @return sdu sensor units
     */
    public static Map<DeviceClass, String> getSduSensorUnitMap() {
        EnumMap<DeviceClass, String> sensorUnits = new EnumMap<>(DeviceClass.class);

        sensorUnits.put(DeviceClass.S1, "°C");
        sensorUnits.put(DeviceClass.S2, "%");
        sensorUnits.put(DeviceClass.S3, "LUX");

        return sensorUnits;
    }

    /**
     * Returns a map containing the device name for each actuator device class.
     *
     * @return the actuator device names
     */
    public static Map<DeviceClass, String> getActuatorDeviceMap() {
        EnumMap<DeviceClass, String> actuatorDevices = new EnumMap<>(DeviceClass.class);

        actuatorDevices.put(DeviceClass.A1, "Air conditioner");
        actuatorDevices.put(DeviceClass.A2, "Humidifier");
        actuatorDevices.put(DeviceClass.A3, "Dimmer");

        return actuatorDevices;
    }

    /**
     * Returns a map containing the options for each actuator device class.
     *
     * @return the options for each actuator
     */
    public static Map<DeviceClass, Map<String, Integer>> getActuatorOptions() {
        Map<DeviceClass, Map<String, Integer>> actuatorOptions = new EnumMap<>(DeviceClass.class);

        actuatorOptions.put(DeviceClass.A1, getA1Map());
        actuatorOptions.put(DeviceClass.A2, getA2Map());
        actuatorOptions.put(DeviceClass.A3, getA3Map());

        return actuatorOptions;
    }

    private static Map<String, Integer> getA1Map() {
        Map<String, Integer> a1Map = new LinkedHashMap<>();

        a1Map.put("Off", 0);
        a1Map.put("Low", 1);
        a1Map.put("Medium", 2);
        a1Map.put("High", 3);

        return a1Map;
    }

    private static Map<String, Integer> getA2Map() {
        Map<String, Integer> a2Map = new LinkedHashMap<>();

        a2Map.put("Off", 0);
        a2Map.put("Dehumidify turbo", 1);
        a2Map.put("Dehumidify", 2);
        a2Map.put("Humidify", 3);
        a2Map.put("Humidify turbo", 4);

        return a2Map;
    }

    private static Map<String, Integer> getA3Map() {
        Map<String, Integer> a3Map = new LinkedHashMap<>();

        a3Map.put("Off", 0);
        a3Map.put("Blackout", 1);
        a3Map.put("Night", 2);
        a3Map.put("Dimmed", 3);
        a3Map.put("Lit", 4);
        a3Map.put("Bright", 5);
        a3Map.put("Sun mode (use with care)", 6);

        return a3Map;
    }
}
