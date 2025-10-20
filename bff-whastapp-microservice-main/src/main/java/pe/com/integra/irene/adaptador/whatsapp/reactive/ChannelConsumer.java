package pe.com.integra.irene.adaptador.whatsapp.reactive;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import pe.com.integra.irene.adaptador.whatsapp.processor.output.WhatsAppProcessor;
import pe.com.integra.irene.adaptador.whatsapp.processor.output.WhatsAppProcessorRegistry;
import pe.com.integra.irene.adaptador.whatsapp.service.WhastappService;
import pe.com.integra.irene.shared.models.model.OutputMessage;
import org.jboss.logging.Logger;


@ApplicationScoped
public class ChannelConsumer {
    private static final Logger logger = Logger.getLogger(ChannelConsumer.class);
    private static final Gson gson = new Gson();

    @Inject
    WhatsAppProcessorRegistry processorRegistry;

    @Inject
    WhastappService whatsappService;

    @Incoming("channel-whatsapp")
    @Blocking
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(5000)     
    public Uni<Void> consume(String message) {
        logger.info("Message received: " + message);

        return Uni.createFrom().item(() -> gson.fromJson(message, OutputMessage.class))
                .onItem().transformToUni((OutputMessage outputMessage) -> {
                    WhatsAppProcessor processor = processorRegistry.getProcessor(outputMessage.getMessageType());
                    List<JsonObject> jsonMessages = processor.createMessage(message);
                
                    Uni<Void> sequence = Uni.createFrom().voidItem();
                    for (JsonObject jsonMessage : jsonMessages) {
                        sequence = sequence.onItem().transformToUni(ignored -> 
                            whatsappService.sendMessageReactive(jsonMessage)
                                .onItem().invoke(result -> logger.info("Message sent successfully: " + result))
                                .onFailure().invoke(e -> logger.error("Failed to send message: " + e.getMessage(), e))
                                .replaceWithVoid()
                        );
                    }

                    return sequence;
                })
                .onFailure().invoke(e -> logger.error("Unhandled failure in consumer: " + e.getMessage(), e))
                .replaceWithVoid();
    }
}
