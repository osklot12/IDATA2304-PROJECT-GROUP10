package no.ntnu.network.controlprocess;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.encryption.TlvEncryption;
import no.ntnu.network.message.encryption.cipher.decrypt.DecryptionStrategy;
import no.ntnu.network.message.encryption.cipher.decrypt.PlainTextDecryption;
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
    private DecryptionStrategy decryption;

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
        this.decryption = new PlainTextDecryption();
    }

    /**
     * Sets the decryption used for receiving Tlvs.
     *
     * @param decryption the decryption strategy to use
     */
    public void setDecryption(DecryptionStrategy decryption) {
        if (decryption == null) {
            throw new IllegalArgumentException("Cannot set decryption, because decryption strategy is null.");
        }

        this.decryption = decryption;
    }

    /**
     * Returns the next received TLV.
     * The method blocks until the end of the stream is met or an exception occurs.
     *
     * @return the next received tlv, null if end of stream is met
     * @throws IOException thrown if an I/O exception occurs
     */
    public Tlv getNextTlv() throws IOException {
        Tlv processedTlv = null;

        Tlv encryptedTlv = socketReader.readNextTlv();
        try {
            processedTlv = TlvEncryption.decryptTlv(encryptedTlv, decryption);
        } catch (EncryptionException e) {
            throw new IOException("Could not decrypt the TLV: " + e.getMessage());
        }

        return processedTlv;
    }
}
