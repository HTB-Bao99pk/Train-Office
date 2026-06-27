package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.common.enums.TicketStatus;
import com.hsf302.trainoffice.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("""
            select count(t) > 0
            from Ticket t,
                 TripStation bookedDeparture,
                 TripStation bookedArrival
            where t.seat.seatId = :seatId
              and t.trainTrip.tripId = :tripId
              and t.ticketStatus in :activeStatuses
              and bookedDeparture.trainTrip.tripId = t.booking.trainTrip.tripId
              and bookedDeparture.station.stationId = t.booking.departureStation.stationId
              and bookedArrival.trainTrip.tripId = t.booking.trainTrip.tripId
              and bookedArrival.station.stationId = t.booking.arrivalStation.stationId
              and bookedDeparture.stationOrder < :arrivalOrder
              and bookedArrival.stationOrder > :departureOrder
            """)
    boolean existsActiveOverlappingSeatBooking(@Param("seatId") Long seatId,
                                               @Param("tripId") Long tripId,
                                               @Param("departureOrder") Integer departureOrder,
                                               @Param("arrivalOrder") Integer arrivalOrder,
                                               @Param("activeStatuses") Collection<TicketStatus> activeStatuses);

    @Query("""
            select distinct t.seat.seatId
            from Ticket t,
                 TripStation bookedDeparture,
                 TripStation bookedArrival
            where t.trainTrip.tripId = :tripId
              and t.ticketStatus in :activeStatuses
              and bookedDeparture.trainTrip.tripId = t.booking.trainTrip.tripId
              and bookedDeparture.station.stationId = t.booking.departureStation.stationId
              and bookedArrival.trainTrip.tripId = t.booking.trainTrip.tripId
              and bookedArrival.station.stationId = t.booking.arrivalStation.stationId
              and bookedDeparture.stationOrder < :arrivalOrder
              and bookedArrival.stationOrder > :departureOrder
            """)
    List<Long> findBlockedSeatIds(@Param("tripId") Long tripId,
                                  @Param("departureOrder") Integer departureOrder,
                                  @Param("arrivalOrder") Integer arrivalOrder,
                                  @Param("activeStatuses") Collection<TicketStatus> activeStatuses);

    List<Ticket> findByBooking_BookingIdOrderByTicketIdAsc(Long bookingId);
}
