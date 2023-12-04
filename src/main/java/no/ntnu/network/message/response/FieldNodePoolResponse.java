package no.ntnu.network.message.response;

import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.Map;

/**
 * A response to a successful field node pool pull request, containing the field node pool for the central server.
 */
public class FieldNodePoolResponse extends StandardProcessingResponseMessage<ControlPanelContext> {
    private final Map<Integer, String> fieldNodePool;

    /**
     * Creates a new FieldNodePoolResponse.
     *
     * @param fieldNodePool the field node pool
     */
    public FieldNodePoolResponse(Map<Integer, String> fieldNodePool) {
        super(NofspSerializationConstants.FIELD_NODE_POOL_CODE);

        if (fieldNodePool == null) {
            throw new IllegalArgumentException("Cannot create FieldNodePoolResponse, because fieldNodePool is null.");
        }

        this.fieldNodePool = fieldNodePool;
    }

    /**
     * Creates a new FieldNodePoolResponse.
     *
     * @param id the message id
     */
    public FieldNodePoolResponse(int id, Map<Integer, String> fieldNodePool) {
        this(fieldNodePool);

        setId(id);
    }

    @Override
    protected void handleResponseProcessing(ControlPanelContext context) {
        // TODO handle further processing
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitResponseMessage(this, DataTypeConverter.getSerializableFieldNodePool(fieldNodePool));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof FieldNodePoolResponse f)) {
            return false;
        }

        return super.equals(f) && fieldNodePool.equals(f.fieldNodePool);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + fieldNodePool.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "field node pool of size " + fieldNodePool.size() + " provided";
    }
}
