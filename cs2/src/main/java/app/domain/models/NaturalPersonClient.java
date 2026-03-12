package app.domain.models;

import  java.sql.Date;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class NaturalPersonClient extends Client{

    private String fullName;
    private int ID;
    private Date birthDate;

    
}
