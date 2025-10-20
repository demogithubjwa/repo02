package pe.com.integra.irene.scheduler.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import pe.com.integra.irene.shared.models.model.EntryMessage;
import pe.com.integra.irene.shared.models.model.MessageChannel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RegisterForReflection
public class SessionDetail {
    private String id;
    private String userId;
    private String sessionId;
    private LocalDateTime latestMessageDate;
    private MessageChannel channel;
    private EntryMessage.UserMode userMode;
    private Map<String, Object> metadata = new HashMap<>();

    public SessionDetail() {
    }

    public String getUserId() {
        return userId;
    }


    public String getSessionId() {
        return sessionId;
    }


    public LocalDateTime getLatestMessageDate() {
        return latestMessageDate;
    }


    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public EntryMessage.UserMode getUserMode() {
        return userMode;
    }

    @Override
    public String toString() {
        return "SessionDetail{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", latestMessageDate=" + latestMessageDate +
                ", channel=" + channel +
                ", userMode=" + userMode +
                ", metadata=" + metadata +
                '}';
    }
}
