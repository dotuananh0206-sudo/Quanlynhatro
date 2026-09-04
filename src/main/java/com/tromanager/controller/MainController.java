package com.tromanager.controller;

import com.tromanager.model.*;
import com.tromanager.service.MockDataService;
import com.tromanager.ui.IconHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.*;

public class MainController {
    private static MainController activeController;
    private static String pendingRoomStatus = "Tất cả trạng thái";
    private static String pendingInvoiceStatus = "Tất cả trạng thái";
    private static String pendingContractFilter = "Tất cả hợp đồng";
    private static String pendingTaskStatus = "Tất cả trạng thái";
    private static String pendingSearch = "";

    @FXML private BorderPane rootPane;
    @FXML private StackPane logoContainer;
    @FXML private StackPane avatarContainer;
    @FXML private StackPane searchIconContainer;
    @FXML private Button btnNotification;
    @FXML private Button btnAddQuick;

    @FXML private Label lblBreadcrumb;
    @FXML private Label lblPageTitle;
    @FXML private TextField txtSearch;

    // Navigation buttons
    @FXML private Button btnNavDashboard;
    @FXML private Button btnNavRooms;
    @FXML private Button btnNavTenants;
    @FXML private Button btnNavContracts;
    @FXML private Button btnNavInvoices;
    @FXML private Button btnNavAssets;
    @FXML private Button btnNavTasks;
    @FXML private Button btnNavChat;
    @FXML private Button btnNavReports;
    @FXML private Button btnNavSettings;

    @FXML private Button btnLogout;

    @FXML private StackPane contentArea;

    private final Map<String, Node> viewCache = new HashMap<>();
    private final List<Button> navButtons = new ArrayList<>();
    private final MockDataService dataService = MockDataService.getInstance();

    @FXML
    public void initialize() {
        activeController = this;
        setupIcons();
        setupNavigation();
        setupActions();

        // Load default Dashboard view
        switchView("Dashboard", btnNavDashboard, "System  /  Tổng quan", "Bảng Tổng Quan Dashboard", "/com/tromanager/fxml/dashboard_view.fxml");
    }

    private void setupIcons() {
        // App Logo
        logoContainer.getChildren().add(IconHelper.getLogoIcon(20, "#ffffff"));

        // User Avatar
        avatarContainer.getChildren().add(IconHelper.getIcon("tenant", 16, "#ffffff"));

        // Search icon
        searchIconContainer.getChildren().add(IconHelper.getIcon("search", 14, "#94a3b8"));

        // Notification Bell
        btnNotification.setGraphic(IconHelper.getIcon("bell", 16, "#475569"));

        // Nav Icons
        btnNavDashboard.setGraphic(IconHelper.getIcon("dashboard", 16, "#94a3b8"));
        btnNavRooms.setGraphic(IconHelper.getIcon("building", 16, "#94a3b8"));
        btnNavTenants.setGraphic(IconHelper.getIcon("users", 16, "#94a3b8"));
        btnNavContracts.setGraphic(IconHelper.getIcon("contract", 16, "#94a3b8"));
        btnNavInvoices.setGraphic(IconHelper.getIcon("invoice", 16, "#94a3b8"));
        btnNavAssets.setGraphic(IconHelper.getIcon("asset", 16, "#94a3b8"));
        btnNavTasks.setGraphic(IconHelper.getIcon("task", 16, "#94a3b8"));
        btnNavChat.setGraphic(IconHelper.getIcon("chat", 16, "#94a3b8"));
        btnNavReports.setGraphic(IconHelper.getIcon("report", 16, "#94a3b8"));
        btnNavSettings.setGraphic(IconHelper.getIcon("settings", 16, "#94a3b8"));

        // Logout
        btnLogout.setGraphic(IconHelper.getIcon("logout", 16, "#ef4444"));
    }

