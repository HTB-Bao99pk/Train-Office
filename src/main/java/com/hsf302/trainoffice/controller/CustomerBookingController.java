package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.common.enums.Gender;
import com.hsf302.trainoffice.dto.*;
import com.hsf302.trainoffice.entity.Booking;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.service.*;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class CustomerBookingController {
    private static final String BOOKING_SESSION_KEY = "bookingSession";
    private static final String GUEST_BOOKING_IDS_SESSION_KEY = "verifiedGuestBookingIds";

    private final TrainTripService trainTripService;
    private final StationService stationService;
    private final BookingService bookingService;
    private final BookingFlowService bookingFlowService;
    private final TicketService ticketService;
    private final UserService userService;

    public CustomerBookingController(TrainTripService trainTripService,
                                     StationService stationService,
                                     BookingService bookingService,
                                     BookingFlowService bookingFlowService,
                                     TicketService ticketService, UserService userService) {
        this.trainTripService = trainTripService;
        this.stationService = stationService;
        this.bookingService = bookingService;
        this.bookingFlowService = bookingFlowService;
        this.ticketService = ticketService;
        this.userService = userService;
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
                    ? bookingFlowService.createPassengerInfoForm(
                            bookingSession.getPassengerCount(),
                            currentUser(session))
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
    public String history(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Object sessionUser = session.getAttribute("currentUser");
        if (sessionUser instanceof User user) {
            model.addAttribute("recentBookings", bookingService.getBookingsForUser(user));
            return "booking/history";
        }
        return "redirect:/booking/lookup";
    }

    @GetMapping("/booking/lookup")
    public String lookup() {
        return "booking/lookup";
    }

    @PostMapping("/booking/lookup")
    public String findGuestBookings(@RequestParam String email,
                                    @RequestParam String phone,
                                    HttpSession session,
                                    Model model) {
        session.removeAttribute(GUEST_BOOKING_IDS_SESSION_KEY);
        try {
            List<Booking> bookings = bookingService.findGuestBookings(email, phone);
            if (bookings.isEmpty()) {
                model.addAttribute("error", "No guest bookings found for this email and phone");
                model.addAttribute("email", email);
                model.addAttribute("phone", phone);
                return "booking/lookup";
            }
            session.setAttribute(GUEST_BOOKING_IDS_SESSION_KEY, bookings.stream()
                    .map(Booking::getBookingId)
                    .collect(Collectors.toCollection(HashSet::new)));
            model.addAttribute("recentBookings", bookings);
            model.addAttribute("guestMode", true);
            return "booking/history";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            return "booking/lookup";
        }
    }

    @GetMapping("/booking/{bookingId}")
    public String detail(@PathVariable Long bookingId,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Object sessionUser = session.getAttribute("currentUser");
        if (!(sessionUser instanceof User user)) {
            redirectAttributes.addFlashAttribute("error", "Please log in to view booking detail");
            return "redirect:/booking/search";
        }
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking.getUser() == null || !booking.getUser().getUserId().equals(user.getUserId())) {
                redirectAttributes.addFlashAttribute("error", "Booking does not belong to current user");
                return "redirect:/booking/history";
            }
            model.addAttribute("booking", booking);
            model.addAttribute("passengers", bookingService.getPassengersForBooking(bookingId));
            model.addAttribute("tickets", ticketService.getTicketsByBookingId(bookingId));
            return "booking/detail";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/booking/history";
        }
    }


    @PostMapping("/booking/{bookingId}/cancel")
    public String cancel(@PathVariable Long bookingId,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        Object sessionUser = session.getAttribute("currentUser");
        if (!(sessionUser instanceof User user)) {
            redirectAttributes.addFlashAttribute("error", "Please log in to cancel booking");
            return "redirect:/login";
        }
        try {
            bookingService.cancelPendingBooking(bookingId, user);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/booking/" + bookingId;
    }

    @GetMapping("/booking/guest/{bookingId}")
    public String guestDetail(@PathVariable Long bookingId,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (!isVerifiedGuestBooking(session, bookingId)) {
            redirectAttributes.addFlashAttribute("error", "Please verify your guest booking first");
            return "redirect:/booking/lookup";
        }
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking.getUser() != null) {
                redirectAttributes.addFlashAttribute("error", "Booking does not belong to a guest");
                return "redirect:/booking/lookup";
            }
            model.addAttribute("booking", booking);
            model.addAttribute("passengers", bookingService.getPassengersForBooking(bookingId));
            model.addAttribute("tickets", ticketService.getTicketsByBookingId(bookingId));
            model.addAttribute("guestMode", true);
            return "booking/detail";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/booking/lookup";
        }
    }

    @PostMapping("/booking/guest/{bookingId}/cancel")
    public String guestCancel(@PathVariable Long bookingId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isVerifiedGuestBooking(session, bookingId)) {
            redirectAttributes.addFlashAttribute("error", "Please verify your guest booking first");
            return "redirect:/booking/lookup";
        }
        try {
            bookingService.cancelPendingGuestBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/booking/guest/" + bookingId;
    }

    private String showTripResults(TripSearchForm form, BindingResult bindingResult, Model model) {
        model.addAttribute("stations", stationService.getAllStations());

        if (bindingResult.hasErrors()) {
            model.addAttribute("trips", List.of());
            return "booking/trips";
        }

        if (Boolean.TRUE.equals(form.getRoundTrip())) {
            return showRoundTripResults(form, model);
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

    private String showRoundTripResults(TripSearchForm form, Model model) {
        if (form.getReturnDate() == null) {
            model.addAttribute("error", "Please select a return date for round trip.");
            model.addAttribute("tripSearchForm", form);
            model.addAttribute("stations", stationService.getAllStations());
            return "booking/search";
        }

        if (form.getReturnDate().isBefore(form.getDepartureDate())) {
            model.addAttribute("error", "Return date must be after or equal to departure date.");
            model.addAttribute("tripSearchForm", form);
            model.addAttribute("stations", stationService.getAllStations());
            return "booking/search";
        }

        List<TripSearchResult> outboundTrips = List.of();
        List<TripSearchResult> inboundTrips = List.of();

        String outboundError = null;
        String inboundError = null;

        try {
            outboundTrips = trainTripService.searchTrips(form);
        } catch (IllegalArgumentException ex) {
            outboundError = ex.getMessage();
        }

        try {
            TripSearchForm returnForm = buildReturnTripSearchForm(form);
            inboundTrips = trainTripService.searchTrips(returnForm);
        } catch (IllegalArgumentException ex) {
            inboundError = ex.getMessage();
        }

        model.addAttribute("tripSearchForm", form);

        model.addAttribute("outboundTrips", outboundTrips);
        model.addAttribute("inboundTrips", inboundTrips);

        model.addAttribute("outboundError", outboundError);
        model.addAttribute("inboundError", inboundError);

        model.addAttribute("departureDate", form.getDepartureDate());
        model.addAttribute("returnDate", form.getReturnDate());

        return "booking/round-trip-results";
    }

    private TripSearchForm buildReturnTripSearchForm(TripSearchForm form) {
        TripSearchForm returnForm = new TripSearchForm();

        returnForm.setDepartureStationId(form.getArrivalStationId());
        returnForm.setArrivalStationId(form.getDepartureStationId());
        returnForm.setDepartureDate(form.getReturnDate());
        returnForm.setPassengerCount(form.getPassengerCount());
        returnForm.setRoundTrip(false);

        return returnForm;
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

    private User currentUser(HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");
        return sessionUser instanceof User user ? user : null;
    }

    private boolean isVerifiedGuestBooking(HttpSession session, Long bookingId) {
        Object value = session.getAttribute(GUEST_BOOKING_IDS_SESSION_KEY);
        return value instanceof Set<?> bookingIds && bookingIds.contains(bookingId);
    }

    private String redirectToSeatSelection(SeatSelectionForm form) {
        return "redirect:/booking/" + form.getTrainTripId() + "/seats"
                + "?departureStationId=" + form.getDepartureStationId()
                + "&arrivalStationId=" + form.getArrivalStationId()
                + "&passengerCount=" + form.getPassengerCount();
    }

}
