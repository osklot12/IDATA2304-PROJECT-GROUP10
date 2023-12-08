package no.ntnu.network.controlprocess;

import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.tools.logger.SystemOutLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Sends Type-Value-Length structures of bytes to a remote socket using TCP.
 */
public class TcpTlvSender {
    private final OutputStream outputStream;

    /**
     * Creates a new TCPMessageSender.
     *
     * @param socket socket to send messages to
     * @throws IOException thrown if an I/O exception occurs
     */
    public TcpTlvSender(Socket socket) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Cannot create TcpTlvSender, because socket is null.");
        }

        this.outputStream = socket.getOutputStream();
    }

    /**
     * Sends a TLV to the remote socket.
     *
     * @param tlv tlv to send
     * @throws IOException thrown if an I/O exception occurs
     */
    public void sendTlv(Tlv tlv) throws IOException {
        outputStream.write(tlv.toBytes());
        outputStream.flush();
    }
}
