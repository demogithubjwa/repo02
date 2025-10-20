package pe.com.integra.irene.adaptador.whatsapp.processor.output;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import pe.com.integra.irene.shared.models.model.OutputMessage;
import pe.com.integra.irene.shared.models.model.OutputMessageText;

import jakarta.enterprise.context.Dependent;

@Dependent
public class TextMessageProcessor implements WhatsAppProcessor {
    private static final Gson gson = new Gson();

    @Override
    public boolean supports(OutputMessage.MessageType messageType) {
        return messageType == OutputMessage.MessageType.TEXT;
    }

    @Override
    public List<JsonObject> createMessage(String outputMessage) {
        List<JsonObject> messages = new ArrayList<>();

        OutputMessageText textMessage = gson.fromJson(outputMessage, OutputMessageText.class);

        String rawText = textMessage.getText();

        // Reemplazar el formato Markdown con texto plano
        String formattedText = rawText.replaceAll("\\[\\*?(.*?)\\*?\\]\\((.*?)\\)", "$1: $2");

        // Paso 1: Reemplazar **negrita** por un marcador temporal
        formattedText = formattedText.replaceAll("\\*\\*(.*?)\\*\\*", "<NEGRITA>$1</NEGRITA>");

        // Paso 2: Reemplazar *cursiva* por _
        formattedText = formattedText.replaceAll("\\*(.*?)\\*", "_$1_");

        // Paso 3: Restaurar la negrita usando los marcadores temporales
        formattedText = formattedText.replaceAll("<NEGRITA>(.*?)</NEGRITA>", "*$1*");

        String[] textParts = formattedText.split("<br\\s*/?>");

        for (String part : textParts) {
            if (part != null && !part.trim().isEmpty()) {
                JsonObject message = new JsonObject();
                message.addProperty("messaging_product", "whatsapp");
                message.addProperty("recipient_type", "individual");
                message.addProperty("to", textMessage.getUserId());
                message.addProperty("type", "text");

                JsonObject textObj = new JsonObject();
                textObj.addProperty("preview_url", true);
                textObj.addProperty("body", part.trim());
                message.add("text", textObj);

                messages.add(message);
            }
        }

        return messages;
    }
}