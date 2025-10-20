package pe.com.integra.irene.adaptador.whatsapp.processor.output;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pe.com.integra.irene.shared.models.model.OutputMessage;
import pe.com.integra.irene.shared.models.model.OutputMessageVideo;

import jakarta.enterprise.context.Dependent;

@Dependent
public class VideoMessageProcessor implements WhatsAppProcessor {
    private static final Gson gson = new Gson();

    @Override
    public boolean supports(OutputMessage.MessageType messageType) {
        return messageType == OutputMessage.MessageType.VIDEO;
    }

    @Override
    public List<JsonObject> createMessage(String outputMessage) {
        List<JsonObject> messages = new ArrayList<>();

        OutputMessageVideo videoMessage = gson.fromJson(outputMessage, OutputMessageVideo.class);

       if (isSupportedVideoExtension(videoMessage.getUrl())) {
           messages.add(createNativeVideoMessage(videoMessage));
       } else {
           messages.add(createLinkVideoMessage(videoMessage));
       }

        return messages;
    }

    private boolean isSupportedVideoExtension(String url) {
        var supportedVideoExtensions = getSupportedVideoExtensions();
        return supportedVideoExtensions.stream()
                .anyMatch(extension -> url.toLowerCase().contains("." + extension));
    }

    private List<String> getSupportedVideoExtensions() {
        return List.of("mp4", "3gp");
    }

    private JsonObject createNativeVideoMessage(OutputMessageVideo videoMessage) {
        JsonObject message = new JsonObject();
        message.addProperty("messaging_product", "whatsapp");
        message.addProperty("recipient_type", "individual");
        message.addProperty("to", videoMessage.getUserId());
        message.addProperty("type", "video");

        String rawText = Stream.of(videoMessage.getTitle(), videoMessage.getDescription())
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));

        JsonObject videoObj = new JsonObject();
        videoObj.addProperty("link", videoMessage.getUrl());
        videoObj.addProperty("caption", rawText);
        message.add("video", videoObj);

        return message;
    }

    private JsonObject createLinkVideoMessage(OutputMessageVideo videoMessage) {
        JsonObject message = new JsonObject();
        message.addProperty("messaging_product", "whatsapp");
        message.addProperty("recipient_type", "individual");
        message.addProperty("to", videoMessage.getUserId());
        message.addProperty("type", "text");

        String rawText = Stream.of(videoMessage.getTitle(), videoMessage.getDescription(), videoMessage.getUrl())
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));

        JsonObject textObj = new JsonObject();
        textObj.addProperty("preview_url", true);
        textObj.addProperty("body", rawText);
        message.add("text", textObj);

        return message;
    }
}