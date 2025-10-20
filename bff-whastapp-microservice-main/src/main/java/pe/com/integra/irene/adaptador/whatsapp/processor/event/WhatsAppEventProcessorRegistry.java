package pe.com.integra.irene.adaptador.whatsapp.processor.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;


@ApplicationScoped
public class WhatsAppEventProcessorRegistry {
    private static final Logger logger = Logger.getLogger(WhatsAppEventProcessorRegistry.class);

    private final List<WhatsAppEventProcessor> processors = new ArrayList<>();

    @Inject
    public WhatsAppEventProcessorRegistry(Instance<WhatsAppEventProcessor> availableProcessors) {
        availableProcessors.forEach(this::registerProcessor);
    }

    public void registerProcessor(WhatsAppEventProcessor processor) {
        processors.add(processor);
        logger.infof("Registered WhatsApp processor: %s", processor.getClass().getName());
    }

    public WhatsAppEventProcessor getProcessor(String messageType) {
        return processors.stream()
            .filter(p -> p.supports(messageType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported message type: " + messageType));
    }
}