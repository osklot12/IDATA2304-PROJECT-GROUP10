package no.ntnu.controlpanel;

/**
 * A listener listening for events for a control panel.
 */
public interface ControlPanelListener {
    /**
     * Triggers when the control panel receives a field node pool.
     *
     * @param controlPanel the control panel that received the pool
     */
    void fieldNodePoolReceived(ControlPanel controlPanel);

    /**
     * Triggers when a virtual field node is added to the control panel.
     *
     * @param fieldNodeAddress the address of the added field node
     */
    void fieldNodeAdded(int fieldNodeAddress);

    /**
     * Triggers when a virtual field node is removed from the control panel.
     *
     * @param fieldNodeAddress the address of the removed field node
     */
    void fieldNodeRemoved(int fieldNodeAddress);
}
