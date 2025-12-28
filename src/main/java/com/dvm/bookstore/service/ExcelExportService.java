package com.dvm.bookstore.service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ExcelExportService {
    ByteArrayInputStream generateOrdersExcel() throws IOException;

    void exportOrders(HttpServletResponse response) throws Exception;
}

