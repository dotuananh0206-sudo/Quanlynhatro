package com.tromanager.controller;

import com.tromanager.model.TaskItem;
import com.tromanager.service.MockDataService;
import com.tromanager.ui.IconHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

public class TasksController {

    @FXML private StackPane iconTodo;
    @FXML private StackPane iconInProgress;
    @FXML private StackPane iconDone;
    @FXML private StackPane iconEmergency;
    @FXML private VBox cardTodo;
    @FXML private VBox cardInProgress;
    @FXML private VBox cardDone;
    @FXML private VBox cardEmergency;

    @FXML private TextField txtSearchTask;
    @FXML private ComboBox<String> cbPriorityFilter;
    @FXML private ComboBox<String> cbStatusFilter;
    @FXML private Button btnAddTask;

    @FXML private VBox colTodoContainer;
    @FXML private VBox colInProgressContainer;
    @FXML private VBox colDoneContainer;

    private final MockDataService dataService = MockDataService.getInstance();

    @FXML
    public void initialize() {
        setupIcons();
        setupFilter();
        setupMetricLinks();
        renderKanban();
        setupActions();
    }

    private void setupMetricLinks() {
        link(cardTodo, "Cần xử lý", "");
        link(cardInProgress, "Đang xử lý", "");
        link(cardDone, "Đã hoàn thành", "");
        link(cardEmergency, "Tất cả trạng thái", "Khẩn cấp");
    }

    private String selectedStatus = "Tất cả trạng thái";

    private void link(VBox card, String status, String priority) {
        card.setStyle(card.getStyle() + ";-fx-cursor: hand;");
        card.setOnMouseClicked(event -> {
            selectedStatus = status;
            cbPriorityFilter.setValue(priority.isEmpty() ? "Tất cả mức độ" : priority);
            cbStatusFilter.setValue(status);
            renderKanban();
        });
    }

    private void setupIcons() {
        iconTodo.getChildren().add(IconHelper.getIcon("task", 18, "#2563eb"));
        iconInProgress.getChildren().add(IconHelper.getIcon("task", 18, "#d97706"));
        iconDone.getChildren().add(IconHelper.getIcon("check", 18, "#059669"));
        iconEmergency.getChildren().add(IconHelper.getIcon("alert", 18, "#dc2626"));
    }

    private void setupFilter() {
        cbPriorityFilter.getItems().addAll("Tất cả mức độ", "Khẩn cấp", "Cao", "Trung bình");
        cbPriorityFilter.setValue("Tất cả mức độ");
        selectedStatus = MainController.consumeTaskStatus();
        txtSearchTask.setText(MainController.consumeSearch());

        cbStatusFilter.getItems().addAll("Tất cả trạng thái", "Cần xử lý", "Đang xử lý", "Đã hoàn thành", "Chưa hoàn thành");
        cbStatusFilter.setValue("Tất cả trạng thái");
        cbStatusFilter.setValue(selectedStatus);
        cbPriorityFilter.setOnAction(e -> renderKanban());
        cbStatusFilter.setOnAction(e -> {
            selectedStatus = cbStatusFilter.getValue();
            renderKanban();
        });
        txtSearchTask.textProperty().addListener((obs, oldV, newV) -> renderKanban());
    }

    private void renderKanban() {
        colTodoContainer.getChildren().clear();
        colInProgressContainer.getChildren().clear();
        colDoneContainer.getChildren().clear();

        String q = txtSearchTask.getText().trim().toLowerCase();
        String priority = cbPriorityFilter.getValue();

        List<TaskItem> todoList = dataService.getTasks().stream().filter(task -> task.getStatus() == TaskItem.TaskStatus.TODO).toList();
        List<TaskItem> inProgressList = dataService.getTasks().stream().filter(task -> task.getStatus() == TaskItem.TaskStatus.IN_PROGRESS).toList();
        List<TaskItem> doneList = dataService.getTasks().stream().filter(task -> task.getStatus() == TaskItem.TaskStatus.DONE).toList();

        populateColumn(colTodoContainer, todoList, q, priority, "Bắt đầu xử lý");
        populateColumn(colInProgressContainer, inProgressList, q, priority, "Đánh dấu xong");
        populateColumn(colDoneContainer, doneList, q, priority, "Xem chi tiết");
    }

    private void populateColumn(VBox container, List<TaskItem> list, String q, String priorityFilter, String actionLabel) {
        for (TaskItem task : list) {
            boolean matchText = q.isEmpty() || task.getTitle().toLowerCase().contains(q) || task.getLocation().toLowerCase().contains(q);
            boolean matchPriority = priorityFilter.equals("Tất cả mức độ") || task.getPriority().getLabel().equalsIgnoreCase(priorityFilter);
                boolean matchStatus = selectedStatus.equals("Tất cả trạng thái")
                    || (selectedStatus.equals("Chưa hoàn thành") && task.getStatus() != TaskItem.TaskStatus.DONE)
                    || task.getStatus().getLabel().equals(selectedStatus);

            if (!matchText || !matchPriority || !matchStatus) continue;

            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");

            HBox top = new HBox(6);
            top.setAlignment(Pos.CENTER_LEFT);
            Label lblPriority = new Label(task.getPriority().getLabel());
            lblPriority.getStyleClass().add(task.getPriority().getStyleClass());
            Pane sp = new Pane();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Label lblTime = new Label(task.getTime());
            lblTime.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8;");
            top.getChildren().addAll(lblPriority, sp, lblTime);

            Label lblTitle = new Label(task.getTitle());
            lblTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-wrap-text: true;");

            Label lblLoc = new Label("Vị trí: " + task.getLocation());
            lblLoc.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

            Label lblAssignee = new Label("Phụ trách: " + (task.getAssignee().isBlank() ? "Chưa phân công" : task.getAssignee()));
            lblAssignee.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            Label lblDescription = new Label(task.getDescription());
            lblDescription.setWrapText(true);
            lblDescription.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

            HBox actions = new HBox(6);
            Button btnAction = new Button(actionLabel);
            btnAction.setMaxWidth(Double.MAX_VALUE);
            btnAction.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-font-size: 11px; -fx-font-weight: 600; -fx-padding: 5 10; -fx-background-radius: 6; -fx-cursor: hand;");
            btnAction.setOnAction(e -> {
                if (task.getStatus() == TaskItem.TaskStatus.TODO) task.setStatus(TaskItem.TaskStatus.IN_PROGRESS);
                else if (task.getStatus() == TaskItem.TaskStatus.IN_PROGRESS) task.setStatus(TaskItem.TaskStatus.DONE);
                renderKanban();
            });
            Button btnEdit = new Button("Sửa");
            btnEdit.setOnAction(e -> DialogHelper.showEditTaskDialog(task, this::renderKanban));
            Button btnDelete = new Button("Xóa");
            btnDelete.setOnAction(e -> {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Xóa công việc này khỏi danh sách?", ButtonType.OK, ButtonType.CANCEL);
                confirmation.setTitle("Xóa công việc");
                confirmation.showAndWait().filter(ButtonType.OK::equals).ifPresent(ok -> {
                    dataService.getTasks().remove(task);
                    renderKanban();
                });
            });
            actions.getChildren().addAll(btnAction, btnEdit, btnDelete);
            HBox.setHgrow(btnAction, Priority.ALWAYS);

            card.getChildren().addAll(top, lblTitle, lblLoc, lblAssignee, lblDescription, actions);
            container.getChildren().add(card);
        }
    }

    private void setupActions() {
        btnAddTask.setOnAction(e -> {
            DialogHelper.showAddTaskDialog();
            renderKanban();
        });
    }
}