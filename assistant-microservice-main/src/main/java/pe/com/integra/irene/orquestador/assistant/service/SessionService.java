package pe.com.integra.irene.orquestador.assistant.service;

import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Timeout;

import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.SessionResponse;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;
import pe.com.integra.irene.orquestador.assistant.model.SessionDetail;
import pe.com.integra.irene.orquestador.assistant.repository.SessionRepository;
import pe.com.integra.irene.shared.models.model.EntryMessage;

import java.time.LocalDateTime;

@ApplicationScoped
public class SessionService {
    private static final Logger logger = Logger.getLogger(SessionService.class);

    @ConfigProperty(name = "session.expiration.time", defaultValue = "300")
    int sessionExpirationTime;

    @Inject
    SessionRepository sessionRepository;

    @Timeout(5000)
    public String manageSession(Assistant assistant, String environmentId, EntryMessage entryMessage) {
        String masterUserId = entryMessage.getUserId();
        SessionDetail session = sessionRepository.getSession(masterUserId);
        String sessionId = session != null ? session.getSessionId() : null;

        if (sessionId != null) {
            logger.infof("Session found for user %s: %s", masterUserId, sessionId);
            sessionRepository.saveSession(session, masterUserId, sessionExpirationTime);
            return sessionId;
        }

        CreateSessionOptions sessionOptions = new CreateSessionOptions.Builder(environmentId).build();
        SessionResponse sessionResponse = assistant.createSession(sessionOptions).execute().getResult();

        SessionDetail newSession = new SessionDetail();
        newSession.setSessionId(sessionResponse.getSessionId());
        newSession.setUserId(masterUserId);
        newSession.setChannel(entryMessage.getChannel());
        newSession.setUserMode(entryMessage.getUserMode());

        sessionRepository.saveSession(newSession, masterUserId, sessionExpirationTime);

        logger.infof("Created new session for user %s: %s", masterUserId, sessionResponse.getSessionId());
        return sessionResponse.getSessionId();
    }
}
