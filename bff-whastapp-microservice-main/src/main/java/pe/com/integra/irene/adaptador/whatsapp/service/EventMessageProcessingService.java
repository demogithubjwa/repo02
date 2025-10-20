package pe.com.integra.irene.adaptador.whatsapp.service;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import pe.com.integra.irene.adaptador.whatsapp.model.Message;
import pe.com.integra.irene.adaptador.whatsapp.processor.event.WhatsAppEventProcessorRegistry;
import pe.com.integra.irene.shared.models.model.EntryMessage;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EventMessageProcessingService {
    private static final Logger logger = Logger.getLogger(EventMessageProcessingService.class);

    @Inject
    WhatsAppEventProcessorRegistry eventProcessorRegistry;

    @Inject
    @Channel("entry-assistant")
    Emitter<EntryMessage> entryAssistantEmitter;

    public Uni<Void> process(Message message) {
        if (message == null) {
            logger.warn("Null message received.");
            return Uni.createFrom().voidItem();
        }
    
        return Uni.createFrom().item(() -> eventProcessorRegistry.getProcessor(message.getType()))
            .onItem().ifNull().failWith(() -> new IllegalArgumentException("Unsupported message type: " + message.getType()))
            .onItem().transformToUni(eventProcessor -> 
                eventProcessor.createEntryMessage(message)
            )
            .onItem().ifNull().continueWith(() -> {
                logger.warn("EntryMessage creation returned null for message: " + message);
                return null;
            })
            .onItem().ifNotNull().transformToUni(entryMessage -> 
                Uni.createFrom().completionStage(() -> entryAssistantEmitter.send(entryMessage))
            )
            .onFailure().invoke(e -> logger.error("Error processing individual message: " + message, e))
            .replaceWithVoid();
    }   
}
