package app.application.adapters.api.response;
 

public record GeneralBankingProductResponse(
        String productCode,
        String productName,
        String category,
        boolean requiresApproval
) {}
 