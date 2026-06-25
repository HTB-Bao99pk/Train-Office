package com.hsf302.trainoffice.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.Data;

@Controller
public class MockUiController {

    @ModelAttribute
    void common(Model model) {
        model.addAttribute("form", new UiForm());
        model.addAttribute("filter", new UiForm());
        model.addAttribute("page", map("number", 0, "totalPages", 3, "first", true, "last", false));
        model.addAttribute("breadcrumbs", List.of(map("label", "RailJet UI", "url", null)));
        model.addAttribute("adminMenus", adminMenus());
        model.addAttribute("items", sampleItems());
        model.addAttribute("detail", sampleItems().get(0));
        model.addAttribute("seats", seats());
        model.addAttribute("trips", trips());
        model.addAttribute("tickets", tickets());
        model.addAttribute("recentBookings", bookings());
        model.addAttribute("topRoutes", List.of(map("name", "Ha Noi - Sai Gon", "count", 128), map("name", "Da Nang - Hue", "count", 76)));
        model.addAttribute("paymentMethods", List.of("VNPAY", "MOMO", "BANK_TRANSFER", "CASH"));
        model.addAttribute("totalItems", 128);
        model.addAttribute("activeItems", 96);
        model.addAttribute("pendingItems", 18);
        model.addAttribute("issueItems", 4);
        model.addAttribute("totalRevenue", "125,000,000 VND");
        model.addAttribute("totalBookings", 342);
        model.addAttribute("occupancyRate", "82%");
        model.addAttribute("pendingRefunds", 6);
        model.addAttribute("chartLabels", List.of("Mon", "Tue", "Wed", "Thu", "Fri"));
        model.addAttribute("chartData", List.of(12, 19, 9, 15, 22));
    }

    @GetMapping("/")
    String home() {
        return "booking/search";
    }





    @GetMapping("/forgot-password")
    String forgotPassword(Model model) {
        model.addAttribute("authTitle", "Quên mật khẩu / Forgot password");
        model.addAttribute("authAction", "/forgot-password");
        model.addAttribute("authButton", "Send reset link / Gửi link");
        model.addAttribute("showEmail", true);
        model.addAttribute("showPassword", false);
        return "auth/forgot-password";
    }

    @GetMapping({"/admin", "/admin/dashboard"})
    String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/reports/{report}")
    String report(@PathVariable String report) {
        return switch (report) {
            case "revenue", "bookings", "occupancy" -> "reports/" + report;
            default -> "reports/revenue";
        };
    }

    @GetMapping("/admin/{module}")
    String adminList(@PathVariable String module) {
        return module + "/list";
    }

    @GetMapping("/admin/{module}/create")
    String adminCreate(@PathVariable String module) {
        return module + "/create";
    }

    @GetMapping("/admin/{module}/{id}/edit")
    String adminEdit(@PathVariable String module) {
        return module + "/edit";
    }

    @GetMapping("/admin/{module}/{id}")
    String adminDetail(@PathVariable String module) {
        return module + "/detail";
    }

    @GetMapping("/admin/routes/{id}/stations")
    String routeStations() {
        return "routes/stations";
    }

    @GetMapping("/admin/seats/layout")
    String seatLayout() {
        return "seats/layout";
    }

    @GetMapping("/admin/refunds/{id}/review")
    String refundReview() {
        return "refunds/review";
    }

    @GetMapping("/booking/search")
    String bookingSearch() {
        return "booking/search";
    }

    @GetMapping("/booking/trips")
    String bookingTrips() {
        return "booking/trips";
    }

    @GetMapping("/booking/{id}/seats")
    String seatSelection() {
        return "booking/seat-selection";
    }

    @GetMapping("/booking/passenger-info")
    String passengerInfo() {
        return "booking/passenger-info";
    }

    @GetMapping("/booking/confirmation")
    String confirmation() {
        return "booking/confirmation";
    }

    @GetMapping("/booking/success")
    String bookingSuccess() {
        return "booking/success";
    }

    @GetMapping("/booking/history")
    String bookingHistory() {
        return "booking/history";
    }

    @GetMapping("/profile")
    String profile() {
        return "customer/profile";
    }

    @GetMapping("/booking/{id}")
    String bookingDetail() {
        return "booking/detail";
    }

    @GetMapping("/tickets")
    String ticketsList() {
        return "tickets/list";
    }

    @GetMapping("/tickets/{id}")
    String ticketDetail() {
        return "tickets/detail";
    }

    @GetMapping("/tickets/{id}/print")
    String ticketPrint() {
        return "tickets/print";
    }

    @GetMapping("/payments/{id}")
    String payment() {
        return "payments/payment";
    }

    @GetMapping("/payments/success")
    String paymentSuccess() {
        return "payments/success";
    }

    @GetMapping("/payments/failed")
    String paymentFailed() {
        return "payments/failed";
    }

