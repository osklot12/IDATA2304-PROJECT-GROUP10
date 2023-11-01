package no.ntnu.network.centralserver.clientproxy;

import java.net.Socket;

/**
 * A proxy for a client using the services of the central server, used for storing information about a client and
 * interacting with them.
 */
public abstract class ClientProxy {
    private final Socket controlSocket;

    /**
     * Creates a new ClientProxy.
     *
     * @param controlSocket client socket
     */
    protected ClientProxy(Socket controlSocket) {
        if (controlSocket == null) {
            throw new IllegalArgumentException("Cannot create Client, because client socket is null.");
        }

        this.controlSocket = controlSocket;
    }

    /**
     * Returns the control socket of the client.
     *
     * @return control socket
     */
    public Socket getControlSocket() {
        return controlSocket;
    }
}
