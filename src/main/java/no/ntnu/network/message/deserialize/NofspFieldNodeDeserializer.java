package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.FieldNodeMessage;

import java.io.IOException;

/**
 * A deserializer for deserializing field node messages.
 */
public class NofspFieldNodeDeserializer extends NofspDeserializer implements MessageDeserializer<FieldNodeMessage> {
    @Override
    public FieldNodeMessage deserializeMessage(byte[] bytes) throws IOException {
        return null;
    }
}
