package com.tromanager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
    public enum InvoiceStatus {
        UNPAID("Chưa thanh toán", "badge-red"),
        DUE_SOON("Sắp hạn", "badge-yellow"),
        OVERDUE("Quá hạn", "badge-red"),
        PARTIAL("Thanh toán một phần", "badge-yellow"),
        PAID("Đã thanh toán", "badge-green");

        private final String label;
        private final String styleClass;

        InvoiceStatus(String label, String styleClass) {
            this.label = label;
            this.styleClass = styleClass;
        }

        public String getLabel() { return label; }
        public String getStyleClass() { return styleClass; }
    }

    private String code;
    private String roomName;
    private String tenantName;
    private String monthPeriod; // "Tháng 08/2024"
    private long roomAmount;
    private long electricAmount;
    private long waterAmount;
    private long otherAmount;
    private long amount;
    private String dueDate;
    private InvoiceStatus status;
    private long oldElectricReading;
    private long newElectricReading;
    private long oldWaterReading;
    private long newWaterReading;
    private int occupantCount;
    private long cleaningAmount;
    private long trashAmount;
    private long internetAmount;
    private long discount;
    private long previousDebt;
    private long paidAmount;
    private String note = "";
    private String paymentDate = "";
    private String paymentMethod = "";
    private String paymentNote = "";
    private LocalDate invoiceDate = LocalDate.now();
    private final List<InvoiceDetail> details = new ArrayList<>();

    public Invoice(String code, String roomName, String tenantName, String monthPeriod, long roomAmount, long electricAmount, long waterAmount, long otherAmount, String dueDate, InvoiceStatus status) {
        this.code = code;
        this.roomName = roomName;
        this.tenantName = tenantName;
        this.monthPeriod = monthPeriod;
        this.roomAmount = roomAmount;
        this.electricAmount = electricAmount;
        this.waterAmount = waterAmount;
        this.amount = roomAmount + electricAmount + waterAmount + otherAmount;
        this.dueDate = dueDate;
        this.status = status;
        this.oldElectricReading = 0;
        this.newElectricReading = 0;
        this.oldWaterReading = 0;
        this.newWaterReading = 0;
        this.occupantCount = 1;
        this.otherAmount = otherAmount;
    }

    public Invoice(String roomName, String tenantName, long amount, String dueDate, InvoiceStatus status) {
        this("HDN-" + roomName, roomName, tenantName, "Tháng " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy")), amount - 500000, 350000, 100000, 50000, dueDate, status);
    }

    public Invoice(String code, String roomName, String tenantName, String monthPeriod,
                   long roomAmount, long electricAmount, long waterAmount, long otherAmount,
                   String dueDate, InvoiceStatus status, long oldElectricReading,
                   long newElectricReading, long oldWaterReading, long newWaterReading,
                   int occupantCount, long cleaningAmount, long trashAmount, long internetAmount) {
        this(code, roomName, tenantName, monthPeriod, roomAmount, electricAmount, waterAmount, otherAmount, dueDate, status);
        this.oldElectricReading = oldElectricReading;
        this.newElectricReading = newElectricReading;
        this.oldWaterReading = oldWaterReading;
        this.newWaterReading = newWaterReading;
        this.occupantCount = occupantCount;
        this.cleaningAmount = cleaningAmount;
        this.trashAmount = trashAmount;
        this.internetAmount = internetAmount;
        rebuildDetails();
    }

    public String getCode() { return code; }
    public String getRoomName() { return roomName; }
    public String getTenantName() { return tenantName; }
    public String getMonthPeriod() { return monthPeriod; }
    public long getRoomAmount() { return roomAmount; }
    public long getElectricAmount() { return electricAmount; }
    public long getWaterAmount() { return waterAmount; }
    public long getOtherAmount() { return otherAmount; }
    public long getAmount() { return amount; }
    public String getDueDate() { return dueDate; }
    public InvoiceStatus getStatus() {
        if (status == InvoiceStatus.PAID) return status;
        LocalDate due = getDueDateValue();
        if (due == null) return status;
        if (due.isBefore(LocalDate.now())) return InvoiceStatus.OVERDUE;
        if (due.isBefore(LocalDate.now().plusDays(7))) return InvoiceStatus.DUE_SOON;
        return paidAmount > 0 ? InvoiceStatus.PARTIAL : InvoiceStatus.UNPAID;
    }
    public long getOldElectricReading() { return oldElectricReading; }
    public long getNewElectricReading() { return newElectricReading; }
    public long getOldWaterReading() { return oldWaterReading; }
    public long getNewWaterReading() { return newWaterReading; }
    public int getOccupantCount() { return occupantCount; }
    public long getCleaningAmount() { return cleaningAmount; }
    public long getTrashAmount() { return trashAmount; }
    public long getInternetAmount() { return internetAmount; }
    public long getDiscount() { return discount; }
    public long getPreviousDebt() { return previousDebt; }
    public long getPaidAmount() { return paidAmount; }
    public String getNote() { return note; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentNote() { return paymentNote; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public LocalDate getDueDateValue() {
        if (dueDate == null) return null;
        if (dueDate.equalsIgnoreCase("Hôm nay")) return LocalDate.now();
        if (dueDate.equalsIgnoreCase("Ngày mai")) return LocalDate.now().plusDays(1);
        for (DateTimeFormatter formatter : new DateTimeFormatter[]{DateTimeFormatter.ofPattern("dd/MM/yyyy"), DateTimeFormatter.ISO_LOCAL_DATE}) {
            try { return LocalDate.parse(dueDate, formatter); } catch (DateTimeParseException ignored) { }
        }
        return null;
    }
    public List<InvoiceDetail> getDetails() { return details; }

    public void setDiscount(long discount) { this.discount = discount; recalculateAmount(); }
    public void setPreviousDebt(long previousDebt) { this.previousDebt = previousDebt; recalculateAmount(); }
    public void setPaidAmount(long paidAmount) { this.paidAmount = paidAmount; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public void setNote(String note) { this.note = note == null ? "" : note; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate == null ? "" : paymentDate; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod == null ? "" : paymentMethod; }
    public void setPaymentNote(String paymentNote) { this.paymentNote = paymentNote == null ? "" : paymentNote; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate == null ? LocalDate.now() : invoiceDate; }
    public long getSubtotal() { return roomAmount + electricAmount + waterAmount + otherAmount; }
    public long getTotalAmount() { return amount; }

    public void recalculateAmount() {
        amount = getSubtotal() - discount + previousDebt;
    }

    public void rebuildDetails() {
        details.clear();
        details.add(new InvoiceDetail(1, "Tiền nhà", 0, 0, 1, roomAmount, roomAmount, ""));
        details.add(new InvoiceDetail(2, "Tiền điện", oldElectricReading, newElectricReading,
                newElectricReading - oldElectricReading, electricAmount == 0 ? 0 : electricAmount / Math.max(1, newElectricReading - oldElectricReading), electricAmount, ""));
        details.add(new InvoiceDetail(3, "Tiền nước", oldWaterReading, newWaterReading,
                newWaterReading - oldWaterReading, waterAmount == 0 ? 0 : waterAmount / Math.max(1, newWaterReading - oldWaterReading), waterAmount, ""));
        details.add(new InvoiceDetail(4, "Tiền wifi", 0, 0, 1, internetAmount, internetAmount, ""));
        details.add(new InvoiceDetail(5, "Vệ sinh", 0, 0, 1, cleaningAmount, cleaningAmount, ""));
        if (trashAmount > 0) details.add(new InvoiceDetail(6, "Phí rác", 0, 0, 1, trashAmount, trashAmount, ""));
    }

    public String getFormattedAmount() {
        return String.format("%,d VNĐ", amount).replace(',', '.');
    }

    public String getFormattedRoomAmount() {
        return String.format("%,d VNĐ", roomAmount).replace(',', '.');
    }

    public String getFormattedElectricAmount() {
        return String.format("%,d VNĐ", electricAmount).replace(',', '.');
    }

    public String getFormattedWaterAmount() {
        return String.format("%,d VNĐ", waterAmount).replace(',', '.');
    }

    public String getFormattedOtherAmount() {
        return String.format("%,d VNĐ", otherAmount).replace(',', '.');
    }
}