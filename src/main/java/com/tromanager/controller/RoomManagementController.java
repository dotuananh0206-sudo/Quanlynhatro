package com.tromanager.controller;

import com.tromanager.model.Room;
import com.tromanager.model.RoomStatus;
import com.tromanager.service.MockDataService;
import com.tromanager.ui.IconHelper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.stream.Collectors;

public class RoomManagementController {

    @FXML private ComboBox<String> cbBuilding;
    @FXML private ComboBox<String> cbRoomStatus;
    @FXML private Button btnAddRoom;

    @FXML private VBox floorsContainer;

    // Details Panel
    @FXML private VBox detailsPanel;
    @FXML private Label lblDetailRoomName;
    @FXML private Label lblDetailLocation;
    @FXML private Label lblDetailStatus;

    @FXML private VBox boxTenantInfo;
    @FXML private Label lblTenantName;
    @FXML private Label lblPhoneNumber;
    @FXML private Label lblContractPeriod;

    @FXML private Label lblRentPrice;
    @FXML private Label lblElectricityPrice;
    @FXML private Label lblWaterPrice;
    @FXML private Label lblArea;

    @FXML private FlowPane amenitiesFlowPane;

    @FXML private Button btnCreateInvoice;
    @FXML private Button btnViewContract;
    @FXML private Button btnEditRoom;

    private final MockDataService dataService = MockDataService.getInstance();
    private Room selectedRoom;
    private String selectedStatusFilter = "Tất cả trạng thái";
    private String selectedSearch = "";
    private final Map<Room, VBox> cardNodeMap = new HashMap<>();

    @FXML
    public void initialize() {
        refreshBuildingChoices();
        cbRoomStatus.getItems().addAll("Tất cả trạng thái", "Trống", "Đang thuê", "Sắp hết HĐ");
        selectedStatusFilter = MainController.consumeRoomStatusFilter();
        cbRoomStatus.setValue(selectedStatusFilter);
        cbRoomStatus.setOnAction(e -> applyStatusFilter(cbRoomStatus.getValue()));
        cbBuilding.setOnAction(e -> renderRoomGrid(filteredRooms(selectedStatusFilter)));

        btnAddRoom.setOnAction(e -> {
            DialogHelper.showAddRoomDialog(newRoom -> {
                refreshBuildingChoices();
                cbBuilding.setValue(newRoom.getBuildingName());
                selectedSearch = "";
                renderRoomGrid(filteredRooms(selectedStatusFilter));
            });
        });

        setupActionButtons();
        selectedSearch = MainController.consumeSearch().toLowerCase();
        renderRoomGrid(filteredRooms(selectedStatusFilter, selectedSearch));
    }

