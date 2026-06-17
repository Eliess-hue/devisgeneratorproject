package fr.devisgenerator.devisgenerator.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        long totalQuotes,
        long pendingQuotes,
        long acceptedQuotes,
        BigDecimal totalRevenue,
        List<MonthlyRevenueResponse> monthlyRevenues,
        List<DashboardQuoteResponse> recentQuotes
) {}