package demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewRequiredSqlExample {
    public ResultSet findByStatus(Connection connection, String status)
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id, username FROM users WHERE status = ?");
        statement.setString(1, status);
        return statement.executeQuery();
    }
}
