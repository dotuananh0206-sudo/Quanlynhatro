package com.tromanager.controller;

import com.tromanager.service.AccountService;
import com.tromanager.service.SettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SettingsController {
    private final SettingsService settings = SettingsService.getInstance();

    @FXML private Button btnResetSettings;
    @FXML private Button btnSaveSettings;
    @FXML private Button btnChangePassword;

    @FXML private TextField txtBuildingName;
    @FXML private TextField txtOwnerName;
    @FXML private TextField txtAddress;
    @FXML private TextField txtPhone;

    @FXML private TextField txtElectricityPrice;
    @FXML private TextField txtWaterPrice;
    @FXML private TextField txtWifiPrice;
    @FXML private TextField txtGarbagePrice;
    @FXML private TextField txtParkingPrice;
    @FXML private ComboBox<String> cbWaterType;

    @FXML private ComboBox<String> cbBankName;
    @FXML private TextField txtBankNumber;
    @FXML private TextField txtBankHolder;

    @FXML private TextField txtBillingDay;
    @FXML private TextField txtIssueDay;
    @FXML private TextField txtDueDay;

    @FXML
    public void initialize() {
        cbWaterType.getItems().addAll("Theo chỉ số khối (m³)", "Theo đầu người (người/tháng)", "Trọn gói theo phòng");
        cbWaterType.setValue("Theo chỉ số khối (m³)");

        cbBankName.getItems().addAll("Techcombank", "Vietcombank", "MB Bank", "BIDV", "VietinBank", "ACB");
        cbBankName.setValue("Techcombank");

        loadSettings();

        btnSaveSettings.setOnAction(e -> {
            AccountService.getInstance().setDisplayName(txtOwnerName.getText());
            saveSettings();
            DialogHelper.showInfo("Lưu cấu hình", "Cập nhật thành công!", "Toàn bộ thông tin khu trọ, đơn giá dịch vụ điện nước và tài khoản thanh toán đã được lưu vào hệ thống.");
        });

        btnChangePassword.setOnAction(e -> DialogHelper.showChangePasswordDialog());

        btnResetSettings.setOnAction(e -> {
            txtElectricityPrice.setText("3500");
            txtWaterPrice.setText("25000");
            txtWifiPrice.setText("50000");
            txtGarbagePrice.setText("30000");
            txtParkingPrice.setText("100000");
            DialogHelper.showInfo("Khôi phục", "Khôi phục mặc định", "Đã đặt lại đơn giá điện nước về mức tiêu chuẩn.");
        });
    }

    private void loadSettings() {
        txtBuildingName.setText(settings.get("buildingName", txtBuildingName.getText()));
        txtOwnerName.setText(settings.get("ownerName", AccountService.getInstance().getDisplayName()));
        txtAddress.setText(settings.get("address", txtAddress.getText()));
        txtPhone.setText(settings.get("phone", txtPhone.getText()));
        txtElectricityPrice.setText(settings.get("electricityPrice", txtElectricityPrice.getText()));
        txtWaterPrice.setText(settings.get("waterPrice", txtWaterPrice.getText()));
        txtWifiPrice.setText(settings.get("wifiPrice", txtWifiPrice.getText()));
        txtGarbagePrice.setText(settings.get("garbagePrice", txtGarbagePrice.getText()));
        txtParkingPrice.setText(settings.get("parkingPrice", txtParkingPrice.getText()));
        cbWaterType.setValue(settings.get("waterType", cbWaterType.getValue()));
        cbBankName.setValue(settings.get("bankName", cbBankName.getValue()));
        txtBankNumber.setText(settings.get("bankNumber", txtBankNumber.getText()));
        txtBankHolder.setText(settings.get("bankHolder", txtBankHolder.getText()));
        txtBillingDay.setText(settings.get("billingDay", txtBillingDay.getText()));
        txtIssueDay.setText(settings.get("issueDay", txtIssueDay.getText()));
        txtDueDay.setText(settings.get("dueDay", txtDueDay.getText()));
    }

    private void saveSettings() {
        settings.put("buildingName", txtBuildingName.getText());
        settings.put("ownerName", txtOwnerName.getText());
        settings.put("address", txtAddress.getText());
        settings.put("phone", txtPhone.getText());
        settings.put("electricityPrice", txtElectricityPrice.getText());
        settings.put("waterPrice", txtWaterPrice.getText());
        settings.put("wifiPrice", txtWifiPrice.getText());
        settings.put("garbagePrice", txtGarbagePrice.getText());
        settings.put("parkingPrice", txtParkingPrice.getText());
        settings.put("waterType", cbWaterType.getValue());
        settings.put("bankName", cbBankName.getValue());
        settings.put("bankNumber", txtBankNumber.getText());
        settings.put("bankHolder", txtBankHolder.getText());
        settings.put("billingDay", txtBillingDay.getText());
        settings.put("issueDay", txtIssueDay.getText());
        settings.put("dueDay", txtDueDay.getText());
    }
}