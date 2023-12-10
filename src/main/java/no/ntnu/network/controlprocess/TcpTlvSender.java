package no.ntnu.network.controlprocess;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.encryption.TlvEncryption;
import no.ntnu.network.message.encryption.cipher.encrypt.EncryptionStrategy;
import no.ntnu.network.message.encryption.cipher.encrypt.PlainTextEncryption;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Sends Type-Value-Length structures of bytes to a remote socket using TCP.
 */
public class TcpTlvSender {
    private final OutputStream outputStream;
    private EncryptionStrategy encryption;

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
        this.encryption = new PlainTextEncryption();
    }

    /**
     * Sets the encryption used for sending Tlvs.
     *
     * @param encryption the encryption strategy to use
     */
    public void setEncryption(EncryptionStrategy encryption) {
        if (encryption == null) {
            throw new IllegalArgumentException("Cannot set encryption, because encryption strategy is null.");
        }

        this.encryption = encryption;
    }

    /**
     * Sends a TLV to the remote socket.
     *
     * @param tlv tlv to send
     * @throws IOException thrown if an I/O exception occurs
     */
    public void sendTlv(Tlv tlv) throws IOException {
        Tlv processedTlv = null;

        try {
            processedTlv = TlvEncryption.encryptTlv(tlv, encryption);
        } catch (EncryptionException e) {
            throw new IOException("Could not encrypt the TLV: " + e.getMessage());
        }

        writeAndFlush(processedTlv);
    }

    /**
     * Writes the TLV to the output stream and flushes it.
     *
     * @param tlv the tlv to write and flush
     * @throws IOException thrown if an I/O exception occurs
     */
    private void writeAndFlush(Tlv tlv) throws IOException {
        if (tlv != null) {
            outputStream.write(tlv.toBytes());
            outputStream.flush();
        }
    }
}
