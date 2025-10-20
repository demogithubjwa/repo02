package pe.com.integra.irene.shared.models.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputMessageVideo extends OutputMessage {
    private String url;
    private String title;
    private String description;
}
