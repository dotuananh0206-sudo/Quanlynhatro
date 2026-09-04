package com.tromanager.model;

public class Tenant {
    private String id;
    private String name;
    private String phone;
    private String roomName;
    private String idCard; // CCCD
    private String hometown;
    private String startDate;
    private long deposit;
    private boolean tempReg; // Đăng ký tạm trú
    private String email = "";
    private String address = "";
    private String emergencyContact = "";
    private String emergencyPhone = "";
    private String checkoutDate = "";
    private String checkoutNote = "";

    public Tenant(String id, String name, String phone, String roomName, String idCard, String hometown, String startDate, long deposit, boolean tempReg) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.roomName = roomName;
        this.idCard = idCard;
        this.hometown = hometown;
        this.startDate = startDate;
        this.deposit = deposit;
        this.tempReg = tempReg;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getRoomName() { return roomName; }
    public String getIdCard() { return idCard; }
    public String getHometown() { return hometown; }
    public String getStartDate() { return startDate; }
    public long getDeposit() { return deposit; }
    public boolean isTempReg() { return tempReg; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public void setHometown(String hometown) { this.hometown = hometown; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setDeposit(long deposit) { this.deposit = deposit; }
    public void setTempReg(boolean tempReg) { this.tempReg = tempReg; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getEmergencyContact() { return emergencyContact; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public String getCheckoutDate() { return checkoutDate; }
    public String getCheckoutNote() { return checkoutNote; }
    public void setEmail(String email) { this.email = value(email); }
    public void setAddress(String address) { this.address = value(address); }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = value(emergencyContact); }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = value(emergencyPhone); }
    public void setCheckoutDate(String checkoutDate) { this.checkoutDate = value(checkoutDate); }
    public void setCheckoutNote(String checkoutNote) { this.checkoutNote = value(checkoutNote); }

    private static String value(String text) { return text == null ? "" : text.trim(); }

    public String getFormattedDeposit() {
        return String.format("%,dđ", deposit).replace(',', '.');
    }

    public String getTempRegStatus() {
        return tempReg ? "Đã đăng ký" : "Chưa đăng ký";
    }

    public String getTempRegBadgeClass() {
        return tempReg ? "badge-green" : "badge-red";
    }
}