package com.tromanager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Contract {
    public enum Status {
        ACTIVE("Đang hiệu lực", "badge-green"),
        EXPIRING("Sắp hết hạn", "badge-yellow"),
        EXPIRED("Đã hết hạn", "badge-red");

        private final String label;
        private final String styleClass;

        Status(String label, String styleClass) {
            this.label = label;
            this.styleClass = styleClass;
        }

        public String getLabel() { return label; }
        public String getStyleClass() { return styleClass; }
    }

    private String code;
    private String roomName;
    private String tenantName;
    private String phone;
    private String ownerName = "Khu trọ Hòa Bình";
    private String ownerAddress = "";
    private String ownerPhone = "";
    private String tenantAddress = "";
    private String tenantIdCard = "";
    private String startDate;
    private String expiryDate;
    private long rentPrice;
    private long deposit;
    private int remainingDays;
    private Status status;

    public Contract(String code, String roomName, String tenantName, String phone, String startDate, String expiryDate, long rentPrice, long deposit, int remainingDays, Status status) {
        this.code = code;
        this.roomName = roomName;
        this.tenantName = tenantName;
        this.phone = phone;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.rentPrice = rentPrice;
        this.deposit = deposit;
        this.remainingDays = remainingDays;
        this.status = status;
    }

    public Contract(String roomName, String tenantName, String expiryDate, int remainingDays) {
        this("HD-" + roomName, roomName, tenantName, "0912 345 678", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), expiryDate, 3500000L, 3500000L, remainingDays, Status.EXPIRING);
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = value(ownerName); }
    public String getOwnerAddress() { return ownerAddress; }
    public void setOwnerAddress(String ownerAddress) { this.ownerAddress = value(ownerAddress); }
    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = value(ownerPhone); }
    public String getTenantAddress() { return tenantAddress; }
    public void setTenantAddress(String tenantAddress) { this.tenantAddress = value(tenantAddress); }
    public String getTenantIdCard() { return tenantIdCard; }
    public void setTenantIdCard(String tenantIdCard) { this.tenantIdCard = value(tenantIdCard); }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public long getRentPrice() { return rentPrice; }
    public void setRentPrice(long rentPrice) { this.rentPrice = rentPrice; }
    public long getDeposit() { return deposit; }
    public void setDeposit(long deposit) { this.deposit = deposit; }
    public int getRemainingDays() {
        LocalDate expiry = parseDate(expiryDate);
        return expiry == null ? remainingDays : (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiry);
    }
    public void setRemainingDays(int remainingDays) { this.remainingDays = remainingDays; }
    public Status getStatus() {
        if (status == Status.EXPIRED && remainingDays == 0) return Status.EXPIRED;
        int days = getRemainingDays();
        if (days < 0) return Status.EXPIRED;
        if (days <= 30) return Status.EXPIRING;
        return Status.ACTIVE;
    }
    public void setStatus(Status status) { this.status = status; }

    public String getFormattedRentPrice() {
        return String.format("%,dđ", rentPrice).replace(',', '.');
    }

    public String getFormattedDeposit() {
        return String.format("%,dđ", deposit).replace(',', '.');
    }

    public String getRemainingText() {
        int days = getRemainingDays();
        return days < 0 ? "Đã quá hạn " + Math.abs(days) + " ngày" : "Còn " + days + " ngày";
    }

    public String getPeriod() {
        return startDate + " - " + expiryDate;
    }

    public LocalDate getExpiryDateValue() {
        return parseDate(expiryDate);
    }

    private LocalDate parseDate(String value) {
        if (value == null) return null;
        for (DateTimeFormatter formatter : new DateTimeFormatter[]{DateTimeFormatter.ofPattern("dd/MM/yyyy"), DateTimeFormatter.ISO_LOCAL_DATE}) {
            try { return LocalDate.parse(value, formatter); } catch (DateTimeParseException ignored) { }
        }
        return null;
    }

    private static String value(String text) {
        return text == null ? "" : text.trim();
    }
}