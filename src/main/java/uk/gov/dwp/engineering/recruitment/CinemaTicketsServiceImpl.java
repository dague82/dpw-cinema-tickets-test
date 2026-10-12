package uk.gov.dwp.engineering.recruitment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.mytest.services.validator.CinemaTicketApiValidatorService;
import uk.gov.dwp.engineering.recruitment.mytest.validation.CinemaTicketApiValidatorResponse;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static uk.gov.dwp.engineering.recruitment.domain.TicketType.ADULT;

@Service
public class CinemaTicketsServiceImpl implements CinemaTicketsService {

  private final PaymentService paymentService;

  private final SeatReservationService seatReservationService;

  @Autowired
  CinemaTicketApiValidatorService cinemaTicketApiValidatorService;

  public CinemaTicketsServiceImpl(PaymentService paymentService,SeatReservationService seatReservationService) {
    this.paymentService = paymentService;
    this.seatReservationService = seatReservationService;
  }

  @Override
  public BookingConfirmation purchaseTickets(final Long accountId,
      final TicketRequest... ticketRequests)
      throws InvalidBookingException {
    BookingConfirmation response;
      switch (cinemaTicketApiValidatorService.cinemaTicketApiValidateFullRequest(accountId,ticketRequests)){
        case CinemaTicketApiValidatorResponse.IsValid valid -> {
          response = processPurchase(accountId,ticketRequests);
        }
        case CinemaTicketApiValidatorResponse.Invalid(var details) -> throw new InvalidBookingException(details);
      }
    return response;
  }

  private BookingConfirmation processPurchase(Long accountId, TicketRequest[] ticketRequests) {
    BigDecimal totalAmountTicket = BigDecimal.ZERO;
    long totalAmountSeats = 0L;
    for (TicketRequest ticketRequest : ticketRequests) {
      BigDecimal ticketBD = new BigDecimal(ticketRequest.ticketCount());
      BigDecimal price  = getPriceByTicketType(ticketRequest.type());
      totalAmountTicket = totalAmountTicket.add(price.multiply(ticketBD));
      if(ticketRequest.type() != TicketType.INFANT) {
        totalAmountSeats = totalAmountSeats + ticketRequest.ticketCount();
      }
    }
    totalAmountTicket = totalAmountTicket.setScale(2, RoundingMode.HALF_UP);
    paymentService.debitAccount(accountId,totalAmountTicket);
    seatReservationService.reserveSeats(accountId,totalAmountSeats);
    return new BookingConfirmation(accountId,(int)totalAmountSeats, totalAmountTicket.doubleValue());
  }

  private BigDecimal getPriceByTicketType(TicketType ticketType) {
    BigDecimal bigDecimal = switch (ticketType) {
      case ADULT -> new BigDecimal(25.99);
      case CHILD -> new BigDecimal(17.50);
      case INFANT -> new BigDecimal(0.0);
    };
    return bigDecimal;
  }

}
