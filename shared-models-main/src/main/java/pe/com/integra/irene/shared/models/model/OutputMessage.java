package pe.com.integra.irene.shared.models.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputMessage {
    public enum Status {
        PARTIAL, COMPLETE, FINAL, ERROR, INIT
    }
    
    public enum MessageType {
        TEXT, OPTION, IMAGE, VIDEO
    }

    private String requestId;
    private String id;
    private Long streamId;
    private String userId;
    private ResponseType responseType;
    private MessageType messageType;
    private Status status;
    private Map<String, Object> metadata = new HashMap<>();
}
