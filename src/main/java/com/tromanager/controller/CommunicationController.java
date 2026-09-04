package com.tromanager.controller;

import com.tromanager.model.MessageItem;
import com.tromanager.service.MockDataService;
import com.tromanager.ui.IconHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

import java.util.*;

public class CommunicationController {

    @FXML private StackPane iconAnnounce;
    @FXML private StackPane iconUnread;
    @FXML private StackPane iconRequests;
    @FXML private StackPane iconSatisfaction;

    @FXML private Button btnBroadcast;
    @FXML private TextField txtSearchChat;
    @FXML private VBox chatListContainer;
    @FXML private ToggleButton btnInbox;
    @FXML private ToggleButton btnNotifications;
    @FXML private ToggleButton btnSent;
    @FXML private ToggleButton btnArchived;
    @FXML private ComboBox<String> cboMessageFilter;

    @FXML private Label lblChatHeaderInitials;
    @FXML private Label lblChatHeaderName;
    @FXML private Button btnCallTenant;
    @FXML private Button btnMarkRead;
    @FXML private Button btnArchive;
    @FXML private Button btnRestore;
    @FXML private Button btnAttach;

    @FXML private VBox messagesStreamContainer;
    @FXML private TextField txtMessageInput;
    @FXML private Button btnSendMessage;
    @FXML private Label lblSentCount;
    @FXML private Label lblUnreadCount;
    @FXML private Label lblNotificationCount;
    @FXML private Label lblResponseRate;
    @FXML private VBox metricSent;
    @FXML private VBox metricUnread;
    @FXML private VBox metricRequests;
    @FXML private VBox metricSatisfaction;

    private final MockDataService dataService = MockDataService.getInstance();
    private String currentMode = "Hộp thư";
    private String selectedRoom = "P.101";
    private final Set<String> archivedRooms = new HashSet<>();
    private final Set<String> readRooms = new HashSet<>();
    private final List<String> drafts = new ArrayList<>();
    private final List<String> scheduledMessages = new ArrayList<>();
    private String attachedFileName;

    @FXML
    public void initialize() {
        setupIcons();
        setupFilters();
        setupMetricActions();
        updateMetrics();
        renderChatList();
        renderMessagesStream();
        setupActions();
    }

    private void setupIcons() {
        iconAnnounce.getChildren().add(IconHelper.getIcon("chat", 18, "#2563eb"));
        iconUnread.getChildren().add(IconHelper.getIcon("chat", 18, "#d97706"));
        iconRequests.getChildren().add(IconHelper.getIcon("alert", 18, "#dc2626"));
        iconSatisfaction.getChildren().add(IconHelper.getIcon("check", 18, "#059669"));
    }

