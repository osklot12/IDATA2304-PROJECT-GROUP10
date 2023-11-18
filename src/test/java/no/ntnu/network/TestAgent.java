package no.ntnu.network;

import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * A communication agent for testing purposes.
 * It can be difficult and sometimes impossible to test real communication agents, and this class provides means
 * to test without having a proper agent.
 */
public class TestAgent implements CommunicationAgent {
    private RequestMessage requestSent = null;
    private ResponseMessage responseSent = null;
    private boolean responseAccepted = false;
    private boolean closed = false;

    @Override
    public void sendRequest(RequestMessage request) throws IOException {
        requestSent = request;
    }

    @Override
    public void sendResponse(ResponseMessage response) throws IOException {
        responseSent = response;
    }

    @Override
    public boolean acceptResponse(ResponseMessage responseMessage) {
        return responseAccepted = true;
    }

    @Override
    public void close() {
        closed = true;
    }

    /**
     * Returns the last request sent.
     *
     * @return last request sent
     */
    public RequestMessage getRequestSent() {
        return requestSent;
    }

    /**
     * Returns the last response sent.
     *
     * @return last response sent
     */
    public ResponseMessage getResponseSent() {
        return responseSent;
    }

    /**
     * Returns whether the last response was accepted or not.
     *
     * @return true if last response was accepted
     */
    public boolean responseAccepted() {
        return responseAccepted;
    }

    /**
     * Returns whether the agent is closed or not.
     *
     * @return true if closed
     */
    public boolean isClosed() {
        return closed;
    }
}
