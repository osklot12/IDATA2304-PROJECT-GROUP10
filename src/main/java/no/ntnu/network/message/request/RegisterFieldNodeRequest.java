package no.ntnu.network.message.request;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableMap;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.Map;

/**
 * A request to register a {@code FieldNode} at the central server.
 */
public class RegisterFieldNodeRequest extends RequestMessage<ServerContext> {
    private final Map<Integer, DeviceClass> fnst;
    private final Map<Integer, Integer> fnsm;
    private final String name;

    /**
     * Creates a new RegisterFieldNodeRequest.
     *
     * @param fnst the field node system table
     * @param fnsm the field node status map
     * @param name name of the field node
     */
    public RegisterFieldNodeRequest(Map<Integer, DeviceClass> fnst, Map<Integer, Integer> fnsm, String name) {
        super(NofspSerializationConstants.REGISTER_FIELD_NODE_COMMAND);
        if (fnst == null) {
            throw new IllegalArgumentException("Cannot create RegisterFieldNodeRequest, because fnst is null.");
        }

        if (fnsm == null) {
            throw new IllegalArgumentException("Cannot create RegisterFieldNodeRequest, because fnsm is null.");
        }

        if (name == null) {
            throw new IllegalArgumentException("Cannot create RegisterFieldNodeRequest, because name is null");
        }

        this.fnst = fnst;
        this.fnsm = fnsm;
        this.name = name;
    }

    /**
     * Creates a new RegisterFieldNodeRequest.
     *
     * @param id the message id
     * @param fnst the field node system table
     * @param fnsm the field node status map
     * @param name name of the field node
     */
    public RegisterFieldNodeRequest(int id, Map<Integer, DeviceClass> fnst, Map<Integer, Integer> fnsm, String name) {
        this(fnst, fnsm, name);

        setId(id);
    }

    /**
     * Returns the FNST for the field node.
     *
     * @return the fnst
     */
    public Map<Integer, DeviceClass> getFnst() {
        return fnst;
    }

    /**
     * Returns the field node status map.
     *
     * @return the fnsm
     */
    public Map<Integer, Integer> getFnsm() {
        return fnsm;
    }

    /**
     * Returns the name for the field node.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(ServerContext context) {
        ResponseMessage response = null;

        try {
            int clientAddress = context.registerFieldNode(fnst, fnsm, name);
            response = new RegistrationConfirmationResponse<>(clientAddress);
        } catch (ClientRegistrationException e) {
            response = new RegistrationDeclinedError<>(e.getMessage());
        }

        return response;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRequestMessage(this, DataTypeConverter.getSerializableFnst(fnst),
                DataTypeConverter.getSerializableFnsm(fnsm), new ByteSerializableString(name));
    }

    @Override
    public String toString() {
        return "requesting to register field node";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof RegisterFieldNodeRequest r)) {
            return false;
        }

        return super.equals(r) && fnst.equals(r.getFnst()) && fnsm.equals(r.getFnsm()) && name.equals(r.getName());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + fnst.hashCode();

        return result;
    }
}
