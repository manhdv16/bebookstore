package com.dvm.bookstore.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class OrderExportRepository {

    private final DataSource dataSource;

    public void streamAllOrders(RowCallbackHandler handler) throws SQLException {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);

        PreparedStatement ps = conn.prepareStatement(
        """
            select order_id, order_date, reviewed, status,
            total_amount, total_book, user_id
            from orderr
            order by order_id
            """, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY
        );
        ps.setFetchSize(1000);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            handler.processRow(rs);
        }
        rs.close();
        ps.close();
        conn.close();
    }
}
