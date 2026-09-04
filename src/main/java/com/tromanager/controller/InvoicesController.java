package com.tromanager.controller;

import com.tromanager.model.Invoice;
import com.tromanager.service.MockDataService;
import com.tromanager.ui.IconHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

public class InvoicesController {

    @FXML private StackPane iconCollected;
    @FXML private StackPane iconUnpaid;
    @FXML private StackPane iconTotalInvoices;
    @FXML private StackPane iconOverdue;
    @FXML private VBox cardCollected;
    @FXML private VBox cardUnpaid;
    @FXML private VBox cardTotalInvoices;
    @FXML private VBox cardOverdue;

    @FXML private TextField txtSearchInvoice;
    @FXML private ComboBox<String> cbInvoiceStatus;
    @FXML private Button btnBatchInvoice;
    @FXML private Button btnAddInvoice;

    @FXML private VBox invoicesListContainer;

    // Details Panel
    @FXML private VBox invoiceDetailsPanel;
    @FXML private Label lblDetailCode;
    @FXML private Label lblDetailPeriod;
    @FXML private Label lblDetailStatus;
    @FXML private Label lblDetailTenant;
    @FXML private Label lblDetailRoomAmount;
    @FXML private Label lblDetailElectricAmount;
    @FXML private Label lblDetailWaterAmount;
    @FXML private Label lblDetailOtherAmount;
    @FXML private Label lblDetailDiscount;
    @FXML private Label lblDetailPreviousDebt;
    @FXML private Label lblDetailElectricReading;
    @FXML private Label lblDetailWaterReading;
    @FXML private Label lblDetailServiceBreakdown;
    @FXML private Label lblDetailTotalAmount;

    @FXML private Button btnPayInvoice;
    @FXML private Button btnPrintInvoice;
    @FXML private Button btnSendNotification;

    private final MockDataService dataService = MockDataService.getInstance();
    private Invoice selectedInvoice;
    private final Map<Invoice, HBox> rowMap = new HashMap<>();

    @FXML
    public void initialize() {
        setupIcons();
        setupFilter();
        setupMetricLinks();
        renderInvoicesList(dataService.getInvoices());
        setupActions();
    }

    private void setupMetricLinks() {
        link(cardCollected, "Đã thanh toán");
        link(cardUnpaid, "Chưa thanh toán");
        link(cardTotalInvoices, "Tất cả trạng thái");
        link(cardOverdue, "Sắp hạn");
    }

    private void link(VBox card, String filter) {
        card.setStyle(card.getStyle() + ";-fx-cursor: hand;");
        card.setOnMouseClicked(event -> {
            cbInvoiceStatus.setValue(filter);
            applyFilter();
        });
    }

    private void setupIcons() {
        iconCollected.getChildren().add(IconHelper.getIcon("money", 18, "#059669"));
        iconUnpaid.getChildren().add(IconHelper.getIcon("alert", 18, "#dc2626"));
        iconTotalInvoices.getChildren().add(IconHelper.getIcon("invoice", 18, "#2563eb"));
        iconOverdue.getChildren().add(IconHelper.getIcon("check", 18, "#059669"));
    }

    private void setupFilter() {
        cbInvoiceStatus.getItems().addAll("Tất cả trạng thái", "Đã thanh toán", "Chưa thanh toán", "Thanh toán một phần", "Sắp hạn", "Quá hạn");
        cbInvoiceStatus.setValue(MainController.consumeInvoiceStatusFilter());
        txtSearchInvoice.setText(MainController.consumeSearch());

        cbInvoiceStatus.setOnAction(e -> applyFilter());
        txtSearchInvoice.textProperty().addListener((obs, oldV, newV) -> applyFilter());
    }

    private void applyFilter() {
        String q = txtSearchInvoice.getText().trim().toLowerCase();
        String filter = cbInvoiceStatus.getValue();

        List<Invoice> filtered = new ArrayList<>();
        for (Invoice inv : dataService.getInvoices()) {
            boolean matchText = q.isEmpty() || inv.getCode().toLowerCase().contains(q) || inv.getTenantName().toLowerCase().contains(q) || inv.getRoomName().toLowerCase().contains(q);
            boolean matchFilter = filter.equals("Tất cả trạng thái") ||
                    (filter.equals("Đã thanh toán") && inv.getStatus() == Invoice.InvoiceStatus.PAID) ||
                    (filter.equals("Chưa thanh toán") && inv.getStatus() == Invoice.InvoiceStatus.UNPAID) ||
                    (filter.equals("Thanh toán một phần") && inv.getStatus() == Invoice.InvoiceStatus.PARTIAL) ||
                    (filter.equals("Quá hạn") && inv.getStatus() == Invoice.InvoiceStatus.OVERDUE) ||
                    (filter.equals("Sắp hạn") && inv.getStatus() == Invoice.InvoiceStatus.DUE_SOON);

            if (matchText && matchFilter) {
                filtered.add(inv);
            }
        }
        renderInvoicesList(filtered);
    }

