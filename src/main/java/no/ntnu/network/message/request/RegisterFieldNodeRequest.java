package no.ntnu.network.message.request;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.centralserver.clientproxy.FieldNodeClientProxy;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableMap;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.Map;

/**
 * A request to register a {@code FieldNode} at the central server.
 */
public class RegisterFieldNodeRequest extends RequestMessage implements Message<ServerContext> {
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
     * Returns the FNST for the field node.
     *
     * @return the fnst
     */
    public Map<Integer, DeviceClass> getFnst() {
        return fnst;
    }

    /**
     * Returns the FNST in a serializable format.
     *
     * @return the serializable fnst
     */
    public ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> getSerializableFnst() {
        ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> result = new ByteSerializableMap<>();

        fillSerializableFnst(result);

        return result;
    }

    private void fillSerializableFnst(ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> result) {
        fnst.forEach((key, value) -> {
            ByteSerializableInteger integerKey = new ByteSerializableInteger(key);
            ByteSerializableString stringValue = new ByteSerializableString(value.name());
            result.put(integerKey, stringValue);
        });
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
     * Returns the field node status map in a serializable format.
     *
     * @return serializable fnsm
     */
    public ByteSerializableMap<ByteSerializableInteger, ByteSerializableInteger> getSerializableFnsm() {
        ByteSerializableMap<ByteSerializableInteger, ByteSerializableInteger> result = new ByteSerializableMap<>();

        fillSerializableFnsm(result);

        return result;
    }

    /**
     * Fills a serializable map with the entries from the FNSM.
     *
     * @param result the serializable map to fill
     */
    private void fillSerializableFnsm(ByteSerializableMap<ByteSerializableInteger, ByteSerializableInteger> result) {
        fnsm.forEach((key, value) -> {
            result.put(new ByteSerializableInteger(key), new ByteSerializableInteger(value));
        });
    }

    /**
     * Returns the name for the field node.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name in a serializable format.
     *
     * @return the serializable name
     */
    public ByteSerializableString getSerializableName() {
        return new ByteSerializableString(name);
    }

    @Override
    public void process(ServerContext context) throws IOException {
        context.logReceivingRequest(this);

        ResponseMessage response = null;
        try {
            int clientAddress = context.registerFieldNode(fnst, fnsm, name);
            response = new RegistrationConfirmationResponse<>(clientAddress);
        } catch (ClientRegistrationException e) {
            response = new RegistrationDeclinedError<>(e.getMessage());
        }

        context.respond(response);
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRegisterFieldNodeRequest(this);
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
