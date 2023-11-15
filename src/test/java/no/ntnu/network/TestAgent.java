package no.ntnu.network;

import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * A communication agent for testing.
 * It can be difficult and sometimes impossible to test real communication agents, and this class provides means
 * to test without having a proper agent.
 */
public class TestAgent implements CommunicationAgent {
    public RequestMessage requestSent = null;
    public ResponseMessage responseSent = null;
    public boolean responseAccepted = false;
    public boolean closed = false;

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
}
