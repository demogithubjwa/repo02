package pe.com.integra.irene.adaptador.whatsapp.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class Message {
    public String from;
    public String id;
    public String timestamp;
    public Text text;
    public String type;
    public Interactive interactive;
}
