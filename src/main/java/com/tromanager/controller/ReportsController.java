package com.tromanager.controller;

import com.tromanager.service.MockDataService;
import com.tromanager.ui.IconHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.*;

public class ReportsController {

    @FXML private StackPane iconTotalRev;
    @FXML private StackPane iconTotalExp;
    @FXML private StackPane iconNetProfit;
    @FXML private StackPane iconROI;

    @FXML private Button btnExportExcel;
    @FXML private VBox cashflowListContainer;

    @FXML
    public void initialize() {
        setupIcons();
        renderCashflowTable();
        btnExportExcel.setOnAction(e -> exportExcel());
    }

    private void setupIcons() {
        iconTotalRev.getChildren().add(IconHelper.getIcon("money", 18, "#2563eb"));
        iconTotalExp.getChildren().add(IconHelper.getIcon("alert", 18, "#dc2626"));
        iconNetProfit.getChildren().add(IconHelper.getIcon("check", 18, "#059669"));
        iconROI.getChildren().add(IconHelper.getIcon("report", 18, "#2563eb"));
    }

    private void renderCashflowTable() {
        cashflowListContainer.getChildren().clear();

        List<String[]> data = Arrays.asList(
                new String[]{"Tháng 08/2024", "156.800.000đ", "44.300.000đ", "112.500.000đ", "87.5%", "71.7%"},
                new String[]{"Tháng 07/2024", "156.000.000đ", "42.100.000đ", "113.900.000đ", "87.5%", "73.0%"},
                new String[]{"Tháng 06/2024", "153.000.000đ", "41.500.000đ", "111.500.000đ", "85.4%", "72.8%"},
                new String[]{"Tháng 05/2024", "151.000.000đ", "40.000.000đ", "111.000.000đ", "83.3%", "73.5%"},
                new String[]{"Tháng 04/2024", "149.000.000đ", "39.200.000đ", "109.800.000đ", "81.2%", "73.6%"},
                new String[]{"Tháng 03/2024", "142.000.000đ", "38.500.000đ", "103.500.000đ", "79.1%", "72.8%"}
        );

        for (String[] rowData : data) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 8 0; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");

            Label lblMonth = new Label(rowData[0]);
            lblMonth.setPrefWidth(90);
            lblMonth.setStyle("-fx-font-weight: 700; -fx-text-fill: #0f172a; -fx-font-size: 12px;");

            Label lblRev = new Label(rowData[1]);
            lblRev.setPrefWidth(140);
            lblRev.setStyle("-fx-font-weight: 700; -fx-text-fill: #2563eb; -fx-font-size: 12px;");

            Label lblExp = new Label(rowData[2]);
            lblExp.setPrefWidth(140);
            lblExp.setStyle("-fx-font-weight: 600; -fx-text-fill: #dc2626; -fx-font-size: 12px;");

            Label lblNet = new Label(rowData[3]);
            lblNet.setPrefWidth(140);
            lblNet.setStyle("-fx-font-weight: 800; -fx-text-fill: #059669; -fx-font-size: 12px;");

            Label lblOcc = new Label(rowData[4]);
            lblOcc.setPrefWidth(120);
            lblOcc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

            Pane sp = new Pane();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label lblMargin = new Label(rowData[5]);
            lblMargin.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #15803d; -fx-font-weight: 700; -fx-font-size: 11px; -fx-padding: 2 8; -fx-background-radius: 6;");

            row.getChildren().addAll(lblMonth, lblRev, lblExp, lblNet, lblOcc, sp, lblMargin);
            cashflowListContainer.getChildren().add(row);
        }
    }

    private void exportExcel() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Xuất báo cáo tài chính");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files", "*.xlsx"));
        File file = chooser.showSaveDialog(btnExportExcel.getScene().getWindow());
        if (file == null) return;
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream output = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("Dòng tiền");
            String[] headers = {"Tháng", "Doanh thu", "Chi phí", "Lợi nhuận", "Tỷ lệ lấp đầy", "Biên lợi nhuận"};
            Row header = sheet.createRow(0);
            for (int index = 0; index < headers.length; index++) header.createCell(index).setCellValue(headers[index]);

            String[][] rows = {
                    {"08/2024", "156800000", "44300000", "112500000", "87.5%", "71.7%"},
                    {"07/2024", "156000000", "42100000", "113900000", "87.5%", "73.0%"},
                    {"06/2024", "153000000", "41500000", "111500000", "85.4%", "72.8%"},
                    {"05/2024", "151000000", "40000000", "111000000", "83.3%", "73.5%"},
                    {"04/2024", "149000000", "39200000", "109800000", "81.2%", "73.6%"},
                    {"03/2024", "142000000", "38500000", "103500000", "79.1%", "72.8%"}
            };
            for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                for (int columnIndex = 0; columnIndex < rows[rowIndex].length; columnIndex++) {
                    row.createCell(columnIndex).setCellValue(rows[rowIndex][columnIndex]);
                }
            }
            for (int index = 0; index < headers.length; index++) sheet.autoSizeColumn(index);
            workbook.write(output);
            DialogHelper.showInfo("Xuất Excel", "Đã xuất báo cáo", "Tệp đã được tạo tại: " + file.getAbsolutePath());
        } catch (IOException ex) {
            DialogHelper.showInfo("Xuất Excel thất bại", "Không thể tạo tệp", ex.getMessage());
        }
    }
}