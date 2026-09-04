package com.tromanager.controller;

import com.tromanager.model.*;
import com.tromanager.service.MockDataService;
import com.tromanager.ui.DonutChart;
import com.tromanager.ui.IconHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private StackPane iconTotalRooms;
    @FXML private StackPane iconRentedRooms;
    @FXML private StackPane iconVacantRooms;
    @FXML private StackPane iconMonthlyRevenue;
    @FXML private StackPane iconUnpaidRevenue;
    @FXML private VBox cardTotalRooms;
    @FXML private VBox cardRentedRooms;
    @FXML private VBox cardVacantRooms;
    @FXML private VBox cardMonthlyRevenue;
    @FXML private VBox cardUnpaidRevenue;
    @FXML private Label lblTotalRooms;
    @FXML private Label lblRentedRooms;
    @FXML private Label lblVacantRooms;
    @FXML private Label lblMonthlyRevenue;
    @FXML private Label lblUnpaidRevenue;

    @FXML private HBox barChartContainer;
    @FXML private StackPane donutChartContainer;

    @FXML private StackPane iconInvoicesHeader;
    @FXML private StackPane iconContractsHeader;
    @FXML private StackPane iconTasksHeader;

    @FXML private VBox invoicesListContainer;
    @FXML private VBox contractsListContainer;
    @FXML private VBox tasksListContainer;
    @FXML private VBox invoicesSection;
    @FXML private VBox contractsSection;
    @FXML private VBox tasksSection;

    private final MockDataService dataService = MockDataService.getInstance();

    @FXML
    public void initialize() {
        setupIcons();
        setupBarChart();
        setupDonutChart();
        setupMetrics();
        populateInvoices();
        populateContracts();
        populateTasks();
    }

    private void setupMetrics() {
        List<Room> rooms = dataService.getRooms();
        long rented = rooms.stream().filter(room -> room.getStatus() == RoomStatus.RENTED).count();
        long vacant = rooms.stream().filter(room -> room.getStatus() == RoomStatus.VACANT).count();
        long monthlyRevenue = dataService.getInvoices().stream()
                .filter(invoice -> invoice.getStatus() == Invoice.InvoiceStatus.PAID)
                .mapToLong(Invoice::getAmount).sum();
        long unpaidRevenue = dataService.getInvoices().stream()
                .filter(invoice -> invoice.getStatus() != Invoice.InvoiceStatus.PAID)
                .mapToLong(Invoice::getAmount).sum();
        lblTotalRooms.setText(String.valueOf(rooms.size()));
        lblRentedRooms.setText(String.valueOf(rented));
        lblVacantRooms.setText(String.valueOf(vacant));
        lblMonthlyRevenue.setText(formatMoney(monthlyRevenue));
        lblUnpaidRevenue.setText(formatMoney(unpaidRevenue));

        clickable(cardTotalRooms, () -> MainController.navigateTo("rooms", "Tất cả trạng thái"));
        clickable(cardRentedRooms, () -> MainController.navigateTo("rooms", "Đang thuê"));
        clickable(cardVacantRooms, () -> MainController.navigateTo("rooms", "Trống"));
        clickable(cardMonthlyRevenue, () -> MainController.navigateTo("invoices", "Đã thanh toán"));
        clickable(cardUnpaidRevenue, () -> MainController.navigateTo("invoices", "Chưa thanh toán"));
        clickable(invoicesSection, () -> MainController.navigateTo("invoices", "Sắp hạn"));
        clickable(contractsSection, () -> MainController.navigateTo("contracts", "Sắp hết hạn (< 30 ngày)"));
        clickable(tasksSection, () -> MainController.navigateTo("tasks", "Chưa hoàn thành"));
    }

    private void clickable(Region region, Runnable action) {
        region.setCursor(Cursor.HAND);
        region.setOnMouseClicked(event -> {
            event.consume();
            action.run();
        });
    }

    private String roomSummary(List<Room> rooms) {
        if (rooms.isEmpty()) return "Không có phòng phù hợp.";
        return rooms.stream().map(room -> room.getName() + " | " + room.getBuildingName() + " | " + room.getStatus().getDisplayName() + " | " + room.getFormattedPrice()).collect(Collectors.joining("\n"));
    }

    private String invoiceSummary(List<Invoice> invoices) {
        if (invoices.isEmpty()) return "Không có hóa đơn phù hợp.";
        return invoices.stream().map(invoice -> invoice.getCode() + " | " + invoice.getRoomName() + " | " + invoice.getTenantName() + " | " + invoice.getFormattedAmount() + " | Hạn: " + invoice.getDueDate()).collect(Collectors.joining("\n"));
    }

    private String contractSummary(List<Contract> contracts) {
        if (contracts.isEmpty()) return "Không có hợp đồng sắp hết hạn.";
        return contracts.stream().map(contract -> contract.getCode() + " | " + contract.getRoomName() + " | " + contract.getTenantName() + " | Hết hạn: " + contract.getExpiryDate()).collect(Collectors.joining("\n"));
    }

    private String taskSummary(List<TaskItem> tasks) {
        if (tasks.isEmpty()) return "Không có công việc cần xử lý.";
        return tasks.stream().map(task -> task.getTitle() + " | " + task.getLocation() + " | " + task.getTime() + " | " + task.getPriority().getLabel()).collect(Collectors.joining("\n"));
    }

    private String formatMoney(long amount) {
        return String.format("%,dđ", amount).replace(',', '.');
    }

    private void setupIcons() {
        iconTotalRooms.getChildren().add(IconHelper.getIcon("building", 18, "#2563eb"));
        iconRentedRooms.getChildren().add(IconHelper.getIcon("key", 18, "#059669"));
        iconVacantRooms.getChildren().add(IconHelper.getIcon("lock", 18, "#d97706"));
        iconMonthlyRevenue.getChildren().add(IconHelper.getIcon("money", 18, "#2563eb"));
        iconUnpaidRevenue.getChildren().add(IconHelper.getIcon("alert", 18, "#dc2626"));

        iconInvoicesHeader.getChildren().add(IconHelper.getIcon("document", 18, "#2563eb"));
        iconContractsHeader.getChildren().add(IconHelper.getIcon("shield", 18, "#d97706"));
        iconTasksHeader.getChildren().add(IconHelper.getIcon("check", 18, "#dc2626"));
    }

    private void setupBarChart() {
        barChartContainer.getChildren().clear();
        List<RevenueData> list = dataService.getRevenueData();
        double maxRevenue = 170.0;
        double maxBarHeight = 120.0;

        for (RevenueData item : list) {
            VBox col = new VBox(6);
            col.setAlignment(Pos.BOTTOM_CENTER);

            // Value label
            Label lblVal = new Label(item.getFormattedValue());
            lblVal.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");

            // Blue bar with rounded top corners
            double barHeight = (item.getRevenueMillions() / maxRevenue) * maxBarHeight;
            StackPane bar = new StackPane();
            bar.setPrefSize(28, barHeight);
            bar.setMinSize(28, barHeight);
            bar.setMaxSize(28, barHeight);
            bar.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 6 6 0 0; -fx-cursor: hand;");

            bar.setOnMouseEntered(e -> bar.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 6 6 0 0; -fx-cursor: hand;"));
            bar.setOnMouseExited(e -> bar.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 6 6 0 0; -fx-cursor: hand;"));

            // Month label
            Label lblMonth = new Label(item.getMonth());
            lblMonth.setStyle("-fx-font-size: 11px; -fx-font-weight: 500; -fx-text-fill: #64748b;");

            col.getChildren().addAll(lblVal, bar, lblMonth);
            barChartContainer.getChildren().add(col);
        }
    }

    private void setupDonutChart() {
        DonutChart chart = new DonutChart(dataService.getOccupancyRate() * 100);
        donutChartContainer.getChildren().add(chart);
    }

    private void populateInvoices() {
        invoicesListContainer.getChildren().clear();
        for (Invoice inv : dataService.getInvoices().stream().filter(invoice -> invoice.getStatus() == Invoice.InvoiceStatus.DUE_SOON).collect(Collectors.toList())) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 8 0; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

            Label lblRoom = new Label(inv.getRoomName());
            lblRoom.setPrefWidth(48);
            lblRoom.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-size: 12px;");

            Label lblAmount = new Label(inv.getFormattedAmount());
            lblAmount.setPrefWidth(90);
            lblAmount.setStyle("-fx-font-weight: 700; -fx-text-fill: #1e293b; -fx-font-size: 12px;");

            Label lblDue = new Label(inv.getDueDate());
            lblDue.setPrefWidth(80);
            lblDue.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblBadge = new Label(inv.getStatus().getLabel());
            lblBadge.getStyleClass().add(inv.getStatus().getStyleClass());

            row.getChildren().addAll(lblRoom, lblAmount, lblDue, spacer, lblBadge);
            clickable(row, () -> MainController.navigateTo("invoices", "Sắp hạn", inv.getCode()));
            invoicesListContainer.getChildren().add(row);
        }
    }

    private void populateContracts() {
        contractsListContainer.getChildren().clear();
        for (Contract c : dataService.getContracts().stream().filter(contract -> contract.getStatus() == Contract.Status.EXPIRING).collect(Collectors.toList())) {
            HBox card = new HBox(10);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-padding: 8 0; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

            // Room Badge
            Label lblRoom = new Label(c.getRoomName());
            lblRoom.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 4 8; -fx-background-radius: 6;");

            VBox info = new VBox(2);
            Label lblName = new Label(c.getTenantName());
            lblName.setStyle("-fx-font-weight: 700; -fx-text-fill: #1e293b; -fx-font-size: 12px;");
            Label lblDate = new Label("Hết hạn: " + c.getExpiryDate());
            lblDate.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            info.getChildren().addAll(lblName, lblDate);

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblDays = new Label(c.getRemainingText());
            lblDays.getStyleClass().add("badge-yellow");

            card.getChildren().addAll(lblRoom, info, spacer, lblDays);
            clickable(card, () -> MainController.navigateTo("contracts", "Sắp hết hạn (< 30 ngày)", c.getCode()));
            contractsListContainer.getChildren().add(card);
        }
    }

    private void populateTasks() {
        tasksListContainer.getChildren().clear();
        for (TaskItem task : dataService.getTasks()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 8 0; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

            VBox info = new VBox(2);
            Label lblTitle = new Label(task.getTitle());
            lblTitle.setStyle("-fx-font-weight: 700; -fx-text-fill: #1e293b; -fx-font-size: 12px;");
            Label lblSub = new Label(task.getSubtitle());
            lblSub.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            info.getChildren().addAll(lblTitle, lblSub);

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblPriority = new Label(task.getPriority().getLabel());
            lblPriority.getStyleClass().add(task.getPriority().getStyleClass());

            row.getChildren().addAll(info, spacer, lblPriority);
            clickable(row, () -> MainController.navigateTo("tasks", null, task.getTitle()));
            tasksListContainer.getChildren().add(row);
        }
    }
}
