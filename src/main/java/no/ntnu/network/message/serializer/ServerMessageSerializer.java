package no.ntnu.network.message.serializer;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.request.RequestMessage;

/**
 * A serializer for the central server, implementing data marshalling described by NOFSP.
 */
public class ServerMessageSerializer implements MessageSerializer {
    private CentralServer centralServer;

    /**
     * Creates a new ServerMessageSerializer.
     */
    public ServerMessageSerializer(CentralServer centralServer) {
        if (centralServer == null) {
            throw new IllegalArgumentException("Cannot create ServerMessageSerializer, because central server is null.");
        }

        this.centralServer = new CentralServer();
    }

    @Override
    public byte[] serialize(Message message) throws SerializationException {
        if (message == null) {
            throw new IllegalArgumentException("Cannot serialize message, because message is null.");
        }

        byte[] messageBytes = null;

        if (message instanceof RequestMessage requestMessage) {
            messageBytes = getRequestMessageBytes(requestMessage);
        } else {
            throw new SerializationException("Cannot serialize message as message type is not" +
                    "recognized.");
        }

        return messageBytes;
    }

    private byte[] getRequestMessageBytes(RequestMessage requestMessage) {
        return null;
    }

    @Override
    public Message deserialize(byte[] bytes) throws SerializationException {
        return null;
    }

    private static byte[] toBytes(int value, int length) {
        long maxValue = (1L << (length * 8)) - 1;

        if (value < 0 || value > maxValue) {
            return null;
        }

        byte[] byteArray = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            byteArray[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return byteArray;
    }

    private static byte[] mergeArrays(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }

        byte[] result = new byte[totalLength];

        int currentPos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentPos, array.length);
            currentPos += array.length;
        }

        return result;
    }

}
