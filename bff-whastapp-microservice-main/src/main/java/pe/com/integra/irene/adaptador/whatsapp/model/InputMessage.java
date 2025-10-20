package pe.com.integra.irene.adaptador.whatsapp.model;

import lombok.Data;

import java.util.ArrayList;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Data
@RegisterForReflection
public class InputMessage {
    public String object;
    public ArrayList<Entry> entry;
}