    private void setupNavigation() {
        navButtons.addAll(Arrays.asList(
                btnNavDashboard, btnNavRooms, btnNavTenants, btnNavContracts,
                btnNavInvoices, btnNavAssets, btnNavTasks, btnNavChat,
                btnNavReports, btnNavSettings
        ));

        btnNavDashboard.setOnAction(e -> switchView("Dashboard", btnNavDashboard, "System  /  Tổng quan", "Bảng Tổng Quan Dashboard", "/com/tromanager/fxml/dashboard_view.fxml"));
        btnNavRooms.setOnAction(e -> switchView("Rooms", btnNavRooms, "System  /  Khu trọ & Phòng", "Quản Lý Khu Trọ & Phòng", "/com/tromanager/fxml/room_management_view.fxml"));
        btnNavTenants.setOnAction(e -> switchView("Tenants", btnNavTenants, "System  /  Khách thuê", "Quản Lý Khách Thuê", "/com/tromanager/fxml/tenants_view.fxml"));
        btnNavContracts.setOnAction(e -> switchView("Contracts", btnNavContracts, "System  /  Hợp đồng", "Quản Lý Hợp Đồng", "/com/tromanager/fxml/contracts_view.fxml"));
        btnNavInvoices.setOnAction(e -> switchView("Invoices", btnNavInvoices, "System  /  Hóa đơn", "Quản Lý Hóa Đơn & Thu Tiền", "/com/tromanager/fxml/invoices_view.fxml"));
        btnNavAssets.setOnAction(e -> switchView("Assets", btnNavAssets, "System  /  Tài sản", "Quản Lý Tài Sản & Trang Thiết Bị", "/com/tromanager/fxml/assets_view.fxml"));
        btnNavTasks.setOnAction(e -> switchView("Tasks", btnNavTasks, "System  /  Công việc", "Danh Sách Công Việc & Bảo Trì", "/com/tromanager/fxml/tasks_view.fxml"));
        btnNavChat.setOnAction(e -> switchView("Chat", btnNavChat, "System  /  Tin nhắn & Thông báo", "Tin Nhắn & Thông Báo", "/com/tromanager/fxml/communication_view.fxml"));
        btnNavReports.setOnAction(e -> switchView("Reports", btnNavReports, "System  /  Báo cáo", "Báo Cáo Thống Kê & Doanh Thu", "/com/tromanager/fxml/reports_view.fxml"));
        btnNavSettings.setOnAction(e -> switchView("Settings", btnNavSettings, "System  /  Cài đặt", "Cài Đặt Hệ Thống & Đơn Giá", "/com/tromanager/fxml/settings_view.fxml"));
    }

