package no.ntnu.fieldnode.device.sensor;

/**
 * Sensor data consisting of a single Double-value of data with a given SI-unit.
 * SDU stands for Single Double-value with SI-Unit.
 */
public record SDUSensorData(double value, String unit){};
