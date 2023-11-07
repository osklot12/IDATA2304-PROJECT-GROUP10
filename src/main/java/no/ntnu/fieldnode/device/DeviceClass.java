package no.ntnu.fieldnode.device;

import java.util.ArrayList;
import java.util.List;

/**
 * All available device classes for the application.
 */
public enum DeviceClass {
    S1("Temperature sensor using SDU sensor data."),
    S2("Humidity sensor using SDU sensor data."),
    S3("Luminosity sensor using SDU sensor data."),
    A1("Fan actuator using 4 different states for its fan."),
    A2("Humidifier actuator using 5 different states for its humidifier"),
    A3("Light dimmer actuator using 7 different states for its dimmer.");

    private final String description;

    /**
     * Creates a new {@code DeviceClass} with a given description.
     *
     * @param description a descriptive text for the device class
     */
    DeviceClass(String description) {
        this.description = description;
    }

    /**
     * Returns a description of the device class.
     *
     * @return description of device class
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a list of the DeviceClass constants as String.
     *
     * @return a list of DeviceClass strings
     */
    public static List<String> getStringArray() {
        List<String> result = new ArrayList<>();

        for (DeviceClass deviceClass : values()) {
            result.add(deviceClass.name());
        }

        return result;
    }

    /**
     * Returns the DeviceClass constant with a given name.
     *
     * @param name the name of the DeviceClass
     * @return DeviceClass matching the name
     */
    public static DeviceClass getDeviceClass(String name) {
        DeviceClass result = null;

        int searchIndex = 0;
        while (result == null && searchIndex < values().length) {
            DeviceClass currentLookup = values()[searchIndex];

            if (currentLookup.name().equals(name)) {
                result = currentLookup;
            }

            searchIndex++;
        }

        return result;
    }
}
