package nl.first8.services;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;

import org.codehaus.jackson.map.ObjectMapper;

@ApplicationScoped
public class PushService {

	private static final Logger log = Logger.getLogger(PushService.class.getName());

    private static final Set<Session> sessions =
            Collections.synchronizedSet(new HashSet<Session>());


    public PushService() {
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public boolean removeSession(Session session) {
        return sessions.remove(session);
    }

    public void push(Object obj) {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(toJson(obj));
            } catch (Exception e) {
                log.log(Level.SEVERE, "Unable to send object to session..", e);
            }
        }
    }

    private String toJson(Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
