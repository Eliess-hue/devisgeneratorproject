package fr.devisgenerator.devisgenerator.util;

import fr.devisgenerator.devisgenerator.enums.QuoteStatus;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class PdfFormatter {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE);

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.FRANCE);

    private PdfFormatter() {
    }

    public static String formatMoney(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(amount);
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }

    public static String formatVatRate(BigDecimal vatRate) {
        BigDecimal percentage = vatRate
                .multiply(BigDecimal.valueOf(100))
                .stripTrailingZeros();

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.FRANCE);
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(percentage) + " %";
    }

    public static String formatStatus(QuoteStatus status) {

        return switch (status) {
            case DRAFT -> "Brouillon";
            case PENDING -> "En attente";
            case ACCEPTED -> "Accepté";
            case REFUSED -> "Refusé";
            case EXPIRED -> "Expiré";
        };

    }

}
