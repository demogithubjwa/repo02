package pe.com.integra.irene.shared.models.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputMessagePause extends OutputMessage {
    private Long time;
    private Boolean typing;
}
