package pe.com.integra.irene.orquestador.assistant.processor;

import java.util.ArrayList;
import java.util.List;

import  pe.com.integra.irene.shared.models.model.EntryMessage;
import  pe.com.integra.irene.shared.models.model.OutputMessage;
import  pe.com.integra.irene.shared.models.model.OutputMessageOption;

import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;

import jakarta.enterprise.context.Dependent;

@Dependent
public class SuggestionResponseProcessor  extends AbstractResponseProcessor<OutputMessageOption> {
    @Override
    public boolean supports(String responseType) {
        return "suggestion".equalsIgnoreCase(responseType);
    }

    @Override
    protected OutputMessageOption createOutputMessage() {
        return new OutputMessageOption();
    }

    @Override
    public OutputMessageOption processSpecific(EntryMessage entryMessage, Object genericData, OutputMessageOption outputMessage) {
        RuntimeResponseGeneric generic = (RuntimeResponseGeneric) genericData;

        outputMessage.setMessageType(OutputMessage.MessageType.OPTION);
        outputMessage.setStatus(OutputMessage.Status.FINAL);
        outputMessage.setTitle(generic.title());
        outputMessage.setDescription(generic.description());

        List<OutputMessageOption.Option> optionsList = new ArrayList<>();
        var options = generic.suggestions();
        
        for (var option : options) {
            OutputMessageOption.Option optionItem = outputMessage.new Option();
            optionItem.setLabel(option.getLabel());
            optionItem.setValue(option.getLabel());
            optionsList.add(optionItem);
        }

        outputMessage.setOptions(optionsList);

        return outputMessage;
    }
}
