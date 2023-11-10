package no.ntnu.network.message.serialize.tool;

import java.io.IOException;

/**
 * A source of bytes on which bytes can be read from.
 */
public interface ByteSource {
    /**
     * Reads the next byte from the source.
     *
     * @return next byte of data, or -1 if end of source is reached
     * @throws IOException thrown if an I/O error occurs
     */
    int read() throws IOException;
}
