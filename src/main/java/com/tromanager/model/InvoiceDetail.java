package com.tromanager.model;

public class InvoiceDetail {
    private final int sequence;
    private final String serviceName;
    private final long previousIndex;
    private final long currentIndex;
    private final long quantity;
    private final long unitPrice;
    private final long amount;
    private final String note;

    public InvoiceDetail(int sequence, String serviceName, long previousIndex, long currentIndex,
                         long quantity, long unitPrice, long amount, String note) {
        this.sequence = sequence;
        this.serviceName = serviceName;
        this.previousIndex = previousIndex;
        this.currentIndex = currentIndex;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.note = note == null ? "" : note;
    }

    public int getSequence() { return sequence; }
    public String getServiceName() { return serviceName; }
    public long getPreviousIndex() { return previousIndex; }
    public long getCurrentIndex() { return currentIndex; }
    public long getQuantity() { return quantity; }
    public long getUnitPrice() { return unitPrice; }
    public long getAmount() { return amount; }
    public String getNote() { return note; }
}