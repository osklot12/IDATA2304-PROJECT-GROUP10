package no.ntnu.network.message.response;

import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.Map;

/**
 * A response to a {@code SubscribeToFieldNodeRequest}, confirming that the control panel has been subscribed.
 * The response contains all the information the control panel needs about the field node.
 */
public class SubscribedToFieldNodeResponse extends ResponseMessage implements Message<ControlPanelContext> {
    private final Map<Integer, DeviceClass> fnst;
    private final Map<Integer, Integer> fnsm;
    private final String name;

    /**
     * Creates a new SubscribedToFieldNodeResponse.
     *
     * @param fnst the field node system table
     * @param fnsm the field node status map
     * @param name the name of the field node
     */
    public SubscribedToFieldNodeResponse(Map<Integer, DeviceClass> fnst, Map<Integer, Integer> fnsm, String name) {
        super(NofspSerializationConstants.SUBSCRIBED_TO_FIELD_NODE_CODE);
        if (fnst == null) {
            throw new IllegalArgumentException("Cannot create SubscribedToFieldNodeResponse, because fnst is null.");
        }

        if (fnsm == null) {
            throw new IllegalArgumentException("Cannot create SubscribedToFieldNodeResponse, because fnsm is null.");
        }

        if (name == null) {
            throw new IllegalArgumentException("Cannot create SubscribedToFieldNodeResponse, because name is null.");
        }

        this.fnst = fnst;
        this.fnsm = fnsm;
        this.name = name;
    }

    /**
     * Creates a new SubscribedToFieldNodeResponse.
     *
     * @param id the message id
     * @param fnst the field node system table
     * @param fnsm the field node status map
     * @param name the name of the field node
     */
    public SubscribedToFieldNodeResponse(int id, Map<Integer, DeviceClass> fnst, Map<Integer, Integer> fnsm, String name) {
        this(fnst, fnsm, name);

        setId(id);
    }

    @Override
    public void process(ControlPanelContext context) throws IOException {
        if (context.acceptResponse(this)) {
            context.logReceivingResponse(this);
        }
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitResponseMessage(this, DataTypeConverter.getSerializableFnst(fnst),
                DataTypeConverter.getSerializableFnsm(fnsm), new ByteSerializableString(name));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SubscribedToFieldNodeResponse s)) {
            return false;
        }

        return super.equals(s) && fnst.equals(s.fnst) && fnsm.equals(s.fnsm) && name.equals(s.name);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + fnst.hashCode();
        result = result * 31 + fnsm.hashCode();
        result = result * 31 + name.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "successfully subscribed to field node '" + name + "'.";
    }
}
