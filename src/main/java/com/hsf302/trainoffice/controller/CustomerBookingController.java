package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.common.enums.Gender;
import com.hsf302.trainoffice.dto.BookingConfirmationView;
import com.hsf302.trainoffice.dto.BookingSession;
import com.hsf302.trainoffice.dto.PassengerInfoForm;
import com.hsf302.trainoffice.dto.SeatSelectionForm;
import com.hsf302.trainoffice.dto.TripSearchForm;
import com.hsf302.trainoffice.dto.TripSegment;
import com.hsf302.trainoffice.entity.Booking;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.service.BookingFlowService;
import com.hsf302.trainoffice.service.BookingService;
import com.hsf302.trainoffice.service.StationService;
import com.hsf302.trainoffice.service.TrainTripService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerBookingController {
    private static final String BOOKING_SESSION_KEY = "bookingSession";

    private final TrainTripService trainTripService;
    private final StationService stationService;
    private final BookingService bookingService;
    private final BookingFlowService bookingFlowService;

    public CustomerBookingController(TrainTripService trainTripService,
                                     StationService stationService,
                                     BookingService bookingService,
                                     BookingFlowService bookingFlowService) {
        this.trainTripService = trainTripService;
        this.stationService = stationService;
        this.bookingService = bookingService;
        this.bookingFlowService = bookingFlowService;
    }

    @GetMapping("/booking/search")
    public String search(Model model) {
        if (!model.containsAttribute("tripSearchForm")) {
            model.addAttribute("tripSearchForm", new TripSearchForm());
        }
        model.addAttribute("stations", stationService.getAllStations());
        return "booking/search";
    }

    @GetMapping("/booking/trips")
    public String tripsGet(@Valid @ModelAttribute("tripSearchForm") TripSearchForm form,
                           BindingResult bindingResult,
                           Model model) {
        return showTripResults(form, bindingResult, model);
    }

    @PostMapping("/booking/trips")
    public String tripsPost(@Valid @ModelAttribute("tripSearchForm") TripSearchForm form,
                            BindingResult bindingResult,
                            Model model) {
        return showTripResults(form, bindingResult, model);
    }

    @GetMapping("/booking/{tripId}/seats")
    public String seats(@PathVariable Long tripId,
                        @RequestParam Long departureStationId,
                        @RequestParam Long arrivalStationId,
                        @RequestParam(defaultValue = "1") int passengerCount,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        try {
            TripSegment segment = trainTripService.getValidSegment(tripId, departureStationId, arrivalStationId);
            SeatSelectionForm form = new SeatSelectionForm();
            form.setTrainTripId(tripId);
            form.setDepartureStationId(departureStationId);
            form.setArrivalStationId(arrivalStationId);
            form.setPassengerCount(passengerCount);
            model.addAttribute("seatSelectionForm", form);
            model.addAttribute("trip", trainTripService.getTripById(tripId).orElseThrow());
            model.addAttribute("segment", segment);
            model.addAttribute("seats", bookingFlowService.getSeatAvailability(tripId, departureStationId, arrivalStationId));
            return "booking/seat-selection";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/booking/search";
        }
    }

    @PostMapping("/booking/seat-selection")
    public String selectSeats(@Valid @ModelAttribute("seatSelectionForm") SeatSelectionForm form,
                              BindingResult bindingResult,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()
                || form.getSeatIds().size() != form.getPassengerCount()) {
            redirectAttributes.addFlashAttribute("error", "Please select exactly " + form.getPassengerCount() + " seats");
            return redirectToSeatSelection(form);
        }
        try {
            session.setAttribute(BOOKING_SESSION_KEY, bookingFlowService.buildBookingSession(form));
            return "redirect:/booking/passenger-info";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return redirectToSeatSelection(form);
        }
    }

    @GetMapping("/booking/passenger-info")
    public String passengerInfo(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        BookingSession bookingSession = getBookingSession(session);
        if (bookingSession == null) {
            redirectAttributes.addFlashAttribute("error", "Please select seats before entering passenger information");
            return "redirect:/booking/search";
        }
        if (!model.containsAttribute("passengerInfoForm")) {
            PassengerInfoForm form = bookingSession.getPassengerInfo() == null
                    ? bookingFlowService.createPassengerInfoForm(bookingSession.getPassengerCount())
                    : bookingSession.getPassengerInfo();
            model.addAttribute("passengerInfoForm", form);
        }
        model.addAttribute("genders", Gender.values());
        model.addAttribute("bookingSession", bookingSession);
        return "booking/passenger-info";
    }

    @PostMapping("/booking/passenger-info")
    public String savePassengerInfo(@Valid @ModelAttribute("passengerInfoForm") PassengerInfoForm form,
                                    BindingResult bindingResult,
                                    HttpSession session,
                                    Model model) {
        BookingSession bookingSession = getBookingSession(session);
        if (bookingSession == null) {
            return "redirect:/booking/search";
        }
        if (bindingResult.hasErrors() || form.getPassengers().size() != bookingSession.getPassengerCount()) {
            model.addAttribute("genders", Gender.values());
            model.addAttribute("bookingSession", bookingSession);
            return "booking/passenger-info";
        }
        bookingFlowService.savePassengerInfo(bookingSession, form);
        session.setAttribute(BOOKING_SESSION_KEY, bookingSession);
        return "redirect:/booking/confirmation";
    }

    @GetMapping("/booking/confirmation")
    public String confirmation(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        BookingSession bookingSession = getBookingSession(session);
        if (bookingSession == null || bookingSession.getPassengerInfo() == null) {
            redirectAttributes.addFlashAttribute("error", "Please complete booking information first");
            return "redirect:/booking/search";
        }
        try {
            addConfirmationModel(bookingFlowService.buildConfirmation(bookingSession), model);
            return "booking/confirmation";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/booking/search";
        }
    }

    @PostMapping("/booking/confirmation")
    public String createBooking(HttpSession session, RedirectAttributes redirectAttributes) {
        BookingSession bookingSession = getBookingSession(session);
        if (bookingSession == null || bookingSession.getPassengerInfo() == null) {
            redirectAttributes.addFlashAttribute("error", "Please complete booking information first");
            return "redirect:/booking/search";
        }
        try {
            Object sessionUser = session.getAttribute("currentUser");
            Booking booking = sessionUser instanceof User user
                    ? bookingFlowService.createBooking(bookingSession, user)
                    : bookingFlowService.createBooking(bookingSession, null);
            session.removeAttribute(BOOKING_SESSION_KEY);
            return "redirect:/booking/success?bookingId=" + booking.getBookingId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/booking/confirmation";
        }
    }

    @GetMapping("/booking/success")
    public String success(@RequestParam(required = false) Long bookingId, Model model) {
        if (bookingId != null) {
            model.addAttribute("booking", bookingService.getBookingById(bookingId));
        }
        return "booking/success";
    }

    @GetMapping("/booking/history")
    public String history(HttpSession session, Model model) {
        Object sessionUser = session.getAttribute("currentUser");
        if (sessionUser instanceof User user) {
            model.addAttribute("recentBookings", bookingService.getBookingsForUser(user));
        } else {
            model.addAttribute("recentBookings", List.of());
        }
        return "booking/history";
    }

    @GetMapping("/booking/{bookingId}")
    public String detail(@PathVariable Long bookingId,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Object sessionUser = session.getAttribute("currentUser");
        if (!(sessionUser instanceof User)) {
            redirectAttributes.addFlashAttribute("error", "Please log in to view booking detail");
            return "redirect:/booking/search";
        }
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            model.addAttribute("booking", booking);
            model.addAttribute("tickets", booking.getTickets());
            return "booking/detail";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/booking/history";
        }
    }

    private String showTripResults(TripSearchForm form, BindingResult bindingResult, Model model) {
        model.addAttribute("stations", stationService.getAllStations());
        if (bindingResult.hasErrors()) {
            model.addAttribute("trips", List.of());
            return "booking/trips";
        }
        try {
            model.addAttribute("trips", trainTripService.searchTrips(form));
            model.addAttribute("tripSearchForm", form);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("trips", List.of());
        }
        return "booking/trips";
    }

    private void addConfirmationModel(BookingConfirmationView confirmation, Model model) {
        model.addAttribute("bookingSession", confirmation.getBookingSession());
        model.addAttribute("passengerInfo", confirmation.getPassengerInfo());
        model.addAttribute("trip", confirmation.getTrip());
        model.addAttribute("segment", confirmation.getSegment());
        model.addAttribute("selectedSeats", confirmation.getSelectedSeats());
        model.addAttribute("totalAmount", confirmation.getTotalAmount());
    }

    private BookingSession getBookingSession(HttpSession session) {
        Object value = session.getAttribute(BOOKING_SESSION_KEY);
        return value instanceof BookingSession bookingSession ? bookingSession : null;
    }

    private String redirectToSeatSelection(SeatSelectionForm form) {
        return "redirect:/booking/" + form.getTrainTripId() + "/seats"
                + "?departureStationId=" + form.getDepartureStationId()
                + "&arrivalStationId=" + form.getArrivalStationId()
                + "&passengerCount=" + form.getPassengerCount();
    }
}