    private void renderChatList() {
        chatListContainer.getChildren().clear();

        if ("Đã gửi".equals(currentMode)) {
            renderSentList();
            return;
        }
        if ("Thông báo".equals(currentMode)) {
            renderNotificationList();
            return;
        }

        List<String[]> channels = Arrays.asList(
                new String[]{"Nguyễn Văn An", "P.101", "Dạ em đã chuyển khoản rồi ạ!", "09:15", "1"},
                new String[]{"Trần Thị Lan", "P.102", "Vòi sen tắm bị rỉ nước anh ơi", "10:05", "2"},
                new String[]{"Phạm Thị Thùy", "P.202", "Anh ơi em muốn gia hạn thêm 1 năm", "11:30", "0"},
                new String[]{"Vũ Hoàng Long", "P.203", "Tuần này em về quê anh nhé", "Hôm qua", "0"},
                new String[]{"Trịnh Quốc Bảo", "P.301", "Cảm ơn anh đã hỗ trợ!", "24/08", "0"}
        );

        String query = txtSearchChat.getText().trim().toLowerCase(Locale.ROOT);
        String filter = cboMessageFilter.getValue();
        for (String[] ch : channels) {
            boolean matchesQuery = query.isEmpty() || String.join(" ", ch).toLowerCase(Locale.ROOT).contains(query);
            boolean isArchived = archivedRooms.contains(ch[1]);
            boolean matchesFilter = filter == null || "Tất cả".equals(filter) || ("Chưa đọc".equals(filter) && !"0".equals(ch[4]) && !readRooms.contains(ch[1])) || ("Ưu tiên".equals(filter) && ("P.102".equals(ch[1]) || "P.202".equals(ch[1])));
            boolean matchesMode = "Đã lưu trữ".equals(currentMode) ? isArchived : !isArchived;
            if (!matchesQuery || !matchesFilter || !matchesMode) continue;
            HBox item = new HBox(10);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10 12; -fx-background-radius: 8; -fx-cursor: hand;");

            StackPane av = new StackPane();
            av.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 50%; -fx-pref-width: 34px; -fx-pref-height: 34px;");
            Label lblIn = new Label(ch[0].substring(0, 1));
            lblIn.setStyle("-fx-font-weight: 800; -fx-text-fill: #2563eb; -fx-font-size: 12px;");
            av.getChildren().add(lblIn);

            VBox info = new VBox(2);
            HBox top = new HBox(4);
            Label lblN = new Label(ch[0]);
            lblN.setStyle("-fx-font-weight: 700; -fx-font-size: 12px; -fx-text-fill: #0f172a;");
            Label lblR = new Label("(" + ch[1] + ")");
            lblR.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            top.getChildren().addAll(lblN, lblR);

            Label lblLast = new Label(ch[2]);
            lblLast.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-max-width: 150;");
            info.getChildren().addAll(top, lblLast);

            Pane sp = new Pane();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label lblT = new Label(ch[3]);
            lblT.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8;");

            item.getChildren().addAll(av, info, sp, lblT);
            item.setOnMouseClicked(e -> {
                selectedRoom = ch[1];
                lblChatHeaderName.setText(ch[0] + " (" + ch[1] + ")");
                lblChatHeaderInitials.setText(ch[0].substring(0, 1));
                updateArchiveControls();
                renderMessagesStream();
            });

            chatListContainer.getChildren().add(item);
        }
        if (chatListContainer.getChildren().isEmpty()) {
            Label empty = new Label("Không có tin nhắn phù hợp");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-padding: 18;");
            chatListContainer.getChildren().add(empty);
        }
    }

    private void renderSentList() {
        List<MessageItem> sentMessages = dataService.getMessages().stream()
                .filter(MessageItem::isOwner)
                .collect(java.util.stream.Collectors.toList());
        if (sentMessages.isEmpty()) {
            addEmptyState("Chưa có thông báo đã gửi");
            return;
        }
        for (MessageItem message : sentMessages) {
            HBox item = new HBox(10);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10 12; -fx-background-radius: 8; -fx-cursor: hand;");
            StackPane icon = new StackPane();
            icon.setStyle("-fx-background-color: #dbeafe; -fx-background-radius: 50%; -fx-pref-width: 34px; -fx-pref-height: 34px;");
            Label iconLabel = new Label("S");
            iconLabel.setStyle("-fx-font-weight: 800; -fx-text-fill: #2563eb;");
            icon.getChildren().add(iconLabel);
            VBox info = new VBox(2);
            Label recipient = new Label("Đã gửi tới " + message.getRoom());
            recipient.setStyle("-fx-font-weight: 700; -fx-font-size: 12px; -fx-text-fill: #0f172a;");
            Label content = new Label(message.getContent());
            content.setMaxWidth(190);
            content.setEllipsisString("...");
            content.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            info.getChildren().addAll(recipient, content);
            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label time = new Label(message.getTime());
            time.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8;");
            item.getChildren().addAll(icon, info, spacer, time);
            item.setOnMouseClicked(e -> showSentDetail(message));
            chatListContainer.getChildren().add(item);
        }
    }

