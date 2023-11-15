package no.ntnu.network.message.context;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.network.CommunicationAgent;
import no.ntnu.network.centralserver.CentralHub;
import no.ntnu.network.centralserver.ClientHandler;
import no.ntnu.network.centralserver.clientproxy.ClientProxy;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;
import java.net.Socket;

/**
 * A context for processing messages for the central server.
 * The context provides access to objects that control messages needs for processing.
 */
public class ServerContext implements MessageContext {
    private final CommunicationAgent agent;
    private final CentralHub centralHub;

    /**
     * Creates a new CentralServerContext.
     *
     * @param agent the communication agent
     * @param centralHub the central hub to operate on
     */
    public ServerContext(CommunicationAgent agent, CentralHub centralHub) {
        this.agent = agent;
        this.centralHub = centralHub;
    }

    /**
     * Registers a client proxy at the central hub.
     *
     * @param clientProxy the client proxy to register
     * @throws ClientRegistrationException thrown if registration fails
     */
    public int registerClient(ClientProxy clientProxy) throws ClientRegistrationException {
        return centralHub.registerClient(clientProxy);
    }

    /**
     * Returns the communication agent.
     *
     * @return the communication agent
     */
    public CommunicationAgent getAgent() {
        return agent;
    }

    @Override
    public void respond(ResponseMessage responseMessage) throws IOException {
        agent.sendResponse(responseMessage);
    }

    @Override
    public boolean acceptResponse(ResponseMessage response) {
        return agent.acceptResponse(response);
    }
}
