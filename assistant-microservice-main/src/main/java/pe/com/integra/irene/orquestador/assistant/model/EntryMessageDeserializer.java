package pe.com.integra.irene.orquestador.assistant.model;


import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import pe.com.integra.irene.shared.models.model.EntryMessage;

public class EntryMessageDeserializer extends ObjectMapperDeserializer<EntryMessage> {
    public EntryMessageDeserializer() {
        super(EntryMessage.class);
    }
}