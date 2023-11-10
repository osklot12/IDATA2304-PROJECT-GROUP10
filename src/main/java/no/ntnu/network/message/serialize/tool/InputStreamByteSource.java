package no.ntnu.network.message.serialize.tool;

import java.io.IOException;
import java.io.InputStream;

/**
 * Adapter for an {@code InputStream}, making it acting as a {@code ByteSource}.
 */
public class InputStreamByteSource implements ByteSource {
    private final InputStream inputStream;

    /**
     * Creates a new InputStreamByteSource.
     *
     * @param inputStream the input stream
     */
    public InputStreamByteSource(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Cannot create InputStreamByteSource, because input stream is null");
        }

        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
}
