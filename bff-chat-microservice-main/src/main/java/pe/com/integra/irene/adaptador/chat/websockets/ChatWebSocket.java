package pe.com.integra.irene.adaptador.chat.websockets;

import java.util.UUID;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import pe.com.integra.irene.shared.models.model.EntryMessage;
import pe.com.integra.irene.shared.models.model.MessageChannel;
import pe.com.integra.irene.shared.models.model.OutputMessage;
import pe.com.integra.irene.shared.models.model.OutputMessageText;
import org.jboss.logging.Logger;

import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.PathParam;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;

@WebSocket(path = "/chat/{userid}")  
public class ChatWebSocket {
    private static final Logger logger = Logger.getLogger(ChatWebSocket.class);

    @Inject
    @Channel("entry-assistant")
    Emitter<EntryMessage> entryAssistantEmitter;

    @OnOpen       
    public void onOpen(@PathParam String userid, WebSocketConnection connection) {
        WebSocketSessionManager.addSession(userid, connection);
        logger.infof("User %s connected", userid); 
    }

    @OnClose                    
    public void onClose(@PathParam String userid, WebSocketConnection connection) {
        WebSocketSessionManager.removeSession(userid);
        logger.infof("User %s disconnected", userid);
    }

    @OnTextMessage
    OutputMessage process(@PathParam String userid, EntryMessage message) {
        try {
            var channel = new MessageChannel();
            channel.setName("chat");
            channel.setSupportStream(true);

            String id = UUID.randomUUID().toString();
            message.setRequestId(id);
            message.setChannel(channel);
            message.setUserId(userid);
            entryAssistantEmitter.send(message);

            var outputMessage = createInitResponse(message);
            return outputMessage;
        } catch (Exception e) {
            logger.errorf("Error processing message for user %s: %s", userid, e.getMessage(), e);
            return createErrorResponse(message, e.getMessage());
        }
    }

    private OutputMessage createInitResponse(EntryMessage message) {
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.setRequestId(message.getRequestId());
        outputMessage.setUserId(message.getUserId());
        outputMessage.setStatus(OutputMessage.Status.INIT);
        return outputMessage;
    }

    private OutputMessageText createErrorResponse(EntryMessage message, String error) {
        OutputMessageText outputMessage = new OutputMessageText();
        outputMessage.setRequestId(message.getRequestId());
        outputMessage.setUserId(message.getUserId());
        outputMessage.setStatus(OutputMessage.Status.ERROR);
        outputMessage.setText(error);
        return outputMessage;
    }

    public void sendMessage(String userid, String message) {
        WebSocketConnection connection = WebSocketSessionManager.getSession(userid);
        if (connection != null) {
            try {
                connection.sendTextAndAwait(message);
                logger.infof("Message sent to user %s: %s", userid, message);
            } catch (Exception e) {
                logger.errorf("Error sending message to user %s: %s", userid, e.getMessage(), e);
            }
        } else {
            logger.warnf("No active connection found for user %s", userid);
        }
    }
}