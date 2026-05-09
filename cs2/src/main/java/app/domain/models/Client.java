package app.domain.models;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import app.domain.models.enums.ClientStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@Entity
@Getter
@Setter
@NoArgsConstructor

public abstract class Client{

    private String address;
    private String phoneNumber;
    private String email;
    @Enumerated(EnumType.STRING)
    private ClientStatus clientStatus;
    
    
}
