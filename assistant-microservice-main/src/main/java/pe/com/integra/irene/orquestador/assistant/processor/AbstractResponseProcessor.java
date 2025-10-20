package pe.com.integra.irene.orquestador.assistant.processor;

import java.util.UUID;

import  pe.com.integra.irene.shared.models.model.EntryMessage;
import  pe.com.integra.irene.shared.models.model.OutputMessage;

public abstract class AbstractResponseProcessor<T extends OutputMessage> implements ResponseProcessor {

    @Override
    public OutputMessage process(EntryMessage entryMessage, Object genericData) {
        T outputMessage = createOutputMessage();

        outputMessage.setId(UUID.randomUUID().toString());
        outputMessage.setRequestId(entryMessage.getRequestId());
        outputMessage.setUserId(entryMessage.getUserId());
        outputMessage.setResponseType(entryMessage.getResponseType());
    
        processSpecific(entryMessage, genericData, outputMessage);

        return outputMessage;
    }

    protected abstract T createOutputMessage();

    protected abstract T processSpecific(EntryMessage entryMessage, Object genericData, T outputMessage);
}