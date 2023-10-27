package no.ntnu.broker;

import java.util.ArrayList;
import java.util.List;

public abstract class SubscriberList<T> {
    private final List<T> subscribers;

    /**
     * Constructor for the EventBroker class.
     */
    public SubscriberList() {
        this.subscribers = new ArrayList<>();
    }

    /**
     * Adds a subscriber to the list of subscribers.
     * Cannot add a subscriber that already exists in the list.
     *
     * @param subscriber subscriber to add
     * @return true if successfully added, false on error
     */
    public boolean addSubscriber(T subscriber) {
        boolean success = false;

        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
            success = true;
        }

        return success;
    }

    /**
     * Removes a subscriber from the list of subscribers.
     *
     * @param subscriber subscriber to remove
     * @return true if successfully removed, false on error
     */
    public boolean removeSubscriber(T subscriber) {
        return subscribers.remove(subscriber);
    }

    /**
     * Returns the subscribers for the broker.
     *
     * @return list of subscribers
     */
    public List<T> getSubscribers() {
        return subscribers;
    }
}
