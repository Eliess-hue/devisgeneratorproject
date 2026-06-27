package fr.devisgenerator.devisgenerator.dto.request;

import fr.devisgenerator.devisgenerator.enums.QuoteStatus;

import java.time.LocalDate;

public record QuoteFilterRequest(

        String search,

        QuoteStatus status,

        LocalDate from,

        LocalDate to

) {}
