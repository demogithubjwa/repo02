package pe.com.integra.irene.orquestador.assistant.processor;

import java.util.ArrayList;
import java.util.List;

import  pe.com.integra.irene.shared.models.model.EntryMessage;
import  pe.com.integra.irene.shared.models.model.OutputMessage;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;


@ApplicationScoped
public class ResponseProcessorRegistry {
    private static final Logger logger = Logger.getLogger(ResponseProcessorRegistry.class);

    private final List<ResponseProcessor> processors = new ArrayList<>();

    @Inject
    public ResponseProcessorRegistry(Instance<ResponseProcessor> availableProcessors) {
        availableProcessors.forEach(this::registerProcessor);
    }

    public void registerProcessor(ResponseProcessor processor) {
        processors.add(processor);
        logger.infof("Registered processor: %s", processor.getClass().getName());
    }

    public OutputMessage processResponse(EntryMessage entryMessage, String responseType, Object genericData) {
        return processors.stream()
            .filter(p -> p.supports(responseType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported response type: " + responseType))
            .process(entryMessage, genericData);
    }
}