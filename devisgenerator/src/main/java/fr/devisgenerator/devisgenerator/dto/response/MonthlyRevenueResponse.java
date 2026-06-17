package fr.devisgenerator.devisgenerator.dto.response;

import java.math.BigDecimal;

public record MonthlyRevenueResponse(
        String month,
        BigDecimal revenue
) {}