    @GetMapping("/invoices/{id}")
    String invoiceDetail() {
        return "invoices/detail";
    }

    @GetMapping("/invoices/{id}/print")
    String invoicePrint() {
        return "invoices/print";
    }

    @GetMapping("/refunds/request")
    String refundRequest() {
        return "refunds/request";
    }

    @GetMapping("/refunds/{id}")
    String refundDetail() {
        return "refunds/detail";
    }

    @PostMapping({ "/forgot-password", "/booking/passenger-info", "/booking/confirmation",
            "/payments/process", "/refunds/request", "/admin/{module}/save", "/admin/{module}/delete"})
    String mockSubmit() {
        return "redirect:/";
    }

    private static List<Map<String, Object>> adminMenus() {
        return List.of(
                map("key", "users", "label", "Users / Người dùng", "icon", " fas fa-users"),
                map("key", "passengers", "label", "Passengers / Hành khách", "icon", " fas fa-user-tag"),
                map("key", "stations", "label", "Stations / Nhà ga", "icon", " fas fa-map-marker-alt"),
                map("key", "routes", "label", "Routes / Tuyến", "icon", " fas fa-route"),
                map("key", "trains", "label", "Trains / Tàu", "icon", " fas fa-train"),
                map("key", "coaches", "label", "Coaches / Toa", "icon", " fas fa-subway"),
                map("key", "seats", "label", "Seats / Ghế", "icon", " fas fa-chair"),
                map("key", "trips", "label", "Trips / Chuyến", "icon", " fas fa-calendar-alt"),
                map("key", "schedules", "label", "Schedules / Lịch", "icon", " fas fa-clock"),
                map("key", "trip-stations", "label", "Trip Stations", "icon", " fas fa-map"),
                map("key", "payments", "label", "Payments", "icon", " fas fa-credit-card"),
                map("key", "invoices", "label", "Invoices", "icon", " fas fa-file-invoice"),
                map("key", "refunds", "label", "Refunds", "icon", " fas fa-undo"),
                map("key", "reports/revenue", "label", "Reports", "icon", " fas fa-chart-pie"));
    }

    private static List<Map<String, Object>> sampleItems() {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(map("id", 1, "userId", 1, "username", "admin", "passwordHash", "***", "role", "ADMIN", "status", "ACTIVE",
                "createdAt", "2026-06-24", "updatedAt", "2026-06-24", "passengerId", 1, "user", "admin",
                "fullName", "Nguyen Van A", "identityNumber", "012345678901", "dateOfBirth", "1998-01-01",
                "gender", "MALE", "stationId", 1, "stationCode", "HAN", "stationName", "Ha Noi", "city", "Ha Noi",
                "routeId", 1, "routeCode", "R001", "routeName", "Ha Noi - Sai Gon", "distanceKm", 1726,
                "routeStationId", 1,
                "trainId", 1, "trainCode", "SE1", "trainName", "RailJet Express", "trainType", "EXPRESS",
                "coachId", 1, "train", "SE1", "coachNumber", "C01", "coachType", "SOFT_SEAT", "capacity", 64,
                "seatId", 1, "coach", "C01", "seatNumber", "A1", "seatType", "WINDOW", "extraPrice", "20000",
                "tripId", 1, "route", "Ha Noi - Sai Gon", "departureTime", "08:00 24/06/2026",
                "arrivalTime", "20:00 24/06/2026", "basePrice", "450,000 VND", "tripStationId", 1,
                "trainTrip", "SE1 / R001", "station", "Da Nang", "stationOrder", 2,
                "paymentId", 1, "booking", "RJ0001", "paymentMethod", "VNPAY", "amount", "900,000 VND",
                "transactionCode", "TXN001", "otpCode", "123456", "paymentStatus", "PAID", "paidAt", "2026-06-24",
                "invoiceId", 1, "invoiceCode", "INV0001", "payment", "TXN001", "totalAmount", "900,000 VND",
                "issuedAt", "2026-06-24", "refundId", 1, "refundCode", "RF0001", "ticket", "RJ-T001",
                "refundAmount", "450,000 VND", "refundReason", "Change plan", "refundStatus", "PENDING",
                "refundedAt", ""));
        rows.add(new LinkedHashMap<>(rows.get(0)));
        rows.get(1).put("id", 2);
        rows.get(1).put("status", "PENDING");
        return rows;
    }

    private static List<Map<String, Object>> seats() {
        List<Map<String, Object>> seats = new ArrayList<>();
        for (int i = 1; i <= 32; i++) {
            seats.add(map("seatId", i, "seatNumber", "A" + i, "status", i % 7 == 0 ? "BOOKED" : "AVAILABLE"));
        }
        return seats;
    }

