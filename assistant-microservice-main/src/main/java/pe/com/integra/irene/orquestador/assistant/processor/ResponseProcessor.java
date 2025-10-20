package pe.com.integra.irene.orquestador.assistant.processor;

import  pe.com.integra.irene.shared.models.model.EntryMessage;
import  pe.com.integra.irene.shared.models.model.OutputMessage;

public interface ResponseProcessor {
    boolean supports(String responseType);
    OutputMessage process(EntryMessage entryMessage, Object genericData);
}