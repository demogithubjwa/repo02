package pe.com.integra.irene.shared.models.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntryMessage {
    public enum UserMode {
        ANONYMOUS, REGISTERED
    }

    public enum InputType {
        TEXT, AUDIO
    }
    
    private String requestId;
    private String userId;
    private UserMode userMode;
    private InputType inputType;
    private ResponseType responseType;
    private String text;
    private MessageChannel channel;
    private Map<String, Object> metadata = new HashMap<>();
}