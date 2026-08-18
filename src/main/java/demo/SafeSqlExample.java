package demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SafeSqlExample {
    public ResultSet findByUsername(Connection connection, String username)
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id, username FROM users WHERE username = ?");
        statement.setString(1, username);
        return statement.executeQuery();
    }
}