    private void renderNotificationList() {
        addNotificationListItem("Hóa đơn tháng 08/2024", "4 khách thuê chưa thanh toán", "Cần xử lý", "#fef3c7");
        addNotificationListItem("Bảo trì phòng P.102", "Vòi sen bị rỉ nước", "Ưu tiên cao", "#fee2e2");
        addNotificationListItem("Hợp đồng sắp hết hạn", "P.202 và P.203 cần nhắc gia hạn", "Nhắc việc", "#dbeafe");
    }

    private void addNotificationListItem(String title, String summary, String tag, String color) {
        VBox item = new VBox(4);
        item.setStyle("-fx-background-color: " + color + "; -fx-padding: 10 12; -fx-background-radius: 8; -fx-cursor: hand;");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a;");
        Label summaryLabel = new Label(summary + "  •  " + tag);
        summaryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #475569;");
        item.getChildren().addAll(titleLabel, summaryLabel);
        item.setOnMouseClicked(e -> showNotificationDetail(title, summary, tag));
        chatListContainer.getChildren().add(item);
    }

    private void addEmptyState(String message) {
        Label empty = new Label(message);
        empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-padding: 18;");
        chatListContainer.getChildren().add(empty);
    }

    private void setupFilters() {
        cboMessageFilter.getItems().addAll("Tất cả", "Chưa đọc", "Ưu tiên");
        cboMessageFilter.getSelectionModel().selectFirst();
        ToggleGroup group = new ToggleGroup();
        btnInbox.setToggleGroup(group);
        btnNotifications.setToggleGroup(group);
        btnSent.setToggleGroup(group);
        btnArchived.setToggleGroup(group);
        btnInbox.setOnAction(e -> switchMode("Hộp thư"));
        btnNotifications.setOnAction(e -> switchMode("Thông báo"));
        btnSent.setOnAction(e -> switchMode("Đã gửi"));
        btnArchived.setOnAction(e -> switchMode("Đã lưu trữ"));
        txtSearchChat.textProperty().addListener((obs, oldValue, newValue) -> renderChatList());
        cboMessageFilter.setOnAction(e -> renderChatList());
    }

    private void setupMetricActions() {
        metricSent.setOnMouseClicked(e -> showMetricDetails("Thông báo đã gửi", "Đã gửi", "12 thông báo đã được gửi qua Zalo, SMS và Email. 10 tin đã nhận, 2 tin đang chờ xác nhận."));
        metricUnread.setOnMouseClicked(e -> {
            cboMessageFilter.getSelectionModel().select("Chưa đọc");
            switchMode("Hộp thư");
            showMetricDetails("Tin nhắn chưa đọc", "Hộp thư", "Các cuộc trò chuyện cần phản hồi được lọc ở danh sách bên trái. Chọn một cuộc trò chuyện để xử lý ngay.");
        });
        metricRequests.setOnMouseClicked(e -> showMetricDetails("Yêu cầu sửa chữa mới", "Thông báo", "Có 3 yêu cầu mới: vòi sen P.102, điều hòa P.305 và bóng đèn hành lang tầng 2. Bạn có thể chuyển tiếp cho đội bảo trì từ mục Công việc."));
        metricSatisfaction.setOnMouseClicked(e -> showMetricDetails("Độ hài lòng cư dân", "Thông báo", "Điểm hài lòng hiện tại là 98.5%, dựa trên 40 phản hồi gần nhất. 39 đánh giá 5 sao và 1 đánh giá 4 sao."));
    }

    private void showMetricDetails(String title, String mode, String detail) {
        currentMode = mode;
        updateArchiveControls();
        renderChatList();
        messagesStreamContainer.getChildren().clear();
        if ("Đã gửi".equals(mode)) {
            renderSentDetails();
            return;
        }
        addNotification(title, detail, "Vừa cập nhật", "Chi tiết", "#e0f2fe");
        if ("Thông báo".equals(mode)) {
            addNotification("Thao tác nhanh", "Chọn Thông báo ở danh sách bên trái để xem các cảnh báo hóa đơn, bảo trì và hợp đồng.", "Ngay bây giờ", "Đi đến danh sách", "#f0fdf4");
        }
    }

