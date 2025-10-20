package pe.com.integra.irene.adaptador.whatsapp.processor.output;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jboss.logging.Logger;
import pe.com.integra.irene.shared.models.model.OutputMessage;
import pe.com.integra.irene.shared.models.model.OutputMessageOption;

import jakarta.enterprise.context.Dependent;

@Dependent
public class OptionMessageProcessor implements WhatsAppProcessor {
    private static final Logger logger = Logger.getLogger(OptionMessageProcessor.class);
    private static final Gson gson = new Gson();
    
    @Override
    public boolean supports(OutputMessage.MessageType messageType) {
        return messageType == OutputMessage.MessageType.OPTION;
    }

    private static String limitStringLength(String input, int maxLength) {
      if (input == null) {
          return null; 
      }
      return input.length() > maxLength ? input.substring(0, maxLength) : input;
    }

    @Override
    public List<JsonObject> createMessage(String outputMessage) {
        List<JsonObject> messages = new ArrayList<>();
        OutputMessageOption optionMessage = gson.fromJson(outputMessage, OutputMessageOption.class);
        if (optionMessage.getOptions().size() <= 3) {
            JsonObject message = createReplyMessage(optionMessage);
            messages.add(message);
        } else {
            JsonObject message = createInteractiveListMessage(optionMessage);
            messages.add(message);
        }

        return messages;
    }

    private JsonObject createReplyMessage(OutputMessageOption optionMessage) {
        JsonObject message = new JsonObject();
        message.addProperty("messaging_product", "whatsapp");
        message.addProperty("recipient_type", "individual");
        message.addProperty("to", optionMessage.getUserId());
        message.addProperty("type", "interactive");

        JsonObject interactive = new JsonObject();

        interactive.addProperty("type", "button");

        Boolean hasMetadata = optionMessage.getMetadata() != null && !optionMessage.getMetadata().isEmpty();

        String messageHeader = hasMetadata && optionMessage.getMetadata().containsKey("messageHeader") ?
                (String) optionMessage.getMetadata().get("messageHeader")
                : null;

        String messageFooter = hasMetadata && optionMessage.getMetadata().containsKey("messageFooter") ?
                (String) optionMessage.getMetadata().get("messageFooter")
                : null;

        String messageBody = hasMetadata && optionMessage.getMetadata().containsKey("messageBody") ?
                (String) optionMessage.getMetadata().get("messageBody")
                : "Opciones";

        if (messageHeader != null) {
            JsonObject headerObj = new JsonObject();
            headerObj.addProperty("text", messageHeader);
            headerObj.addProperty("type", "text");
            interactive.add("header", headerObj);
        }

        if (messageFooter != null) {
            JsonObject footerObj = new JsonObject();
            footerObj.addProperty("text", messageFooter);
            interactive.add("footer", footerObj);
        }

        JsonObject bodyObj = new JsonObject();
        bodyObj.addProperty("text", messageBody);
        interactive.add("body", bodyObj);

        JsonObject action = new JsonObject();

        JsonArray buttonsArray = new JsonArray();
        for (var option : optionMessage.getOptions()) {
            JsonObject button = new JsonObject();
            button.addProperty("type", "reply");

            JsonObject reply = new JsonObject();
            reply.addProperty("id", limitStringLength(option.getValue(), 20));
            reply.addProperty("title", limitStringLength(option.getLabel(), 20));

            button.add("reply", reply);
            buttonsArray.add(button);
        }

        action.add("buttons", buttonsArray);
        interactive.add("action", action);
        message.add("interactive", interactive);


        return message;
    }

    private JsonObject createInteractiveListMessage(OutputMessageOption optionMessage) {
        JsonObject message = new JsonObject();
        message.addProperty("messaging_product", "whatsapp");
        message.addProperty("recipient_type", "individual");
        message.addProperty("to", optionMessage.getUserId());
        message.addProperty("type", "interactive");

        JsonObject interactive = new JsonObject();

        Boolean hasMetadata = optionMessage.getMetadata() != null && !optionMessage.getMetadata().isEmpty();

        String buttonText = hasMetadata && optionMessage.getMetadata().containsKey("buttonText") ?
                (String) optionMessage.getMetadata().get("buttonText")
                : "Opciones";

        String sectionTitle = hasMetadata && optionMessage.getMetadata().containsKey("sectionTitle") ?
                (String) optionMessage.getMetadata().get("sectionTitle")
                : "Opciones";

        String messageBody = hasMetadata && optionMessage.getMetadata().containsKey("messageBody") ?
                (String) optionMessage.getMetadata().get("messageBody")
                : "Seleccionar una opción";

        String messageFooter = hasMetadata && optionMessage.getMetadata().containsKey("messageFooter") ?
                (String) optionMessage.getMetadata().get("messageFooter")
                : null;

        String messageHeader = hasMetadata && optionMessage.getMetadata().containsKey("messageHeader") ?
                (String) optionMessage.getMetadata().get("messageHeader")
                : null;

        JsonObject action = new JsonObject();

        interactive.addProperty("type", "list");

        if (messageHeader != null) {
            JsonObject headerObj = new JsonObject();
            headerObj.addProperty("text", messageHeader);
            headerObj.addProperty("type", "text");
            interactive.add("header", headerObj);
        }

        JsonObject bodyObj = new JsonObject();
        bodyObj.addProperty("text", messageBody);
        interactive.add("body", bodyObj);


        if (messageFooter != null) {
            JsonObject footerObj = new JsonObject();
            footerObj.addProperty("text", messageFooter);
            interactive.add("footer", footerObj);
        }

        List<Map<String, String>> metadataOptions = List.of();

        if (Objects.nonNull(optionMessage.getMetadata()) && optionMessage.getMetadata().containsKey("options")) {
            metadataOptions = (List<Map<String, String>>) optionMessage.getMetadata().get("options");
        }

        JsonArray sectionsArray = new JsonArray();

        JsonObject section = new JsonObject();
        section.addProperty("title", sectionTitle);

        JsonArray rowsArray = new JsonArray();
        for (var option : optionMessage.getOptions()) {
            JsonObject row = new JsonObject();
            row.addProperty("id", limitStringLength(option.getValue(), 20));
            row.addProperty("title", limitStringLength(option.getLabel(), 20));

            metadataOptions.stream().filter(metadataOption -> metadataOption.get("id")
                    .equals(option.getValue())).findFirst()
                    .ifPresent(metadataOption -> {
                        if (metadataOption.containsKey("description")) {
                            row.addProperty("description", metadataOption.get("description"));
                        }
            });


            rowsArray.add(row);
        }

        section.add("rows", rowsArray);
        sectionsArray.add(section);

        action.add("sections", sectionsArray);

        action.addProperty("button", buttonText);

        interactive.add("action", action);
        message.add("interactive", interactive);

        return message;
    }
}