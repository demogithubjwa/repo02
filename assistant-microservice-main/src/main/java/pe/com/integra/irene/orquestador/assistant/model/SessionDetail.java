package pe.com.integra.irene.orquestador.assistant.model;

import pe.com.integra.irene.shared.models.model.EntryMessage;
import pe.com.integra.irene.shared.models.model.MessageChannel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getLatestMessageDate() {
        return latestMessageDate;
    }

    public void setLatestMessageDate(LocalDateTime latestMessageDate) {
        this.latestMessageDate = latestMessageDate;
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

    public void setId(String id) {
        this.id = id;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public void setChannel(MessageChannel channel) {
        this.channel = channel;
    }

    public EntryMessage.UserMode getUserMode() {
        return userMode;
    }

    public void setUserMode(EntryMessage.UserMode userMode) {
        this.userMode = userMode;
    }
}
