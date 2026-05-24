package app.application.persistence.mongodb.documents;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "operations_log")
public class OperationsLogDocument {


    @Id
    private String logbookId;

    @Indexed
    private String operationType;

    @Indexed
    private LocalDateTime operationDateTime;

    @Indexed
    private Long userId;

    private String userRole;

    @Indexed
    private String affectedProductId;

    private Map<String, Object> detailData;
    
}
