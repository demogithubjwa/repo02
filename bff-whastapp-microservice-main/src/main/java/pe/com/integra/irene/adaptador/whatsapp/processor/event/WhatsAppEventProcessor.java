package pe.com.integra.irene.adaptador.whatsapp.processor.event;

import pe.com.integra.irene.adaptador.whatsapp.model.Message;
import pe.com.integra.irene.shared.models.model.EntryMessage;

import io.smallrye.mutiny.Uni;

public interface WhatsAppEventProcessor {
    boolean supports(String messageType);
    Uni<EntryMessage> createEntryMessage(Message eventMessage);
}