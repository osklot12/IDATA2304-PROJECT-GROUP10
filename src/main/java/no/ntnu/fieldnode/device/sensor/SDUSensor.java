package no.ntnu.fieldnode.device.sensor;

import no.ntnu.broker.SduSensorDataBroker;
import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.device.DeviceClass;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A sensor for capturing SDU data.
 */
public abstract class SDUSensor implements Sensor {
    protected final DeviceClass deviceClass;
    protected final String unit;
    protected final int sensorNoise;
    protected final SduSensorDataBroker dataBroker;
    private Timer captureTimer;
    private static final int CAPTURE_INTERVAL = 1000;
    protected double sensorData;
    protected Environment environment;

    /**
     * Creates a new SDUSensor.
     *
     * @param deviceClass the class of the device
     * @param sensorNoise amount of noise interfering with the sensor. 0 is no noise, higher values adds more noise.
     *                    noise. Must be a non-negative value.
     */
    protected SDUSensor(DeviceClass deviceClass, String unit, int sensorNoise) {
        if (null == deviceClass) {
            throw new IllegalArgumentException("Cannot create SDUSensor, because device class is null.");
        }

        if (sensorNoise < 0) {
            throw new IllegalArgumentException("Cannot create SDUSensor, because sensor noise is negative.");
        }

        this.deviceClass = deviceClass;
        this.unit = unit;
        this.sensorNoise = sensorNoise;
        this.dataBroker = new SduSensorDataBroker();
    }

    /**
     * Starts the capturing of sensor data every 1 second.
     */
    @Override
    public void start() {
        captureTimer = new Timer();
        this.captureTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (null != environment) captureData();
            }
        }, CAPTURE_INTERVAL, CAPTURE_INTERVAL);
    }

    /**
     * Stops the capturing of sensor data.
     */
    @Override
    public void stop() {
        if (captureTimer != null) {
            captureTimer.cancel();
        }
    }

    /**
     * Returns the last sensor data captured by the sensor.
     *
     * @return last sensor data captured
     */
    public double getSensorData() {
        return this.sensorData;
    }

    protected double roundValue(double value) {
        return (double) Math.round(value * 100) / 100;
    }

    protected double addNoise(double value) {
        return SduSensorNoiseGenerator.generateNoise(value, sensorNoise);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public DeviceClass getDeviceClass() {
        return deviceClass;
    }

    @Override
    public void addListener(SduSensorListener listener, int fieldNodeAddress) {
        dataBroker.put(listener, fieldNodeAddress);
    }

    @Override
    public void removeListener(SduSensorListener listener) {
        dataBroker.remove(listener);
    }
}
