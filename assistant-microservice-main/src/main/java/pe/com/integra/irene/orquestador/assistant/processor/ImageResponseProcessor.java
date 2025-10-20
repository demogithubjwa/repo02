package pe.com.integra.irene.orquestador.assistant.processor;

import  pe.com.integra.irene.shared.models.model.OutputMessage;
import  pe.com.integra.irene.shared.models.model.OutputMessageImage;

import  pe.com.integra.irene.shared.models.model.EntryMessage;

import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;

import jakarta.enterprise.context.Dependent;

@Dependent
public class ImageResponseProcessor extends AbstractResponseProcessor<OutputMessageImage> {

    @Override
    public boolean supports(String responseType) {
        return "image".equalsIgnoreCase(responseType);
    }

    @Override
    protected OutputMessageImage createOutputMessage() {
        return new OutputMessageImage();
    }

    @Override
    public OutputMessageImage processSpecific(EntryMessage entryMessage, Object genericData, OutputMessageImage outputMessage) {
        RuntimeResponseGeneric generic = (RuntimeResponseGeneric) genericData;

        outputMessage.setMessageType(OutputMessage.MessageType.IMAGE);
        outputMessage.setStatus(OutputMessage.Status.FINAL);
        outputMessage.setUrl(generic.source());

        return outputMessage;
    }
}