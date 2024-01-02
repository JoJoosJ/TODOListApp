import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Database {

    Connection con = null;
    private Statement sm;
    private ResultSet rs = null;
    public void verbinden() {
        String dbHost = "localhost";
        String dbPort = "1433";
        String dbName = "DB2Projekt";
        String dbUser = "sa";
        String dbPass = "Sqlserveradmin1";

        String connectionUrl = "jdbc:sqlserver://" +
                dbHost + ":" +
                dbPort + ";" +
                "databaseName=" + dbName + ";" +
                "user=" + dbUser + ";" +
                "password=" + dbPass + ";" +
                "encrypt=" + "true" + ";" +
                "trustServerCertificate=" + "true" + ";";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(connectionUrl);
            sm = con.createStatement();
            System.err.println("Connection Successful!");
        } catch(Exception e) {
            System.err.println("Fehler beim Verbinden: " + e);
        }
    }
    public void addEvent(String eventName){

        LocalDate localDate = LocalDate.now();

        String sql = "INSERT INTO timetable ([Time created], [Event], [Rating]) VALUES (GETDATE(), ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setDate(1, Date.valueOf(localDate));
            statement.setString(2, eventName);
            statement.setString(3, "Planned");
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
