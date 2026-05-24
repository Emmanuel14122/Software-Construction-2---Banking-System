package app.application.api.response;

import app.domain.models.enums.ClientStatus;

public record ClientResponse(
        Long id,
        String address,
        String phoneNumber,
        String email,
        ClientStatus clientStatus
) {}