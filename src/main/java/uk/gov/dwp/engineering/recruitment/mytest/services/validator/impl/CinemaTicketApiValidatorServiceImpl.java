package uk.gov.dwp.engineering.recruitment.mytest.services.validator.impl;

import org.springframework.stereotype.Service;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.mytest.services.validator.CinemaTicketApiValidatorService;
import uk.gov.dwp.engineering.recruitment.mytest.validation.CinemaTicketApiValidatorResponse;

@Service
public class CinemaTicketApiValidatorServiceImpl implements CinemaTicketApiValidatorService {

    @Override
    public CinemaTicketApiValidatorResponse cinemaTicketApiValidateAccountId(Long accountId) {
        return accountId != null && accountId > 0 ? new CinemaTicketApiValidatorResponse.IsValid()
                :  new CinemaTicketApiValidatorResponse.Invalid("Account ID invalid")
                ;
    }

    @Override
    public CinemaTicketApiValidatorResponse cinemaTicketApiValidateRequest(TicketRequest... ticketRequest) {
        if(ticketRequest == null || ticketRequest.length == 0)
            return new CinemaTicketApiValidatorResponse.Invalid("Ticket Request invalid");
        // Validations
        int ticketCount = 0;
        int ticketAdultCount = 0;
        int ticketChildCount = 0;
        int ticketInfantCount = 0;
        for (TicketRequest ticket : ticketRequest) {
            if (ticket == null)
                return new CinemaTicketApiValidatorResponse.Invalid("Ticket Request invalid - One element is null in the list.");
            if (ticket.ticketCount() <= 0 || ticket.type() == null)
                return new CinemaTicketApiValidatorResponse.Invalid("Ticket Request invalid - Check Ticket type and ticket count.");
            ticketCount += ticket.ticketCount();
            if (ticketCount > 25)
                return new CinemaTicketApiValidatorResponse.Invalid("You can not purchase more than 25 Tickets at the same time");
            switch (ticket.type()) {
                case ADULT -> ticketAdultCount++;
                case CHILD -> ticketChildCount++;
                case INFANT -> ticketInfantCount++;
            }
        }
        if(ticketAdultCount == 0 && (ticketChildCount > 0 || ticketInfantCount > 0)){
            return new CinemaTicketApiValidatorResponse.Invalid("You can not purchase and INFANT or CHILD ticket without  an adult one.");
        }
        return new CinemaTicketApiValidatorResponse.IsValid();
    }

    @Override
    public CinemaTicketApiValidatorResponse cinemaTicketApiValidateFullRequest(Long accountId, TicketRequest... ticketRequest) {
        switch(cinemaTicketApiValidateAccountId(accountId)) {
            case CinemaTicketApiValidatorResponse.IsValid  v -> {return cinemaTicketApiValidateRequest(ticketRequest);}
            case CinemaTicketApiValidatorResponse.Invalid(var details)  -> throw  new InvalidBookingException(details);
        }
    }
}
