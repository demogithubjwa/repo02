package pe.com.integra.irene.adaptador.chat.websockets;

import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

import io.quarkus.websockets.next.WebSocketConnection;

public class WebSocketSessionManager {
    private static final Logger logger = Logger.getLogger(WebSocketSessionManager.class);

    private static final ConcurrentHashMap<String, WebSocketConnection> sesiones = new ConcurrentHashMap<>();

    public static void addSession(String session, WebSocketConnection connection) {
        if (session == null || session.isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (connection == null) {
            throw new IllegalArgumentException("WebSocketConnection cannot be null");
        }
        sesiones.put(session, connection);
        logger.infof("Session added: %s. Total sessions: %d", session, sesiones.size());
    }

    public static void removeSession(String session) {
        if (session == null || session.isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        WebSocketConnection connection = sesiones.remove(session);
        if (connection != null) {
            connection.close();
            logger.infof("Session removed: %s. Total sessions: %d", session, sesiones.size());
        } else {
            logger.warnf("Attempted to remove non-existing session: %s", session);
        }
    }

    public static WebSocketConnection getSession(String session) {
        return sesiones.get(session);
    }

    public static ConcurrentHashMap<String, WebSocketConnection> getAllSessions() {
        return sesiones;
    }
}
