package fr.devisgenerator.devisgenerator.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PdfFormatterTest {

    @Test
    void formatMoneyShouldFormatPositiveAmount() {
        assertEquals(
                "1\u202F250,00\u00A0€",
                PdfFormatter.formatMoney(
                        BigDecimal.valueOf(1250)
                )
        );
    }

    @Test
    void formatMoneyShouldFormatAmountWithDecimals() {
        assertEquals(
                "1\u202F250,99\u00A0€",
                PdfFormatter.formatMoney(
                        BigDecimal.valueOf(1250.99)
                )
        );
    }

    @Test
    void formatMoneyShouldFormatZeroAmount() {
        assertEquals(
                "0,00\u00A0€",
                PdfFormatter.formatMoney(
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void formatMoneyShouldFormatNegativeAmount() {
        assertEquals(
                "-50,00\u00A0€",
                PdfFormatter.formatMoney(
                        BigDecimal.valueOf(-50)
                )
        );
    }

    @Test
    void formatVatRateShouldFormatStandardVat() {
        assertEquals(
                "20 %",
                PdfFormatter.formatVatRate(
                        BigDecimal.valueOf(0.20)
                )
        );
    }

    @Test
    void formatVatRateShouldFormatReducedVat() {
        assertEquals(
                "5,5 %",
                PdfFormatter.formatVatRate(
                        BigDecimal.valueOf(0.055)
                )
        );
    }

    @Test
    void formatVatRateShouldFormatZeroVat() {
        assertEquals(
                "0 %",
                PdfFormatter.formatVatRate(
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void formatVatRateShouldRemoveTrailingZeros() {
        assertEquals(
                "20 %",
                PdfFormatter.formatVatRate(
                        new BigDecimal("0.2000")
                )
        );
    }

    @Test
    void formatDateShouldFormatDateWithPadding() {
        assertEquals(
                "05/03/2026",
                PdfFormatter.formatDate(
                        LocalDate.of(
                                2026,
                                3,
                                5
                        )
                )
        );
    }

    @Test
    void formatDateTimeShouldFormatDateTimeWithPadding() {
        assertEquals(
                "05/03/2026 09:07",
                PdfFormatter.formatDateTime(
                        LocalDateTime.of(
                                2026,
                                3,
                                5,
                                9,
                                7
                        )
                )
        );
    }

}
