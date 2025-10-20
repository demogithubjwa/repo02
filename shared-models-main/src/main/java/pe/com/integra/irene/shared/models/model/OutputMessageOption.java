package pe.com.integra.irene.shared.models.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputMessageOption extends OutputMessage {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Option {
        private String label;
        private String value;
    }

    private String title;
    private String description;
    private List<Option> options;
}
