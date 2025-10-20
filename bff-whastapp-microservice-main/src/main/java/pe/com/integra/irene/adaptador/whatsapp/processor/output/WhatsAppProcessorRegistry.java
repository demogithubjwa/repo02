package pe.com.integra.irene.adaptador.whatsapp.processor.output;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import pe.com.integra.irene.shared.models.model.OutputMessage;
import org.jboss.logging.Logger;


@ApplicationScoped
public class WhatsAppProcessorRegistry {
    private static final Logger logger = Logger.getLogger(WhatsAppProcessorRegistry.class);

    private final List<WhatsAppProcessor> processors = new ArrayList<>();

    @Inject
    public WhatsAppProcessorRegistry(Instance<WhatsAppProcessor> availableProcessors) {
        availableProcessors.forEach(this::registerProcessor);
    }

    public void registerProcessor(WhatsAppProcessor processor) {
        processors.add(processor);
        logger.infof("Registered WhatsApp processor: %s", processor.getClass().getName());
    }

    public WhatsAppProcessor getProcessor(OutputMessage.MessageType messageType) {
        return processors.stream()
            .filter(p -> p.supports(messageType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported message type: " + messageType));
    }
}