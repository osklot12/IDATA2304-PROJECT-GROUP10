package no.ntnu.network.message.request;

import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.DisconnectionAllowedResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.AuthenticationFailedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A request message for sent from a client to the server, requesting to disconnect.
 * The request provides a proper way of disconnecting from the server, allowing the server to prevent disconnections
 * of client while crucial tasks are being performed.
 */
public class DisconnectRequest extends StandardProcessingRequestMessage<ServerContext> {
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
    protected ResponseMessage executeAndCreateResponse(ServerContext context) {
        ResponseMessage response = null;

        if (context.isClientRegistered()) {
            context.deregisterClient();
            response = new DisconnectionAllowedResponse<>();
        } else {
            response = new AuthenticationFailedError<>();
        }

        return response;
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitRequestMessage(this);
    }

    @Override
    public String toString() {
        return "requesting to disconnect from the server";
    }
}
