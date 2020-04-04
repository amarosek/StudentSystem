import java.sql.*;
import java.util.Scanner;

public class StudentApp {

    private final String url = "jdbc:postgresql://localhost/Students";
    private final String user = "postgres";
    private final String password = "admin";

    int pay;
    int id;

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }


    public StudentApp() {
        displayMenu();
    }

    public void displayMenu() {
        while (true) {
            final StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Choose one of the options: ")
                    .append("\n1. View students,")
                    .append("\n2. Add student,")
                    .append("\n3. Manage students payment,")
                    .append("\n4. Exit application.");

            System.out.println(sb.toString());

            Scanner in = new Scanner(System.in);
            int command = in.nextInt();

            switch (command) {
                case 1:
                    getStudents();
                    break;
                case 2:
                    addStudent();
                    break;
                case 3:
                    manageStudent();
                    payStudent();
                    break;
                case 4:
                    exitSystem();
                    break;
                default:
                    System.out.println("Wrong command, try again.");
                    break;
            }
        }
    }

    public void getStudents() {

        String SQL = "SELECT id, firstName, lastName, payment FROM student";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            displayStudent(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public long addStudent() {

        String SQL = "INSERT INTO student(firstName,lastName, age, payment) "
                + "VALUES(?,?,?,?)";

        long id = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            Student student = new Student();

            System.out.println("Enter person first name: ");
            Scanner line2 = new Scanner(System.in);
            student.setFirstName(line2.nextLine());

            System.out.println("Enter person last name: ");
            Scanner line3 = new Scanner(System.in);
            student.setLastName(line3.nextLine());

            System.out.println("Enter person age: ");
            Scanner line4 = new Scanner(System.in);
            student.setAge(line4.nextInt());

            System.out.println("How many subjects you need to pay for: ");
            Scanner line5 = new Scanner(System.in);
            int amount = line5.nextInt();
            student.setPayment(amount * 300);

            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setInt(3, student.getAge());
            pstmt.setDouble(4, student.getPayment());

            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return id;
    }

    public void manageStudent() {

        String SQL = "SELECT id,firstName,lastName, age, payment "
                + "FROM student "
                + "WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {



            System.out.println("Enter student's id: ");
            Scanner line1 = new Scanner(System.in);
            id = line1.nextInt();

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                pay= ((Number) rs.getObject("payment")).intValue();
            }
            System.out.println("Student needs to pay: " + pay);


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public int payStudent(){
        String SQL = "UPDATE student "
                + "SET payment = ? "
                + "WHERE id = ?";

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {


            int amount;
            int payment = pay;



            System.out.println("Enter how much you want to pay: ");
            Scanner line2 = new Scanner(System.in);
            amount = line2.nextInt();

            payment -= amount;


            pstmt.setInt(1, payment);
            pstmt.setInt(2, id);

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return affectedrows;
        }


    public void exitSystem() {

        System.exit(0);

        return;
    }

    private void displayStudent(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println(rs.getString("id") + "\t"
                    + rs.getString("firstName") + "\t"
                    + rs.getString("lastName") + "\t"
                    + rs.getInt("payment"));
        }
    }
}
