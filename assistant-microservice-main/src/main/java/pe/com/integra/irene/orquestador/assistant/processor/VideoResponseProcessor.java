package pe.com.integra.irene.orquestador.assistant.processor;

import  pe.com.integra.irene.shared.models.model.OutputMessage;
import  pe.com.integra.irene.shared.models.model.OutputMessageVideo;
import  pe.com.integra.irene.shared.models.model.EntryMessage;

import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;

import jakarta.enterprise.context.Dependent;

@Dependent
public class VideoResponseProcessor extends AbstractResponseProcessor<OutputMessageVideo> {

    @Override
    public boolean supports(String responseType) {
        return "video".equalsIgnoreCase(responseType);
    }

    @Override
    protected OutputMessageVideo createOutputMessage() {
        return new OutputMessageVideo();
    }

    @Override
    public OutputMessageVideo processSpecific(EntryMessage entryMessage, Object genericData, OutputMessageVideo outputMessage) {
        RuntimeResponseGeneric generic = (RuntimeResponseGeneric) genericData;

        outputMessage.setMessageType(OutputMessage.MessageType.VIDEO);
        outputMessage.setStatus(OutputMessage.Status.FINAL);
        outputMessage.setUrl(generic.source());

        return outputMessage;
    }
}