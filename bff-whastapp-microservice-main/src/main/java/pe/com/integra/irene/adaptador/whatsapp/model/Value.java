package pe.com.integra.irene.adaptador.whatsapp.model;

import lombok.Data;

import java.util.ArrayList;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Data
@RegisterForReflection
public class Value {
    @Data
    public static class Metadata {
        public String display_phone_number;
        public String phone_number_id;
    }

    @Data
    public static class Profile {
        public String name;
    }

    @Data
    public static class Contact {
        public Profile profile;
        public String wa_id;
    }

    public String messaging_product;
    public Metadata metadata;
    public ArrayList<Contact> contacts;
    public ArrayList<Message> messages;
}
