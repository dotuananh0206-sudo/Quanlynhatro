package com.tromanager.model;

public class Asset {
    public enum AssetStatus {
        GOOD("Hoạt động tốt", "badge-green"),
        MAINTENANCE("Cần bảo trì", "badge-yellow"),
        BROKEN("Hỏng / Cần thay", "badge-red"),
        DISPOSED("Đã thanh lý / hủy", "badge-red");

        private final String label;
        private final String styleClass;

        AssetStatus(String label, String styleClass) {
            this.label = label;
            this.styleClass = styleClass;
        }

        public String getLabel() { return label; }
        public String getStyleClass() { return styleClass; }
    }

    private String code;
    private String name;
    private String roomName;
    private String category;
    private int quantity;
    private long value;
    private String installDate;
    private AssetStatus status;
    private String supplier = "";
    private String warranty = "";
    private String conditionNote = "";
    private String maintenanceNote = "";
    private String disposalDate = "";
    private String disposalReason = "";
    private long disposalValue;

    public Asset(String code, String name, String roomName, String category, int quantity, long value, String installDate, AssetStatus status) {
        this.code = code;
        this.name = name;
        this.roomName = roomName;
        this.category = category;
        this.quantity = quantity;
        this.value = value;
        this.installDate = installDate;
        this.status = status;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getRoomName() { return roomName; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public long getValue() { return value; }
    public String getInstallDate() { return installDate; }
    public AssetStatus getStatus() { return status; }
    public String getSupplier() { return supplier; }
    public String getWarranty() { return warranty; }
    public String getConditionNote() { return conditionNote; }
    public String getMaintenanceNote() { return maintenanceNote; }
    public String getDisposalDate() { return disposalDate; }
    public String getDisposalReason() { return disposalReason; }
    public long getDisposalValue() { return disposalValue; }
    public void setName(String name) { this.name = name; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public void setCategory(String category) { this.category = category; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setValue(long value) { this.value = value; }
    public void setInstallDate(String installDate) { this.installDate = installDate; }
    public void setStatus(AssetStatus status) { this.status = status; }
    public void setSupplier(String supplier) { this.supplier = value(supplier); }
    public void setWarranty(String warranty) { this.warranty = value(warranty); }
    public void setConditionNote(String conditionNote) { this.conditionNote = value(conditionNote); }
    public void setMaintenanceNote(String maintenanceNote) { this.maintenanceNote = value(maintenanceNote); }
    public void setDisposalDate(String disposalDate) { this.disposalDate = value(disposalDate); }
    public void setDisposalReason(String disposalReason) { this.disposalReason = value(disposalReason); }
    public void setDisposalValue(long disposalValue) { this.disposalValue = disposalValue; }

    private static String value(String text) { return text == null ? "" : text.trim(); }

    public String getFormattedValue() {
        return String.format("%,dđ", value).replace(',', '.');
    }
}