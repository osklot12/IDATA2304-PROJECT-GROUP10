package no.ntnu.network.message.request;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A request message for sent from a client to the server, requesting to disconnect.
 * The request provides a proper way of disconnecting from the server, allowing the server to prevent disconnections
 * of client while crucial tasks are being performed.
 */
public class DisconnectRequest extends RequestMessage implements Message<ServerContext> {
    /**
     * Creates a new DisconnectRequest.
     */
    public DisconnectRequest() {
        super(NofspSerializationConstants.DISCONNECT_CLIENT_COMMAND);
    }

    /**
     * Creates a new DisconnectRequest.
     *
     * @param id the message id
     */
    public DisconnectRequest(int id) {
        this();

        setId(id);
    }

    @Override
    public void process(ServerContext context) throws IOException {

    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return new byte[0];
    }
}
