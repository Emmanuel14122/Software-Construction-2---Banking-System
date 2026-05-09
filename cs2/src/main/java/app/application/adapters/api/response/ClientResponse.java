package app.application.adapters.api.response;

import app.domain.models.enums.ClientStatus;

public record ClientResponse(
        String address,
        String phoneNumber,
        String email,
        ClientStatus clientStatus
) {}