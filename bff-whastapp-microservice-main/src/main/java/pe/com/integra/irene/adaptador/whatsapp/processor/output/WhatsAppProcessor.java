package pe.com.integra.irene.adaptador.whatsapp.processor.output;

import com.google.gson.JsonObject;

import java.util.List;

import pe.com.integra.irene.shared.models.model.OutputMessage;

public interface WhatsAppProcessor {
    boolean supports(OutputMessage.MessageType messageType);
    List<JsonObject> createMessage(String outputMessage);
}