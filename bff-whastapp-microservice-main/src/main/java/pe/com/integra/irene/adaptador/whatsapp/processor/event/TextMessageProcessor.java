package pe.com.integra.irene.adaptador.whatsapp.processor.event;

import pe.com.integra.irene.adaptador.whatsapp.model.Message;
import pe.com.integra.irene.shared.models.model.EntryMessage;
import pe.com.integra.irene.shared.models.model.MessageChannel;
import pe.com.integra.irene.shared.models.model.ResponseType;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.Dependent;

@Dependent
public class TextMessageProcessor implements WhatsAppEventProcessor {
    @Override
    public boolean supports(String messageType) {
        return "text".equalsIgnoreCase(messageType);
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

            if (eventMessage.getText() != null) {
                entryMessage.setText(eventMessage.getText().getBody());
            }

            return entryMessage;
        });
    }
}