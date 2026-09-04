package com.tromanager.controller;

import com.tromanager.model.Contract;
import com.tromanager.service.MockDataService;
import com.tromanager.ui.IconHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

public class ContractsController {

    @FXML private StackPane iconActiveContracts;
    @FXML private StackPane iconExpiringContracts;
    @FXML private StackPane iconExpiredContracts;
    @FXML private StackPane iconNewContracts;
    @FXML private VBox cardActiveContracts;
    @FXML private VBox cardExpiringContracts;
    @FXML private VBox cardExpiredContracts;
    @FXML private VBox cardNewContracts;

    @FXML private TextField txtSearchContract;
    @FXML private ComboBox<String> cbContractFilter;
    @FXML private Button btnAddContract;

    @FXML private VBox contractsListContainer;

    // Details Panel
    @FXML private VBox contractDetailsPanel;
    @FXML private Label lblDetailCode;
    @FXML private Label lblDetailRoom;
    @FXML private Label lblDetailStatus;
    @FXML private Label lblDetailTenant;
    @FXML private Label lblDetailPhone;
    @FXML private Label lblDetailOwner;
    @FXML private Label lblDetailOwnerAddress;
    @FXML private Label lblDetailOwnerPhone;
    @FXML private Label lblDetailTenantAddress;
    @FXML private Label lblDetailPeriod;
    @FXML private Label lblDetailRentPrice;
    @FXML private Label lblDetailDeposit;
    @FXML private Label lblDetailRemaining;

    @FXML private Button btnRenewContract;
    @FXML private Button btnEditContract;
    @FXML private Button btnViewContract;
    @FXML private Button btnPrintContract;
    @FXML private Button btnTerminateContract;
    @FXML private Button btnDeleteContract;

    private final MockDataService dataService = MockDataService.getInstance();
    private Contract selectedContract;
    private final Map<Contract, HBox> rowMap = new HashMap<>();

    @FXML
    public void initialize() {
        setupIcons();
        setupFilter();
        setupMetricLinks();
        renderContractsList(dataService.getContracts());
        setupActions();
    }

    private void setupMetricLinks() {
        link(cardActiveContracts, "Đang hiệu lực");
        link(cardExpiringContracts, "Sắp hết hạn (< 30 ngày)");
        link(cardExpiredContracts, "Đã hết hạn");
        link(cardNewContracts, "Tất cả hợp đồng");
    }

    private void link(VBox card, String filter) {
        card.setStyle(card.getStyle() + ";-fx-cursor: hand;");
        card.setOnMouseClicked(event -> {
            cbContractFilter.setValue(filter);
            applyFilter();
        });
    }

    private void setupIcons() {
        iconActiveContracts.getChildren().add(IconHelper.getIcon("contract", 18, "#059669"));
        iconExpiringContracts.getChildren().add(IconHelper.getIcon("shield", 18, "#d97706"));
        iconExpiredContracts.getChildren().add(IconHelper.getIcon("alert", 18, "#dc2626"));
        iconNewContracts.getChildren().add(IconHelper.getIcon("plus", 18, "#2563eb"));
    }

    private void setupFilter() {
        cbContractFilter.getItems().addAll("Tất cả hợp đồng", "Đang hiệu lực", "Sắp hết hạn (< 30 ngày)", "Đã hết hạn");
        cbContractFilter.setValue(MainController.consumeContractFilter());
        txtSearchContract.setText(MainController.consumeSearch());

        cbContractFilter.setOnAction(e -> applyFilter());
        txtSearchContract.textProperty().addListener((obs, oldV, newV) -> applyFilter());
    }

    private void applyFilter() {
        String q = txtSearchContract.getText().trim().toLowerCase();
        String filter = cbContractFilter.getValue();

        List<Contract> filtered = new ArrayList<>();
        for (Contract c : dataService.getContracts()) {
            boolean matchText = q.isEmpty() || c.getCode().toLowerCase().contains(q) || c.getTenantName().toLowerCase().contains(q) || c.getRoomName().toLowerCase().contains(q);
            boolean matchFilter = filter.equals("Tất cả hợp đồng") ||
                    (filter.equals("Đang hiệu lực") && c.getStatus() == Contract.Status.ACTIVE) ||
                        (filter.equals("Sắp hết hạn (< 30 ngày)") && c.getStatus() == Contract.Status.EXPIRING) ||
                        (filter.equals("Đã hết hạn") && c.getStatus() == Contract.Status.EXPIRED);

            if (matchText && matchFilter) {
                filtered.add(c);
            }
        }
        renderContractsList(filtered);
    }

