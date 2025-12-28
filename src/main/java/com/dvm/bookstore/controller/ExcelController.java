package com.dvm.bookstore.controller;

import com.dvm.bookstore.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class ExcelController {

    private final ExcelExportService excelExportService;

     @GetMapping("/generate")
     public ResponseEntity<byte[]> generateExcel() throws IOException {
         ByteArrayInputStream excelFile = excelExportService.generateOrdersExcel();
         HttpHeaders headers = new HttpHeaders();
         headers.add("Content-Disposition", "attachment; filename=orders.xlsx");
         return ResponseEntity.ok()
                 .headers(headers)
                 .contentType(MediaType.APPLICATION_OCTET_STREAM)
                 .body(excelFile.readAllBytes());
     }

     @GetMapping("/order-export")
    public void exportOrders(HttpServletResponse response) throws Exception{
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=orders.xlsx");
        excelExportService.exportOrders(response);
    }
}
