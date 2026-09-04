package com.tromanager.controller;

import com.tromanager.model.*;
import com.tromanager.service.AccountService;
import com.tromanager.service.MockDataService;
import com.tromanager.service.SettingsService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser;
import javafx.print.PrinterJob;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class DialogHelper {

    public static boolean showLoginDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Đăng nhập TroManager");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox brand = new VBox(12);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPrefWidth(230);
        brand.setPadding(new Insets(30, 26, 30, 26));
        brand.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 10 0 0 10;");
        Label brandName = new Label("TroManager");
        brandName.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: white;");
        Label brandLine = new Label("QUẢN LÝ PHÒNG TRỌ");
        brandLine.setStyle("-fx-font-size: 10px; -fx-font-weight: 700; -fx-text-fill: #60a5fa; -fx-letter-spacing: 1px;");
        Label brandDescription = new Label("Quản lý phòng, khách thuê và hợp đồng trong một không gian thống nhất.");
        brandDescription.setWrapText(true);
        brandDescription.setStyle("-fx-font-size: 12px; -fx-text-fill: #cbd5e1; -fx-line-spacing: 4px;");
        Region brandSpacer = new Region();
        VBox.setVgrow(brandSpacer, Priority.ALWAYS);
        Label brandFooter = new Label("BẢNG ĐIỀU HÀNH CHỦ NHÀ");
        brandFooter.setStyle("-fx-font-size: 10px; -fx-font-weight: 700; -fx-text-fill: #64748b;");
        brand.getChildren().addAll(brandName, brandLine, brandDescription, brandSpacer, brandFooter);

        VBox form = new VBox(14);
        form.setPrefWidth(390);
        form.setPadding(new Insets(30, 32, 26, 32));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 0 10 10 0;");
        Label title = new Label("Chào mừng trở lại");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        Label subtitle = new Label("Đăng nhập để tiếp tục đến bảng điều hành");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        TextField username = field("Tên đăng nhập");
        username.setText(AccountService.getInstance().getUsername());
        username.getStyleClass().add("account-field");
        TextInputControl password = new PasswordField();
        password.setPromptText("Mật khẩu");
        password.getStyleClass().add("account-field");
        Label usernameLabel = new Label("TÊN ĐĂNG NHẬP");
        Label passwordLabel = new Label("MẬT KHẨU");
        usernameLabel.getStyleClass().add("account-field-label");
        passwordLabel.getStyleClass().add("account-field-label");
        Button forgotPassword = new Button("Quên mật khẩu?");
        forgotPassword.getStyleClass().add("account-link");
        forgotPassword.setOnAction(event -> showForgotPasswordDialog());
        HBox forgotRow = new HBox(forgotPassword);
        forgotRow.setAlignment(Pos.CENTER_RIGHT);
        form.getChildren().addAll(title, subtitle, new Region(), usernameLabel, username, passwordLabel, password, forgotRow);
        VBox.setMargin(form.getChildren().get(2), new Insets(2));

        HBox content = new HBox(0, brand, form);
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.18), 18, 0, 0, 6);");

        dialog.getDialogPane().setContent(content);
        ButtonType loginButton = new ButtonType("Đăng nhập", ButtonBar.ButtonData.OK_DONE);
        ButtonType exitButton = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButton, exitButton);
        dialog.getDialogPane().getStyleClass().add("account-dialog");
        dialog.getDialogPane().setPrefWidth(620);
        dialog.getDialogPane().setPrefHeight(390);
        styleDialog(dialog);
        dialog.setOnShown(event -> password.requestFocus());

        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == exitButton) return false;
            if (result.get() == loginButton && AccountService.getInstance().authenticate(username.getText(), password.getText())) return true;
            showInfo("Đăng nhập thất bại", "Thông tin không chính xác", "Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng thử lại.");
        }
    }

    private static void showForgotPasswordDialog() {
        TextInputDialog dialog = new TextInputDialog(AccountService.getInstance().getUsername());
        dialog.setTitle("Quên mật khẩu");
        dialog.setHeaderText("Khôi phục mật khẩu tài khoản");
        dialog.setContentText("Tên đăng nhập:");
        styleDialog(dialog);
        dialog.showAndWait().ifPresent(username -> {
            String temporaryPassword = AccountService.getInstance().resetPassword(username);
            if (temporaryPassword == null) {
                showInfo("Không tìm thấy tài khoản", "Khôi phục thất bại", "Tên đăng nhập không tồn tại.");
            } else {
                showInfo("Khôi phục thành công", "Mật khẩu tạm thời", "Mật khẩu mới của bạn là: " + temporaryPassword + "\nVui lòng đăng nhập và đổi lại mật khẩu trong mục Cài đặt.");
            }
        });
    }

    public static void showChangePasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cập nhật mật khẩu");
        dialog.setHeaderText("Đổi mật khẩu tài khoản " + AccountService.getInstance().getUsername());
        dialog.initModality(Modality.APPLICATION_MODAL);

        GridPane grid = formGrid();
        PasswordField current = new PasswordField();
        PasswordField next = new PasswordField();
        PasswordField confirmation = new PasswordField();
        current.setPromptText("Mật khẩu hiện tại");
        next.setPromptText("Mật khẩu mới (ít nhất 8 ký tự)");
        confirmation.setPromptText("Nhập lại mật khẩu mới");
        grid.add(new Label("Mật khẩu hiện tại *"), 0, 0);
        grid.add(current, 1, 0);
        grid.add(new Label("Mật khẩu mới *"), 0, 1);
        grid.add(next, 1, 1);
        grid.add(new Label("Nhập lại mật khẩu *"), 0, 2);
        grid.add(confirmation, 1, 2);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        prepareFormDialog(dialog, grid);

        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            if (AccountService.getInstance().updatePassword(current.getText(), next.getText(), confirmation.getText())) {
                showInfo("Cập nhật tài khoản", "Đổi mật khẩu thành công", "Mật khẩu tài khoản đã được cập nhật.");
                return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể đổi mật khẩu", "Mật khẩu hiện tại không đúng, mật khẩu mới phải có ít nhất 6 ký tự và hai lần nhập phải trùng nhau.");
        }
    }

    public static void showInfo(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        styleDialog(alert);
        alert.showAndWait();
    }

    public static void showRoomFilterDialog(Consumer<String> onStatusSelected) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Bộ lọc phòng");
        dialog.setHeaderText("Lọc phòng theo trạng thái");
        GridPane grid = formGrid();
        ComboBox<String> status = combo("Tất cả trạng thái", "Trống", "Đang thuê", "Sắp hết HĐ");
        addRows(grid, new String[]{"Trạng thái"}, status);
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        dialog.showAndWait().filter(ButtonType.OK::equals).ifPresent(result -> onStatusSelected.accept(status.getValue()));
    }

    public static void showAddQuickDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Thêm Nhanh Mới");
        dialog.setHeaderText("Chọn thao tác bạn muốn thêm nhanh vào hệ thống:");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));

        Button btnAddRoom = new Button("🏢 Thêm Phòng / Tầng mới");
        btnAddRoom.setMaxWidth(Double.MAX_VALUE);
        btnAddRoom.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-padding: 10 16; -fx-font-weight: 600; -fx-background-radius: 8;");
        btnAddRoom.setOnAction(e -> {
            dialog.close();
            Platform.runLater(DialogHelper::showAddRoomDialog);
        });

        Button btnAddTenant = new Button("👤 Thêm Khách Thuê mới");
        btnAddTenant.setMaxWidth(Double.MAX_VALUE);
        btnAddTenant.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-padding: 10 16; -fx-font-weight: 600; -fx-background-radius: 8;");
        btnAddTenant.setOnAction(e -> {
            dialog.close();
            Platform.runLater(DialogHelper::showAddTenantDialog);
        });

        Button btnAddContract = new Button("📝 Lập Hợp Đồng mới");
        btnAddContract.setMaxWidth(Double.MAX_VALUE);
        btnAddContract.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-padding: 10 16; -fx-font-weight: 600; -fx-background-radius: 8;");
        btnAddContract.setOnAction(e -> {
            dialog.close();
            Platform.runLater(DialogHelper::showAddContractDialog);
        });

        Button btnAddInvoice = new Button("💵 Tạo Hóa Đơn thu tiền");
        btnAddInvoice.setMaxWidth(Double.MAX_VALUE);
        btnAddInvoice.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-padding: 10 16; -fx-font-weight: 600; -fx-background-radius: 8;");
        btnAddInvoice.setOnAction(e -> {
            dialog.close();
            Platform.runLater(DialogHelper::showAddInvoiceDialog);
        });

        content.getChildren().addAll(btnAddRoom, btnAddTenant, btnAddContract, btnAddInvoice);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        styleDialog(dialog);
        dialog.showAndWait();
    }

    public static void showAddRoomDialog() {
        showAddRoomDialog(null);
    }

    public static void showAddRoomDialog(Consumer<Room> onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm Phòng Mới");
        dialog.setHeaderText("Nhập thông tin phòng trọ cần thêm vào hệ thống:");
        dialog.initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField txtRoomName = new TextField();
        txtRoomName.setPromptText("VD: P.401");

        ComboBox<String> cbFloor = new ComboBox<>();
        cbFloor.getItems().addAll("Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4");
        cbFloor.setValue("Tầng 1");

        TextField txtPrice = new TextField("3500000");
        txtPrice.setPromptText("Giá thuê hàng tháng (VNĐ)");

        TextField txtArea = new TextField("25");
        txtArea.setPromptText("Diện tích (m²)");

        TextField txtBuilding = new TextField("Khu trọ Hòa Bình");
        ComboBox<String> cbStatus = combo("Trống", "Đang thuê", "Sắp hết HĐ");
        TextField txtElectricity = new TextField("3500");
        TextField txtWater = new TextField("25000");
        TextField txtAmenities = new TextField("Máy lạnh, Wifi, WC riêng");

        grid.add(new Label("Mã / Tên phòng *:"), 0, 0);
        grid.add(txtRoomName, 1, 0);
        grid.add(new Label("Tầng:"), 0, 1);
        grid.add(cbFloor, 1, 1);
        grid.add(new Label("Khu trọ:"), 0, 2);
        grid.add(txtBuilding, 1, 2);
        grid.add(new Label("Trạng thái:"), 0, 3);
        grid.add(cbStatus, 1, 3);
        grid.add(new Label("Giá thuê (VNĐ):"), 0, 4);
        grid.add(txtPrice, 1, 4);
        grid.add(new Label("Điện (VNĐ/kWh):"), 0, 5);
        grid.add(txtElectricity, 1, 5);
        grid.add(new Label("Nước (VNĐ/m³):"), 0, 6);
        grid.add(txtWater, 1, 6);
        grid.add(new Label("Diện tích (m²):"), 0, 7);
        grid.add(txtArea, 1, 7);
        grid.add(new Label("Tiện nghi:"), 0, 8);
        grid.add(txtAmenities, 1, 8);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        prepareFormDialog(dialog, grid);
        styleDialog(dialog);
        dialog.setOnShown(event -> txtRoomName.requestFocus());

        while (true) {
            java.util.Optional<ButtonType> response = dialog.showAndWait();
            if (response.isEmpty() || response.get() == ButtonType.CANCEL) return;
            if (response.get() == ButtonType.OK && validNumbers(txtPrice, txtElectricity, txtWater, txtArea) && !txtRoomName.getText().trim().isEmpty()) {
                String room = txtRoomName.getText().trim();
                Room newRoom = new Room(String.valueOf(System.currentTimeMillis()), room, Integer.parseInt(cbFloor.getValue().replace("Tầng ", "")), txtBuilding.getText().trim(), roomStatus(cbStatus.getValue()), "Chưa có khách", "", "", "", Long.parseLong(txtPrice.getText().trim()), Long.parseLong(txtElectricity.getText().trim()), Long.parseLong(txtWater.getText().trim()), Integer.parseInt(txtArea.getText().trim()), Arrays.asList(txtAmenities.getText().split(",\s*")));
                MockDataService.getInstance().getRooms().add(newRoom);
                showInfo("Thêm phòng", "Đã lưu thành công", "Phòng " + room + " đã được thêm vào danh sách.");
                if (onSaved != null) onSaved.accept(newRoom);
                return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể lưu", "Vui lòng nhập tên phòng và các trường số hợp lệ. Form vẫn được giữ lại để bạn sửa.");
        }
    }

    public static void showAddTenantDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm khách thuê");
        dialog.setHeaderText("Nhập đầy đủ thông tin cá nhân và thuê phòng");
        GridPane grid = formGrid();
        TextField id = field("KT-NEW"), name = field("Họ và tên *"), phone = field("Số điện thoại *"), room = field("Phòng thuê *"), idCard = field("Số CCCD"), hometown = field("Quê quán"), start = field("Ngày nhận phòng (dd/MM/yyyy)"), deposit = field("Tiền đặt cọc (VNĐ)");
        CheckBox tempReg = new CheckBox("Đã đăng ký tạm trú");
        addRows(grid, new String[]{"Mã khách thuê", "Họ và tên", "Số điện thoại", "Phòng thuê", "CCCD", "Quê quán", "Ngày nhận phòng", "Tiền đặt cọc"}, id, name, phone, room, idCard, hometown, start, deposit);
        grid.add(tempReg, 1, 8);
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        dialog.setOnShown(event -> name.requestFocus());
        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            if (result.get() == ButtonType.OK && !name.getText().trim().isEmpty() && !phone.getText().trim().isEmpty() && !room.getText().trim().isEmpty() && isNumber(deposit)) {
                MockDataService.getInstance().getTenants().add(new Tenant(id.getText().trim(), name.getText().trim(), phone.getText().trim(), room.getText().trim(), idCard.getText().trim(), hometown.getText().trim(), start.getText().trim(), Long.parseLong(deposit.getText().trim()), tempReg.isSelected()));
                showInfo("Thêm khách thuê", "Đã lưu thành công", "Thông tin của " + name.getText().trim() + " đã được thêm.");
                return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể lưu", "Họ tên, số điện thoại, phòng thuê và tiền cọc là bắt buộc. Form vẫn được giữ lại để bạn sửa.");
        }
    }

    public static void showEditTenantDialog(Tenant tenant, Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chỉnh sửa thông tin khách thuê");
        dialog.setHeaderText("Cập nhật hồ sơ và thông tin liên hệ");
        GridPane grid = formGrid();
        TextField id = field(tenant.getId());
        id.setEditable(false);
        TextField name = field(tenant.getName());
        TextField phone = field(tenant.getPhone());
        TextField room = field(tenant.getRoomName());
        TextField idCard = field(tenant.getIdCard());
        TextField hometown = field(tenant.getHometown());
        TextField start = field(tenant.getStartDate());
        TextField deposit = field(String.valueOf(tenant.getDeposit()));
        TextField email = field(tenant.getEmail());
        TextField address = field(tenant.getAddress());
        TextField emergencyContact = field(tenant.getEmergencyContact());
        TextField emergencyPhone = field(tenant.getEmergencyPhone());
        CheckBox tempReg = new CheckBox("Đã đăng ký tạm trú");
        tempReg.setSelected(tenant.isTempReg());
        addRows(grid, new String[]{"Mã khách thuê", "Họ và tên *", "Số điện thoại *", "Phòng thuê *", "CCCD", "Quê quán", "Ngày nhận phòng", "Tiền đặt cọc (VNĐ)", "Email", "Địa chỉ thường trú", "Người liên hệ khẩn cấp", "SĐT liên hệ khẩn cấp"}, id, name, phone, room, idCard, hometown, start, deposit, email, address, emergencyContact, emergencyPhone);
        grid.add(tempReg, 1, 12);
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        dialog.setOnShown(event -> name.requestFocus());
        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            if (result.get() == ButtonType.OK && !name.getText().trim().isEmpty() && !phone.getText().trim().isEmpty() && !room.getText().trim().isEmpty() && validNumbers(deposit)) {
                Room oldRoom = dataService().findRoom(tenant.getRoomName());
                Room newRoom = dataService().findRoom(room.getText().trim());
                if (newRoom == null) {
                    showInfo("Phòng không tồn tại", "Không thể cập nhật", "Vui lòng chọn đúng phòng đang có trong danh sách.");
                    continue;
                }
                if (newRoom != oldRoom && newRoom.getStatus() != RoomStatus.VACANT) {
                    showInfo("Phòng đã có khách", "Không thể cập nhật", "Phòng " + newRoom.getName() + " đang được thuê hoặc chưa sẵn sàng.");
                    continue;
                }
                tenant.setName(name.getText());
                tenant.setPhone(phone.getText());
                tenant.setRoomName(room.getText());
                tenant.setIdCard(idCard.getText());
                tenant.setHometown(hometown.getText());
                tenant.setStartDate(start.getText());
                tenant.setDeposit(Long.parseLong(deposit.getText().trim()));
                tenant.setEmail(email.getText());
                tenant.setAddress(address.getText());
                tenant.setEmergencyContact(emergencyContact.getText());
                tenant.setEmergencyPhone(emergencyPhone.getText());
                tenant.setTempReg(tempReg.isSelected());
                if (oldRoom != null && oldRoom != newRoom) {
                    oldRoom.setStatus(RoomStatus.VACANT);
                    oldRoom.setTenantName("Chưa có khách");
                    oldRoom.setPhoneNumber("");
                }
                newRoom.setStatus(RoomStatus.RENTED);
                newRoom.setTenantName(tenant.getName());
                newRoom.setPhoneNumber(tenant.getPhone());
                showInfo("Chỉnh sửa khách thuê", "Đã lưu thành công", "Hồ sơ của " + tenant.getName() + " đã được cập nhật.");
                if (onSaved != null) onSaved.run();
                return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể lưu", "Họ tên, số điện thoại, phòng thuê và tiền đặt cọc là bắt buộc.");
        }
    }

    public static void showCheckoutDialog(Tenant tenant, Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thủ tục trả phòng");
        dialog.setHeaderText("Chốt công tơ, công nợ và bàn giao phòng");
        GridPane grid = formGrid();
        TextField checkoutDate = field(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        TextField electric = field("0");
        TextField water = field("0");
        TextField outstanding = field(String.valueOf(dataService().getPreviousDebt(tenant.getRoomName())));
        TextField depositDeduction = field("0");
        TextField refund = field(String.valueOf(tenant.getDeposit()));
        ComboBox<String> condition = combo("Tốt", "Cần sửa chữa", "Hư hỏng cần bồi thường");
        TextArea note = new TextArea();
        note.setPromptText("Ghi chú bàn giao, tài sản, chìa khóa...");
        note.setPrefRowCount(3);
        addRows(grid, new String[]{"Khách thuê", "Phòng", "Ngày trả phòng", "Chỉ số điện cuối", "Chỉ số nước cuối", "Công nợ cần thu (VNĐ)", "Khấu trừ tiền cọc (VNĐ)", "Tiền cọc hoàn lại (VNĐ)", "Tình trạng phòng", "Ghi chú bàn giao"}, field(tenant.getName()), field(tenant.getRoomName()), checkoutDate, electric, water, outstanding, depositDeduction, refund, condition, note);
        TextField tenantField = (TextField) grid.getChildren().get(1);
        TextField roomField = (TextField) grid.getChildren().get(3);
        tenantField.setEditable(false);
        roomField.setEditable(false);
        Runnable updateRefund = () -> {
            try {
                long value = Long.parseLong(depositDeduction.getText().trim());
                refund.setText(String.valueOf(Math.max(0, tenant.getDeposit() - value)));
            } catch (NumberFormatException ignored) {
                refund.setText("");
            }
        };
        depositDeduction.textProperty().addListener((obs, old, value) -> updateRefund.run());
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            try {
                long finalElectric = nonNegative(electric, "Chỉ số điện cuối");
                long finalWater = nonNegative(water, "Chỉ số nước cuối");
                long debt = nonNegative(outstanding, "Công nợ");
                long deduction = nonNegative(depositDeduction, "Khấu trừ tiền cọc");
                if (deduction > tenant.getDeposit()) throw new IllegalArgumentException("Khấu trừ tiền cọc không được lớn hơn tiền cọc hiện có.");
                if (debt > 0) showInfo("Xác nhận công nợ", "Khoản cần thu", "Công nợ khi trả phòng: " + money(debt));
                Room room = dataService().findRoom(tenant.getRoomName());
                if (room != null) {
                    room.setStatus(RoomStatus.VACANT);
                    room.setTenantName("Chưa có khách");
                    room.setPhoneNumber("");
                    room.setContractStart("");
                    room.setContractEnd("");
                }
                dataService().getContracts().stream().filter(contract -> contract.getRoomName().equals(tenant.getRoomName()) && contract.getTenantName().equals(tenant.getName())).findFirst().ifPresent(contract -> contract.setStatus(Contract.Status.EXPIRED));
                tenant.setCheckoutDate(checkoutDate.getText());
                tenant.setCheckoutNote("Chỉ số điện: " + finalElectric + ", nước: " + finalWater + "; Tình trạng: " + condition.getValue() + "; " + note.getText());
                showInfo("Trả phòng", "Đã hoàn tất thủ tục", "Phòng " + tenant.getRoomName() + " đã được chuyển sang trạng thái trống. Tiền cọc hoàn lại: " + money(tenant.getDeposit() - deduction));
                if (onSaved != null) onSaved.run();
                return;
            } catch (IllegalArgumentException ex) {
                showInfo("Thông tin chưa hợp lệ", "Không thể hoàn tất", ex.getMessage());
            }
        }
    }

    public static void showAddContractDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Lập hợp đồng thuê");
        dialog.setHeaderText("Nhập thông tin hợp đồng và thời hạn thuê");
        GridPane grid = formGrid();
        TextField code = field("Mã hợp đồng *"), room = field("Phòng thuê *"), tenant = field("Khách thuê *"), phone = field("SĐT Bên B"), tenantAddress = field("Địa chỉ Bên B"), tenantIdCard = field("CCCD Bên B"), ownerName = field("Tên Bên A"), ownerAddress = field("Địa chỉ Bên A"), ownerPhone = field("SĐT Bên A"), start = field("Ngày bắt đầu (dd/MM/yyyy)"), expiry = field("Ngày hết hạn (dd/MM/yyyy)"), rent = field("Giá thuê (VNĐ)"), deposit = field("Tiền đặt cọc (VNĐ)"), days = field("Số ngày còn lại");
        ComboBox<String> status = combo("Đang hiệu lực", "Sắp hết hạn", "Đã hết hạn");
        ownerName.setText(SettingsService.getInstance().get("buildingName", "Khu trọ Hòa Bình"));
        ownerAddress.setText(SettingsService.getInstance().get("address", ""));
        ownerPhone.setText(SettingsService.getInstance().get("phone", ""));
        addRows(grid, new String[]{"Mã hợp đồng", "Phòng thuê", "Khách thuê", "SĐT Bên B", "Địa chỉ Bên B", "CCCD Bên B", "Tên Bên A", "Địa chỉ Bên A", "SĐT Bên A", "Ngày bắt đầu", "Ngày hết hạn", "Giá thuê", "Tiền đặt cọc", "Còn lại (ngày)", "Trạng thái"}, code, room, tenant, phone, tenantAddress, tenantIdCard, ownerName, ownerAddress, ownerPhone, start, expiry, rent, deposit, days, status);
        dialog.getDialogPane().setContent(grid); addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        dialog.setOnShown(event -> code.requestFocus());
        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;

            if (result.get() == ButtonType.OK && !code.getText().trim().isEmpty() && !room.getText().trim().isEmpty() && !tenant.getText().trim().isEmpty() && validNumbers(rent, deposit, days)) {
                Contract contract = new Contract(code.getText().trim(), room.getText().trim(), tenant.getText().trim(), phone.getText().trim(), start.getText().trim(), expiry.getText().trim(), Long.parseLong(rent.getText().trim()), Long.parseLong(deposit.getText().trim()), Integer.parseInt(days.getText().trim()), contractStatus(status.getValue()));
                contract.setOwnerName(ownerName.getText());
                contract.setOwnerAddress(ownerAddress.getText());
                contract.setOwnerPhone(ownerPhone.getText());
                contract.setTenantAddress(tenantAddress.getText());
                contract.setTenantIdCard(tenantIdCard.getText());
                MockDataService.getInstance().getContracts().add(contract);
                showContractPreview(contract);
                return;
            }

            showInfo("Dữ liệu chưa hợp lệ", "Không thể lưu", "Vui lòng kiểm tra các trường bắt buộc và nhập số hợp lệ. Form vẫn được giữ lại để bạn sửa.");
        }
    }

    public static void showContractPreview(Contract contract) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Hợp đồng thuê trọ - " + contract.getCode());
        dialog.setHeaderText("Bản hợp đồng theo mẫu điều khoản chuẩn");
        dialog.initModality(Modality.APPLICATION_MODAL);

        TextArea document = new TextArea(contractDocument(contract));
        document.setEditable(false);
        document.setWrapText(true);
        document.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 13px; -fx-text-fill: #1e293b;");
        document.setPrefRowCount(28);
        document.setPrefColumnCount(76);

        dialog.getDialogPane().setContent(document);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefWidth(760);
        dialog.getDialogPane().setPrefHeight(640);
        styleDialog(dialog);
        dialog.showAndWait();
    }

    public static void showEditContractDialog(Contract contract, Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chỉnh sửa hợp đồng " + contract.getCode());
        dialog.setHeaderText("Cập nhật đầy đủ thông tin bên thuê, thời hạn và điều khoản tài chính");
        GridPane grid = formGrid();
        TextField code = field(contract.getCode());
        TextField room = field(contract.getRoomName());
        TextField tenant = field(contract.getTenantName());
        TextField phone = field(contract.getPhone());
        TextField tenantAddress = field(contract.getTenantAddress());
        TextField tenantIdCard = field(contract.getTenantIdCard());
        TextField ownerName = field(contract.getOwnerName());
        TextField ownerAddress = field(contract.getOwnerAddress());
        TextField ownerPhone = field(contract.getOwnerPhone());
        TextField start = field(contract.getStartDate());
        TextField expiry = field(contract.getExpiryDate());
        TextField rent = field(String.valueOf(contract.getRentPrice()));
        TextField deposit = field(String.valueOf(contract.getDeposit()));
        TextField days = field(String.valueOf(contract.getRemainingDays()));
        ComboBox<String> status = combo("Đang hiệu lực", "Sắp hết hạn", "Đã hết hạn");
        status.setValue(contract.getStatus().getLabel());
        addRows(grid, new String[]{"Mã hợp đồng", "Phòng thuê", "Khách thuê", "SĐT Bên B", "Địa chỉ Bên B", "CCCD Bên B", "Tên Bên A", "Địa chỉ Bên A", "SĐT Bên A", "Ngày bắt đầu", "Ngày hết hạn", "Giá thuê (VNĐ)", "Tiền đặt cọc (VNĐ)", "Còn lại (ngày)", "Trạng thái"}, code, room, tenant, phone, tenantAddress, tenantIdCard, ownerName, ownerAddress, ownerPhone, start, expiry, rent, deposit, days, status);
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        dialog.setOnShown(event -> tenant.requestFocus());
        while (true) {
            java.util.Optional<ButtonType> response = dialog.showAndWait();
            if (response.isEmpty() || response.get() == ButtonType.CANCEL) return;
            if (response.get() == ButtonType.OK && !code.getText().trim().isEmpty() && !room.getText().trim().isEmpty() && !tenant.getText().trim().isEmpty() && validNumbers(rent, deposit, days)) {
                contract.setCode(code.getText().trim());
                contract.setRoomName(room.getText().trim());
                contract.setTenantName(tenant.getText().trim());
                contract.setPhone(phone.getText().trim());
                contract.setTenantAddress(tenantAddress.getText());
                contract.setTenantIdCard(tenantIdCard.getText());
                contract.setOwnerName(ownerName.getText());
                contract.setOwnerAddress(ownerAddress.getText());
                contract.setOwnerPhone(ownerPhone.getText());
                contract.setStartDate(start.getText().trim());
                contract.setExpiryDate(expiry.getText().trim());
                contract.setRentPrice(Long.parseLong(rent.getText().trim()));
                contract.setDeposit(Long.parseLong(deposit.getText().trim()));
                contract.setRemainingDays(Integer.parseInt(days.getText().trim()));
                contract.setStatus(contractStatus(status.getValue()));
                showInfo("Chỉnh sửa hợp đồng", "Đã lưu thành công", "Hợp đồng " + contract.getCode() + " đã được cập nhật.");
                if (onSaved != null) onSaved.run();
                return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể lưu", "Mã, phòng, khách thuê và các trường số là bắt buộc. Form vẫn được giữ lại để bạn sửa.");
        }
    }

    private static String contractDocument(Contract contract) {
        return "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\n"
            + "Độc lập - Tự do - Hạnh phúc\n"
            + "--------------------\n\n"
            + "HỢP ĐỒNG THUÊ PHÒNG TRỌ\n"
            + "Số: " + contract.getCode() + "\n\n"
            + "Căn cứ Bộ luật Dân sự hiện hành và sự tự nguyện thỏa thuận của các bên;\n"
            + "Hôm nay, ngày " + contract.getStartDate() + ", tại Khu trọ Hòa Bình, chúng tôi gồm:\n\n"
            + "BÊN CHO THUÊ (BÊN A)\n"
            + "Họ và tên/Đại diện: " + valueOrBlank(contract.getOwnerName()) + "\n"
            + "Địa chỉ: " + valueOrBlank(contract.getOwnerAddress()) + "\n"
            + "Số điện thoại: " + valueOrBlank(contract.getOwnerPhone()) + "\n\n"
            + "BÊN THUÊ (BÊN B)\n"
            + "Họ và tên: " + contract.getTenantName() + "\n"
            + "Số điện thoại: " + valueOrBlank(contract.getPhone()) + "\n"
            + "Số CCCD: " + valueOrBlank(contract.getTenantIdCard()) + "\n"
            + "Địa chỉ thường trú: " + valueOrBlank(contract.getTenantAddress()) + "\n\n"
            + "Sau khi bàn bạc, hai bên thống nhất ký hợp đồng với các điều khoản sau:\n\n"
                + "ĐIỀU 1. ĐỐI TƯỢNG VÀ MỤC ĐÍCH THUÊ\n"
            + "1.1. Bên A đồng ý cho Bên B thuê phòng " + contract.getRoomName() + " tại Khu trọ Hòa Bình để ở.\n"
            + "1.2. Bên B không được tự ý chuyển nhượng, cho thuê lại hoặc sử dụng phòng trái pháp luật.\n\n"
                + "ĐIỀU 2. THỜI HẠN THUÊ\n"
            + "2.1. Thời hạn thuê từ ngày " + contract.getStartDate() + " đến hết ngày " + contract.getExpiryDate() + ".\n"
            + "2.2. Việc gia hạn phải được hai bên thỏa thuận và lập thành văn bản trước khi hợp đồng hết hạn.\n\n"
                + "ĐIỀU 3. GIÁ THUÊ VÀ TIỀN ĐẶT CỌC\n"
            + "3.1. Giá thuê phòng: " + money(contract.getRentPrice()) + " / tháng, thanh toán vào đầu mỗi tháng.\n"
            + "3.2. Tiền đặt cọc: " + money(contract.getDeposit()) + ". Tiền cọc được hoàn trả khi kết thúc hợp đồng sau khi đối soát công nợ và tình trạng tài sản.\n"
            + "3.3. Bên B chịu trách nhiệm thanh toán đúng hạn; trường hợp chậm thanh toán, hai bên thực hiện theo thỏa thuận và nội quy khu trọ.\n\n"
                + "ĐIỀU 4. CHI PHÍ SỬ DỤNG\n"
            + "Bên B thanh toán riêng các khoản điện, nước, internet, vệ sinh và dịch vụ phát sinh theo bảng giá/thông báo của khu trọ tại từng thời điểm.\n"
            + "Chỉ số điện, nước và tài sản bàn giao được đối soát khi nhận phòng và khi thanh lý hợp đồng.\n\n"
                + "ĐIỀU 5. QUYỀN VÀ NGHĨA VỤ\n"
            + "5.1. Bên A bàn giao phòng đúng thỏa thuận, bảo đảm quyền sử dụng ổn định và sửa chữa các hư hỏng thuộc trách nhiệm của Bên A.\n"
            + "5.2. Bên B giữ gìn tài sản, tuân thủ nội quy, bảo đảm an ninh trật tự, thanh toán đúng hạn và bồi thường hư hỏng do lỗi của mình.\n"
            + "5.3. Bên B có trách nhiệm khai báo tạm trú và không lưu trú người khác dài ngày khi chưa được Bên A đồng ý.\n\n"
                + "ĐIỀU 6. CHẤM DỨT HỢP ĐỒNG\n"
            + "Hợp đồng chấm dứt khi hết hạn, theo thỏa thuận bằng văn bản hoặc khi một bên vi phạm nghiêm trọng nghĩa vụ. Bên muốn chấm dứt trước hạn phải thông báo cho bên còn lại trước ít nhất 30 ngày, trừ trường hợp pháp luật hoặc thỏa thuận có quy định khác.\n\n"
                + "ĐIỀU 7. ĐIỀU KHOẢN CHUNG\n"
            + "Hai bên cam kết thực hiện đúng các điều khoản trên. Mọi sửa đổi, bổ sung chỉ có giá trị khi được hai bên thống nhất bằng văn bản. Hợp đồng được lập thành 02 bản có giá trị như nhau, mỗi bên giữ 01 bản.\n\n"
            + "                    ĐẠI DIỆN CÁC BÊN\n"
            + "\n"
            + "          BÊN CHO THUÊ (BÊN A)                         BÊN THUÊ (BÊN B)\n"
            + "          (Ký, ghi rõ họ tên)                           (Ký, ghi rõ họ tên)\n\n"
            + "\n"
            + "Lưu ý: Mẫu hợp đồng cần được các bên rà soát, bổ sung thông tin còn thiếu và ký xác nhận trước khi sử dụng.";
    }

    private static String valueOrBlank(String value) {
        return value == null || value.trim().isEmpty() ? "................................" : value.trim();
    }

    private static String money(long value) {
        return String.format("%,d VNĐ", value).replace(',', '.');
    }

    public static void showAddInvoiceDialog() {
        showInvoiceDialog(null, null);
    }

    public static void showEditRoomDialog(Room room, Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chỉnh sửa phòng " + room.getName());
        dialog.setHeaderText("Cập nhật vị trí, trạng thái, giá thuê, dịch vụ và tiện nghi phòng");
        GridPane grid = formGrid();
        TextField name = field(room.getName());
        ComboBox<String> floor = combo("Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4");
        floor.setValue("Tầng " + room.getFloor());
        TextField building = field(room.getBuildingName());
        ComboBox<String> status = combo("Trống", "Đang thuê", "Sắp hết HĐ");
        status.setValue(room.getStatus().getDisplayName());
        TextField rent = field(String.valueOf(room.getPriceMonthly()));
        TextField electricity = field(String.valueOf(room.getElectricityPrice()));
        TextField water = field(String.valueOf(room.getWaterPrice()));
        TextField area = field(String.valueOf(room.getArea()));
        TextField amenities = field(String.join(", ", room.getAmenities()));
        addRows(grid, new String[]{"Mã / Tên phòng", "Tầng", "Khu trọ", "Trạng thái", "Giá thuê (VNĐ)", "Điện (VNĐ/kWh)", "Nước (VNĐ/m³)", "Diện tích (m²)", "Tiện nghi"}, name, floor, building, status, rent, electricity, water, area, amenities);
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        dialog.setOnShown(event -> name.requestFocus());
        while (true) {
            java.util.Optional<ButtonType> response = dialog.showAndWait();
            if (response.isEmpty() || response.get() == ButtonType.CANCEL) return;
            if (response.get() == ButtonType.OK && !name.getText().trim().isEmpty() && validNumbers(rent, electricity, water, area)) {
                room.setName(name.getText().trim());
                room.setFloor(Integer.parseInt(floor.getValue().replace("Tầng ", "")));
                room.setBuildingName(building.getText().trim());
                room.setStatus(roomStatus(status.getValue()));
                room.setPriceMonthly(Long.parseLong(rent.getText().trim()));
                room.setElectricityPrice(Long.parseLong(electricity.getText().trim()));
                room.setWaterPrice(Long.parseLong(water.getText().trim()));
                room.setArea(Integer.parseInt(area.getText().trim()));
                room.setAmenities(Arrays.stream(amenities.getText().split(",\s*")).map(String::trim).filter(value -> !value.isEmpty()).toList());
                showInfo("Chỉnh sửa phòng", "Đã lưu thành công", "Thông tin phòng " + room.getName() + " đã được cập nhật.");
                if (onSaved != null) onSaved.run();
                return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể lưu", "Tên phòng và các trường số là bắt buộc. Form vẫn được giữ lại để bạn sửa.");
        }
    }

    public static void showInvoiceDialog(Room room, Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tạo phiếu thu tiền phòng");
        dialog.setHeaderText("Nhập chỉ số cuối kỳ và các khoản điều chỉnh");
        GridPane grid = formGrid();
        TextField code = field("Mã hóa đơn *");
        ComboBox<String> roomChoice = new ComboBox<>();
        roomChoice.setMaxWidth(Double.MAX_VALUE);
        MockDataService.getInstance().getRooms().forEach(item -> roomChoice.getItems().add(item.getName()));
        if (room != null) roomChoice.setValue(room.getName());
        else if (!roomChoice.getItems().isEmpty()) roomChoice.setValue(roomChoice.getItems().get(0));
        TextField tenant = field(room == null ? "" : room.getTenantName());
        tenant.setEditable(false);
        String currentPeriod = "Tháng " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        TextField period = field(currentPeriod);
        TextField rent = field(room == null ? "0" : String.valueOf(room.getPriceMonthly()));
        TextField oldElectric = field("0");
        TextField newElectric = field("0");
        TextField electric = field("0");
        electric.setEditable(false);
        TextField oldWater = field("0");
        TextField newWater = field("0");
        TextField water = field("0");
        water.setEditable(false);
        TextField cleaning = field("0");
        TextField internet = field("0");
        TextField discount = field("0");
        TextField previousDebt = field("0");
        TextArea note = new TextArea();
        note.setPrefRowCount(2);
        addRows(grid, new String[]{"Mã phiếu thu", "Phòng", "Khách thuê", "Kỳ hóa đơn", "Tiền nhà (VNĐ)", "Chỉ số điện đầu", "Chỉ số điện cuối", "Tiền điện (VNĐ)", "Chỉ số nước đầu", "Chỉ số nước cuối", "Tiền nước (VNĐ)", "Phí wifi (VNĐ)", "Phí vệ sinh (VNĐ)", "Giảm giá (VNĐ)", "Nợ tháng trước (VNĐ)", "Ghi chú"}, code, roomChoice, tenant, period, rent, oldElectric, newElectric, electric, oldWater, newWater, water, internet, cleaning, discount, previousDebt, note);
        Runnable recalculate = () -> {
            Room selected = findRoom(roomChoice.getValue());
            if (selected == null) return;
            tenant.setText(selected.getTenantName());
            if (!oldElectric.isFocused()) oldElectric.setText(String.valueOf(dataService().getPreviousElectricReading(selected.getName())));
            if (!oldWater.isFocused()) oldWater.setText(String.valueOf(dataService().getPreviousWaterReading(selected.getName())));
            if (!rent.isFocused()) rent.setText(String.valueOf(selected.getPriceMonthly()));
            if (!previousDebt.isFocused()) previousDebt.setText(String.valueOf(dataService().getPreviousDebt(selected.getName())));
            try {
                long electricUsage = Long.parseLong(newElectric.getText().trim()) - Long.parseLong(oldElectric.getText().trim());
                long waterUsage = Long.parseLong(newWater.getText().trim()) - Long.parseLong(oldWater.getText().trim());
                long electricCost = electricUsage * selected.getElectricityPrice();
                long waterCost = waterUsage * selected.getWaterPrice();
                electric.setText(String.valueOf(electricCost));
                water.setText(String.valueOf(waterCost));
            } catch (NumberFormatException ignored) {
                electric.setText("");
                water.setText("");
            }
        };
        roomChoice.setOnAction(event -> recalculate.run());
        newElectric.textProperty().addListener((obs, old, value) -> recalculate.run());
        newWater.textProperty().addListener((obs, old, value) -> recalculate.run());
        rent.textProperty().addListener((obs, old, value) -> recalculate.run());
        recalculate.run();
        dialog.getDialogPane().setContent(grid);
        ButtonType save = new ButtonType("Lưu hóa đơn", ButtonBar.ButtonData.OK_DONE);
        ButtonType preview = new ButtonType("Xem trước", ButtonBar.ButtonData.HELP);
        ButtonType print = new ButtonType("In hóa đơn", ButtonBar.ButtonData.HELP);
        ButtonType pdf = new ButtonType("Xuất PDF", ButtonBar.ButtonData.HELP);
        dialog.getDialogPane().getButtonTypes().addAll(save, preview, print, pdf, ButtonType.CANCEL);
        prepareFormDialog(dialog, grid);
        dialog.setOnShown(event -> code.requestFocus());
        while (true) {
            java.util.Optional<ButtonType> response = dialog.showAndWait();
            if (response.isEmpty() || response.get() == ButtonType.CANCEL) return;
            Invoice invoice = buildInvoice(code, roomChoice, tenant, period, rent, electric, water, oldElectric, newElectric, oldWater, newWater, cleaning, internet, discount, previousDebt, note);
            if (invoice == null) continue;
            if (response.get() == save) {
                if (dataService().findInvoice(invoice.getRoomName(), invoice.getMonthPeriod()) != null) {
                    showInfo("Trùng kỳ hóa đơn", "Không thể lưu", "Phòng này đã có hóa đơn cho kỳ " + invoice.getMonthPeriod() + ".");
                    continue;
                }
                dataService().getInvoices().add(invoice);
                showInfo("Tạo hóa đơn", "Đã lưu thành công", "Phiếu thu " + invoice.getCode() + " đã được lưu.");
                if (onSaved != null) onSaved.run();
                return;
            }
            if (response.get() == preview) showInvoicePreview(invoice);
            if (response.get() == print) printInvoice(invoice);
            if (response.get() == pdf) exportInvoicePdf(invoice);
        }
    }

    public static void showInvoicePreview(Invoice invoice) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Phiếu thu tiền phòng - " + invoice.getCode());
        dialog.setHeaderText("Phiếu thu tiền phòng " + invoice.getMonthPeriod());
        TextArea document = new TextArea(invoiceDocument(invoice));
        document.setEditable(false);
        document.setWrapText(false);
        document.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px;");
        document.setPrefRowCount(28);
        document.setPrefColumnCount(95);
        dialog.getDialogPane().setContent(document);
        ButtonType print = new ButtonType("In hóa đơn", ButtonBar.ButtonData.OK_DONE);
        ButtonType pdf = new ButtonType("Xuất PDF", ButtonBar.ButtonData.HELP);
        dialog.getDialogPane().getButtonTypes().addAll(print, pdf, ButtonType.CLOSE);
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefWidth(850);
        dialog.getDialogPane().setPrefHeight(650);
        styleDialog(dialog);
        dialog.showAndWait().ifPresent(result -> {
            if (result == print) printInvoice(invoice);
            if (result == pdf) exportInvoicePdf(invoice);
        });
    }

    public static void showPaymentDialog(Invoice invoice, Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ghi nhận thanh toán " + invoice.getCode());
        GridPane grid = formGrid();
        TextField amount = field(String.valueOf(Math.max(0, invoice.getTotalAmount() - invoice.getPaidAmount())));
        TextField date = field(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        ComboBox<String> method = combo("Tiền mặt", "Chuyển khoản");
        TextArea note = new TextArea();
        note.setPrefRowCount(2);
        addRows(grid, new String[]{"Số tiền thanh toán (VNĐ)", "Ngày thanh toán", "Phương thức", "Ghi chú"}, amount, date, method, note);
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            try {
                long paid = nonNegative(amount, "Số tiền thanh toán");
                long totalPaid = invoice.getPaidAmount() + paid;
                if (totalPaid > invoice.getTotalAmount()) throw new IllegalArgumentException("Số tiền thanh toán không được lớn hơn số tiền còn phải thu.");
                invoice.setPaidAmount(totalPaid);
                invoice.setPaymentDate(date.getText().trim());
                invoice.setPaymentMethod(method.getValue());
                invoice.setPaymentNote(note.getText());
                invoice.setStatus(totalPaid == invoice.getTotalAmount() ? Invoice.InvoiceStatus.PAID : Invoice.InvoiceStatus.PARTIAL);
                if (onSaved != null) onSaved.run();
                return;
            } catch (IllegalArgumentException ex) {
                showInfo("Thanh toán chưa hợp lệ", "Không thể ghi nhận", ex.getMessage());
            }
        }
    }

    private static String invoiceDocument(Invoice invoice) {
        String line = "--------------------------------------------------------------------------\n";
        return "Địa chỉ: Khu trọ Hòa Bình\n\n"
            + "                    PHIẾU THU TIỀN PHÒNG\n\n"
            + "Tên khách thuê: " + invoice.getTenantName() + "        Phòng số: " + invoice.getRoomName() + "\n"
            + "Ngày lập phiếu: " + invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
            + "Mã phiếu: " + invoice.getCode() + "\n\n"
            + line
            + String.format("%-4s %-25s %-10s %-10s %-8s %-14s %-14s %s%n", "STT", "Nội dung", "Chỉ số đầu", "Chỉ số cuối", "Số lượng", "Đơn giá", "Thành tiền", "Ghi chú")
            + line
            + String.format("%-4s %-25s %-10s %-10s %-8s %-14s %-14s %s%n", "1", "Tiền nhà", "", "", "1", money(invoice.getRoomAmount()), money(invoice.getRoomAmount()), "")
            + String.format("%-4s %-25s %-10s %-10s %-8s %-14s %-14s %s%n", "2", "Tiền điện", invoice.getOldElectricReading(), invoice.getNewElectricReading(), invoice.getNewElectricReading() - invoice.getOldElectricReading(), "", money(invoice.getElectricAmount()), "")
            + String.format("%-4s %-25s %-10s %-10s %-8s %-14s %-14s %s%n", "3", "Tiền nước", invoice.getOldWaterReading(), invoice.getNewWaterReading(), invoice.getNewWaterReading() - invoice.getOldWaterReading(), "", money(invoice.getWaterAmount()), "")
            + String.format("%-4s %-25s %-10s %-10s %-8s %-14s %-14s %s%n", "4", "Tiền wifi", "", "", "1", money(invoice.getInternetAmount()), money(invoice.getInternetAmount()), "")
            + String.format("%-4s %-25s %-10s %-10s %-8s %-14s %-14s %s%n", "5", "Vệ sinh", "", "", "1", money(invoice.getCleaningAmount()), money(invoice.getCleaningAmount()), "")
            + line
            + String.format("Tạm tính:                         %s%nGiảm giá:                         %s%nNợ tháng trước:                   %s%nTổng thanh toán:                  %s%n%n", money(invoice.getSubtotal()), money(invoice.getDiscount()), money(invoice.getPreviousDebt()), money(invoice.getTotalAmount()))
            + "                    Người lập phiếu              Người thuê\n\n"
            + "                    (Ký và ghi rõ họ tên)        (Ký và ghi rõ họ tên)\n";
    }

    private static MockDataService dataService() { return MockDataService.getInstance(); }

    private static Invoice buildInvoice(TextField code, ComboBox<String> roomChoice, TextField tenant, TextField period,
                                        TextField rent, TextField electric, TextField water, TextField oldElectric,
                                        TextField newElectric, TextField oldWater, TextField newWater, TextField cleaning,
                                        TextField internet, TextField discount, TextField previousDebt, TextArea note) {
        try {
            if (code.getText().isBlank() || roomChoice.getValue() == null || tenant.getText().isBlank()) throw new IllegalArgumentException("Mã phiếu, phòng và khách thuê là bắt buộc.");
            long rentValue = positive(rent, "Đơn giá tiền nhà");
            long oldE = nonNegative(oldElectric, "Chỉ số điện đầu"), newE = nonNegative(newElectric, "Chỉ số điện cuối");
            long oldW = nonNegative(oldWater, "Chỉ số nước đầu"), newW = nonNegative(newWater, "Chỉ số nước cuối");
            if (newE < oldE || newW < oldW) throw new IllegalArgumentException("Chỉ số cuối phải lớn hơn hoặc bằng chỉ số đầu.");
            long cleaningValue = nonNegative(cleaning, "Phí vệ sinh"), internetValue = nonNegative(internet, "Phí wifi");
            long discountValue = nonNegative(discount, "Giảm giá"), debtValue = nonNegative(previousDebt, "Nợ tháng trước");
            Room selected = findRoom(roomChoice.getValue());
            long electricAmount = (newE - oldE) * selected.getElectricityPrice();
            long waterAmount = (newW - oldW) * selected.getWaterPrice();
            Invoice invoice = new Invoice(code.getText().trim(), selected.getName(), tenant.getText().trim(), period.getText().trim(), rentValue,
                    electricAmount, waterAmount, cleaningValue + internetValue, "", Invoice.InvoiceStatus.UNPAID,
                    oldE, newE, oldW, newW, 1, cleaningValue, 0, internetValue);
            invoice.setDiscount(discountValue);
            invoice.setPreviousDebt(debtValue);
            invoice.setNote(note.getText());
            if (invoice.getDiscount() > invoice.getSubtotal()) throw new IllegalArgumentException("Giảm giá không được lớn hơn tạm tính.");
            invoice.rebuildDetails();
            return invoice;
        } catch (IllegalArgumentException ex) {
            showInfo("Dữ liệu chưa hợp lệ", "Không thể tạo phiếu thu", ex.getMessage());
            return null;
        }
    }

    private static long nonNegative(TextInputControl field, String label) {
        long value = Long.parseLong(field.getText().trim());
        if (value < 0) throw new IllegalArgumentException(label + " không được âm.");
        return value;
    }

    private static long positive(TextInputControl field, String label) {
        long value = nonNegative(field, label);
        if (value == 0) throw new IllegalArgumentException(label + " phải lớn hơn 0.");
        return value;
    }

    private static void printInvoice(Invoice invoice) {
        Text text = new Text(invoiceDocument(invoice));
        text.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 10px;");
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            if (job.printPage(text)) job.endJob(); else job.cancelJob();
        }
    }

    private static void exportInvoicePdf(Invoice invoice) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Xuất phiếu thu PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
        File file = chooser.showSaveDialog(null);
        if (file == null) return;
        try (org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument()) {
            org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage(org.apache.pdfbox.pdmodel.common.PDRectangle.A4);
            document.addPage(page);
            org.apache.pdfbox.pdmodel.font.PDFont font = org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;
            File arial = new File("C:\\Windows\\Fonts\\arial.ttf");
            if (arial.isFile()) font = org.apache.pdfbox.pdmodel.font.PDType0Font.load(document, arial);
            try (org.apache.pdfbox.pdmodel.PDPageContentStream stream = new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page)) {
                stream.beginText();
                stream.setFont(font, 9);
                stream.setLeading(12);
                stream.newLineAtOffset(36, 806);
                for (String line : invoiceDocument(invoice).split("\\R", -1)) {
                    stream.showText(line.length() > 115 ? line.substring(0, 115) : line);
                    stream.newLine();
                }
                stream.endText();
            }
            document.save(file);
            showInfo("Xuất PDF", "Đã xuất phiếu thu", "Tệp đã được tạo tại: " + file.getAbsolutePath());
        } catch (IOException ex) {
            showInfo("Xuất PDF thất bại", "Không thể tạo tệp", ex.getMessage());
        }
    }

    private static Room findRoom(String name) {
        return MockDataService.getInstance().getRooms().stream().filter(room -> room.getName().equals(name)).findFirst().orElse(null);
    }

    private static boolean readingsAreValid(TextInputControl oldElectric, TextInputControl newElectric, TextInputControl oldWater, TextInputControl newWater) {
        return Long.parseLong(newElectric.getText().trim()) >= Long.parseLong(oldElectric.getText().trim())
                && Long.parseLong(newWater.getText().trim()) >= Long.parseLong(oldWater.getText().trim());
    }

    public static void showAddTaskDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm công việc mới");
        dialog.setHeaderText("Tạo yêu cầu và phân công xử lý");
        GridPane grid = formGrid();
        TextField title = field("Tên công việc *");
        TextField location = field("Vị trí/phòng thực hiện *");
        TextField due = field("Hạn xử lý (dd/MM/yyyy)");
        TextField assignee = field("Người phụ trách");
        TextArea description = new TextArea();
        description.setPromptText("Mô tả chi tiết, vật tư cần dùng hoặc yêu cầu của khách...");
        description.setPrefRowCount(3);
        ComboBox<String> priority = combo("Khẩn cấp", "Cao", "Trung bình");
        ComboBox<String> status = combo("Cần xử lý", "Đang xử lý", "Đã hoàn thành");
        addRows(grid, new String[]{"Tên công việc", "Vị trí/phòng", "Hạn xử lý", "Người phụ trách", "Mô tả", "Mức độ ưu tiên", "Trạng thái"}, title, location, due, assignee, description, priority, status);
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        dialog.setOnShown(event -> title.requestFocus());

        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            if (!title.getText().trim().isEmpty() && !location.getText().trim().isEmpty()) {
                MockDataService.getInstance().getTasks().add(new TaskItem(title.getText().trim(), location.getText().trim(), valueOrBlank(due.getText()), taskPriority(priority.getValue()), taskStatus(status.getValue()), assignee.getText(), description.getText()));
                showInfo("Thêm công việc", "Đã lưu thành công", "Công việc đã được thêm vào danh sách xử lý.");
                return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể lưu", "Tên công việc và vị trí/phòng là bắt buộc. Form vẫn được giữ lại để bạn sửa.");
        }
    }

    public static void showEditTaskDialog(TaskItem task, Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chỉnh sửa công việc");
        dialog.setHeaderText("Cập nhật nội dung, phân công và tiến độ");
        GridPane grid = formGrid();
        TextField title = field(task.getTitle());
        TextField location = field(task.getLocation());
        TextField due = field(task.getTime());
        TextField assignee = field(task.getAssignee());
        TextArea description = new TextArea(task.getDescription());
        description.setPrefRowCount(3);
        ComboBox<String> priority = combo("Khẩn cấp", "Cao", "Trung bình");
        priority.setValue(task.getPriority().getLabel());
        ComboBox<String> status = combo("Cần xử lý", "Đang xử lý", "Đã hoàn thành");
        status.setValue(task.getStatus().getLabel());
        addRows(grid, new String[]{"Tên công việc", "Vị trí/phòng", "Hạn xử lý", "Người phụ trách", "Mô tả", "Mức độ ưu tiên", "Trạng thái"}, title, location, due, assignee, description, priority, status);
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            if (!title.getText().trim().isEmpty() && !location.getText().trim().isEmpty()) {
                task.setTitle(title.getText());
                task.setLocation(location.getText());
                task.setTime(valueOrBlank(due.getText()));
                task.setAssignee(assignee.getText());
                task.setDescription(description.getText());
                task.setPriority(taskPriority(priority.getValue()));
                task.setStatus(taskStatus(status.getValue()));
                showInfo("Chỉnh sửa công việc", "Đã lưu thành công", "Công việc đã được cập nhật.");
                if (onSaved != null) onSaved.run();
                return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể lưu", "Tên công việc và vị trí/phòng là bắt buộc.");
        }
    }

    public static void showAddAssetDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm tài sản");
        dialog.setHeaderText("Nhập đầy đủ thông tin tài sản và thiết bị");
        GridPane grid = formGrid();
        TextField code = field("Mã tài sản *"), name = field("Tên tài sản *"), room = field("Vị trí/phòng *"), category = field("Danh mục"), quantity = field("Số lượng"), value = field("Giá trị (VNĐ)"), installDate = field("Ngày lắp đặt");
        ComboBox<String> status = combo("Hoạt động tốt", "Cần bảo trì", "Hỏng / Cần thay", "Đã thanh lý / hủy");
        addRows(grid, new String[]{"Mã tài sản", "Tên tài sản", "Vị trí/phòng", "Danh mục", "Số lượng", "Giá trị (VNĐ)", "Ngày lắp đặt", "Trạng thái"}, code, name, room, category, quantity, value, installDate, status);
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        dialog.setOnShown(event -> code.requestFocus());

        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            if (!code.getText().trim().isEmpty() && !name.getText().trim().isEmpty() && !room.getText().trim().isEmpty() && validNumbers(quantity, value)) {
                MockDataService.getInstance().getAssets().add(new Asset(code.getText().trim(), name.getText().trim(), room.getText().trim(), category.getText().trim(), Integer.parseInt(quantity.getText().trim()), Long.parseLong(value.getText().trim()), installDate.getText().trim(), assetStatus(status.getValue())));
                showInfo("Thêm tài sản", "Đã lưu thành công", "Tài sản " + name.getText().trim() + " đã được thêm vào danh sách.");
                return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể lưu", "Mã, tên, vị trí và các trường số là bắt buộc. Form vẫn được giữ lại để bạn sửa.");
        }
    }

    public static void showMaintenanceDialog(Asset asset, Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Yêu cầu bảo trì / sửa chữa");
        dialog.setHeaderText("Tạo phiếu xử lý cho " + asset.getName());
        GridPane grid = formGrid();
        TextField assetInfo = field(asset.getCode() + " - " + asset.getName());
        assetInfo.setEditable(false);
        TextField location = field(asset.getRoomName());
        TextField reason = field("Mô tả sự cố *");
        TextField assignee = field("Người phụ trách");
        TextField due = field("Hạn xử lý (dd/MM/yyyy)");
        TextField estimate = field("Chi phí dự kiến (VNĐ)");
        TextArea note = new TextArea();
        note.setPromptText("Chi tiết sự cố, vật tư cần thay, ghi chú nghiệm thu...");
        note.setPrefRowCount(3);
        ComboBox<String> priority = combo("Khẩn cấp", "Cao", "Trung bình");
        addRows(grid, new String[]{"Tài sản", "Vị trí", "Nội dung sự cố", "Người phụ trách", "Hạn xử lý", "Chi phí dự kiến", "Mức độ ưu tiên", "Ghi chú"}, assetInfo, location, reason, assignee, due, estimate, priority, note);
        dialog.getDialogPane().setContent(grid);
        addOkCancel(dialog);
        prepareFormDialog(dialog, grid);
        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            if (!reason.getText().trim().isEmpty() && nonNegativeText(estimate)) {
                asset.setStatus(Asset.AssetStatus.MAINTENANCE);
                asset.setMaintenanceNote(reason.getText() + " | " + note.getText());
                dataService().getTasks().add(new TaskItem("Bảo trì: " + asset.getName(), location.getText(), valueOrBlank(due.getText()), taskPriority(priority.getValue()), TaskItem.TaskStatus.TODO, assignee.getText(), reason.getText() + " | Chi phí dự kiến: " + estimate.getText() + " VNĐ"));
                showInfo("Bảo trì", "Đã tạo yêu cầu", "Yêu cầu bảo trì đã được đưa vào danh sách Công việc.");
                if (onSaved != null) onSaved.run();
                return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể tạo yêu cầu", "Nội dung sự cố là bắt buộc và chi phí dự kiến phải là số không âm.");
        }
    }

    public static void showEditAssetDialog(Asset asset, Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cập nhật tài sản " + asset.getCode());
        dialog.setHeaderText("Cập nhật vị trí, thông số và tình trạng sử dụng");
        GridPane grid = formGrid();
        TextField code = field(asset.getCode()); code.setEditable(false);
        TextField name = field(asset.getName());
        TextField location = field(asset.getRoomName());
        TextField category = field(asset.getCategory());
        TextField quantity = field(String.valueOf(asset.getQuantity()));
        TextField value = field(String.valueOf(asset.getValue()));
        TextField installDate = field(asset.getInstallDate());
        TextField supplier = field(asset.getSupplier());
        TextField warranty = field(asset.getWarranty());
        TextArea condition = new TextArea(asset.getConditionNote()); condition.setPrefRowCount(2);
        ComboBox<String> status = combo("Hoạt động tốt", "Cần bảo trì", "Hỏng / Cần thay", "Đã thanh lý / hủy");
        status.setValue(asset.getStatus().getLabel());
        addRows(grid, new String[]{"Mã tài sản", "Tên tài sản", "Vị trí lắp đặt", "Danh mục", "Số lượng", "Giá trị mua mới (VNĐ)", "Ngày bàn giao", "Nhà cung cấp", "Bảo hành", "Tình trạng/ghi chú", "Trạng thái"}, code, name, location, category, quantity, value, installDate, supplier, warranty, condition, status);
        dialog.getDialogPane().setContent(grid); addOkCancel(dialog); prepareFormDialog(dialog, grid);
        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            if (!name.getText().trim().isEmpty() && !location.getText().trim().isEmpty() && validNumbers(quantity, value)) {
                asset.setName(name.getText()); asset.setRoomName(location.getText()); asset.setCategory(category.getText());
                asset.setQuantity(Integer.parseInt(quantity.getText().trim())); asset.setValue(Long.parseLong(value.getText().trim()));
                asset.setInstallDate(installDate.getText()); asset.setSupplier(supplier.getText()); asset.setWarranty(warranty.getText());
                asset.setConditionNote(condition.getText()); asset.setStatus(assetStatus(status.getValue()));
                if (onSaved != null) onSaved.run();
                showInfo("Cập nhật tài sản", "Đã lưu thành công", "Thông tin tài sản đã được cập nhật."); return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể cập nhật", "Tên, vị trí và các trường số là bắt buộc.");
        }
    }

    public static void showDisposeAssetDialog(Asset asset, Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thanh lý / hủy tài sản");
        dialog.setHeaderText("Lập biên bản xử lý " + asset.getCode());
        GridPane grid = formGrid();
        TextField date = field(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        ComboBox<String> type = combo("Thanh lý", "Hủy bỏ");
        TextField reason = field("Lý do xử lý *");
        TextField value = field("Giá trị thu hồi (VNĐ)");
        TextArea note = new TextArea(); note.setPromptText("Tình trạng, người phê duyệt, biên bản bàn giao..."); note.setPrefRowCount(3);
        addRows(grid, new String[]{"Ngày thực hiện", "Hình thức", "Lý do", "Giá trị thu hồi (VNĐ)", "Ghi chú"}, date, type, reason, value, note);
        dialog.getDialogPane().setContent(grid); addOkCancel(dialog); prepareFormDialog(dialog, grid);
        while (true) {
            java.util.Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL) return;
            if (!reason.getText().trim().isEmpty() && nonNegativeText(value)) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Tài sản sẽ được đánh dấu đã thanh lý/hủy và không còn ở trạng thái sử dụng.", ButtonType.OK, ButtonType.CANCEL);
                confirmation.setTitle("Xác nhận xử lý tài sản");
                if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) continue;
                asset.setStatus(Asset.AssetStatus.DISPOSED); asset.setDisposalDate(date.getText()); asset.setDisposalReason(type.getValue() + ": " + reason.getText() + " | " + note.getText()); asset.setDisposalValue(Long.parseLong(value.getText().trim()));
                if (onSaved != null) onSaved.run();
                showInfo("Thanh lý / hủy tài sản", "Đã cập nhật hồ sơ", "Tài sản đã được ghi nhận là " + type.getValue().toLowerCase() + "."); return;
            }
            showInfo("Dữ liệu chưa hợp lệ", "Không thể xử lý", "Lý do là bắt buộc và giá trị thu hồi phải là số không âm.");
        }
    }

    private static boolean nonNegativeText(TextInputControl field) {
        try { return Long.parseLong(field.getText().trim()) >= 0; } catch (NumberFormatException ex) { return false; }
    }

    private static Asset.AssetStatus assetStatus(String value) {
        if ("Cần bảo trì".equals(value)) return Asset.AssetStatus.MAINTENANCE;
        if ("Hỏng / Cần thay".equals(value)) return Asset.AssetStatus.BROKEN;
        if ("Đã thanh lý / hủy".equals(value)) return Asset.AssetStatus.DISPOSED;
        return Asset.AssetStatus.GOOD;
    }

    private static TaskItem.Priority taskPriority(String value) {
        if ("Khẩn cấp".equals(value)) return TaskItem.Priority.EMERGENCY;
        if ("Cao".equals(value)) return TaskItem.Priority.HIGH;
        return TaskItem.Priority.MEDIUM;
    }

    private static TaskItem.TaskStatus taskStatus(String value) {
        if ("Đang xử lý".equals(value)) return TaskItem.TaskStatus.IN_PROGRESS;
        if ("Đã hoàn thành".equals(value)) return TaskItem.TaskStatus.DONE;
        return TaskItem.TaskStatus.TODO;
    }

    private static GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        return grid;
    }

    private static TextField field(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setEditable(true);
        field.setDisable(false);
        field.setPrefWidth(430);
        return field;
    }

    private static ComboBox<String> combo(String... values) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(values);
        combo.setValue(values[0]);
        combo.setMaxWidth(Double.MAX_VALUE);
        return combo;
    }

    private static void addRows(GridPane grid, String[] labels, Control... controls) {
        for (int index = 0; index < controls.length; index++) {
            String labelText = labels[index];
            if (index < 3 && !labelText.endsWith("*")) labelText += " *";
            Label label = new Label(labelText);
            label.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #334155;");
            label.setMinWidth(155);
            grid.add(label, 0, index);
            grid.add(controls[index], 1, index);
        }
    }

    private static void addOkCancel(Dialog<ButtonType> dialog) {
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);
    }

    private static void prepareFormDialog(Dialog<ButtonType> dialog, GridPane grid) {
        grid.setMinWidth(560);
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialog.getDialogPane().setContent(scroll);
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefWidth(680);
        dialog.getDialogPane().setPrefHeight(520);
    }

    private static boolean validNumbers(TextInputControl... fields) {
        for (TextInputControl field : fields) {
            if (!isNumber(field)) return false;
        }
        return true;
    }

    private static boolean isNumber(TextInputControl field) {
        try {
            return !field.getText().trim().isEmpty() && Long.parseLong(field.getText().trim()) >= 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private static RoomStatus roomStatus(String value) {
        if ("Đang thuê".equals(value)) return RoomStatus.RENTED;
        if ("Sắp hết HĐ".equals(value)) return RoomStatus.EXPIRING_SOON;
        return RoomStatus.VACANT;
    }

    private static Contract.Status contractStatus(String value) {
        if ("Sắp hết hạn".equals(value)) return Contract.Status.EXPIRING;
        if ("Đã hết hạn".equals(value)) return Contract.Status.EXPIRED;
        return Contract.Status.ACTIVE;
    }

    private static Invoice.InvoiceStatus invoiceStatus(String value) {
        if ("Sắp hạn".equals(value)) return Invoice.InvoiceStatus.DUE_SOON;
        if ("Đã thanh toán".equals(value)) return Invoice.InvoiceStatus.PAID;
        return Invoice.InvoiceStatus.UNPAID;
    }

    public static void styleDialog(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        String stylesheet = DialogHelper.class.getResource("/com/tromanager/css/styles.css").toExternalForm();
        if (!pane.getStylesheets().contains(stylesheet)) {
            pane.getStylesheets().add(stylesheet);
        }
        pane.setStyle("-fx-font-family: 'Segoe UI', system-ui, sans-serif; -fx-font-size: 13px; -fx-background-color: white;");
    }
}
