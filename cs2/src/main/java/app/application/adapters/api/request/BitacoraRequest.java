package app.application.adapters.api.request;
 
import lombok.Getter;
import lombok.Setter;
 
import java.util.Map;
import java.time.LocalDateTime;
 
@Getter
@Setter
public class BitacoraRequest {
 
    private String idBitacora;
    private String operationType;
    private LocalDateTime operationDateTime;
    private Long idUser;
    private String rolUser;
    private String idProductoAfectado;

    private Map<String, Object> detailData;
}