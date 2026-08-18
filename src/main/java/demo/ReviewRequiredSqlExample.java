package demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReviewRequiredSqlExample {
    public ResultSet runExistingQuery(Connection connection, String existingQuery)
            throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(existingQuery);
    }
}
