package pe.com.integra.irene.orquestador.assistant.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import  pe.com.integra.irene.shared.models.model.EntryMessage;
import org.jboss.logging.Logger;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.*;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AssistantService {
    private static final Logger logger = Logger.getLogger(AssistantService.class);

    @ConfigProperty(name = "watson.assistant.apikey") 
    String apiKey;

    @ConfigProperty(name = "watson.assistant.url")
    String serviceUrl;

    @ConfigProperty(name = "watson.assistant.version")
    String version;
    
    @ConfigProperty(name = "watson.assistant.environment_id")
    String environmentId;

    @ConfigProperty(name = "watson.assistant.assistant_id")
    String assistantId;

    @Inject
    SessionService sessionService;

    private Assistant assistant;

    @PostConstruct
    public void initialize() {
        logger.info("AssistantService initialized");
        this.createInstance();
    }

    public void createInstance() {
        try {
            IamAuthenticator waAuthenticator = new IamAuthenticator.Builder()
            .apikey(this.apiKey)
            .build();
        this.assistant = new Assistant(this.version, waAuthenticator);
        assistant.setServiceUrl(this.serviceUrl);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Retry(maxRetries = 3, delay = 1000)
    public Multi<MessageStreamResponse> sendMessageStream(EntryMessage entryMessage) {
        return Uni.createFrom().item(() -> {
                try {
                    return sessionService.manageSession(assistant, environmentId, entryMessage);
                } catch (Exception e) {
                    logger.error("Error managing session: " + e.getMessage(), e);
                    throw new RuntimeException("Failed to manage session for user: " + entryMessage.getUserId(), e);
                }
            })
            .onItem().transformToUni((String sessionId) -> {
                var messageInput = new MessageInput.Builder()
                    .messageType("text")
                    .text(entryMessage.getText())
                    .build();

                MessageContext context = createContext(entryMessage);

                var messageStreamOptions = new MessageStreamOptions.Builder()
                    .assistantId(this.assistantId)
                    .environmentId(this.environmentId)
                    .sessionId(sessionId)
                    .userId(entryMessage.getUserId())
                    .input(messageInput)
                    .context(context)
                    .build();
                
                return Uni.createFrom().item(() -> assistant.messageStream(messageStreamOptions).execute().getResult());
            })            
            .onItem().transformToMulti(inputStream -> {
                var messageDeserializer = new MessageEventDeserializer.Builder(inputStream).build();
                return Multi.createFrom().items(() -> Stream.of(messageDeserializer.messages()));
            })
            .flatMap(messages -> Multi.createFrom().iterable(messages))
            .onFailure().invoke(e -> logger.error("Error during message processing: " + e.getMessage(), e));
    } 

    private MessageContext createContext(EntryMessage entryMessage) {
        Map<String, Object> integrations = new HashMap<>();
        integrations.put("channel", new HashMap<String, Object>() {{
            put("name", entryMessage.getChannel().getName());
            put("userId", entryMessage.getUserId());
        }});

        var context = new MessageContext.Builder()
            .integrations(integrations)
            .build();
        
        return context;
    }
}
