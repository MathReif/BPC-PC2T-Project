package services;

import java.util.*;
import java.io.*;
import java.sql.*;
import students.*;

public class Database {
    private List<Student> telecommunicationsStudents;
    private List<Student> cybersecurityStudents;
    private int nextId;
    private Connection dbConnection;

    public Database() {
        telecommunicationsStudents = new ArrayList<>();
        cybersecurityStudents = new ArrayList<>();
        nextId = 1;
        connectToDatabase();
        loadFromDatabase();
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

    public void addStudent(int type, String firstName, String lastName, int birthYear) {
        Student student;
        if (type == 1) {
            student = new TelecommunicationsStudent(nextId, firstName, lastName, birthYear);
            telecommunicationsStudents.add(student);
        } else {
            student = new CybersecurityStudent(nextId, firstName, lastName, birthYear);
            cybersecurityStudents.add(student);
        }
        nextId++;
    }

    public void addGrade(int studentId, int grade) {
        Student student = findStudentById(studentId);
        if (student != null) {
            student.addGrade(grade);
        } else {
            System.out.println("Student with ID " + studentId + " not found.");
        }
    }

    public void removeStudent(int studentId) {
        Student student = findStudentById(studentId);
        if (student != null) {
            if (student instanceof TelecommunicationsStudent) {
                telecommunicationsStudents.remove(student);
            } else {
                cybersecurityStudents.remove(student);
            }
            removeStudentFromDatabase(studentId);
            System.out.println("Student with ID " + studentId + " has been removed.");
        } else {
            System.out.println("Student with ID " + studentId + " not found.");
        }
    }

    private void removeStudentFromDatabase(int studentId) {
        try (PreparedStatement pstmt1 = dbConnection.prepareStatement("DELETE FROM grades WHERE student_id = ?");
             PreparedStatement pstmt2 = dbConnection.prepareStatement("DELETE FROM students WHERE id = ?")) {
            pstmt1.setInt(1, studentId);
            pstmt1.executeUpdate();
            pstmt2.setInt(1, studentId);
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing student from database: " + e.getMessage());
        }
    }

    public Student findStudentById(int studentId) {
        for (Student s : telecommunicationsStudents) {
            if (s.getId() == studentId) return s;
        }
        for (Student s : cybersecurityStudents) {
            if (s.getId() == studentId) return s;
        }
        return null;
    }

    public void performStudentSkill(int studentId) {
        Student student = findStudentById(studentId);
        if (student != null) {
            student.performSkill();
        } else {
            System.out.println("Student with ID " + studentId + " not found.");
        }
    }

    public void printAllStudents() {
        System.out.println("\nTelecommunications Students:");
        telecommunicationsStudents.sort(Comparator.comparing(Student::getLastName));
        telecommunicationsStudents.forEach(System.out::println);
        System.out.println("\nCybersecurity Students:");
        cybersecurityStudents.sort(Comparator.comparing(Student::getLastName));
        cybersecurityStudents.forEach(System.out::println);
    }

    public void printDepartmentAverages() {
        double telecomAvg = calculateDepartmentAverage(telecommunicationsStudents);
        double cyberAvg = calculateDepartmentAverage(cybersecurityStudents);
        System.out.println("\nDepartment Averages:");
        System.out.printf("Telecommunications: %.2f\n", telecomAvg);
        System.out.printf("Cybersecurity: %.2f\n", cyberAvg);
    }

    private double calculateDepartmentAverage(List<Student> students) {
        if (students.isEmpty()) return 0.0;
        double sum = 0;
        for (Student s : students) {
            sum += s.getAverage();
        }
        return sum / students.size();
    }

    public void printStudentCounts() {
        System.out.println("\nStudent Counts:");
        System.out.println("Telecommunications: " + telecommunicationsStudents.size());
        System.out.println("Cybersecurity: " + cybersecurityStudents.size());
    }

    public void saveStudentToFile(int studentId, String filename) {
        Student student = findStudentById(studentId);
        if (student != null) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
                String type = (student instanceof TelecommunicationsStudent) ? "telecom" : "cyber";
                pw.println("==== STUDENT ====");
                pw.println("TYPE: " + type);
                pw.println("ID: " + student.getId());
                pw.println("FirstName: " + student.getFirstName());
                pw.println("LastName: " + student.getLastName());
                pw.println("BirthYear: " + student.getBirthYear());
                pw.println("Grades: " + student.getGrades().toString().replaceAll("[\\[\\]\\s]", ""));
                System.out.println("Student saved to text file: " + filename);
            } catch (IOException e) {
                System.err.println("Error saving student to file: " + e.getMessage());
            }
        } else {
            System.out.println("Student with ID " + studentId + " not found.");
        }
    }

    public void loadStudentFromFile(String filename, int targetId) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("==== STUDENT ====")) {
                    String type = null, firstName = null, lastName = null, gradesLine = null;
                    int id = -1, birthYear = -1;
                    for (int i = 0; i < 5; i++) {
                        line = br.readLine();
                        if (line != null && line.contains(": ")) {
                            String[] parts = line.split(": ");
                            if (parts.length > 1) {
                                String key = parts[0].trim().toLowerCase();
                                String value = parts[1].trim();
                                switch (key) {
                                    case "type":
                                        type = value;
                                        break;
                                    case "id":
                                        id = Integer.parseInt(value);
                                        break;
                                    case "firstname":
                                        firstName = value;
                                        break;
                                    case "lastname":
                                        lastName = value;
                                        break;
                                    case "birthyear":
                                        birthYear = Integer.parseInt(value);
                                        break;
                                    case "grades":
                                        gradesLine = value;
                                        break;
                                }
                            }
                        }
                    }
                    if (id == targetId) {
                        if (findStudentById(id) != null) {
                            System.out.println("Student with ID " + id + " already exists in memory.");
                            return;
                        }
                        Student student;
                        if (type != null && type.equalsIgnoreCase("telecom")) {
                            student = new TelecommunicationsStudent(id, firstName, lastName, birthYear);
                            telecommunicationsStudents.add(student);
                        } else {
                            student = new CybersecurityStudent(id, firstName, lastName, birthYear);
                            cybersecurityStudents.add(student);
                        }
                        if (gradesLine != null && !gradesLine.isEmpty()) {
                            String[] gradesArray = gradesLine.split(",");
                            for (String g : gradesArray) {
                                student.addGrade(Integer.parseInt(g.trim()));
                            }
                        }
                        if (id >= nextId) {
                            nextId = id + 1;
                        }
                        System.out.println("Loaded student: " + student);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                System.out.println("Student with ID " + targetId + " not found in the file.");
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading student from file: " + e.getMessage());
        }
    }

    private void loadFromDatabase() {
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
                    telecommunicationsStudents.add(student);
                } else {
                    student = new CybersecurityStudent(id, firstName, lastName, birthYear);
                    cybersecurityStudents.add(student);
                }
                if (id >= nextId) {
                    nextId = id + 1;
                }
            }
            try (Statement gradeStmt = dbConnection.createStatement();
                 ResultSet gradeRs = gradeStmt.executeQuery("SELECT * FROM grades")) {
                while (gradeRs.next()) {
                    int studentId = gradeRs.getInt("student_id");
                    int grade = gradeRs.getInt("grade");
                    Student student = findStudentById(studentId);
                    if (student != null) {
                        student.addGrade(grade);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading from database: " + e.getMessage());
        }
    }

    public void saveAllToDatabase() {
        try {
            try (Statement stmt = dbConnection.createStatement()) {
                stmt.execute("DELETE FROM students");
                stmt.execute("DELETE FROM grades");
            }
            for (Student student : telecommunicationsStudents) {
                saveStudentToDatabase(student);
                for (int grade : student.getGrades()) {
                    saveGradeToDatabase(student.getId(), grade);
                }
            }
            for (Student student : cybersecurityStudents) {
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