package no.ntnu.network.centralserver;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.TestControlCommAgent;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.representation.FieldNodeInformation;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory class for creating central hubs for testing purposes.
 */
public class CentralHubTestFactory {
    /**
     * Does not allow creating instances of the class.
     */
    private CentralHubTestFactory() {}

    /**
     * Creates a central hub and populates it with test client proxies.
     *
     * @return a populated central hub
     */
    public static CentralHub getPopulatedHub() {
        CentralHub hub = new CentralHub();

        addFieldNodeProxyOne(hub);
        addFieldNodeProxyTwo(hub);

        return hub;
    }

    private static void addFieldNodeProxyOne(CentralHub hub) {
        Map<Integer, DeviceClass> fnst = new HashMap<>();
        fnst.put(2, DeviceClass.A1);
        fnst.put(3, DeviceClass.S3);

        Map<Integer, Integer> fnsm = new HashMap<>();
        fnsm.put(2, 3);
        fnsm.put(3, 0);

        FieldNodeInformation fieldNodeInformation = new FieldNodeInformation(fnst, fnsm, "Greenhouse node");
        hub.registerFieldNode(fieldNodeInformation, new TestControlCommAgent());
    }

    private static void addFieldNodeProxyTwo(CentralHub hub) {
        Map<Integer, DeviceClass> fnst = new HashMap<>();
        fnst.put(2, DeviceClass.A1);
        fnst.put(1, DeviceClass.A2);
        fnst.put(3, DeviceClass.S1);

        Map<Integer, Integer> fnsm = new HashMap<>();
        fnsm.put(2, 3);
        fnsm.put(1, 0);
        fnsm.put(3, 0);

        FieldNodeInformation fieldNodeInformation = new FieldNodeInformation(fnst, fnsm, "Outdoor node");
        hub.registerFieldNode(fieldNodeInformation, new TestControlCommAgent());
    }
}
