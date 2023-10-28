package no.ntnu.fieldnode.device;

/**
 * Lists all available device classes for the application.
 */
public enum DeviceClass {
    S1("Temperature sensor using SDU sensor data."),
    S2("Humidity sensor using SDU sensor data."),
    S3("Luminosity sensor using SDU sensor data."),
    A1("Fan actuator using 4 different states for its fan."),
    A2("Humidifier actuator using 5 different states for its humidifier"),
    A3("Light dimmer actuator using 7 different states for its dimmer.");

    private final String description;

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
}
