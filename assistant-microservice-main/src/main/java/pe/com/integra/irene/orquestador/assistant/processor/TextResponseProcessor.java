package pe.com.integra.irene.orquestador.assistant.processor;

import  pe.com.integra.irene.shared.models.model.EntryMessage;
import  pe.com.integra.irene.shared.models.model.OutputMessage;
import  pe.com.integra.irene.shared.models.model.OutputMessageText;

import com.ibm.watson.assistant.v2.model.CompleteItem;
import com.ibm.watson.assistant.v2.model.PartialItem;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;

import jakarta.enterprise.context.Dependent;

@Dependent
public class TextResponseProcessor extends AbstractResponseProcessor<OutputMessageText> {

    @Override
    public boolean supports(String responseType) {
        return "text".equalsIgnoreCase(responseType);
    }

    @Override
    protected OutputMessageText createOutputMessage() {
        return new OutputMessageText();
    }

    @Override
    public OutputMessageText processSpecific(EntryMessage entryMessage, Object genericData, OutputMessageText outputMessage) {
        outputMessage.setMessageType(OutputMessage.MessageType.TEXT);
        if (genericData instanceof PartialItem) {
            PartialItem partialItem = (PartialItem) genericData;
            outputMessage.setText(partialItem.getText());
            outputMessage.setStreamId(partialItem.getStreamingMetadata().getId());
            outputMessage.setStatus(OutputMessage.Status.PARTIAL);
        } else if (genericData instanceof CompleteItem) {
            CompleteItem completeItem = (CompleteItem) genericData;
            outputMessage.setText(completeItem.text());
            outputMessage.setStreamId(completeItem.getStreamingMetadata().getId());
            outputMessage.setStatus(OutputMessage.Status.COMPLETE);
        } else if (genericData instanceof RuntimeResponseGeneric) {
            RuntimeResponseGeneric generic = (RuntimeResponseGeneric) genericData;
            outputMessage.setText(generic.text());
            outputMessage.setStatus(OutputMessage.Status.FINAL);
        }
        return outputMessage;
    }
}