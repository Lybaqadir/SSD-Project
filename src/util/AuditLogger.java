import java.sql.*;

// A utility class that audits actions and save them to the database
public class AuditLogger(){
    public static void log (int userId, String action, String details){
        //SQL Query + NOW() automated fill with the date and time at the moment itself
        String sql = "INSERT INTO audit_logs (userId, action, details, timestamp) VALUES ('"+userId+"','"+action+"','"+details+"', NOW());";

        //Try and catch so the system does not crash and display error message

        try{
            connection con = DBUtils.establishConnection();
            Statement statement = con.createStatement();

            statement.executeUpdate(sql);
            System.out.println("Audit log DB updated");

            DBUtils.closeConnection(con,statement);
        } catch (Exception e){
            System.out.println("Error: "+ e.getMessage())
        }
    }
}