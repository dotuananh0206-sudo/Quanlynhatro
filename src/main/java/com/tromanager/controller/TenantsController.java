package com.tromanager.controller;

import com.tromanager.model.Tenant;
import com.tromanager.service.MockDataService;
import com.tromanager.ui.IconHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

public class TenantsController {

    @FXML private StackPane iconTotalTenants;
    @FXML private StackPane iconRegisteredTenants;
    @FXML private StackPane iconUnregisteredTenants;
    @FXML private StackPane iconNewTenants;
    @FXML private VBox cardTotalTenants;
    @FXML private VBox cardRegisteredTenants;
    @FXML private VBox cardUnregisteredTenants;
    @FXML private VBox cardNewTenants;

    @FXML private TextField txtSearchTenant;
    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private Button btnAddTenant;

    @FXML private VBox tenantsListContainer;

    // Details Panel
    @FXML private VBox tenantDetailsPanel;
    @FXML private Label lblAvatarInitials;
    @FXML private Label lblDetailName;
    @FXML private Label lblDetailRoom;
    @FXML private Label lblDetailTempReg;
    @FXML private Label lblDetailPhone;
    @FXML private Label lblDetailIdCard;
    @FXML private Label lblDetailHometown;
    @FXML private Label lblDetailStartDate;
    @FXML private Label lblDetailDeposit;

    @FXML private Button btnEditTenant;
    @FXML private Button btnRegisterTemp;
    @FXML private Button btnLeaveRoom;

    private final MockDataService dataService = MockDataService.getInstance();
    private Tenant selectedTenant;
    private final Map<Tenant, HBox> rowMap = new HashMap<>();

    @FXML
    public void initialize() {
        setupIcons();
        setupFilter();
        setupMetricLinks();
        renderTenantsList(dataService.getTenants());
        setupActions();
    }

    private void setupMetricLinks() {
        link(cardTotalTenants, "Tất cả trạng thái");
        link(cardRegisteredTenants, "Đã đăng ký tạm trú");
        link(cardUnregisteredTenants, "Chưa đăng ký tạm trú");
        link(cardNewTenants, "Tất cả trạng thái");
    }

    private void link(VBox card, String filter) {
        card.setOnMouseClicked(event -> {
            cbFilterStatus.setValue(filter);
            applyFilter();
        });
    }

    private void setupIcons() {
        iconTotalTenants.getChildren().add(IconHelper.getIcon("users", 18, "#2563eb"));
        iconRegisteredTenants.getChildren().add(IconHelper.getIcon("check", 18, "#059669"));
        iconUnregisteredTenants.getChildren().add(IconHelper.getIcon("alert", 18, "#d97706"));
        iconNewTenants.getChildren().add(IconHelper.getIcon("plus", 18, "#2563eb"));
    }

    private void setupFilter() {
        cbFilterStatus.getItems().addAll("Tất cả trạng thái", "Đã đăng ký tạm trú", "Chưa đăng ký tạm trú");
        cbFilterStatus.setValue("Tất cả trạng thái");

        cbFilterStatus.setOnAction(e -> applyFilter());
        txtSearchTenant.textProperty().addListener((obs, oldV, newV) -> applyFilter());
    }

    private void applyFilter() {
        String q = txtSearchTenant.getText().trim().toLowerCase();
        String status = cbFilterStatus.getValue();

        List<Tenant> filtered = new ArrayList<>();
        for (Tenant t : dataService.getTenants()) {
            boolean matchText = q.isEmpty() || t.getName().toLowerCase().contains(q) || t.getPhone().contains(q) || t.getRoomName().toLowerCase().contains(q) || t.getIdCard().contains(q);
            boolean matchStatus = status.equals("Tất cả trạng thái") ||
                    (status.equals("Đã đăng ký tạm trú") && t.isTempReg()) ||
                    (status.equals("Chưa đăng ký tạm trú") && !t.isTempReg());

            if (matchText && matchStatus) {
                filtered.add(t);
            }
        }
        renderTenantsList(filtered);
    }