    private void setupActions() {
        btnAddQuick.setOnAction(e -> DialogHelper.showAddQuickDialog());

        btnNotification.setOnAction(e -> DialogHelper.showInfo("Thông Báo", "Trung tâm thông báo", "Bạn có 4 hóa đơn sắp đến hạn, 4 hợp đồng sắp hết hạn và 2 sự cố kỹ thuật cần xử lý!"));

        btnLogout.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Đăng xuất");
            alert.setHeaderText("Xác nhận đăng xuất");
            alert.setContentText("Bạn có chắc chắn muốn đăng xuất khỏi phần mềm TroManager?");
            alert.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    Platform.exit();
                }
            });
        });

        txtSearch.setOnAction(e -> {
            String q = txtSearch.getText().trim();
            if (!q.isEmpty()) {
                showSearchResults(q);
            }
        });
    }

    private void showSearchResults(String query) {
        String normalized = query.toLowerCase();
        if (dataService.getRooms().stream().anyMatch(room -> contains(room.getName(), normalized) || contains(room.getBuildingName(), normalized) || contains(room.getTenantName(), normalized))) {
            navigateTo("rooms", "Tất cả trạng thái", query);
        } else if (dataService.getTenants().stream().anyMatch(tenant -> contains(tenant.getName(), normalized) || contains(tenant.getPhone(), normalized) || contains(tenant.getRoomName(), normalized) || contains(tenant.getIdCard(), normalized))) {
            navigateTo("tenants");
        } else if (dataService.getContracts().stream().anyMatch(contract -> contains(contract.getCode(), normalized) || contains(contract.getTenantName(), normalized) || contains(contract.getRoomName(), normalized))) {
            navigateTo("contracts", "Tất cả hợp đồng", query);
        } else if (dataService.getInvoices().stream().anyMatch(invoice -> contains(invoice.getCode(), normalized) || contains(invoice.getTenantName(), normalized) || contains(invoice.getRoomName(), normalized) || contains(invoice.getMonthPeriod(), normalized))) {
            navigateTo("invoices", "Tất cả trạng thái", query);
        } else if (dataService.getTasks().stream().anyMatch(task -> contains(task.getTitle(), normalized) || contains(task.getLocation(), normalized))) {
            navigateTo("tasks", null, query);
        } else {
            DialogHelper.showInfo("Tìm kiếm", "Không tìm thấy kết quả", "Không có phòng, khách thuê, hợp đồng hoặc hóa đơn phù hợp với \"" + query + "\".");
        }
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    public static void navigateTo(String module) {
        navigateTo(module, null, null);
    }

    public static void navigateTo(String module, String filter) {
        navigateTo(module, filter, null);
    }

    public static void navigateTo(String module, String filter, String search) {
        if (activeController == null) return;
        pendingRoomStatus = "Tất cả trạng thái";
        pendingInvoiceStatus = "Tất cả trạng thái";
        pendingContractFilter = "Tất cả hợp đồng";
        pendingTaskStatus = "Tất cả trạng thái";
        pendingSearch = search == null ? "" : search;
        if ("rooms".equalsIgnoreCase(module) && filter != null) pendingRoomStatus = filter;
        if ("invoices".equalsIgnoreCase(module) && filter != null) pendingInvoiceStatus = filter;
        if ("contracts".equalsIgnoreCase(module) && filter != null) pendingContractFilter = filter;
        if ("tasks".equalsIgnoreCase(module) && filter != null) pendingTaskStatus = filter;
        activeController.viewCache.remove(module.substring(0, 1).toUpperCase() + module.substring(1).toLowerCase());
        switch (module.toLowerCase()) {
            case "rooms" -> activeController.switchView("Rooms", activeController.btnNavRooms, "System  /  Khu trọ & Phòng", "Quản Lý Khu Trọ & Phòng", "/com/tromanager/fxml/room_management_view.fxml");
            case "tenants" -> activeController.switchView("Tenants", activeController.btnNavTenants, "System  /  Khách thuê", "Quản Lý Khách Thuê", "/com/tromanager/fxml/tenants_view.fxml");
            case "contracts" -> activeController.switchView("Contracts", activeController.btnNavContracts, "System  /  Hợp đồng", "Quản Lý Hợp Đồng", "/com/tromanager/fxml/contracts_view.fxml");
            case "invoices" -> activeController.switchView("Invoices", activeController.btnNavInvoices, "System  /  Hóa đơn", "Quản Lý Hóa Đơn & Thu Tiền", "/com/tromanager/fxml/invoices_view.fxml");
            case "tasks" -> activeController.switchView("Tasks", activeController.btnNavTasks, "System  /  Công việc", "Danh Sách Công Việc & Bảo Trì", "/com/tromanager/fxml/tasks_view.fxml");
            default -> { }
        }
    }

    public static String consumeRoomStatusFilter() {
        String value = pendingRoomStatus;
        pendingRoomStatus = "Tất cả trạng thái";
        return value;
    }

    public static String consumeInvoiceStatusFilter() {
        String value = pendingInvoiceStatus;
        pendingInvoiceStatus = "Tất cả trạng thái";
        return value;
    }

    public static String consumeContractFilter() {
        String value = pendingContractFilter;
        pendingContractFilter = "Tất cả hợp đồng";
        return value;
    }

    public static String consumeTaskStatus() {
        String value = pendingTaskStatus;
        pendingTaskStatus = "Tất cả trạng thái";
        return value;
    }

    public static String consumeSearch() {
        String value = pendingSearch;
        pendingSearch = "";
        return value;
    }

    private void setActiveNavButton(Button activeBtn) {
        for (Button btn : navButtons) {
            btn.getStyleClass().remove("nav-button-active");
            if (!btn.getStyleClass().contains("nav-button")) {
                btn.getStyleClass().add("nav-button");
            }
        }
        if (activeBtn != null && !activeBtn.getStyleClass().contains("nav-button-active")) {
            activeBtn.getStyleClass().add("nav-button-active");
        }
    }

    private void switchView(String key, Button navButton, String breadcrumb, String title, String fxmlPath) {
        setActiveNavButton(navButton);
        lblBreadcrumb.setText(breadcrumb);
        lblPageTitle.setText(title);

        try {
            if (!viewCache.containsKey(key)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Node node = loader.load();
                viewCache.put(key, node);
            }
            contentArea.getChildren().setAll(viewCache.get(key));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}