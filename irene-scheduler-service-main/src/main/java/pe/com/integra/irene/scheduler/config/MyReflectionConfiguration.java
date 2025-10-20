package pe.com.integra.irene.scheduler.config;

import pe.com.integra.irene.shared.models.model.EntryMessage;
import pe.com.integra.irene.shared.models.model.MessageChannel;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets={
        EntryMessage.class,
        MessageChannel.class
})
public class MyReflectionConfiguration {
}