    private void renderInvoicesList(List<Invoice> list) {
        invoicesListContainer.getChildren().clear();
        rowMap.clear();

        for (Invoice inv : list) {
            HBox card = new HBox(14);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("card");
            card.setStyle("-fx-padding: 12 16; -fx-cursor: hand;");

            // Room badge
            Label lblRoomBadge = new Label(inv.getRoomName());
            lblRoomBadge.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 6 10; -fx-background-radius: 6;");

            VBox info = new VBox(2);
            HBox titleRow = new HBox(8);
            titleRow.setAlignment(Pos.CENTER_LEFT);

            Label lblTitle = new Label(inv.getTenantName() + " (" + inv.getCode() + ")");
            lblTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");
            titleRow.getChildren().add(lblTitle);

            Label lblSub = new Label("Kỳ: " + inv.getMonthPeriod() + "  •  Tiền phòng: " + inv.getFormattedRoomAmount() + "  •  Điện nước: " + inv.getFormattedElectricAmount() + " + " + inv.getFormattedWaterAmount());
            lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            info.getChildren().addAll(titleRow, lblSub);

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblTotal = new Label(inv.getFormattedAmount());
            lblTotal.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");

            Label lblStatus = new Label(inv.getStatus().getLabel());
            lblStatus.getStyleClass().add(inv.getStatus().getStyleClass());

            card.getChildren().addAll(lblRoomBadge, info, spacer, lblTotal, lblStatus);
            card.setOnMouseClicked(e -> selectInvoice(inv));

            rowMap.put(inv, card);
            invoicesListContainer.getChildren().add(card);
        }

        if (!list.isEmpty()) {
            selectInvoice(list.get(0));
        }
    }

    private void selectInvoice(Invoice inv) {
        this.selectedInvoice = inv;

        for (Map.Entry<Invoice, HBox> entry : rowMap.entrySet()) {
            entry.getValue().setStyle("-fx-padding: 12 16; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: #f1f5f9; -fx-border-radius: 12;");
        }
        if (rowMap.containsKey(inv)) {
            rowMap.get(inv).setStyle("-fx-padding: 12 16; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: #2563eb; -fx-border-width: 1.8; -fx-border-radius: 12;");
        }

        lblDetailCode.setText(inv.getCode());
        lblDetailPeriod.setText(inv.getMonthPeriod() + " - " + inv.getRoomName());
        lblDetailTenant.setText(inv.getTenantName());
        lblDetailRoomAmount.setText(inv.getFormattedRoomAmount());
        lblDetailElectricAmount.setText(inv.getFormattedElectricAmount());
        lblDetailWaterAmount.setText(inv.getFormattedWaterAmount());
        lblDetailOtherAmount.setText(inv.getFormattedOtherAmount());
        lblDetailDiscount.setText(formatMoney(inv.getDiscount()));
        lblDetailPreviousDebt.setText(formatMoney(inv.getPreviousDebt()));
        lblDetailElectricReading.setText(inv.getOldElectricReading() + " → " + inv.getNewElectricReading());
        lblDetailWaterReading.setText(inv.getOldWaterReading() + " → " + inv.getNewWaterReading() + " (" + inv.getOccupantCount() + " người)");
        lblDetailServiceBreakdown.setText(formatMoney(inv.getCleaningAmount()) + " / " + formatMoney(inv.getTrashAmount()) + " / " + formatMoney(inv.getInternetAmount()));
        lblDetailTotalAmount.setText(inv.getFormattedAmount());

        lblDetailStatus.setText("●  " + inv.getStatus().getLabel());
        lblDetailStatus.getStyleClass().clear();
        lblDetailStatus.getStyleClass().add(inv.getStatus().getStyleClass());
    }

    private String formatMoney(long amount) {
        return String.format("%,d VNĐ", amount).replace(',', '.');
    }

    private void setupActions() {
        btnAddInvoice.setOnAction(e -> DialogHelper.showInvoiceDialog(null, () -> renderInvoicesList(dataService.getInvoices())));
        btnBatchInvoice.setOnAction(e -> DialogHelper.showInfo("Lập hàng loạt", "Tự động tính tiền toàn bộ", "Hệ thống đã tự động tính toán số điện nước và phát sinh 48 hóa đơn cho tháng mới!"));
        btnPayInvoice.setOnAction(e -> {
            if (selectedInvoice != null) {
                DialogHelper.showPaymentDialog(selectedInvoice, () -> {
                    renderInvoicesList(dataService.getInvoices());
                    selectInvoice(selectedInvoice);
                });
            }
        });
        btnPrintInvoice.setOnAction(e -> {
            if (selectedInvoice != null) {
                DialogHelper.showInvoicePreview(selectedInvoice);
            }
        });
        btnSendNotification.setOnAction(e -> {
            if (selectedInvoice != null) {
                DialogHelper.showInfo("Gửi thông báo", "Tin nhắn Zalo/SMS", "Đã gửi thông báo tiền phòng tháng này đến số điện thoại của " + selectedInvoice.getTenantName() + "!");
            }
        });
    }
}