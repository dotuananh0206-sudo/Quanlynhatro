package com.tromanager.model;

public class TaskItem {
    public enum TaskStatus {
        TODO("Cần xử lý"), IN_PROGRESS("Đang xử lý"), DONE("Đã hoàn thành");
        private final String label;
        TaskStatus(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum Priority {
        EMERGENCY("Khẩn cấp", "badge-red"),
        HIGH("Cao", "badge-orange"),
        MEDIUM("Trung bình", "badge-blue");

        private final String label;
        private final String styleClass;

        Priority(String label, String styleClass) {
            this.label = label;
            this.styleClass = styleClass;
        }

        public String getLabel() { return label; }
        public String getStyleClass() { return styleClass; }
    }

    private String title;
    private String location;
    private String time;
    private Priority priority;
    private TaskStatus status;
    private String assignee = "";
    private String description = "";

    public TaskItem(String title, String location, String time, Priority priority) {
        this.title = title;
        this.location = location;
        this.time = time;
        this.priority = priority;
        this.status = TaskStatus.TODO;
    }

    public TaskItem(String title, String location, String time, Priority priority, TaskStatus status) {
        this(title, location, time, priority);
        this.status = status;
    }

    public TaskItem(String title, String location, String time, Priority priority, TaskStatus status, String assignee, String description) {
        this(title, location, time, priority, status);
        this.assignee = assignee == null ? "" : assignee;
        this.description = description == null ? "" : description;
    }

    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getTime() { return time; }
    public Priority getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public String getAssignee() { return assignee; }
    public String getDescription() { return description; }
    public void setTitle(String title) { this.title = title; }
    public void setLocation(String location) { this.location = location; }
    public void setTime(String time) { this.time = time; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setAssignee(String assignee) { this.assignee = assignee == null ? "" : assignee; }
    public void setDescription(String description) { this.description = description == null ? "" : description; }

    public String getSubtitle() {
        return location + " • " + time;
    }
}
