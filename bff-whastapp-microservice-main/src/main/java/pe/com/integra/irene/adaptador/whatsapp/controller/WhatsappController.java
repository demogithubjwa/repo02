package pe.com.integra.irene.adaptador.whatsapp.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import pe.com.integra.irene.adaptador.whatsapp.model.InputMessage;
import pe.com.integra.irene.adaptador.whatsapp.service.EventMessageProcessingService;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@Path("/message")
public class WhatsappController {
    private static final Logger logger = Logger.getLogger(WhatsappController.class);

    @ConfigProperty(name = "whatsapp.verify_token") 
    String whatsappVerifyToken;

    @Inject
    EventMessageProcessingService eventMessageProcessingService;

    @GET
    @Path("/webhook")
    public Uni<String> webhook(
            @QueryParam("hub.mode") String mode,
            @QueryParam("hub.challenge") String challenge,
            @QueryParam("hub.verify_token") String verifyToken) {

        return Uni.createFrom().item(() -> {
            if (whatsappVerifyToken.equals(verifyToken)) {
                logger.info("Webhook verification successful.");
                return challenge;
            } else {
                logger.warn("Webhook verification failed.");
                throw new NotFoundException("Invalid verify token");
            }
        });
    }

    @POST
    @Path("/webhook")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<String> webhookPost(InputMessage inputMessage) {
        if (inputMessage == null || inputMessage.getEntry() == null) {
            logger.warn("Invalid input message format.");
            throw new BadRequestException();
        }

        return Multi.createFrom().iterable(inputMessage.getEntry())
            .onItem().transformToMulti(entry -> 
                entry != null && entry.getChanges() != null
                    ? Multi.createFrom().iterable(entry.getChanges())
                    : Multi.createFrom().empty())
            .concatenate()
            .onItem().transformToMulti(change -> 
                change != null && change.getValue() != null && change.getValue().getMessages() != null
                    ? Multi.createFrom().iterable(change.getValue().getMessages())
                    : Multi.createFrom().empty())
            .concatenate()
            .onItem().transformToUni(eventMessageProcessingService::process)
            .concatenate()
            .collect().asList()
            .map(messages -> "OK")
            .onFailure().recoverWithItem(e -> {
                logger.error("Error processing webhook POST request.", e);
                return "Error processing request";
            });
    }
}
