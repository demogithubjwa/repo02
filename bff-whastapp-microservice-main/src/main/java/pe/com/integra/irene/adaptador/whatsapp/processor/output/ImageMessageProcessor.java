package pe.com.integra.irene.adaptador.whatsapp.processor.output;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import pe.com.integra.irene.shared.models.model.OutputMessage;
import pe.com.integra.irene.shared.models.model.OutputMessageImage;

import jakarta.enterprise.context.Dependent;

@Dependent
public class ImageMessageProcessor implements WhatsAppProcessor {
    private static final Gson gson = new Gson();

    @Override
    public boolean supports(OutputMessage.MessageType messageType) {
        return messageType == OutputMessage.MessageType.IMAGE;
    }

    @Override
    public List<JsonObject> createMessage(String outputMessage) {
        List<JsonObject> messages = new ArrayList<>();

        OutputMessageImage imageMessage = gson.fromJson(outputMessage, OutputMessageImage.class);

        JsonObject message = new JsonObject();
        message.addProperty("messaging_product", "whatsapp");
        message.addProperty("recipient_type", "individual");
        message.addProperty("to", imageMessage.getUserId());
        message.addProperty("type", "image");

        JsonObject imageObj = new JsonObject();
        imageObj.addProperty("link", imageMessage.getUrl());
        message.add("image", imageObj);

        messages.add(message);
        return messages;
    }
}