package no.ntnu.network.message.context;

import no.ntnu.exception.ActuatorInteractionFailedException;
import no.ntnu.exception.NoSuchDeviceException;
import no.ntnu.fieldnode.FieldNode;
import no.ntnu.network.message.request.RegisterFieldNodeRequest;
import no.ntnu.network.representation.FieldNodeInformation;
import no.ntnu.tools.logger.SimpleLogger;
import no.ntnu.network.ControlCommAgent;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * A message context for processing field node messages.
 */
public class FieldNodeContext extends ClientContext {
    private final FieldNode fieldNode;
    private final Set<Integer> adl;
    private final String name;

    /**
     * Creates a FieldNodeContext.
     *
     * @param agent     the communication agent
     * @param fieldNode the field node to operate on
     * @param adl       the active device list for the field node
     * @param name the name of the field node client
     * @param loggers   the loggers
     */
    public FieldNodeContext(ControlCommAgent agent, FieldNode fieldNode, Set<Integer> adl, String name, Set<SimpleLogger> loggers) {
        super(agent, loggers);
        if (fieldNode == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeContext, because field node is null");
        }

        if (name == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeContext, because name is null.");
        }

        this.fieldNode = fieldNode;
        this.adl = adl;
        this.name = name;
    }

    /**
     * Sets the state of an actuator.
     *
     * @param actuatorAddress the address of the actuator
     * @param newState        the new state to set
     * @throws ActuatorInteractionFailedException thrown if state for the given actuator cannot be set
     */
    public void setActuatorState(int actuatorAddress, int newState) throws ActuatorInteractionFailedException {
        fieldNode.setActuatorState(actuatorAddress, newState);
    }

    /**
     * Updates the Active Device List for the field node client.
     * The method takes in a set of device addresses as an argument, where positive addresses indicated the addition
     * these addresses to the ADL, while negative addresses indicates the removal of these addresses.
     *
     * @param adlUpdate the ADL update set
     * @return the updated adl
     */
    public Set<Integer> updateAdl(Set<Integer> adlUpdate) throws NoSuchDeviceException {
        if (adlUpdate == null) {
            throw new IllegalArgumentException("Cannot update ADL, because adlUpdate is null.");
        }

        // checks if all addresses in the update are valid
        checkAdlUpdateValidity(adlUpdate);

        adlUpdate.forEach(address -> {
            if (address < 0) {
                // negative addresses indicates removal
                int addressToRemove = address * -1;
                adl.remove(addressToRemove);
            } else {
                // positive address values indicates addition
                adl.add(address);
            }
        });

        return adl;
    }

    /**
     * Checks the validity of an ADL update.
     *
     * @param adlUpdate the adl update set to check for
     */
    private void checkAdlUpdateValidity(Set<Integer> adlUpdate) throws NoSuchDeviceException {
        adlUpdate.forEach(address -> {
            if (!fieldNodeHasDevice(address) && !fieldNodeHasDevice(address * -1)) {
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

    @Override
    public void register() {
        FieldNodeInformation fieldNodeInformation =
                new FieldNodeInformation(fieldNode.getFNST(), fieldNode.getFNSM(), name);
        try {
            agent.sendRequest(new RegisterFieldNodeRequest(fieldNodeInformation));
        } catch (IOException e) {
            logError("Cannot send registration request: " + e.getMessage());
        }
    }
}
