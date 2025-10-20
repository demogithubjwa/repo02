package pe.com.integra.irene.adaptador.whatsapp.service;

import java.net.URI;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.google.gson.JsonObject;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
public class WhastappService {
    private static final Logger logger = Logger.getLogger(WhastappService.class);

    @ConfigProperty(name = "whatsapp.url") 
    String url;

    @ConfigProperty(name = "whatsapp.acess_token") 
    String accessToken;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public Uni<String> sendMessageReactive(JsonObject message) {
        return Uni.createFrom().emitter(emitter -> {
            try {
                logger.info("Sending message to WhatsApp: " + message.toString());
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/messages"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(message.toString()))
                    .build();

                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .whenComplete((response, error) -> {
                        if (error != null) {
                            emitter.fail(error);
                        } else {
                            emitter.complete(response);
                        }
                    });
            } catch (Exception e) {
                emitter.fail(e);
            }
        });
    }
}
