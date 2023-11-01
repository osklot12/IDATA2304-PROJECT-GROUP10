package no.ntnu.fieldnode.device.sensor;

import no.ntnu.broker.SensorDataBroker;
import no.ntnu.environment.Environment;
import no.ntnu.exception.EnvironmentNotSupportedException;
import no.ntnu.exception.NoEnvironmentSetException;
import no.ntnu.fieldnode.FieldNode;
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
    protected final SensorDataBroker dataBroker;
    protected Timer captureTimer;
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
        this.dataBroker = new SensorDataBroker();
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
                if (!(null == environment)) captureData();
            }
        }, 0, 1000);
    }

    /**
     * Stops the capturing of sensor data.
     */
    @Override
    public void stop() {
        if (captureTimer != null) {
            captureTimer.cancel();
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
        return SDUSensorNoiseGenerator.generateNoise(value, sensorNoise);
    }

    @Override
    public void pushData(SensorListener listener) {
        listener.receiveSensorData(this);
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
    public boolean addListener(SensorListener sensorListener) {
        return dataBroker.addSubscriber(sensorListener);
    }

    @Override
    public boolean removeListener(SensorListener sensorListener) {
        return dataBroker.removeSubscriber(sensorListener);
    }
}
