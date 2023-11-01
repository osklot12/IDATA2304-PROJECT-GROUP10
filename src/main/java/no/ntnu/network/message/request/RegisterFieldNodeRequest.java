package no.ntnu.network.message.request;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.Message;

import java.util.Map;

/**
 * A request sent from a field node to the central server, requesting to register as a client.
 */
public class RegisterFieldNodeRequest implements Request {
    private final Map<Integer, DeviceClass> fnst;
    private String name;

    /**
     * Creates a new RegisterFieldNodeRequest.
     *
     * @param fnst the field node system table for the field node
     * @param name the name for the field node
     */
    public RegisterFieldNodeRequest(Map<Integer, DeviceClass> fnst, String name) {
        if (fnst == null) {
            throw new IllegalArgumentException("Cannot create RegisterFieldNodeRequest, because FNST is null.");
        }

        this.fnst = fnst;
        this.name = name;
    }

    /**
     * Creates a new RegisterFieldNodeRequest.
     *
     * @param fnst the field node system table for the field node
     */
    public RegisterFieldNodeRequest(Map<Integer, DeviceClass> fnst) {
        if (fnst == null) {
            throw new IllegalArgumentException("Cannot create RegisterFieldNodeRequest, because FNST is null.");
        }

        this.fnst = fnst;
    }

    public Message execute() {
        Message response = null;

        return response;
    }
}
