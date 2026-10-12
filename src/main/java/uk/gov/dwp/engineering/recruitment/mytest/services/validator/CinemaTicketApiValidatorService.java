package uk.gov.dwp.engineering.recruitment.mytest.services.validator;

import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.mytest.validation.CinemaTicketApiValidatorResponse;

public interface CinemaTicketApiValidatorService {
    CinemaTicketApiValidatorResponse cinemaTicketApiValidateAccountId(Long accountId);
    CinemaTicketApiValidatorResponse cinemaTicketApiValidateRequest(TicketRequest... ticketRequest);
    CinemaTicketApiValidatorResponse cinemaTicketApiValidateFullRequest(Long accountId,TicketRequest... ticketRequest);

}
