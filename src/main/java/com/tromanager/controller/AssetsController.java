package com.tromanager.controller;

import com.tromanager.model.Asset;
import com.tromanager.service.MockDataService;
import com.tromanager.ui.IconHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

public class AssetsController {

    @FXML private StackPane iconTotalAssets;
    @FXML private StackPane iconGoodAssets;
    @FXML private StackPane iconMaintAssets;
    @FXML private StackPane iconBrokenAssets;

    @FXML private TextField txtSearchAsset;
    @FXML private ComboBox<String> cbCategoryFilter;
    @FXML private ComboBox<String> cbAssetStatus;
    @FXML private VBox cardTotalAssets;
    @FXML private VBox cardGoodAssets;
    @FXML private VBox cardMaintAssets;
    @FXML private VBox cardBrokenAssets;
    @FXML private Button btnAddAsset;

    @FXML private VBox assetsListContainer;

    // Details Panel
    @FXML private VBox assetDetailsPanel;
    @FXML private Label lblDetailCode;
    @FXML private Label lblDetailName;
    @FXML private Label lblDetailStatus;
    @FXML private Label lblDetailLocation;
    @FXML private Label lblDetailCategory;
    @FXML private Label lblDetailQuantity;
    @FXML private Label lblDetailValue;
    @FXML private Label lblDetailInstallDate;

    @FXML private Button btnMaintenance;
    @FXML private Button btnEditAsset;
    @FXML private Button btnLiquidateAsset;

    private final MockDataService dataService = MockDataService.getInstance();
    private Asset selectedAsset;
    private final Map<Asset, HBox> rowMap = new HashMap<>();

    @FXML
    public void initialize() {
        setupIcons();
        setupFilter();
        setupMetricLinks();
        renderAssetsList(dataService.getAssets());
        setupActions();
    }

    private void setupMetricLinks() {
        link(cardTotalAssets, "Tất cả trạng thái");
        link(cardGoodAssets, "Hoạt động tốt");
        link(cardMaintAssets, "Cần bảo trì");
        link(cardBrokenAssets, "Hỏng / Cần thay");
    }

    private void link(VBox card, String status) {
        card.setOnMouseClicked(event -> {
            cbAssetStatus.setValue(status);
            applyFilter();
        });
    }

    private void setupIcons() {
        iconTotalAssets.getChildren().add(IconHelper.getIcon("asset", 18, "#2563eb"));
        iconGoodAssets.getChildren().add(IconHelper.getIcon("check", 18, "#059669"));
        iconMaintAssets.getChildren().add(IconHelper.getIcon("alert", 18, "#d97706"));
        iconBrokenAssets.getChildren().add(IconHelper.getIcon("alert", 18, "#dc2626"));
    }

    private void setupFilter() {
        cbCategoryFilter.getItems().addAll("Tất cả danh mục", "Điện lạnh", "Nội thất", "Thiết bị vệ sinh", "Điện gia dụng", "Mạng viễn thông");
        cbCategoryFilter.setValue("Tất cả danh mục");
        cbAssetStatus.getItems().addAll("Tất cả trạng thái", "Hoạt động tốt", "Cần bảo trì", "Hỏng / Cần thay", "Đã thanh lý / hủy");
        cbAssetStatus.setValue("Tất cả trạng thái");

        cbCategoryFilter.setOnAction(e -> applyFilter());
        cbAssetStatus.setOnAction(e -> applyFilter());
        txtSearchAsset.textProperty().addListener((obs, oldV, newV) -> applyFilter());
    }