    private static List<Map<String, Object>> trips() {
        return List.of(
                map("train", "SE1", "route", "Ha Noi - Sai Gon", "departureTime", "08:00 24/06/2026", "arrivalTime", "20:00 24/06/2026", "basePrice", "450,000 VND"),
                map("train", "SE3", "route", "Ha Noi - Da Nang", "departureTime", "09:30 24/06/2026", "arrivalTime", "18:00 24/06/2026", "basePrice", "350,000 VND"));
    }

    private static List<Map<String, Object>> tickets() {
        return List.of(
                map("ticketCode", "RJ-T001", "passenger", "Nguyen Van A", "seat", "A1", "price", "450,000 VND", "status", "CONFIRMED"),
                map("ticketCode", "RJ-T002", "passenger", "Tran Thi B", "seat", "A2", "price", "450,000 VND", "status", "PENDING"));
    }

    private static List<Map<String, Object>> bookings() {
        return List.of(
                map("bookingCode", "RJ0001", "contactName", "Nguyen Van A", "route", "Ha Noi - Sai Gon", "totalAmount", "900,000 VND", "status", "CONFIRMED"),
                map("bookingCode", "RJ0002", "contactName", "Tran Thi B", "route", "Da Nang - Hue", "totalAmount", "250,000 VND", "status", "PENDING"));
    }

    private static Map<String, Object> map(Object... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(String.valueOf(values[i]), values[i + 1]);
        }
        return map;
    }

    @Data
    public static class UiForm {
        private String keyword = "";
        private String status = "";
        private String fromDate = LocalDate.now().minusDays(7).toString();
        private String toDate = LocalDate.now().toString();
        private String userId = "";
        private String username = "";
        private String passwordHash = "";
        private String role = "CUSTOMER";
        private String createdAt = "";
        private String updatedAt = "";
        private String passengerId = "";
        private String user = "";
        private String contactName = "";
        private String contactPhone = "";
        private String contactEmail = "";
        private String fullName = "";
        private String identityNumber = "";
        private String dateOfBirth = "";
        private String gender = "";
        private String stationId = "";
        private String stationCode = "";
        private String stationName = "";
        private String city = "";
        private String routeId = "";
        private String routeCode = "";
        private String routeName = "";
        private String distanceKm = "";
        private String routeStationId = "";
        private String route = "";
        private String station = "";
        private String stationOrder = "";
        private String trainId = "";
        private String trainCode = "";
        private String trainName = "";
        private String trainType = "";
        private String coachId = "";
        private String train = "";
        private String coachNumber = "";
        private String coachType = "";
        private String capacity = "";
        private String seatId = "";
        private String coach = "";
        private String seatNumber = "";
        private String seatType = "";
        private String extraPrice = "";
        private String tripId = "";
        private String trainTrip = "";
        private String departureTime = "";
        private String arrivalTime = "";
        private String basePrice = "";
        private String tripStationId = "";
        private String paymentId = "";
        private String booking = "";
        private String departureStationId = "";
        private String arrivalStationId = "";
        private String departureDate = LocalDate.now().toString();
        private Integer passengerCount = 1;
        private List<String> seatIds = new ArrayList<>();
        private String paymentMethod = "VNPAY";
        private String amount = "";
        private String transactionCode = "";
        private String otpCode = "";
        private String paymentStatus = "";
        private String paidAt = "";
        private String invoiceId = "";
        private String invoiceCode = "";
        private String payment = "";
        private String totalAmount = "";
        private String issuedAt = "";
        private String refundId = "";
        private String refundCode = "";
        private String ticket = "";
        private String refundAmount = "";
        private String refundReason = "";
        private String refundStatus = "";
        private String refundedAt = "";

        private final Map<String, Object> extra = new LinkedHashMap<>();

        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getFromDate() { return fromDate; }
        public void setFromDate(String fromDate) { this.fromDate = fromDate; }
        public String getToDate() { return toDate; }
        public void setToDate(String toDate) { this.toDate = toDate; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContactName() { return contactName; }
        public void setContactName(String contactName) { this.contactName = contactName; }
        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
        public String getContactEmail() { return contactEmail; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getIdentityNumber() { return identityNumber; }
        public void setIdentityNumber(String identityNumber) { this.identityNumber = identityNumber; }
        public String getDepartureStationId() { return departureStationId; }
        public void setDepartureStationId(String departureStationId) { this.departureStationId = departureStationId; }
        public String getArrivalStationId() { return arrivalStationId; }
        public void setArrivalStationId(String arrivalStationId) { this.arrivalStationId = arrivalStationId; }
        public String getDepartureDate() { return departureDate; }
        public void setDepartureDate(String departureDate) { this.departureDate = departureDate; }
        public Integer getPassengerCount() { return passengerCount; }
        public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }
        public List<String> getSeatIds() { return seatIds; }
        public void setSeatIds(List<String> seatIds) { this.seatIds = seatIds; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getRefundReason() { return refundReason; }
        public void setRefundReason(String refundReason) { this.refundReason = refundReason; }

        public Object get(String key) {
            return extra.getOrDefault(key, "");
        }
    }
}
