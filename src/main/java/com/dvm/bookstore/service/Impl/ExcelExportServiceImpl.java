package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.entity.Order;
import com.dvm.bookstore.repository.OrderExportRepository;
import com.dvm.bookstore.repository.OrderRepository;
import com.dvm.bookstore.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ExcelExportServiceImpl implements ExcelExportService {

    private final OrderRepository orderRepository;
    private final OrderExportRepository orderExportRepository;

    private final int ROW_WINDOW_SIZE = 500;

    @Override
    public ByteArrayInputStream generateOrdersExcel() throws IOException {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found to export.");
        }
        return exportOrdersToExcel(orders);
    }

    private ByteArrayInputStream exportOrdersToExcel(List<Order> orders) throws IOException {
        String[] columns = {"Order ID", "User", "Order Date", "Status", "Total Books", "Total Amount"};

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Orders");

            // Tạo header style
            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerCellStyle.setFont(headerFont);

            // Tạo hàng tiêu đề
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // Ghi dữ liệu
            int rowIdx = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(order.getOrderId());
                row.createCell(1).setCellValue(order.getUser().getUserName());
                row.createCell(2).setCellValue(order.getOrderDate().toString());
                row.createCell(3).setCellValue(order.getStatus());
                row.createCell(4).setCellValue(order.getTotalBook());
                row.createCell(5).setCellValue(order.getTotalAmount());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Override
    public void exportOrders(HttpServletResponse response) throws Exception{
        SXSSFWorkbook workbook = new SXSSFWorkbook(ROW_WINDOW_SIZE);
        Sheet sheet = workbook.createSheet("Orders");

        writeHeaderRow(sheet);

        AtomicInteger rowIndex = new AtomicInteger(1);
        orderExportRepository.streamAllOrders(rs -> {
            Row row = sheet.createRow(rowIndex.getAndIncrement());

            row.createCell(0).setCellValue(rs.getLong("order_id"));
            row.createCell(1).setCellValue(rs.getString("order_date"));
            row.createCell(2).setCellValue(rs.getBoolean("reviewed"));
            row.createCell(3).setCellValue(rs.getString("status"));
            row.createCell(4).setCellValue(rs.getDouble("total_amount"));
            row.createCell(5).setCellValue(rs.getInt("total_book"));
            row.createCell(6).setCellValue(rs.getLong("user_id"));
        });

        workbook.write(response.getOutputStream());
        workbook.dispose();
        workbook.close();

    }

    private void writeHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Order ID", "Order Date", "Reviewed", "Status", "Total Amount", "Total Books", "User ID"};
        CellStyle headerCellStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);

        for (int col = 0; col < columns.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(columns[col]);
            cell.setCellStyle(headerCellStyle);
        }
    }
}

