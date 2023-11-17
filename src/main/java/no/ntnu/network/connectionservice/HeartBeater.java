package no.ntnu.network.connectionservice;

import no.ntnu.network.CommunicationAgent;
import no.ntnu.network.message.request.HeartbeatRequest;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartBeater implements ConnectionService {
    private final CommunicationAgent agent;
    private ScheduledExecutorService scheduler;
    private final long interval;

    public HeartBeater(CommunicationAgent agent, long interval) {
        this.agent = agent;
        this.interval = interval;
    }

    @Override
    public void start() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        // Schedule heartbeat task
        scheduler.scheduleAtFixedRate(this::sendHeartbeat, interval, interval, TimeUnit.MILLISECONDS);
    }

    private void sendHeartbeat() {
        try {
            // Assuming HeartbeatRequest constructor does not require parameters
            agent.sendRequest(new HeartbeatRequest<>());
        } catch (IOException e) {
            // Handle exceptions, possibly logging them or taking other actions
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