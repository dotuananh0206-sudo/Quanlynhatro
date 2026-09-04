package com.tromanager.model;

import java.util.List;

public class Room {
    private String id;
    private String name; // e.g. "P.101"
    private int floor;   // 1, 2, 3
    private String buildingName; // "Khu trọ Hòa Bình"
    private RoomStatus status;
    private String tenantName;
    private String phoneNumber;
    private String contractStart;
    private String contractEnd;
    private long priceMonthly; // e.g. 3500000
    private long electricityPrice; // 3500
    private long waterPrice; // 25000
    private int area; // 25 (m2)
    private List<String> amenities; // ["Máy lạnh", "Wifi", "WC riêng", "Tủ lạnh"]

    public Room(String id, String name, int floor, String buildingName, RoomStatus status,
                String tenantName, String phoneNumber, String contractStart, String contractEnd,
                long priceMonthly, long electricityPrice, long waterPrice, int area, List<String> amenities) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.buildingName = buildingName;
        this.status = status;
        this.tenantName = tenantName;
        this.phoneNumber = phoneNumber;
        this.contractStart = contractStart;
        this.contractEnd = contractEnd;
        this.priceMonthly = priceMonthly;
        this.electricityPrice = electricityPrice;
        this.waterPrice = waterPrice;
        this.area = area;
        this.amenities = amenities;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getContractStart() { return contractStart; }
    public void setContractStart(String contractStart) { this.contractStart = contractStart; }
    public String getContractEnd() { return contractEnd; }
    public void setContractEnd(String contractEnd) { this.contractEnd = contractEnd; }
    public long getPriceMonthly() { return priceMonthly; }
    public void setPriceMonthly(long priceMonthly) { this.priceMonthly = priceMonthly; }
    public long getElectricityPrice() { return electricityPrice; }
    public void setElectricityPrice(long electricityPrice) { this.electricityPrice = electricityPrice; }
    public long getWaterPrice() { return waterPrice; }
    public void setWaterPrice(long waterPrice) { this.waterPrice = waterPrice; }
    public int getArea() { return area; }
    public void setArea(int area) { this.area = area; }
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public String getFormattedPrice() {
        return String.format("%,dđ", priceMonthly).replace(',', '.');
    }

    public String getFormattedElectricity() {
        return String.format("%,dđ/kWh", electricityPrice).replace(',', '.');
    }

    public String getFormattedWater() {
        return String.format("%,dđ/m³", waterPrice).replace(',', '.');
    }

    public String getContractPeriod() {
        if (contractStart == null || contractEnd == null) return "Chưa có HĐ";
        return contractStart + " - " + contractEnd;
    }
}
