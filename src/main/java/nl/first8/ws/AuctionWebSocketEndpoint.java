package nl.first8.ws;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import nl.first8.services.PushService;

@ServerEndpoint(value = "/ws/actions", configurator = GetHttpSessionConfigurator.class)
public class AuctionWebSocketEndpoint {

	private static final Logger log = Logger.getLogger(AuctionWebSocketEndpoint.class.getName());

    @Inject
    private PushService pushService;

    @OnOpen
    public void open(Session session, EndpointConfig config) {
        pushService.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        log.info("closing session: " + session.getId());
        try {
            pushService.removeSession(session);
            session.close();
        } catch( IOException e ) {
            log.log(Level.SEVERE, "problem closing session: " + session.getId(), e);
        }
    }

    @OnError
    public void onError(Throwable error) {
        log.log(Level.SEVERE,"received error",error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) throws IOException {
    }
}
