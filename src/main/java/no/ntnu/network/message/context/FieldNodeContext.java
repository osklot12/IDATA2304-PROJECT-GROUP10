package no.ntnu.network.message.context;

import no.ntnu.exception.NoSuchDeviceException;
import no.ntnu.fieldnode.FieldNode;
import no.ntnu.network.CommunicationAgent;

import java.util.Iterator;
import java.util.Set;

/**
 * A context for processing field node messages.
 */
public class FieldNodeContext extends ClientContext {
    private final FieldNode fieldNode;
    private final Set<Integer> adl;

    /**
     * Creates a FieldNodeContext.
     *
     * @param agent the communication agent
     * @param fieldNode the field node to operate on
     * @param adl the active device list for the field node
     */
    public FieldNodeContext(CommunicationAgent agent, FieldNode fieldNode, Set<Integer> adl) {
        super(agent);
        if (fieldNode == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeContext, because field node is null");
        }

        this.fieldNode = fieldNode;
        this.adl = adl;
    }

    /**
     * Updates the Active Device List for the field node client.
     * The method takes in a set of device addresses as an argument, where positive addresses indicated the addition
     * these addresses to the ADL, while negative addresses indicates the removal of these addresses.
     *
     * @param adlUpdate the ADL update set
     */
    public void updateAdl(Set<Integer> adlUpdate) throws NoSuchDeviceException {
        if (adlUpdate == null) {
            throw new IllegalArgumentException("Cannot update ADL, because adlUpdate is null.");
        }

        // checks if all addresses in the update are valid
        checkAdlUpdateValidity(adlUpdate);

        adlUpdate.forEach(address -> {
            if (address < 0) {
                // negative addresses indicates removal
                adl.remove(address);
            } else {
                // positive address values indicates addition
                adl.add(address);
            }
        });
    }

    /**
     * Checks the validity of an ADL update.
     *
     * @param adlUpdate the adl update set to check for
     */
    private void checkAdlUpdateValidity(Set<Integer> adlUpdate) throws NoSuchDeviceException {
        adlUpdate.forEach(address -> {
            if (!fieldNodeHasDevice(address)) {
                throw new NoSuchDeviceException("Cannot update ADL, because no device with address " + address +
                        " exists.");
            }
        });
    }

    /**
     * Returns a boolean indicating whether the field node owns a device for a given device address.
     *
     * @param deviceAddress the device address to check for
     * @return true if field node has the device
     */
    private boolean fieldNodeHasDevice(int deviceAddress) {
        boolean hasDevice = false;

        Iterator<Integer> addresses = fieldNode.getFNST().keySet().iterator();
        while (addresses.hasNext() && !hasDevice) {
            if (addresses.next() == deviceAddress) {
                hasDevice = true;
            }
        }

        return hasDevice;
    }
}