    private void renderSentDetails() {
        List<MessageItem> sentMessages = dataService.getMessages().stream()
                .filter(MessageItem::isOwner)
                .collect(java.util.stream.Collectors.toList());
        if (sentMessages.isEmpty()) {
            addNotification("Thông báo đã gửi", "Chưa có bản tin nào được gửi trong hệ thống.", "Chưa có dữ liệu", "Trống", "#f8fafc");
            return;
        }
        for (MessageItem message : sentMessages) {
            addNotification("Tin đã gửi tới " + message.getRoom(), message.getContent(), message.getTime(), "Đã gửi", "#eff6ff");
        }
    }

    private void showSentDetail(MessageItem message) {
        selectedRoom = message.getRoom();
        lblChatHeaderName.setText("Đã gửi tới " + message.getRoom());
        messagesStreamContainer.getChildren().clear();
        addNotification("Thông báo đã gửi", message.getContent(), message.getTime(), "Đã ghi nhận", "#eff6ff");
    }

    private void showNotificationDetail(String title, String summary, String tag) {
        messagesStreamContainer.getChildren().clear();
        addNotification(title, summary, "Vừa cập nhật", tag, "#e0f2fe");
    }

    private void switchMode(String mode) {
        currentMode = mode;
        boolean notifications = "Thông báo".equals(mode);
        txtMessageInput.setPromptText(notifications ? "Nhập nội dung thông báo gửi khách thuê..." : "Nhập tin nhắn cho khách thuê...");
        updateArchiveControls();
        renderChatList();
        renderMessagesStream();
    }

    private void updateMetrics() {
        long unread = dataService.getMessages().stream().filter(msg -> !msg.isOwner() && !readRooms.contains(msg.getRoom())).count();
        lblUnreadCount.setText(unread + " tin nhắn");
        lblNotificationCount.setText("8 thông báo");
        lblSentCount.setText("12 thông báo");
        lblResponseRate.setText("98.5%");
    }

    private void updateArchiveControls() {
        boolean archived = archivedRooms.contains(selectedRoom);
        btnArchive.setVisible(!archived);
        btnArchive.setManaged(!archived);
        btnRestore.setVisible(archived);
        btnRestore.setManaged(archived);
    }

