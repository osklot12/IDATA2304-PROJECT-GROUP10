package no.ntnu.network.connectionservice.requestmanager;

import no.ntnu.broker.RequestTimeoutBroker;
import no.ntnu.network.connectionservice.ConnectionService;
import no.ntnu.network.message.request.RequestMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A class managing sent requests.
 */
public class RequestManager implements ConnectionService {
    private final HashMap<Integer, PendingRequest> requests;
    private final RequestTimeoutBroker timeoutBroker;
    private Thread timeoutCheckThread;
    private volatile boolean running;


    /**
     * Creates a new RequestManager.
     */
    public RequestManager() {
        this.requests = new HashMap<>();
        this.timeoutBroker = new RequestTimeoutBroker();
    }

    /**
     * Adds a request message to be handled.
     *
     * @param request the request message to add
     * @param ttl the time-to-live for the request message in milliseconds
     */
    public void putRequest(RequestMessage request, long ttl) {
        if (request == null) {
            throw new IllegalArgumentException("Cannot add request, because request is null");
        }

        if (ttl <= 0) {
            throw new IllegalArgumentException("Cannot add request, because ttl must be bigger than 0");
        }

        assignRequestId(request);
        requests.put(request.getId(), new PendingRequest(request, System.currentTimeMillis(), ttl));
    }

    private void assignRequestId(RequestMessage request) {
        int messageId = getRandomAvailableId();
        request.setId(messageId);
    }

    private int getRandomAvailableId() {
        int counter = 0;
        int randomJumper = new Random().nextInt(50);

        int checkId;
        do {
            counter++;
            checkId = counter * randomJumper;
        } while (requests.containsKey(checkId));

        return checkId;
    }

    /**
     * Pulls a request, removing it from the request handler.
     *
     * @param requestId the message id for the request
     * @return the request message, null if no request with given id exists
     */
    public RequestMessage pullRequest(int requestId) {
        RequestMessage request = null;

        PendingRequest pendingRequest = requests.get(requestId);
        if (pendingRequest != null) {
            request = pendingRequest.request();
            requests.remove(requestId);
        }

        return request;
    }

    /**
     * Handles the timeout for a request message
     *
     * @param request timed out request message
     */
    private void handleRequestTimeout(RequestMessage request) {
        timeoutBroker.notifyListeners(request);
    }

    /**
     * Adds a listener listening for timed out requests.
     *
     * @param listener listener to add
     */
    public void addListener(RequestTimeoutListener listener) {
        timeoutBroker.addSubscriber(listener);
    }

    @Override
    public void start() {
        running = true;
        timeoutCheckThread = new Thread(this::checkForTimeouts);
        timeoutCheckThread.start();
    }

    private void checkForTimeouts() {
        while (running) {
            long currentTime = System.currentTimeMillis();
            List<Integer> timedOutRequests = new ArrayList<>();

            synchronized (requests) {
                requests.forEach((requestId, pendingRequest) -> {
                    long requestTime = pendingRequest.timestamp();
                    long ttl = pendingRequest.ttl();

                    if (currentTime - requestTime > ttl) {
                        handleRequestTimeout(pendingRequest.request());
                        timedOutRequests.add(requestId);
                    }
                });

                timedOutRequests.forEach(requests::remove);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @Override
    public void stop() {
        running = false;
        timeoutCheckThread.interrupt();
    }
}
