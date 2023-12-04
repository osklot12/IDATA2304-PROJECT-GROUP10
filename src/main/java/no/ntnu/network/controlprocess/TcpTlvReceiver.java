package no.ntnu.network.controlprocess;

import no.ntnu.network.message.serialize.tool.InputStreamByteSource;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvFrame;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.net.Socket;

/**
 * Receives Type-Length-Value structures of bytes from a remote socket using TCP.
 */
public class TcpTlvReceiver {
    private final TlvReader socketReader;

    /**
     * Creates a new TCPMessageReceiver.
     *
     * @param socket the socket to receive messages from
     * @param tlvFrame the frame for received tlvs
     */
    public TcpTlvReceiver(Socket socket, TlvFrame tlvFrame) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Cannot create TcpTlvReceiver, because socket is null");
        }

        if (tlvFrame == null) {
            throw new IllegalArgumentException("Cannot create TcpTlvReceiver, because tlvFrame is null.");
        }

        this.socketReader = new TlvReader(new InputStreamByteSource(socket.getInputStream()), tlvFrame);
    }

    /**
     * Returns the next received TLV.
     * The method blocks until the end of the stream is met or an exception occurs.
     *
     * @return the next received tlv, null if end of stream is met
     * @throws IOException thrown if an I/O exception occurs
     */
    public Tlv getNextTlv() throws IOException {
        return socketReader.readNextTlv();
    }
}
