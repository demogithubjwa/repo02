package pe.com.integra.irene.scheduler.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;
import pe.com.integra.irene.shared.models.model.EntryMessage;

import java.util.UUID;

@ApplicationScoped
public class MessageService {
    private static final Logger logger = Logger.getLogger(MessageService.class);

    @Inject
    @Channel("entry-assistant")
    Emitter<EntryMessage> entryAssistantEmitter;

    public Uni<Void> sendMessage(EntryMessage entryMessage) {
        return Uni.createFrom().voidItem()
                .invoke(() -> {
                    entryMessage.setRequestId(generateRequestId());
                    logger.info("Sending message to entry assistant: " + entryMessage.toString());
                    entryAssistantEmitter.send(entryMessage);
                });
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}
