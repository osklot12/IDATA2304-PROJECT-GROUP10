package no.ntnu.network.connectionservice;

import no.ntnu.broker.ConnectionServiceShutdownBroker;
import no.ntnu.network.CommunicationAgent;
import no.ntnu.network.message.request.HeartbeatRequest;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A connection service sending heart beat requests to a remote network entity.
 * The heartbeat requests are sent periodically at a given interval.
 */
public class HeartBeater implements ConnectionService {
    private final CommunicationAgent agent;
    private final long interval;
    private final ConnectionServiceShutdownBroker shutdownBroker;
    private ScheduledExecutorService scheduler;

    /**
     * Creates a new HeartBeater.
     *
     * @param agent the communication agent to send heartbeats to
     * @param interval the interval between the requests sent
     */
    public HeartBeater(CommunicationAgent agent, long interval) {
        this.agent = agent;
        this.interval = interval;
        this.shutdownBroker = new ConnectionServiceShutdownBroker();
    }

    /**
     * Adds a listener to listen for shutdown of the service.
     *
     * @param listener the listener to add
     */
    public void addShutdownListener(ConnServiceShutdownListener listener) {
        shutdownBroker.addSubscriber(listener);
    }

    @Override
    public void start() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        // Schedule heartbeat task
        scheduler.scheduleAtFixedRate(this::sendHeartbeat, interval, interval, TimeUnit.MILLISECONDS);
    }

    /**
     * Sends a heartbeat request.
     */
    private void sendHeartbeat() {
        try {
            agent.sendRequest(new HeartbeatRequest<>());
        } catch (IOException e) {
            stop();
            // notifies the listeners about the shutdown of the service
            shutdownBroker.notifyListeners(this);
        }
    }

    @Override
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }
}