package no.ntnu.network.message.context;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.network.CommunicationAgent;
import no.ntnu.network.ServerAgent;
import no.ntnu.network.centralserver.CentralHub;
import no.ntnu.network.centralserver.clientproxy.ClientProxy;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.ServerLogger;

import java.io.IOException;

/**
 * A context for processing server messages.
 */
public class ServerContext implements MessageContext {
    private final ServerAgent agent;
    private final CentralHub centralHub;
    private final String remoteSocketAddress;

    /**
     * Creates a new CentralServerContext.
     *
     * @param agent the communication agent
     * @param centralHub the central hub to operate on
     * @param remoteSocketAddress the address for the remote socket
     */
    public ServerContext(ServerAgent agent, CentralHub centralHub, String remoteSocketAddress) {
        if (agent == null) {
            throw new IllegalArgumentException("Cannot create ServerContext, because agent is null");
        }

        if (centralHub == null) {
            throw new IllegalArgumentException("Cannot create ServerContext, because central hub is null.");
        }

        if (remoteSocketAddress == null) {
            throw new IllegalArgumentException("Cannot create ServerContext, because remote socket address is null");
        }

        this.agent = agent;
        this.centralHub = centralHub;
        this.remoteSocketAddress = remoteSocketAddress;
    }

    /**
     * Registers a client proxy at the central hub.
     *
     * @param clientProxy the client proxy to register
     * @throws ClientRegistrationException thrown if registration fails
     */
    public int registerClient(ClientProxy clientProxy) throws ClientRegistrationException {
        int clientAddress = centralHub.registerClient(clientProxy);

        if (clientAddress != -1) {
            // agent.registerClient();
        }

        return clientAddress;
    }

    /**
     * Returns the communication agent.
     *
     * @return the communication agent
     */
    public ServerAgent getAgent() {
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

    @Override
    public void logReceivingRequest(RequestMessage request) {
        ServerLogger.requestReceived(request, remoteSocketAddress);
    }

    @Override
    public void logReceivingResponse(ResponseMessage response) {
        ServerLogger.responseReceived(response, remoteSocketAddress);
    }
}
