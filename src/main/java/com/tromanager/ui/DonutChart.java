package com.tromanager.ui;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DonutChart extends StackPane {

    private final Canvas canvas;
    private final Label percentageLabel;
    private final Label subLabel;
    private double rentedPercentage = 87.5;

    public DonutChart() {
        this(87.5);
    }

    public DonutChart(double rentedPercentage) {
        this.rentedPercentage = rentedPercentage;
        this.canvas = new Canvas(160, 160);
        
        VBox textContainer = new VBox(2);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.setMouseTransparent(true);

        percentageLabel = new Label(String.format("%.1f%%", rentedPercentage));
        percentageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #1e293b;");

        subLabel = new Label("Đã thuê");
        subLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 500; -fx-text-fill: #64748b;");

        textContainer.getChildren().addAll(percentageLabel, subLabel);

        this.getChildren().addAll(canvas, textContainer);
        this.setAlignment(Pos.CENTER);
        
        draw();
    }

    public void setRentedPercentage(double percentage) {
        this.rentedPercentage = percentage;
        this.percentageLabel.setText(String.format("%.1f%%", percentage));
        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        
        gc.clearRect(0, 0, w, h);

        double strokeWidth = 14;
        double padding = strokeWidth / 2.0 + 8;
        double radius = (w - padding * 2) / 2.0;
        double cx = w / 2.0;
        double cy = h / 2.0;

        // Background track (light gray)
        gc.setLineWidth(strokeWidth);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setStroke(Color.web("#f1f5f9"));
        gc.strokeOval(padding, padding, radius * 2, radius * 2);

        // Orange arc (Vacant part - 12.5% = 45 deg)
        gc.setStroke(Color.web("#f59e0b"));
        double vacantExtent = 360 * (100 - rentedPercentage) / 100.0;
        double rentedExtent = 360 * (rentedPercentage) / 100.0;

        // Draw orange arc from starting angle
        gc.strokeArc(padding, padding, radius * 2, radius * 2, 90, -vacantExtent, ArcType.OPEN);

        // Green arc (Rented part - 87.5%)
        gc.setStroke(Color.web("#059669")); // Emerald green
        gc.strokeArc(padding, padding, radius * 2, radius * 2, 90 - vacantExtent, -rentedExtent, ArcType.OPEN);
    }
}
