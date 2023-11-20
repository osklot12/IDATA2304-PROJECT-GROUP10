package no.ntnu.network.message.request;

import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.centralserver.clientproxy.FieldNodeClientProxy;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableMap;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.Map;

/**
 * A request to register a {@code FieldNode} at the central server.
 */
public class RegisterFieldNodeRequest extends RequestMessage implements Message<ServerContext> {
    private final Map<Integer, DeviceClass> fnst;
    private final String name;

    /**
     * Creates a new RegisterFieldNodeRequest.
     *
     * @param fnst the field node system table
     * @param name name of the field node
     */
    public RegisterFieldNodeRequest(Map<Integer, DeviceClass> fnst, String name) {
        super(NofspSerializationConstants.REGISTER_FIELD_NODE_COMMAND);
        if (fnst == null) {
            throw new IllegalArgumentException("Cannot create RegisterFieldNodeRequest, because fnst is null.");
        }

        if (name == null) {
            throw new IllegalArgumentException("Cannot create RegisterFieldNodeRequest, because name is null");
        }

        this.fnst = fnst;
        this.name = name;
    }

    /**
     * Creates a new RegisterFieldNodeRequest.
     *
     * @param fnst the field node system table
     */
    public RegisterFieldNodeRequest(Map<Integer, DeviceClass> fnst) {
        this(fnst, null);
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
        // creates a new client proxy for the field node
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

        return super.equals(r) && fnst.equals(r.getFnst()) && name.equals(r.getName());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + fnst.hashCode();

        return result;
    }
}
