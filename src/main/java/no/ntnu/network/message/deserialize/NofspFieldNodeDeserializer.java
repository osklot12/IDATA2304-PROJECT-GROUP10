package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.FieldNodeContext;

import java.io.IOException;

/**
 * A deserializer for deserializing field node messages.
 */
public class NofspFieldNodeDeserializer extends NofspDeserializer implements MessageDeserializer<FieldNodeContext> {
    @Override
    public Message<FieldNodeContext> deserializeMessage(byte[] bytes) throws IOException {
        return null;
    }
}
