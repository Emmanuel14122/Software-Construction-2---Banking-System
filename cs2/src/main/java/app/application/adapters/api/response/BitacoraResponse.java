package app.application.adapters.api.response;
 
import java.time.LocalDateTime;
import java.util.Map;

public record BitacoraResponse(
        String idBitacora,
        String operationType,
        LocalDateTime operationDateTime,
        Long idUser,
        String rolUser,
        String idProductoAfectado,
        Map<String, Object> detailData
) {}