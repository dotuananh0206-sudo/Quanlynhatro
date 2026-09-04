package com.tromanager.model;

public enum RoomStatus {
    RENTED("Đang thuê", "status-rented"),
    VACANT("Trống", "status-vacant"),
    EXPIRING_SOON("Sắp hết HĐ", "status-expiring");

    private final String displayName;
    private final String styleClass;

    RoomStatus(String displayName, String styleClass) {
        this.displayName = displayName;
        this.styleClass = styleClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStyleClass() {
        return styleClass;
    }
}