    private void renderContractsList(List<Contract> list) {
        contractsListContainer.getChildren().clear();
        rowMap.clear();

        for (Contract c : list) {
            HBox card = new HBox(14);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("card");
            card.setStyle("-fx-padding: 12 16; -fx-cursor: hand;");

            // Contract badge
            Label lblCodeBadge = new Label(c.getCode());
            lblCodeBadge.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-font-weight: 800; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-radius: 6;");

            VBox info = new VBox(2);
            HBox titleRow = new HBox(8);
            titleRow.setAlignment(Pos.CENTER_LEFT);

            Label lblTitle = new Label(c.getTenantName() + " (" + c.getRoomName() + ")");
            lblTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");
            titleRow.getChildren().add(lblTitle);

            Label lblSub = new Label("Thời hạn: " + c.getPeriod() + "  •  Tiền thuê: " + c.getFormattedRentPrice() + "/tháng  •  Cọc: " + c.getFormattedDeposit());
            lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            info.getChildren().addAll(titleRow, lblSub);

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblRemaining = new Label(c.getRemainingText());
            lblRemaining.getStyleClass().add(c.getStatus().getStyleClass());

            card.getChildren().addAll(lblCodeBadge, info, spacer, lblRemaining);
            card.setOnMouseClicked(e -> selectContract(c));

            rowMap.put(c, card);
            contractsListContainer.getChildren().add(card);
        }

        if (!list.isEmpty()) {
            selectContract(list.get(0));
        }
    }

    private void selectContract(Contract c) {
        this.selectedContract = c;

        for (Map.Entry<Contract, HBox> entry : rowMap.entrySet()) {
            entry.getValue().setStyle("-fx-padding: 12 16; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-radius: 12;");
        }
        if (rowMap.containsKey(c)) {
            rowMap.get(c).setStyle("-fx-padding: 12 16; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: #2563eb; -fx-border-width: 1.8; -fx-border-radius: 12;");
        }

        lblDetailCode.setText(c.getCode());
        lblDetailRoom.setText("Phòng " + c.getRoomName() + " - Khu trọ Hòa Bình");
        lblDetailOwner.setText(valueOrBlank(c.getOwnerName()));
        lblDetailOwnerAddress.setText(valueOrBlank(c.getOwnerAddress()));
        lblDetailOwnerPhone.setText(valueOrBlank(c.getOwnerPhone()));
        lblDetailTenant.setText(c.getTenantName());
        lblDetailPhone.setText(c.getPhone());
        lblDetailTenantAddress.setText(valueOrBlank(c.getTenantAddress()));
        lblDetailPeriod.setText(c.getPeriod());
        lblDetailRentPrice.setText(c.getFormattedRentPrice() + "/tháng");
        lblDetailDeposit.setText(c.getFormattedDeposit());
        lblDetailRemaining.setText(c.getRemainingText());

        lblDetailStatus.setText("●  " + c.getStatus().getLabel());
        lblDetailStatus.getStyleClass().clear();
        lblDetailStatus.getStyleClass().add(c.getStatus().getStyleClass());
    }

    private void setupActions() {
        btnAddContract.setOnAction(e -> {
            DialogHelper.showAddContractDialog();
            renderContractsList(dataService.getContracts());
        });
        btnEditContract.setOnAction(e -> {
            if (selectedContract != null) {
                DialogHelper.showEditContractDialog(selectedContract, () -> renderContractsList(dataService.getContracts()));
            }
        });
        btnRenewContract.setOnAction(e -> {
            if (selectedContract != null) {
                DialogHelper.showEditContractDialog(selectedContract, () -> renderContractsList(dataService.getContracts()));
            }
        });
        btnPrintContract.setOnAction(e -> {
            if (selectedContract != null) {
                DialogHelper.showContractPreview(selectedContract);
            }
        });
        btnViewContract.setOnAction(e -> {
            if (selectedContract != null) DialogHelper.showContractPreview(selectedContract);
        });
        btnTerminateContract.setOnAction(e -> {
            if (selectedContract != null) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Thanh lý hợp đồng");
                confirmation.setHeaderText("Thanh lý hợp đồng " + selectedContract.getCode());
                confirmation.setContentText("Xác nhận kết thúc hợp đồng của " + selectedContract.getTenantName() + " tại " + selectedContract.getRoomName() + "?");
                DialogHelper.styleDialog(confirmation);
                confirmation.showAndWait().filter(ButtonType.OK::equals).ifPresent(result -> {
                    selectedContract.setStatus(Contract.Status.EXPIRED);
                    selectedContract.setRemainingDays(0);
                    renderContractsList(dataService.getContracts());
                });
            }
        });
        btnDeleteContract.setOnAction(e -> {
            if (selectedContract == null) return;
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Xóa hợp đồng");
            confirmation.setHeaderText("Xóa hợp đồng " + selectedContract.getCode());
            confirmation.setContentText("Hợp đồng sẽ bị xóa khỏi danh sách. Bạn có chắc chắn muốn tiếp tục?");
            DialogHelper.styleDialog(confirmation);
            confirmation.showAndWait().filter(ButtonType.OK::equals).ifPresent(result -> {
                dataService.getContracts().remove(selectedContract);
                selectedContract = null;
                renderContractsList(dataService.getContracts());
            });
        });
    }

    private String valueOrBlank(String value) {
        return value == null || value.trim().isEmpty() ? "Chưa cập nhật" : value.trim();
    }
}