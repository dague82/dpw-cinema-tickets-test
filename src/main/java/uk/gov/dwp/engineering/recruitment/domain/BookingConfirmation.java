package uk.gov.dwp.engineering.recruitment.domain;

public record BookingConfirmation(Long accountId, long seatCount,double totalCost) {

    public BookingConfirmation(long accountId) {
        this(accountId,0L,0.0);
    }
}
