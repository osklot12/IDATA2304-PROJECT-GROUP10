package no.ntnu.network.connectionservice;

import no.ntnu.network.ControlCommAgent;
import no.ntnu.tools.logger.ServerLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A connection service that will automatically close the connection to a client if the client is not
 * accepted/registered within a time limit. The service is essential for only keeping connections that are
 * actually used, not wasting resources on useless connections.
 */
public class ClientGate implements ConnectionService {
    private final ControlCommAgent agent;
    private final long acceptancePhase;
    private ScheduledExecutorService scheduler;

    /**
     * Creates a ClientGate.
     *
     * @param agent the communication agent to possibly close
     * @param acceptancePhase the amount of time to wait before closing the connection, in milliseconds
     */
    public ClientGate(ControlCommAgent agent, long acceptancePhase) {
        if (agent == null) {
            throw new IllegalArgumentException("Cannot create ClientGate, because client is null.");
        }

        if (acceptancePhase <= 0) {
            throw new IllegalArgumentException("Cannot create ClientGate, because acceptance phase must be" +
                    "longer than 0 milliseconds.");
        }

        this.agent = agent;
        this.acceptancePhase = acceptancePhase;
    }

    @Override
    public void start() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        scheduler.schedule(this::closeConnection, acceptancePhase, TimeUnit.MILLISECONDS);
    }

    /**
     * Closes the connection to the client.
     */
    private void closeConnection() {
        ServerLogger.emergency("Client " + agent.getRemoteEntityAsString() + " has not been registered and will be" +
                " disconnected.");
        agent.close();
    }

    @Override
    public void stop() {
        if (scheduler != null && !(scheduler.isShutdown())) {
            scheduler.shutdownNow();
        }
    }
}
