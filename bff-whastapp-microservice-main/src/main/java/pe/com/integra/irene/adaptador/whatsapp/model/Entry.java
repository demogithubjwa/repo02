package pe.com.integra.irene.adaptador.whatsapp.model;

import lombok.Data;

import java.util.ArrayList;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Data
@RegisterForReflection
public class Entry {
    @Data
    public static class Change {
        public Value value;
        public String field;
    }

    public String id;
    public ArrayList<Change> changes;
}
