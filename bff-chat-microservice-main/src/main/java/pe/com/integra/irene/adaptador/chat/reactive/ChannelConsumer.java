package pe.com.integra.irene.adaptador.chat.reactive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import pe.com.integra.irene.shared.models.model.OutputMessage;
import pe.com.integra.irene.adaptador.chat.websockets.ChatWebSocket;
import org.jboss.logging.Logger;

import com.google.gson.Gson;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ChannelConsumer {
    private static final Logger logger = Logger.getLogger(ChannelConsumer.class);
    private static final Gson gson = new Gson();

    @Inject
    ChatWebSocket messageWebSocket;

    @Incoming("channel-chat")
    public void consume(String message) {
        Uni.createFrom().item(message)
            .onItem().ifNull().failWith(new IllegalArgumentException("Message is null or empty"))
            .onItem().invoke(msg -> logger.info("Message received: " + msg))
            .onItem().transform(msg -> gson.fromJson(msg, OutputMessage.class))
            .onFailure().invoke(e -> logger.error("Failed to process message", e))
            .onItem().invoke(outputMessage -> processMessage(outputMessage, message))
            .subscribe().with(
                ignored -> logger.info("Message processed successfully"),
                failure -> logger.error("Message processing failed", failure)
            );
    }

    private void processMessage(OutputMessage outputMessage, String originalMessage) {
        if (outputMessage == null || outputMessage.getUserId() == null) {
            logger.warn("OutputMessage is invalid: " + originalMessage);
            return;
        }

        var userid = outputMessage.getUserId();
        try {
            sendMessage(userid, originalMessage);
            logger.infof("Message sent to user %s: %s", userid, originalMessage);
        } catch (Exception e) {
            logger.errorf("Failed to send message to user %s: %s", userid, e.getMessage(), e);
        }
    }

    private void sendMessage(String userid, String message) {
        messageWebSocket.sendMessage(userid, message);
    }
}
