package pe.com.integra.irene.adaptador.whatsapp.processor.event;

import pe.com.integra.irene.adaptador.whatsapp.model.Message;
import pe.com.integra.irene.shared.models.model.EntryMessage;
import pe.com.integra.irene.shared.models.model.MessageChannel;
import pe.com.integra.irene.shared.models.model.ResponseType;


import jakarta.enterprise.context.Dependent;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;

@Dependent
public class InteractiveMessageProcessor implements WhatsAppEventProcessor {
    private static final Logger logger = Logger.getLogger(InteractiveMessageProcessor.class);

    @Override
    public boolean supports(String messageType) {
        return "interactive".equalsIgnoreCase(messageType);
    }

    @Override
    public Uni<EntryMessage> createEntryMessage(Message eventMessage) {
        if (eventMessage == null) {
            return Uni.createFrom().failure(new IllegalArgumentException("Message cannot be null"));
        }

        return Uni.createFrom().item(() -> {
            var channel = new MessageChannel();
            channel.setName("whatsapp");
            channel.setSupportStream(false);

            EntryMessage entryMessage = new EntryMessage();
            entryMessage.setRequestId(eventMessage.getId());
            entryMessage.setChannel(channel);
            entryMessage.setUserId(eventMessage.getFrom());
            entryMessage.setUserMode(EntryMessage.UserMode.REGISTERED);
            entryMessage.setInputType(EntryMessage.InputType.TEXT);
            entryMessage.setResponseType(ResponseType.TEXT);

            if (eventMessage.getInteractive() != null) {
                if ("list_reply".equals(eventMessage.getInteractive().getType())) {
                    entryMessage.setText(eventMessage.getInteractive().getList_reply() != null
                            ? eventMessage.getInteractive().getList_reply().getTitle() : "");
                } else if ("button_reply".equals(eventMessage.getInteractive().getType())) {
                    entryMessage.setText(eventMessage.getInteractive().getButton_reply() != null
                            ? eventMessage.getInteractive().getButton_reply().getTitle() : "");
                } else {
                    logger.infof("Unsupported interactive type: %s", eventMessage.getInteractive().getType());
                }
            }

            return entryMessage;
        });
    }
}