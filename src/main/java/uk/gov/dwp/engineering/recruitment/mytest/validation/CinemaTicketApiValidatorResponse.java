package uk.gov.dwp.engineering.recruitment.mytest.validation;

public sealed interface CinemaTicketApiValidatorResponse {
    record IsValid() implements CinemaTicketApiValidatorResponse {}
    record Invalid(String details) implements CinemaTicketApiValidatorResponse {}
}
