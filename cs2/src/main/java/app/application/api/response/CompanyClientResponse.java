package app.application.api.response;

public record CompanyClientResponse(
        String nit,
        String companyName,
        String email,
        String phoneNumber,
        String address,
        String legalRepresentativeId
) {}