package demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UnsafeSqlExample {
    public ResultSet findByUsername(Connection connection, String username)
            throws SQLException {
        String query = "SELECT id, username FROM users WHERE username = '"
                + username + "'";
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }
}
