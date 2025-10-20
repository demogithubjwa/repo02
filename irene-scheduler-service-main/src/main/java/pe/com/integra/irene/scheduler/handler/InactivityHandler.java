package pe.com.integra.irene.scheduler.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import pe.com.integra.irene.scheduler.model.SessionDetail;
import pe.com.integra.irene.scheduler.repository.SessionRepository;
import pe.com.integra.irene.scheduler.service.MessageService;
import pe.com.integra.irene.shared.models.model.EntryMessage;
import pe.com.integra.irene.shared.models.model.ResponseType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@ApplicationScoped
public class InactivityHandler {
    private static final Logger logger = Logger.getLogger(InactivityHandler.class);

    @ConfigProperty(name = "inactivity.seconds", defaultValue = "600")
    int inactivitySeconds;

    @ConfigProperty(name = "inactivity.message", defaultValue = "Inactivo")
    String inactivityMessage;

    @Inject
    SessionRepository sessionRepository;

    @Inject
    MessageService messageService;

    public void handleSessionInactivity(SessionDetail session) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime latestMessageTime = session.getLatestMessageDate();

        if (isSessionInactive(session)) {
            handleInactiveSession(session, latestMessageTime);
        }

        if (latestMessageTime != null && !isInactivityFlagSet(session) && now.isAfter(latestMessageTime.plusSeconds(inactivitySeconds))) {
            logger.info("Inactivity time exceeded.");
            markSessionAsInactive(session);
        }
    }

    private boolean isSessionInactive(SessionDetail session) {
        Map<String, Object> metadata = session.getMetadata();
        if (metadata != null && metadata.get("inactivity") != null && (Boolean) metadata.get("inactivity")) {
            if (metadata.get("inactivityTime") != null) {
                LocalDateTime inactivityTime = LocalDateTime.parse((String) metadata.get("inactivityTime"));
                LocalDateTime latestMessageTime = session.getLatestMessageDate();
                return inactivityTime.isBefore(latestMessageTime);
            }
        }
        return false;
    }

    private boolean isInactivityFlagSet(SessionDetail session) {
        if (session.getMetadata() == null) {
            return false;
        }
        Boolean inactivityFlag = (Boolean) session.getMetadata().get("inactivity");
        return inactivityFlag != null && inactivityFlag;
    }

    private void handleInactiveSession(SessionDetail session, LocalDateTime latestMessageTime) {
        logger.info("Deleting session due to inactivity.");
        sessionRepository.deleteSession(session.getId()).subscribe().with(
                unused -> logger.info("Session successfully deleted."),
                error -> logger.error("Error deleting the session.", error)
        );
    }

    private void markSessionAsInactive(SessionDetail session) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("inactivity", true);
        metadata.put("inactivityTime", LocalDateTime.now());

        sessionRepository.updateSession(session, metadata)
                .onItem().transformToUni(unused -> {
                    logger.info("Session updated correctly.");
                    EntryMessage entryMessage = buildEntryMessage(session);
                    return messageService.sendMessage(entryMessage);
                })
                .subscribe().with(
                        unused -> logger.info("Message sent successfully."),
                        error -> logger.error("Error updating the session or sending the message.", error)
                );
    }

    private EntryMessage buildEntryMessage(SessionDetail sessionDetail) {
        EntryMessage entryMessage = new EntryMessage();
        entryMessage.setInputType(EntryMessage.InputType.TEXT);
        entryMessage.setResponseType(ResponseType.TEXT);
        entryMessage.setText(inactivityMessage);
        entryMessage.setUserId(sessionDetail.getUserId());
        entryMessage.setChannel(sessionDetail.getChannel());
        entryMessage.setUserMode(sessionDetail.getUserMode());
        return entryMessage;
    }
}