import org.w3c.dom.events.Event;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Database {

    Connection con = null;
    private Statement sm;
    private ResultSet rs = null;
    public void MSSQLconnect() {
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
            System.err.println("Connection Successfull!");
        } catch(Exception e) {
            System.err.println("Fehler beim Verbinden: " + e);
        }
    }
    public void addEvent(String eventName){

        LocalDate localDate = LocalDate.now();

        String sql = "INSERT INTO timetable ([Time created], [Event], [Rating]) VALUES (?, ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, localDate.toString());
            statement.setString(2, eventName);
            statement.setString(3, "Planned");
            statement.executeUpdate();
            System.out.println("Card " + eventName + " added Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getCardsfromMSSQL(String Rating) {

        List<String> cards = new LinkedList<>();
        try (PreparedStatement statement = con.prepareStatement("SELECT ID, Event, Rating FROM timetable WHERE Rating = ?")) {
            statement.setString(1, Rating);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String ID = rs.getString("ID");
                    String Event = rs.getString("Event");
                    cards.add("(ID:" + ID + ") " + Event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public List<String> getCardsFromArchive(Date startDate) {
        List<String> cards = new LinkedList<>();
        try (CallableStatement statement = con.prepareCall("{call GetCardsFromArchive(?)}")) {
            statement.setTimestamp(1, new Timestamp(startDate.getTime()));

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String Rating = rs.getString("Rating");
                    String Event = rs.getString("Event");
                    //________________Schwierigkeiten__________________
                    Timestamp Date = rs.getTimestamp("TimeDeleted");
                    cards.add(Rating);
                    cards.add("(Deleted At: " + Date + ") " + Event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public void deleteCard(String ID) throws SQLException {
        try(PreparedStatement statement = con.prepareStatement("DELETE FROM timetable WHERE ID = ?")) {
            statement.setString(1, ID);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Card (ID: " + ID + ") Removed Successfully!");
            } else {
                JOptionPane.showMessageDialog(null,"No Card with (ID: " + ID + " ) available", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("No card found with ID: " + ID);
            }
        }
    }

    public void changeRating(String ID, String rating) {
        try (PreparedStatement statement = con.prepareStatement("UPDATE timetable SET Rating = ? WHERE ID = ?")) {
            statement.setString(1, rating);
            statement.setString(2, ID);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Card (ID: " + ID + ") Changed Successfully!");
            } else {
                System.out.println("No card found with ID: " + ID);
                JOptionPane.showMessageDialog(null, "No Card with (ID: " + ID + ") available", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hier können Sie den SQLException ausgeben, um mögliche Fehlermeldungen zu sehen
        }
    }
}


