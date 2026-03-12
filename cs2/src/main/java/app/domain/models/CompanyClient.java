package app.domain.models;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyClient extends Client {

    private String companyName;
    private String NIT;
    private String legalRepresentative;
}