    private void renderTenantsList(List<Tenant> list) {
        tenantsListContainer.getChildren().clear();
        rowMap.clear();

        for (Tenant tenant : list) {
            HBox card = new HBox(14);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("card");
            card.setStyle("-fx-padding: 12 16; -fx-cursor: hand;");

            // Initials avatar circle
            StackPane avatar = new StackPane();
            avatar.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 50%; -fx-pref-width: 38px; -fx-pref-height: 38px;");
            String initials = getInitials(tenant.getName());
            Label lblInit = new Label(initials);
            lblInit.setStyle("-fx-font-weight: 800; -fx-text-fill: #2563eb; -fx-font-size: 13px;");
            avatar.getChildren().add(lblInit);

            VBox info = new VBox(2);
            HBox nameRow = new HBox(8);
            nameRow.setAlignment(Pos.CENTER_LEFT);
            Label lblName = new Label(tenant.getName());
            lblName.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");
            Label lblRoomBadge = new Label(tenant.getRoomName());
            lblRoomBadge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #2563eb; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 2 6; -fx-background-radius: 4;");
            nameRow.getChildren().addAll(lblName, lblRoomBadge);

            Label lblSub = new Label("SĐT: " + tenant.getPhone() + "  •  CCCD: " + tenant.getIdCard() + "  •  Quê: " + tenant.getHometown());
            lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            info.getChildren().addAll(nameRow, lblSub);

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblBadge = new Label(tenant.getTempRegStatus());
            lblBadge.getStyleClass().add(tenant.getTempRegBadgeClass());

            card.getChildren().addAll(avatar, info, spacer, lblBadge);
            card.setOnMouseClicked(e -> selectTenant(tenant));

            rowMap.put(tenant, card);
            tenantsListContainer.getChildren().add(card);
        }

        if (!list.isEmpty()) {
            selectTenant(list.get(0));
        }
    }

    private String getInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[parts.length - 2].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        } else if (parts.length == 1 && !parts[0].isEmpty()) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return "KT";
    }

    private void selectTenant(Tenant tenant) {
        this.selectedTenant = tenant;

        for (Map.Entry<Tenant, HBox> entry : rowMap.entrySet()) {
            entry.getValue().setStyle("-fx-padding: 12 16; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-radius: 12;");
        }
        if (rowMap.containsKey(tenant)) {
            rowMap.get(tenant).setStyle("-fx-padding: 12 16; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: #2563eb; -fx-border-width: 1.8; -fx-border-radius: 12;");
        }

        lblAvatarInitials.setText(getInitials(tenant.getName()));
        lblDetailName.setText(tenant.getName());
        lblDetailRoom.setText("Phòng " + tenant.getRoomName());
        lblDetailPhone.setText(tenant.getPhone());
        lblDetailIdCard.setText(tenant.getIdCard());
        lblDetailHometown.setText(tenant.getHometown());
        lblDetailStartDate.setText(tenant.getStartDate());
        lblDetailDeposit.setText(tenant.getFormattedDeposit());

        lblDetailTempReg.setText("●  " + tenant.getTempRegStatus());
        lblDetailTempReg.getStyleClass().clear();
        lblDetailTempReg.getStyleClass().add(tenant.getTempRegBadgeClass());
    }

    private void setupActions() {
        btnAddTenant.setOnAction(e -> DialogHelper.showAddTenantDialog());
        btnEditTenant.setOnAction(e -> {
            if (selectedTenant != null) {
                DialogHelper.showEditTenantDialog(selectedTenant, () -> {
                    renderTenantsList(dataService.getTenants());
                    selectTenant(selectedTenant);
                });
            }
        });
        btnRegisterTemp.setOnAction(e -> {
            if (selectedTenant != null) {
                DialogHelper.showInfo("Khai báo tạm trú", "Dịch vụ công trực tuyến", "Đã gửi yêu cầu đăng ký tạm trú cho " + selectedTenant.getName() + " đến công an khu vực.");
            }
        });
        btnLeaveRoom.setOnAction(e -> {
            if (selectedTenant != null) {
                DialogHelper.showCheckoutDialog(selectedTenant, () -> {
                    renderTenantsList(dataService.getTenants());
                    selectTenant(selectedTenant);
                });
            }
        });
    }
}