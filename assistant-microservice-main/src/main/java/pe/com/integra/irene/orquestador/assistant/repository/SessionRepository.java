package pe.com.integra.irene.orquestador.assistant.repository;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import pe.com.integra.irene.orquestador.assistant.model.SessionDetail;

import java.time.LocalDateTime;

@ApplicationScoped
public class SessionRepository {
    private static final Logger logger = Logger.getLogger(SessionRepository.class);

    private final ValueCommands<String, SessionDetail> commands;
    private final String sessionPrefix = "session:";

    public SessionRepository(RedisDataSource ds) {
        commands = ds.value(SessionDetail.class);
    }

    public SessionDetail getSession(String masterUserId) {
        var sessionKey = sessionPrefix + masterUserId;
        SessionDetail session = null;
        try {
            session = commands.get(sessionKey);
            logger.infof("Session retrieved for key %s: %s", sessionKey, session);
        } catch (Exception e) {
            logger.error("Error fetching session", e);
        }
        return session;
    }

    public void saveSession(SessionDetail sessionDetail, String masterUserId, int expirationTime) {
        var sessionKey = sessionPrefix + masterUserId;
        try {
            sessionDetail.setId(sessionKey);
            sessionDetail.setLatestMessageDate(LocalDateTime.now());
            commands.setex(sessionKey, expirationTime, sessionDetail);
        } catch (Exception e) {
            logger.error("Error saving session to Redis", e);
        }
    }
}