package pe.com.integra.irene.adaptador.chat.config;

import pe.com.integra.irene.shared.models.model.EntryMessage;
import pe.com.integra.irene.shared.models.model.MessageChannel;
import pe.com.integra.irene.shared.models.model.OutputMessage;
import pe.com.integra.irene.shared.models.model.OutputMessageImage;
import pe.com.integra.irene.shared.models.model.OutputMessageOption;
import pe.com.integra.irene.shared.models.model.OutputMessagePause;
import pe.com.integra.irene.shared.models.model.OutputMessageText;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets={
    EntryMessage.class, 
    MessageChannel.class,
    OutputMessage.class,
    OutputMessageImage.class,
    OutputMessageOption.class,
    OutputMessagePause.class,
    OutputMessageText.class
})
public class MyReflectionConfiguration {    
}
