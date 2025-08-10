package com.dvm.bookstore.service;
import com.dvm.bookstore.entity.Order;
import com.dvm.bookstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final OrderRepository orderRepository;

    public ByteArrayInputStream generateOrdersExcel() throws IOException {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new IOException("No orders found to export.");
        }
//        return null;
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
}

