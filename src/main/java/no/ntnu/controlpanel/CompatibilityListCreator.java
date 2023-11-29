package no.ntnu.controlpanel;

import no.ntnu.fieldnode.device.DeviceClass;

import java.util.HashSet;
import java.util.Set;

/**
 * A utility class for creating different compatibility lists.
 */
public class CompatibilityListCreator {
    /**
     * Does not allow creating instances of the class.
     */
    private CompatibilityListCreator(){}

    /**
     * Creates and returns a complete compatibility list containing all existing device classes.
     *
     * @return a complete compatibility list
     */
    public static Set<DeviceClass> getCompleteCompatibilityList() {
        Set<DeviceClass> set = new HashSet<>();

        set.add(DeviceClass.A1);
        set.add(DeviceClass.A2);
        set.add(DeviceClass.A3);

        set.add(DeviceClass.S1);
        set.add(DeviceClass.S2);
        set.add(DeviceClass.S3);

        return set;
    }
}
