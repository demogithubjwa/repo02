package pe.com.integra.irene.adaptador.whatsapp.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class Interactive {
    @Data
    public static class ListReply {
        private String id;
        private String title;
    }

    @Data
    public static class ButtonReply {
        private String id;
        private String title;
    }

    private String type;
    private ListReply list_reply;
    private ButtonReply button_reply;
}
