package pe.com.integra.irene.scheduler.service;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import pe.com.integra.irene.scheduler.handler.InactivityHandler;
import pe.com.integra.irene.scheduler.repository.SessionRepository;

import java.time.LocalDateTime;

@ApplicationScoped
public class InactivityService {
    private static final Logger logger = Logger.getLogger(InactivityService.class);

    @Inject
    SessionRepository sessionRepository;

    @Inject
    InactivityHandler inactivityHandler;

    @Scheduled(every = "10s")
    void increment() {
        logger.info("Current time: " + LocalDateTime.now());
        sessionRepository.getAllSessions().subscribe().with(session -> {
            try {
                inactivityHandler.handleSessionInactivity(session);
            } catch (Exception ex) {
                logger.error("Error handling session inactivity", ex);
            }
        });
    }
}