    private void applyFilter() {
        String q = txtSearchAsset.getText().trim().toLowerCase();
        String cat = cbCategoryFilter.getValue();
        String status = cbAssetStatus.getValue();

        List<Asset> filtered = new ArrayList<>();
        for (Asset a : dataService.getAssets()) {
            boolean matchText = q.isEmpty() || a.getName().toLowerCase().contains(q) || a.getCode().toLowerCase().contains(q) || a.getRoomName().toLowerCase().contains(q);
            boolean matchCat = cat.equals("Tất cả danh mục") || a.getCategory().equalsIgnoreCase(cat);
            boolean matchStatus = status.equals("Tất cả trạng thái") || a.getStatus().getLabel().equals(status);

            if (matchText && matchCat && matchStatus) {
                filtered.add(a);
            }
        }
        renderAssetsList(filtered);
    }

    private void renderAssetsList(List<Asset> list) {
        assetsListContainer.getChildren().clear();
        rowMap.clear();

        for (Asset a : list) {
            HBox card = new HBox(14);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("card");
            card.setStyle("-fx-padding: 12 16; -fx-cursor: hand;");

            // Code badge
            Label lblCodeBadge = new Label(a.getCode());
            lblCodeBadge.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-font-weight: 800; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-radius: 6;");

            VBox info = new VBox(2);
            HBox titleRow = new HBox(8);
            titleRow.setAlignment(Pos.CENTER_LEFT);

            Label lblTitle = new Label(a.getName());
            lblTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");

            Label lblRoomBadge = new Label(a.getRoomName());
            lblRoomBadge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 2 6; -fx-background-radius: 4;");
            titleRow.getChildren().addAll(lblTitle, lblRoomBadge);

            Label lblSub = new Label("Loại: " + a.getCategory() + "  •  Số lượng: " + a.getQuantity() + "  •  Giá trị: " + a.getFormattedValue());
            lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            info.getChildren().addAll(titleRow, lblSub);

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblStatus = new Label(a.getStatus().getLabel());
            lblStatus.getStyleClass().add(a.getStatus().getStyleClass());

            card.getChildren().addAll(lblCodeBadge, info, spacer, lblStatus);
            card.setOnMouseClicked(e -> selectAsset(a));

            rowMap.put(a, card);
            assetsListContainer.getChildren().add(card);
        }

        if (!list.isEmpty()) {
            selectAsset(list.get(0));
        }
    }

    private void selectAsset(Asset a) {
        this.selectedAsset = a;

        for (Map.Entry<Asset, HBox> entry : rowMap.entrySet()) {
            entry.getValue().setStyle("-fx-padding: 12 16; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-radius: 12;");
        }
        if (rowMap.containsKey(a)) {
            rowMap.get(a).setStyle("-fx-padding: 12 16; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: #2563eb; -fx-border-width: 1.8; -fx-border-radius: 12;");
        }

        lblDetailCode.setText(a.getCode());
        lblDetailName.setText(a.getName());
        lblDetailLocation.setText(a.getRoomName());
        lblDetailCategory.setText(a.getCategory());
        lblDetailQuantity.setText(a.getQuantity() + " chiếc");
        lblDetailValue.setText(a.getFormattedValue());
        lblDetailInstallDate.setText(a.getInstallDate());

        lblDetailStatus.setText("●  " + a.getStatus().getLabel());
        lblDetailStatus.getStyleClass().clear();
        lblDetailStatus.getStyleClass().add(a.getStatus().getStyleClass());
    }

    private void setupActions() {
        btnAddAsset.setOnAction(e -> {
            DialogHelper.showAddAssetDialog();
            renderAssetsList(dataService.getAssets());
        });
        btnMaintenance.setOnAction(e -> {
            if (selectedAsset != null) {
                DialogHelper.showMaintenanceDialog(selectedAsset, () -> renderAssetsList(dataService.getAssets()));
            }
        });
        btnEditAsset.setOnAction(e -> {
            if (selectedAsset != null) {
                DialogHelper.showEditAssetDialog(selectedAsset, () -> renderAssetsList(dataService.getAssets()));
            }
        });
        btnLiquidateAsset.setOnAction(e -> {
            if (selectedAsset != null) {
                DialogHelper.showDisposeAssetDialog(selectedAsset, () -> renderAssetsList(dataService.getAssets()));
            }
        });
    }
}