    private void renderMessagesStream() {
        messagesStreamContainer.getChildren().clear();
        if ("Đã gửi".equals(currentMode)) {
            renderSentDetails();
            return;
        }
        if ("Thông báo".equals(currentMode)) {
            addNotification("Hóa đơn tháng 08/2024", "4 khách thuê chưa thanh toán trước hạn 05/09.", "Hôm nay", "Cần xử lý", "#fef3c7");
            addNotification("Bảo trì phòng P.102", "Yêu cầu vòi sen bị rỉ nước đã được tạo.", "Hôm nay", "Ưu tiên cao", "#fee2e2");
            addNotification("Hợp đồng sắp hết hạn", "P.202 và P.203 cần được nhắc gia hạn.", "Hôm qua", "Nhắc việc", "#dbeafe");
            return;
        }
        for (MessageItem msg : dataService.getMessages()) {
            if (!"Toàn bộ".equals(msg.getRoom()) && !msg.getRoom().equals(selectedRoom)) continue;
            HBox bubbleRow = new HBox();
            bubbleRow.setAlignment(msg.isOwner() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            VBox bubble = new VBox(3);
            bubble.setMaxWidth(380);

            if (msg.isOwner()) {
                bubble.setStyle("-fx-background-color: #2563eb; -fx-padding: 10 14; -fx-background-radius: 12 12 2 12;");
            } else {
                bubble.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 10 14; -fx-background-radius: 12 12 12 2;");
            }

            Label lblText = new Label(msg.getContent());
            lblText.setWrapText(true);
            lblText.setStyle(msg.isOwner() ? "-fx-text-fill: #ffffff; -fx-font-size: 12px;" : "-fx-text-fill: #1e293b; -fx-font-size: 12px;");

            Label lblTime = new Label(msg.getTime());
            lblTime.setStyle(msg.isOwner() ? "-fx-text-fill: #bfdbfe; -fx-font-size: 9px;" : "-fx-text-fill: #94a3b8; -fx-font-size: 9px;");

            bubble.getChildren().addAll(lblText, lblTime);
            bubbleRow.getChildren().add(bubble);

            messagesStreamContainer.getChildren().add(bubbleRow);
        }
        if (messagesStreamContainer.getChildren().isEmpty()) {
            addEmptyState("Chưa có tin nhắn trong cuộc trò chuyện này");
        }
    }

    private void addNotification(String title, String content, String time, String tag, String color) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: " + color + "; -fx-padding: 12; -fx-background-radius: 8;");
        HBox heading = new HBox(8);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: 800; -fx-text-fill: #0f172a;");
        Label tagLabel = new Label(tag);
        tagLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #475569; -fx-background-color: #ffffff99; -fx-padding: 3 6; -fx-background-radius: 4;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        heading.getChildren().addAll(titleLabel, spacer, tagLabel);
        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #334155;");
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b;");
        card.getChildren().addAll(heading, contentLabel, timeLabel);
        messagesStreamContainer.getChildren().add(card);
    }

