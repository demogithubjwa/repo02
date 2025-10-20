package pe.com.integra.irene.adaptador.whatsapp.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class Text {
    public boolean preview_url;
    public String body;
}
