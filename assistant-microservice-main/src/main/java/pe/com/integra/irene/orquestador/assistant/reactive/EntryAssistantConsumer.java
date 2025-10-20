package pe.com.integra.irene.orquestador.assistant.reactive;

import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import  pe.com.integra.irene.shared.models.model.EntryMessage;
import  pe.com.integra.irene.shared.models.model.OutputMessage;
import  pe.com.integra.irene.shared.models.model.ResponseType;

import pe.com.integra.irene.orquestador.assistant.processor.ResponseProcessorRegistry;
import pe.com.integra.irene.orquestador.assistant.service.AssistantService;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.ibm.watson.assistant.v2.model.MessageStreamResponse;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class EntryAssistantConsumer {
    private static final Logger logger = Logger.getLogger(EntryAssistantConsumer.class);

    @Inject
    AssistantService assistantService;

    @Inject
    ResponseProcessorRegistry processorRegistry;

    @Inject
    @Channel("channel-dynamic")
    Emitter<OutputMessage> channelEmitter;

    @Retry(maxRetries = 3, delay = 1000)
    @Incoming("entry-assistant")
    @Blocking
    public void consume(EntryMessage entryMessage) {
        logger.infof("Received message: %s", entryMessage);
        assistantService.sendMessageStream(entryMessage)
            .subscribe()
            .with(
                message -> handleMessage(entryMessage, message),
                failure -> logger.error("Error processing messages", failure)
            );
    }

    private void handleMessage(EntryMessage entryMessage, MessageStreamResponse message) {
        if (message.getPartialItem() != null) {
            handlePartialMessage(entryMessage, message);
        } else if (message.getCompleteItem() != null) {
            handleCompleteMessage(entryMessage, message);
        } else if (message.getFinalResponse() != null) {

            Gson gson = new Gson();
            String json = gson.toJson(message.getFinalResponse());
            logger.infof(json);

            handleFinalResponse(entryMessage, message);
        }
    }

    private void handlePartialMessage(EntryMessage entryMessage, MessageStreamResponse message) {
        if (entryMessage.getChannel().getSupportStream()) {
            processMessage(entryMessage, "text", message.getPartialItem());
        }
    }

    private void handleCompleteMessage(EntryMessage entryMessage, MessageStreamResponse message) {
        if (entryMessage.getChannel().getSupportStream()) {
            processMessage(entryMessage, message.getCompleteItem().responseType(), message.getCompleteItem());
        }
    }

    private void handleFinalResponse(EntryMessage entryMessage, MessageStreamResponse message) {
        message.getFinalResponse().getOutput().getGeneric().forEach(generic -> {
            processMessage(entryMessage, generic.responseType(), generic);
        });
    }

    private void processMessage(EntryMessage entryMessage, String responseType, Object message) {
        try {
            OutputMessage outputMessage = processorRegistry
                .processResponse(entryMessage, responseType, message);

            if (message instanceof RuntimeResponseGeneric) {
                RuntimeResponseGeneric generic = (RuntimeResponseGeneric) message;
                outputMessage.setMetadata(generic.userDefined());
            }

            processOutputMessage(entryMessage, outputMessage);
        } catch (Exception e) {
            logger.errorf("Error processing message for EntryMessage %s: %s", entryMessage, e.getMessage(), e);
        }
    }

    private void processOutputMessage(EntryMessage entryMessage, OutputMessage outputMessage) {
        var channel = entryMessage.getChannel();
        var channelName = channel.getName();
        if (entryMessage.getResponseType() == ResponseType.TEXT) {
            sendToChannel(outputMessage, channelName);
        }
    }

    private void sendToChannel(OutputMessage outputMessage, String channel) {
        try {
            OutgoingKafkaRecordMetadata<String> metadata = OutgoingKafkaRecordMetadata.<String>builder()
                .withTopic("channel-" + channel)
                .build();
            channelEmitter.send(Message.of(outputMessage).addMetadata(metadata));
            logger.infof("Message sent to Kafka topic channel-%s: %s", channel, outputMessage);
        } catch (Exception e) {
            logger.errorf("Error sending message to channel %s: %s", channel, e.getMessage(), e);
        }
    }
}
