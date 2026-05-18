package app.domain.models;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import app.domain.models.enums.ClientStatus;

@Getter
@Setter
@NoArgsConstructor

public abstract class Client{

    private String address;
    private String phoneNumber;
    private String email;
    private ClientStatus clientStatus;
    
    
}
