import java.sql.*;

public class Database {

    Connection con = null;
    private Statement sm;
    private ResultSet rs = null;


    public void verbinden() {
        String dbHost = "127.0.0.1";
        String dbPort = "";
        String dbName = "DB2Project";
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
    public void verbindungTrennen() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.err.println("Verbindung erfolgreich geschlossen.");
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Schließen der Verbindung: " + e);
        }
    }
}