    private void refreshBuildingChoices() {
        String selected = cbBuilding.getValue();
        LinkedHashSet<String> buildings = dataService.getRooms().stream()
                .map(Room::getBuildingName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        cbBuilding.getItems().setAll("Tất cả khu trọ");
        cbBuilding.getItems().addAll(buildings);
        if (selected != null && cbBuilding.getItems().contains(selected)) {
            cbBuilding.setValue(selected);
        } else {
            cbBuilding.setValue("Tất cả khu trọ");
        }
    }

    private void applyStatusFilter(String status) {
        selectedStatusFilter = status;
        renderRoomGrid(filteredRooms(selectedStatusFilter));
    }

    private List<Room> filteredRooms(String status) {
        return filteredRooms(status, selectedSearch);
    }

    private List<Room> filteredRooms(String status, String search) {
        String building = cbBuilding.getValue();
        return dataService.getRooms().stream()
                .filter(room -> "Tất cả khu trọ".equals(building) || room.getBuildingName().equals(building))
                .filter(room -> status == null || "Tất cả trạng thái".equals(status) || room.getStatus().getDisplayName().equals(status))
                .filter(room -> search == null || search.isEmpty() || room.getName().toLowerCase().contains(search) || room.getBuildingName().toLowerCase().contains(search) || room.getTenantName().toLowerCase().contains(search))
                .collect(Collectors.toList());
    }

    private void setupActionButtons() {
        btnCreateInvoice.setOnAction(e -> {
            if (selectedRoom != null) {
                DialogHelper.showInvoiceDialog(selectedRoom, () -> MainController.navigateTo("invoices"));
            }
        });

        btnViewContract.setOnAction(e -> {
            if (selectedRoom != null) {
                dataService.getContracts().stream()
                    .filter(contract -> contract.getRoomName().equals(selectedRoom.getName()))
                    .findFirst()
                    .ifPresentOrElse(DialogHelper::showContractPreview,
                        () -> DialogHelper.showInfo("Hợp đồng", "Chưa có hợp đồng", "Phòng " + selectedRoom.getName() + " hiện chưa có hợp đồng để xem."));
            }
        });

        btnEditRoom.setOnAction(e -> {
            if (selectedRoom != null) {
                DialogHelper.showEditRoomDialog(selectedRoom, this::renderRoomGrid);
            }
        });
    }

    private void renderRoomGrid() {
        renderRoomGrid(filteredRooms(selectedStatusFilter));
    }

    private void renderRoomGrid(List<Room> rooms) {
        floorsContainer.getChildren().clear();
        cardNodeMap.clear();

        List<Room> allRooms = rooms;
        Map<Integer, List<Room>> roomsByFloor = allRooms.stream()
                .collect(Collectors.groupingBy(Room::getFloor, TreeMap::new, Collectors.toList()));

        for (Map.Entry<Integer, List<Room>> entry : roomsByFloor.entrySet()) {
            int floorNum = entry.getKey();
            List<Room> roomsOnFloor = entry.getValue();

            VBox floorSection = new VBox(10);

            Label lblFloor = new Label("TẦNG " + floorNum);
            lblFloor.setStyle("-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #64748b; -fx-padding: 4 0;");
            floorSection.getChildren().add(lblFloor);

            // Grid of room cards (4 columns)
            GridPane grid = new GridPane();
            grid.setHgap(14);
            grid.setVgap(14);

            for (int i = 0; i < 4; i++) {
                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(25.0);
                grid.getColumnConstraints().add(col);
            }

            int col = 0;
            int row = 0;

            for (Room room : roomsOnFloor) {
                VBox card = createRoomCard(room);
                cardNodeMap.put(room, card);
                grid.add(card, col, row);

                col++;
                if (col == 4) {
                    col = 0;
                    row++;
                }
            }

            floorSection.getChildren().add(grid);
            floorsContainer.getChildren().add(floorSection);
        }

        // Select first room by default (P.101)
        if (!allRooms.isEmpty()) {
            selectRoom(allRooms.get(0));
        }
    }

    private VBox createRoomCard(Room room) {
        VBox card = new VBox(8);
        card.getStyleClass().add("room-card");

        // Top Row: Name, Floor, Status Badge, Dots
        HBox topRow = new HBox(6);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label lblName = new Label(room.getName());
        lblName.getStyleClass().add("room-name");

        Label lblFloor = new Label("Tầng " + room.getFloor());
        lblFloor.getStyleClass().add("room-sub");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblStatus = new Label(room.getStatus().getDisplayName());
        lblStatus.getStyleClass().add(getStatusBadgeClass(room.getStatus()));

        Button btnDots = new Button();
        btnDots.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");
        btnDots.setGraphic(IconHelper.getIcon("dots", 14, "#94a3b8"));
        btnDots.setOnAction(e -> {
            e.consume();
            DialogHelper.showInfo("Tùy chọn phòng", "Thao tác nhanh " + room.getName(), "Bạn có thể chỉnh sửa, đổi trạng thái hoặc tạo hóa đơn nhanh cho " + room.getName());
        });

        topRow.getChildren().addAll(lblName, lblFloor, spacer, lblStatus, btnDots);

        // Info Row 1: Tenant / Vacant
        Label lblInfo1 = new Label();
        if (room.getStatus() == RoomStatus.VACANT) {
            lblInfo1.setText("Trạng thái: Phòng trống");
            lblInfo1.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
        } else {
            lblInfo1.setText("Khách: " + room.getTenantName());
            lblInfo1.setStyle("-fx-font-size: 11px; -fx-text-fill: #475569;");
        }

        // Info Row 2: Price
        Label lblPrice = new Label(room.getFormattedPrice());
        lblPrice.getStyleClass().add("room-price");

        // Info Row 3: Contract Expiry / Area
        Label lblInfo2 = new Label();
        if (room.getStatus() == RoomStatus.VACANT) {
            lblInfo2.setText("Diện tích: " + room.getArea() + "m²");
            lblInfo2.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
        } else {
            lblInfo2.setText("Hạn HĐ: " + room.getContractEnd());
            lblInfo2.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
        }

        // Bottom Row: Amenities Icons
        HBox iconsRow = new HBox(10);
        iconsRow.setAlignment(Pos.CENTER_LEFT);
        iconsRow.setStyle("-fx-padding: 4 0 0 0;");

        // Bed icon
        iconsRow.getChildren().add(IconHelper.getIcon("bed", 14, "#94a3b8"));

        if (room.getAmenities().contains("Wifi")) {
            iconsRow.getChildren().add(IconHelper.getIcon("wifi", 14, "#94a3b8"));
        }

        if (room.getAmenities().contains("WC riêng")) {
            iconsRow.getChildren().add(IconHelper.getIcon("room", 14, "#94a3b8"));
        }

        card.getChildren().addAll(topRow, lblInfo1, lblPrice, lblInfo2, iconsRow);

        // Click listener
        card.setOnMouseClicked(e -> selectRoom(room));

        return card;
    }

    private String getStatusBadgeClass(RoomStatus status) {
        switch (status) {
            case RENTED: return "badge-green";
            case VACANT: return "badge-yellow";
            case EXPIRING_SOON: return "badge-red";
            default: return "badge-green";
        }
    }

    private void selectRoom(Room room) {
        // Deselect previous
        if (selectedRoom != null && cardNodeMap.containsKey(selectedRoom)) {
            VBox prevCard = cardNodeMap.get(selectedRoom);
            prevCard.getStyleClass().remove("room-card-selected");
            if (!prevCard.getStyleClass().contains("room-card")) {
                prevCard.getStyleClass().add("room-card");
            }
        }

        // Select new
        this.selectedRoom = room;
        if (cardNodeMap.containsKey(room)) {
            VBox newCard = cardNodeMap.get(room);
            newCard.getStyleClass().remove("room-card");
            if (!newCard.getStyleClass().contains("room-card-selected")) {
                newCard.getStyleClass().add("room-card-selected");
            }
        }

        // Update Right Details Panel
        lblDetailRoomName.setText("Chi Tiết Phòng " + room.getName());
        lblDetailLocation.setText(room.getBuildingName() + " - Tầng " + room.getFloor());

        lblDetailStatus.setText("●  " + room.getStatus().getDisplayName());
        lblDetailStatus.getStyleClass().clear();
        lblDetailStatus.getStyleClass().add(getStatusBadgeClass(room.getStatus()));

        if (room.getStatus() == RoomStatus.VACANT) {
            lblTenantName.setText("Chưa có");
            lblPhoneNumber.setText("---");
            lblContractPeriod.setText("Chưa ký HĐ");
        } else {
            lblTenantName.setText(room.getTenantName());
            lblPhoneNumber.setText(room.getPhoneNumber());
            lblContractPeriod.setText(room.getContractPeriod());
        }

        lblRentPrice.setText(room.getFormattedPrice() + "/tháng");
        lblElectricityPrice.setText(room.getFormattedElectricity());
        lblWaterPrice.setText(room.getFormattedWater());
        lblArea.setText(room.getArea() + "m²");

        amenitiesFlowPane.getChildren().clear();
        for (String item : room.getAmenities()) {
            Label tag = new Label(item);
            tag.getStyleClass().add("amenity-tag");
            amenitiesFlowPane.getChildren().add(tag);
        }
    }
}
