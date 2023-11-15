package no.ntnu.network.controlprocess;

import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.tools.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A map storing pending requests, removing them automatically after a given period of time.
 */
public class PendingRequestMap extends ConcurrentHashMap<Integer, PendingRequest> {
    private final long ttl;

    /**
     * Creates a new PendingRequestMap.
     *
     * @param ttl time-to-live - the amount of time each request can live, in milliseconds
     */
    public PendingRequestMap(long ttl) {
        if (ttl < 1) {
            throw new IllegalArgumentException("Cannot create PendingRequestMap, because ttl must be a positive integer.");
        }

        this.ttl = ttl;
        startTimeoutChecker();
    }

    /**
     * Starts a timeout checker, removing expired requests.
     */
    private void startTimeoutChecker() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        executorService.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            for (Map.Entry<Integer, PendingRequest> entry : entrySet()) {
                if (currentTime - entry.getValue().timestamp() > ttl) {
                    handleTimeout(entry.getKey());
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Handles the case of a timeout for a request.
     *
     * @param key the key for the timed out request
     */
    private void handleTimeout(Integer key) {
        RequestMessage request = get(key).request();
        Logger.info("Request " + request.toString() + " with id " + request.getId().toString() + " has timed out.");
        remove(key);
    }
}