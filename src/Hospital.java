import java.sql.*;
import java.util.Scanner;

public class Hospital {

    static Connection connection = null;
    public static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, NumberFormatException {

        // Connect to database
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/hospital"; // connection string
        String username = "root"; // mysql username
        String password = "SSjscm11821!"; // mysql password
        connection = DriverManager.getConnection(url, username, password);

        // Print the menu
        System.out.println("\nHospital Database Query Menu");
        System.out.println("1. Get all occupied rooms, along with the associated " +
                "patient names and the date the patient was admitted.");
        System.out.println("2. List the rooms that are currently unoccupied.");
        System.out.println("3. List all rooms in the hospital along with patient names and " +
                "admission dates for those that are occupied.");
        System.out.println("4. List all patients in the database, with full personal information.");
        System.out.println("5. List all patients currently admitted to the hospital. " +
                "List only patient identification number and name.");
        System.out.println("6. List all patients who were discharged in a given date range. " +
                "List only patient identification number and name.");
        System.out.println("7. List all patients who were admitted within a given date range. " +
                "List only patient identification number and name.");
        System.out.println("8. For a given patient, list all admissions to the hospital along" +
                " with the diagnosis for each admission.");
        System.out.println("9. For a given patient, list all treatments that were administered. " +
                "Group treatments by admissions. List admissions in descending chronological" +
                " order, and list treatments in ascending chronological order with each admission.");
        System.out.println("10. List patients who were admitted to the hospital within 30 days " +
                "of their last discharge date. For each patient, list their patient identification " +
                "number, name, diagnosis, and admitting doctor.");
        System.out.println("11. For each patient that has ever been admitted to the hospital, " +
                "list their total number of admissions, average duration of each admission, " +
                "longest span between admissions, shortest span between admissions, and average " +
                "span between admissions.");
        System.out.println("12. List the diagnoses given to patients, in descending order of occurrences." +
                " List diagnosis identification number, name, and total occurrences of each diagnosis.");
        System.out.println("13. List the diagnoses given to hospital patients, in descending order of occurrences." +
                " List diagnosis identification number, name, and total occurrences of each diagnosis.");
        System.out.println("14. List the treatments performed on admitted patients, in descending order of occurrences." +
                " List treatment identification number, name, and total occurrences of each treatment.");
        System.out.println("15. List the diagnoses associated with patients who have the highest occurrences " +
                "of admissions to the hospital, in ascending order or correlation.");
        System.out.println("16. For a given treatment occurrence, list the patient name and the doctor " +
                "who ordered the treatment.");
        System.out.println("17. List all workers at the hospital, in ascending last name, first name order." +
                " For each worker, list their name, and job category.");
        System.out.println("18. List the primary doctors of patients with a high admission rate " +
                "(at least 4 admissions within a one-year time frame).");
        System.out.println("19. For a given doctor, list all associated diagnoses in descending order of " +
                "occurrence. For each diagnosis, list the total number of occurrences for the given doctor.");
        System.out.println("20. For a given doctor, list all treatments that they ordered in descending order " +
                "of occurrence. For each treatment, list the total number of occurrences for the given doctor.");
        System.out.println("21. List employees who have been involved in the treatment of every admitted patient.");


        System.out.print("\nPlease make your selection: ");


        try {
            int choice = Integer.parseInt(scan.next());
            switch (choice) {
                case 1 -> getOccupiedRooms();
                case 2 -> getUnoccupiedRooms();
                case 3 -> getAllRooms();
                case 4 -> getAllPatients();
                case 5 -> getCurrentPatients();
                case 6 -> {
                    System.out.print("Enter first date (YYYY-MM-DD): ");
                    String first_discharge = scan.next();
                    System.out.print("Enter second date (YYYY-MM-DD): ");
                    String second_discharge = scan.next();
                    getFromDischargeRange(first_discharge, second_discharge);
                }
                case 7 -> {
                    System.out.print("Enter first date (YYYY-MM-DD): ");
                    String first_admit = scan.next();
                    System.out.print("Enter second date (YYYY-MM-DD): ");
                    String second_admit = scan.next();
                    getFromAdmitRange(first_admit, second_admit);
                }
                case 8 -> {
                    System.out.print("Enter patient id number: ");
                    int patient_id = Integer.parseInt(scan.next());
                    getAdmissionsFor(patient_id);
                }
                case 9 -> {
                    System.out.print("Enter patient id number: ");
                    int treatmentsFor = Integer.parseInt(scan.next());
                    getTreatmentsFor(treatmentsFor);
                }
                case 10 -> getAdmittedWithin30();
                case 11 -> getAdmissionInfo(); // M3 query 2.8
                case 12 -> getCurrentDiagnoses();
                case 13 -> getAllDiagnoses();
                case 14 -> getTreatmentsPerformed(); // 3.3
                case 15 -> getDiagnosesOfMostAdmitted();
                case 16 -> {
                    System.out.print("Enter treatment id number: ");
                    int treatment_id = Integer.parseInt(scan.next());
                    getTreatmentOccurences(treatment_id);
                }
                case 17 -> getWorkers(); // 4.1
                case 18 -> getPrimaryDoctors(); // 4.2
                case 19 -> {
                    System.out.print("Enter doctor id number: ");
                    int doctor = Integer.parseInt(scan.next());
                    getDoctorsDiagnoses(doctor);
                }
                case 20 -> {
                    System.out.print("Enter doctor id number: ");
                    int doctor = Integer.parseInt(scan.next());
                    getDoctorsTreatments(doctor);
                }
                case 21 -> getEmployeesInvolved();
                default -> System.out.println("Invalid selection.");
            }
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid selection");

        }



    }
    public static void getOccupiedRooms() throws SQLException {
        try {
            String query = "SELECT a.room_assigned, p.first_name, p.last_name, a.admit_date " +
                    "FROM hospital.patient_admission a JOIN hospital.patient p ON a.patient_id" +
                    "= p.patient_id JOIN hospital.room r ON r.room_number = a.room_assigned " +
                    "WHERE r.curr_assigned = 1";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");


            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }

    public static void getUnoccupiedRooms() throws SQLException {
        try {
            String query = "SELECT * FROM hospital.room " +
                    "WHERE curr_assigned = 0";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getAllRooms() throws SQLException {
        try {
            String query = "SELECT r.room_number, p.first_name, p.last_name," +
                    " a.admit_date FROM hospital.patient p JOIN hospital.patient_admission a" +
                    " ON p.patient_id = a.patient_id RIGHT JOIN hospital.room r ON a.room_assigned " +
                    "= r.room_number";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getAllPatients() throws SQLException {
        try {
            String query = "SELECT * FROM hospital.patient";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getCurrentPatients() throws SQLException {
        try {
            String query = "SELECT a.patient_id, p.first_name, p.last_name" +
                    " FROM hospital.patient_admission a JOIN hospital.patient p ON" +
                    " a.patient_id = p.patient_id WHERE a.admit_id NOT IN (SELECT " +
                    "d.admit_id FROM hospital.discharge d)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getFromDischargeRange(String first_date, String second_date) throws SQLException {
        try {
            String query = "SELECT p.patient_id, p.first_name, p.last_name" +
                    " FROM hospital.patient p JOIN hospital.patient_admission a ON" +
                    " p.patient_id = a.patient_id JOIN hospital.discharge d ON" +
                    " a.admit_id = d.admit_id WHERE d.discharge_date BETWEEN '"
                    + first_date + "' AND '" + second_date + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }


    }
    public static void getFromAdmitRange(String first_date, String second_date) throws SQLException {
        try {
            String query = "SELECT a.patient_id, p.first_name, p.last_name" +
                    " FROM hospital.patient_admission a JOIN hospital.patient p ON" +
                    " a.patient_id = p.patient_id WHERE a.admit_date BETWEEN '"
                    + first_date + "' AND '" + second_date + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getAdmissionsFor(int patient_id) throws SQLException {
        try {
            String query = "SELECT a.patient_id, p.first_name, p.last_name," +
                    " d.diagnosis_name FROM hospital.patient_admission a" +
                    " JOIN hospital.patient p ON a.patient_id = p.patient_id" +
                    " JOIN hospital.diagnosis d ON a.diagnosis_id = d.diagnosis_id" +
                    " WHERE a.patient_id = " + patient_id;

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }


    }
    public static void getTreatmentsFor(int patient_id) throws SQLException {
        try {
            String query = "SELECT a.admit_id, p.first_name, p.last_name," +
                    " n.treatment_name, a.admit_date, t.time_administered" +
                    " FROM hospital.patient p JOIN hospital.patient_admission a" +
                    " ON a.patient_id = p.patient_id" +
                    " JOIN hospital.treatment_administered t ON a.admit_id = t.admit_id" +
                    " JOIN hospital.treatment n on t.treatment_id = n.treatment_id" +
                    " WHERE a.patient_id = " + patient_id + " GROUP BY a.admit_id, treatment_name" +
                    " ORDER BY a.admit_date DESC, t.time_administered ASC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getAdmittedWithin30() throws SQLException {
        try {
            String query = "SELECT p.first_name, p.last_name, a.admit_date," +
                    " a.diagnosis_id, a.primary_doctor, d.discharge_date, next_admits.next_admit_date" +
                    " FROM (SELECT a.admit_id, a.patient_id, LEAD(a.admit_date, 1, 0) OVER (" +
                    " PARTITION BY a.patient_id ORDER BY a.admit_date) AS next_admit_date" +
                    " FROM hospital.patient_admission a ORDER BY a.admit_date) AS next_admits" +
                    " JOIN hospital.discharge d ON next_admits.admit_id = d.admit_id " +
                    " JOIN hospital.patient_admission a ON next_admits.admit_id = a.admit_id " +
                    " JOIN hospital.patient p ON a.patient_id = p.patient_id" +
                    " WHERE DATEDIFF(next_admits.next_admit_date, d.discharge_date) < 30" +
                    " ORDER BY a.patient_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getAdmissionInfo() throws SQLException {
        try {
            String query = "SELECT t1.patient_id, t2.avg_stay_hrs, t1.total_admissions, t1.longest_span," +
                    " t1.shortest_span, t1.avg_span FROM (SELECT a.patient_id, COUNT(a.patient_id) as total_admissions," +
                    " MAX(DATEDIFF(next_admits.next_admit_date, next_admits.admit_date)) as longest_span," +
                    " MIN(DATEDIFF(next_admits.next_admit_date, next_admits.admit_date)) as shortest_span," +
                    " AVG(DATEDIFF(next_admits.next_admit_date, next_admits.admit_date)) as avg_span FROM (SELECT" +
                    " a.admit_id, a.patient_id, a.admit_date, LEAD(a.admit_date, 1, 0) OVER (PARTITION BY a.patient_id" +
                    " ORDER BY a.admit_date) as next_admit_date FROM hospital.patient_admission a) AS next_admits" +
                    " JOIN hospital.patient_admission a ON next_admits.admit_id = a.admit_id GROUP BY a.patient_id" +
                    " ORDER BY total_admissions DESC) t1 LEFT JOIN (SELECT a.patient_id, AVG(TIMESTAMPDIFF(HOUR," +
                    " a.admit_date, d.discharge_date)) as avg_stay_hrs FROM hospital.patient_admission a JOIN" +
                    " hospital.discharge d ON a.admit_id = d.admit_id GROUP BY a.patient_id) t2 ON t1.patient_id" +
                    " = t2.patient_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getCurrentDiagnoses() throws SQLException {
        try {
            String query = "SELECT a.diagnosis_id, d.diagnosis_name, COUNT(a.diagnosis_id) AS count" +
                    " FROM hospital.patient_admission a JOIN hospital.diagnosis d ON a.diagnosis_id =" +
                    " d.diagnosis_id WHERE a.admit_id NOT IN (SELECT s.admit_id FROM hospital.discharge s)" +
                    " GROUP BY d.diagnosis_name ORDER BY count DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getAllDiagnoses() throws SQLException {
        try {
            String query = "SELECT a.diagnosis_id, d.diagnosis_name, COUNT(a.diagnosis_id) AS count" +
                    " FROM hospital.patient_admission a JOIN hospital.diagnosis d ON a.diagnosis_id =" +
                    " d.diagnosis_id GROUP BY d.diagnosis_name ORDER BY count DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getTreatmentsPerformed() throws SQLException {
        try {
            String query = "SELECT a.treatment_id, t.treatment_name, COUNT(a.treatment_id) AS count" +
                    " FROM hospital.treatment_administered a JOIN hospital.treatment t ON a.treatment_id" +
                    " = t.treatment_id GROUP BY t.treatment_name ORDER BY count DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getDiagnosesOfMostAdmitted() throws SQLException {
        try {
            String query = "SELECT p.first_name, p.last_name, d.diagnosis_name," +
                    " COUNT(DISTINCT a.diagnosis_id) AS diagnosis_count FROM hospital.diagnosis d" +
                    " JOIN hospital.patient_admission a ON d.diagnosis_id = a.diagnosis_id" +
                    " JOIN hospital.patient p on a.patient_id = p.patient_id WHERE a.patient_id =" +
                    " (SELECT a.patient_id FROM hospital.patient_admission a GROUP BY a.patient_id ORDER BY " +
                    " COUNT(a.patient_id) DESC LIMIT 1) GROUP BY d.diagnosis_name ORDER BY diagnosis_count";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch(SQLException e) {
            System.out.println("Error Code: " +e.getErrorCode());
            System.out.println("SQL state: " +e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }

    }
    public static void getTreatmentOccurences(int treatment_id) throws SQLException {
        try {
            String query = "SELECT o.treatment_id, p.first_name, p.last_name, o.ordered_by" +
                    " FROM hospital.patient p JOIN hospital.patient_admission a ON a.patient_id =" +
                    " p.patient_id JOIN hospital.treatment_ordered o ON a.admit_id = o.admit_id" +
                    " WHERE o.treatment_id = " + treatment_id;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch (SQLException e) {
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL state: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }
    }
    public static void getWorkers() throws SQLException {
        try {
            String query = "SELECT last_name, first_name, job_name, job_category" +
                    " FROM hospital.hospital_employee ORDER BY last_name, first_name ASC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch (SQLException e) {
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL state: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }
    }

    public static void getPrimaryDoctors() throws SQLException {
        try {
            String query = "SELECT t1.patient_id, t1.primary_doctor, t1.first_name, t1.last_name\n" +
                    "FROM (SELECT a.patient_id, a.primary_doctor, e.first_name, e.last_name\n" +
                    "FROM hospital.patient_admission a \n" +
                    "JOIN hospital.hospital_employee e ON a.primary_doctor = e.employee_id) t1\n" +
                    "JOIN \n" +
                    "(SELECT a.patient_id, MIN(a.admit_date) as first_admit, MAX(a.admit_date) as last_admit\n" +
                    "FROM hospital.patient_admission a WHERE \n" +
                    "a.patient_id IN\n" +
                    "(SELECT a.patient_id\n" +
                    "FROM hospital.patient_admission a\n" +
                    "GROUP BY a.patient_id HAVING COUNT(a.patient_id) >= 4\n" +
                    "ORDER BY COUNT(a.patient_id) DESC)) t2\n" +
                    "ON t1.patient_id = t2.patient_id\n" +
                    "WHERE DATEDIFF(t2.last_admit, t2.first_admit) <= 365";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch (SQLException e) {
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL state: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }
    }

    public static void getDoctorsDiagnoses(int doctor) throws SQLException {
        try {
            String query = "SELECT a.primary_doctor, d.diagnosis_name, COUNT(a.diagnosis_id) AS count" +
                    " FROM hospital.patient_admission a JOIN hospital.diagnosis d ON a.diagnosis_id = " +
                    " d.diagnosis_id WHERE a.primary_doctor = " + doctor + " GROUP BY d.diagnosis_name" +
                    " ORDER BY count DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch (SQLException e) {
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL state: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }
    }
    public static void getDoctorsTreatments(int doctor) throws SQLException {
        try {
            String query = "SELECT o.ordered_by, o.treatment_id, t.treatment_name, COUNT(o.treatment_id) as count" +
                    " FROM hospital.treatment_ordered o JOIN hospital.treatment t ON o.treatment_id = t.treatment_id" +
                    " WHERE o.ordered_by = " + doctor + " GROUP BY o.treatment_id ORDER BY count DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch (SQLException e) {
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL state: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }
    }

    public static void getEmployeesInvolved() throws SQLException {
        try {
            String query = "SELECT o.admit_id, a.employee_id as administered_by, o.ordered_by, p.employee_id" +
                    " AS performed_by FROM hospital.treatment_administered a JOIN hospital.treatment_ordered o" +
                    " ON a.admit_id = o.admit_id JOIN hospital.treatment_performed p ON a.administer_id = " +
                    " p.administer_id ORDER BY o.admit_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");

            }
        } catch (SQLException e) {
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL state: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
        }
    }


}
