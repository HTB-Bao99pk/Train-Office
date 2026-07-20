package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.entity.Coach;
import com.hsf302.trainoffice.entity.Train;
import com.hsf302.trainoffice.repository.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoachServiceImplTest {

    private final CoachRepository coachRepository = mock(CoachRepository.class);
    private final SeatRepository seatRepository = mock(SeatRepository.class);
    private final CompartmentRepository compartmentRepository = mock(CompartmentRepository.class);
    private final TicketRepository ticketRepository = mock(TicketRepository.class);
    private final CoachServiceImpl service = new CoachServiceImpl(
            coachRepository, seatRepository, compartmentRepository, ticketRepository);

    @Test
    void availableTypesAreTrimmedFilteredDeduplicatedAndSorted() {
        when(coachRepository.findDistinctCoachTypes()).thenReturn(Arrays.asList(
                " Sleeper ", "Soft Seat", null, "", "Hard Seat", "SOFT SEAT", "   "));

        assertEquals(List.of("Hard Seat", "Sleeper", "Soft Seat"), service.getAvailableCoachTypes());
    }

    @Test
    void selectingExistingTypeSavesNormally() {
        when(coachRepository.findDistinctCoachTypes()).thenReturn(List.of("Sleeper", "Soft Seat"));
        Coach coach = normalCoach("C01", "Sleeper");

        assertEquals("Sleeper", service.saveCoach(coach).getCoachType());
        verify(coachRepository).save(coach);
    }

    @Test
    void newTypeIsSavedAndAppearsInNextAvailableList() {
        when(coachRepository.findDistinctCoachTypes())
                .thenReturn(List.of("Sleeper", "Soft Seat"))
                .thenReturn(List.of("Sleeper", "Soft Seat", "Premium Cabin"));
        Coach coach = normalCoach("C02", "  Premium Cabin  ");

        assertEquals("Premium Cabin", service.saveCoach(coach).getCoachType());
        assertEquals(List.of("Premium Cabin", "Sleeper", "Soft Seat"), service.getAvailableCoachTypes());
    }

    @Test
    void caseInsensitiveExistingTypeUsesStoredCanonicalValue() {
        when(coachRepository.findDistinctCoachTypes()).thenReturn(List.of("Soft Seat"));
        Coach coach = normalCoach("C03", " SOFT SEAT ");

        assertEquals("Soft Seat", service.saveCoach(coach).getCoachType());
    }

    @Test
    void blankNewTypeCannotBeSaved() {
        Coach coach = normalCoach("C04", "   ");

        assertEquals("Coach type is required.",
                assertThrows(IllegalStateException.class, () -> service.saveCoach(coach)).getMessage());
        verify(coachRepository, never()).save(any());
    }

    private Coach normalCoach(String number, String type) {
        Train train = new Train();
        train.setTrainId(1L);
        Coach coach = new Coach();
        coach.setTrain(train);
        coach.setCoachNumber(number);
        coach.setCoachType(type);
        coach.setCapacity(1);
        coach.setSleeperCoach(false);
        when(coachRepository.save(coach)).thenAnswer(invocation -> {
            Coach saved = invocation.getArgument(0);
            saved.setCoachId(10L);
            return saved;
        });
        return coach;
    }
}