    private void setupActions() {
        btnBroadcast.setOnAction(e -> showComposeDialog());
        btnCallTenant.setOnAction(e -> DialogHelper.showInfo("Gọi điện", "Liên hệ khách thuê", "Đang kết nối cuộc gọi tới số điện thoại của khách thuê..."));
        btnSendMessage.setOnAction(e -> {
            String text = txtMessageInput.getText().trim();
            if (!text.isEmpty()) {
                if (attachedFileName != null) {
                    text += " [Tệp: " + attachedFileName + "]";
                }
                dataService.getMessages().add(new MessageItem("Chủ nhà trọ", selectedRoom, text, "Vừa xong", true, false));
                txtMessageInput.clear();
                attachedFileName = null;
                txtMessageInput.setPromptText("Nhập tin nhắn cho khách thuê...");
                renderMessagesStream();
                updateMetrics();
            }
        });
        btnMarkRead.setOnAction(e -> {
            readRooms.add(selectedRoom);
            renderChatList();
            updateMetrics();
            DialogHelper.showInfo("Trạng thái tin nhắn", "Đã đánh dấu đã đọc", "Các tin nhắn mới của " + selectedRoom + " đã được cập nhật.");
        });
        btnArchive.setOnAction(e -> {
            archivedRooms.add(selectedRoom);
            currentMode = "Đã lưu trữ";
            btnArchived.setSelected(true);
            renderChatList();
            updateArchiveControls();
            DialogHelper.showInfo("Lưu trữ", "Đã lưu cuộc trò chuyện", "Bạn có thể mở tab “Đã lưu trữ” để xem lại hoặc khôi phục cuộc trò chuyện này.");
        });
        btnRestore.setOnAction(e -> {
            archivedRooms.remove(selectedRoom);
            currentMode = "Hộp thư";
            btnInbox.setSelected(true);
            renderChatList();
            updateArchiveControls();
            DialogHelper.showInfo("Khôi phục", "Đã đưa về hộp thư", "Cuộc trò chuyện đã xuất hiện lại trong Hộp thư.");
        });
        btnAttach.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Chọn tệp đính kèm");
            java.io.File file = chooser.showOpenDialog(btnAttach.getScene().getWindow());
            if (file != null) {
                attachedFileName = file.getName();
                txtMessageInput.setPromptText("Tệp đính kèm: " + attachedFileName);
            }
        });
        txtMessageInput.setOnAction(e -> btnSendMessage.fire());
    }

    private void showComposeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Soạn tin nhắn mới");
        dialog.setHeaderText("Gửi tin nhắn hoặc thông báo tới khách thuê");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ComboBox<String> recipient = new ComboBox<>();
        recipient.getItems().addAll("Nguyễn Văn An (P.101)", "Trần Thị Lan (P.102)", "Phạm Thị Thùy (P.202)", "Tất cả khách thuê (48 phòng)", "Khách đang quá hạn thanh toán");
        recipient.setValue("Nguyễn Văn An (P.101)");
        ComboBox<String> messageType = new ComboBox<>();
        messageType.getItems().addAll("Tin nhắn riêng", "Thông báo toàn bộ", "Nhắc thanh toán", "Thông báo bảo trì");
        messageType.setValue("Tin nhắn riêng");
        ComboBox<String> channel = new ComboBox<>();
        channel.getItems().addAll("Trong ứng dụng", "Zalo", "SMS", "Email", "Zalo + SMS");
        channel.setValue("Trong ứng dụng");
        ComboBox<String> template = new ComboBox<>();
        template.getItems().addAll("Không dùng mẫu", "Nhắc thanh toán tiền phòng", "Lịch bảo trì", "Nhắc gia hạn hợp đồng", "Chào mừng khách thuê mới");
        template.setValue("Không dùng mẫu");
        ComboBox<String> priority = new ComboBox<>();
        priority.getItems().addAll("Bình thường", "Ưu tiên", "Khẩn cấp");
        priority.setValue("Bình thường");
        DatePicker scheduleDate = new DatePicker();
        scheduleDate.setPromptText("Chọn ngày gửi");
        TextField scheduleTime = new TextField("08:00");
        scheduleTime.setPromptText("HH:mm");
        TextArea content = new TextArea();
        content.setPromptText("Nhập nội dung tin nhắn...");
        content.setWrapText(true);
        content.setPrefRowCount(6);
        Label counter = new Label("0/500 ký tự");
        Label preview = new Label("Bản xem trước sẽ hiển thị tại đây");
        preview.setWrapText(true);
        preview.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10; -fx-text-fill: #475569;");
        CheckBox trackDelivery = new CheckBox("Theo dõi trạng thái đã nhận");
        trackDelivery.setSelected(true);
        CheckBox allowReply = new CheckBox("Cho phép khách thuê phản hồi");
        allowReply.setSelected(true);

        Map<String, String> templates = new HashMap<>();
        templates.put("Nhắc thanh toán tiền phòng", "Kính chào quý khách, vui lòng hoàn tất thanh toán tiền phòng trước hạn. Xin cảm ơn!");
        templates.put("Lịch bảo trì", "Thông báo: khu trọ sẽ thực hiện bảo trì theo lịch. Vui lòng sắp xếp thời gian phù hợp.");
        templates.put("Nhắc gia hạn hợp đồng", "Hợp đồng thuê phòng sắp hết hạn. Vui lòng liên hệ quản lý để được hỗ trợ gia hạn.");
        templates.put("Chào mừng khách thuê mới", "Chào mừng bạn đến với khu trọ. Chúc bạn có trải nghiệm thoải mái và thuận tiện!");
        template.setOnAction(e -> {
            if (!"Không dùng mẫu".equals(template.getValue())) content.setText(templates.get(template.getValue()));
        });
        content.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.length() > 500) content.setText(newValue.substring(0, 500));
            counter.setText(content.getText().length() + "/500 ký tự");
            preview.setText(content.getText().isBlank() ? "Bản xem trước sẽ hiển thị tại đây" : "Gửi tới " + recipient.getValue() + "\n\n" + content.getText());
        });
        recipient.setOnAction(e -> content.requestFocus());

        GridPane fields = new GridPane();
        fields.setHgap(12);
        fields.setVgap(10);
        fields.add(new Label("Người nhận"), 0, 0); fields.add(recipient, 1, 0);
        fields.add(new Label("Loại tin"), 0, 1); fields.add(messageType, 1, 1);
        fields.add(new Label("Kênh gửi"), 0, 2); fields.add(channel, 1, 2);
        fields.add(new Label("Mẫu có sẵn"), 0, 3); fields.add(template, 1, 3);
        fields.add(new Label("Mức ưu tiên"), 0, 4); fields.add(priority, 1, 4);
        fields.add(new Label("Lịch gửi"), 0, 5);
        HBox schedule = new HBox(8, scheduleDate, scheduleTime);
        fields.add(schedule, 1, 5);
        fields.add(new Label("Nội dung"), 0, 6); fields.add(content, 1, 6);
        fields.add(counter, 1, 7);
        VBox options = new VBox(6, trackDelivery, allowReply);
        fields.add(options, 1, 8);
        fields.add(new Label("Xem trước"), 0, 9); fields.add(preview, 1, 9);
        ColumnConstraints labelColumn = new ColumnConstraints(95);
        ColumnConstraints valueColumn = new ColumnConstraints(430);
        fields.getColumnConstraints().addAll(labelColumn, valueColumn);

        dialog.getDialogPane().setContent(fields);
        ButtonType sendNow = new ButtonType("Gửi ngay", ButtonBar.ButtonData.OK_DONE);
        ButtonType scheduleSend = new ButtonType("Lên lịch", ButtonBar.ButtonData.APPLY);
        ButtonType saveDraft = new ButtonType("Lưu nháp", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(sendNow, scheduleSend, saveDraft, ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(sendNow).disableProperty().bind(content.textProperty().isEmpty());
        dialog.getDialogPane().lookupButton(scheduleSend).disableProperty().bind(content.textProperty().isEmpty().or(scheduleDate.valueProperty().isNull()));
        dialog.getDialogPane().setStyle("-fx-font-family: 'Segoe UI', system-ui, sans-serif; -fx-font-size: 13px; -fx-background-color: white;");
        dialog.showAndWait().ifPresent(result -> {
            if (result == sendNow) {
                publishComposedMessage(content.getText(), recipient.getValue(), channel.getValue(), "Vừa xong");
                DialogHelper.showInfo("Gửi tin thành công", "Tin đã được gửi", "Tin nhắn đã gửi qua " + channel.getValue() + " và bật theo dõi trạng thái nhận.");
            } else if (result == scheduleSend) {
                scheduledMessages.add(recipient.getValue() + " | " + scheduleDate.getValue() + " " + scheduleTime.getText() + " | " + content.getText());
                DialogHelper.showInfo("Đã lên lịch", "Tin nhắn đang chờ gửi", "Tin sẽ được gửi tới " + recipient.getValue() + " vào " + scheduleDate.getValue() + " lúc " + scheduleTime.getText() + ".");
            } else if (result == saveDraft) {
                drafts.add(recipient.getValue() + " | " + content.getText());
                DialogHelper.showInfo("Đã lưu nháp", "Bản nháp đã được lưu", "Đã lưu " + drafts.size() + " bản nháp. Bạn có thể mở lại nội dung này từ mục Tin nhắn & Thông báo.");
            }
        });
    }

    private void publishComposedMessage(String text, String recipient, String channel, String time) {
        String room = recipient.contains("P.") ? recipient.substring(recipient.indexOf("P."), recipient.indexOf(")")) : "Toàn bộ";
        dataService.getMessages().add(new MessageItem("Chủ nhà trọ", room, text + " [" + channel + "]", time, true, false));
        currentMode = "Đã gửi";
        btnSent.setSelected(true);
        renderChatList();
        renderMessagesStream();
        updateMetrics();
    }
}