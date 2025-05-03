package services;

import java.sql.*;
import java.util.*;
import students.*;

public class Database {
    private Connection dbConnection;

    public Database() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:database/students.db");
            createTables();
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = dbConnection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "id INTEGER PRIMARY KEY, " +
                    "type TEXT, " +
                    "first_name TEXT, " +
                    "last_name TEXT, " +
                    "birth_year INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS grades (" +
                    "student_id INTEGER, " +
                    "grade INTEGER, " +
                    "FOREIGN KEY(student_id) REFERENCES students(id))");
        }
   
}

    public void loadAllStudents(List<Student> telecomList, List<Student> cyberList) {
        Map<Integer, Student> studentMap = new HashMap<>();
        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int birthYear = rs.getInt("birth_year");

                Student student;
                if (type.equals("telecom")) {
                    student = new TelecommunicationsStudent(id, firstName, lastName, birthYear);
                    telecomList.add(student);
                } else {
                    student = new CybersecurityStudent(id, firstName, lastName, birthYear);
                    cyberList.add(student);
                }

                studentMap.put(id, student);

                if (id >= Manager.nextIdStatic) {
                    Manager.nextIdStatic = id + 1;
                }
            }

            try (Statement gradeStmt = dbConnection.createStatement();
                 ResultSet gradeRs = gradeStmt.executeQuery("SELECT * FROM grades")) {
                while (gradeRs.next()) {
                    int studentId = gradeRs.getInt("student_id");
                    int grade = gradeRs.getInt("grade");
                    Student student = studentMap.get(studentId);
                    if (student != null) {
                        student.addGrade(grade);
                    }
                }
            }

            System.out.println("Loaded all students from database.");
        } catch (SQLException e) {
            System.err.println("Error loading from database: " + e.getMessage());
        }
    }
    

    public void saveAllToDatabase(List<Student> telecomStudents, List<Student> cyberStudents) {
        try {
            try (Statement stmt = dbConnection.createStatement()) {
                stmt.execute("DELETE FROM students");
                stmt.execute("DELETE FROM grades");
            }
            for (Student student : telecomStudents) {
                saveStudentToDatabase(student);
                for (int grade : student.getGrades()) {
                    saveGradeToDatabase(student.getId(), grade);
                }
            }
            for (Student student : cyberStudents) {
                saveStudentToDatabase(student);
                for (int grade : student.getGrades()) {
                    saveGradeToDatabase(student.getId(), grade);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving to database: " + e.getMessage());
        }
    }

    private void saveStudentToDatabase(Student student) {
        String type = (student instanceof TelecommunicationsStudent) ? "telecom" : "cyber";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(
                "INSERT INTO students(id, type, first_name, last_name, birth_year) VALUES(?, ?, ?, ?, ?)")) {
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, type);
            pstmt.setString(3, student.getFirstName());
            pstmt.setString(4, student.getLastName());
            pstmt.setInt(5, student.getBirthYear());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving student to database: " + e.getMessage());
        }
    }

    private void saveGradeToDatabase(int studentId, int grade) {
        try (PreparedStatement pstmt = dbConnection.prepareStatement(
                "INSERT INTO grades(student_id, grade) VALUES(?, ?)")) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, grade);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving grade to database: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (dbConnection != null) {
                dbConnection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}