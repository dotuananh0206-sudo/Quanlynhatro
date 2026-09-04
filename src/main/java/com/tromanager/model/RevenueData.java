package com.tromanager.model;

public class RevenueData {
    private String month;
    private double revenueMillions; // e.g. 142.0

    public RevenueData(String month, double revenueMillions) {
        this.month = month;
        this.revenueMillions = revenueMillions;
    }

    public String getMonth() { return month; }
    public double getRevenueMillions() { return revenueMillions; }
    public String getFormattedValue() { return (int) revenueMillions + "M"; }
}
