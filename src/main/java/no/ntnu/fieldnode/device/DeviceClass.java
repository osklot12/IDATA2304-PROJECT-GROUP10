package no.ntnu.fieldnode.device;

/**
 * Lists all available device classes for the application.
 */
public enum DeviceClass {
    S1("Temperature sensor using SDU sensor data."),
    S2("Humidity sensor using SDU sensor data."),
    S3("Luminosity sensor using SDU sensor data."),
    A1("Air conditioner actuator using 4 different states for its fan."),
    A2("Humidifier actuator using 10 different statues for its ");

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
