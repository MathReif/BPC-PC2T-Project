package services;

import java.io.*;
import java.util.*;
import students.*;

public class Manager {
    private List<Student> telecommunicationsStudents;
    private List<Student> cybersecurityStudents;
    private int nextId;
    public static int nextIdStatic = 1;

    Database database = new Database();

    public Manager() {
        telecommunicationsStudents = new ArrayList<>();
        cybersecurityStudents = new ArrayList<>();
        nextId = nextIdStatic;
        loadAllStudentsFromDatabase();
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
        nextIdStatic = nextId;
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
            System.out.println("Student with ID " + studentId + " has been removed.");
        } else {
            System.out.println("Student with ID " + studentId + " not found.");
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
        if (student == null) {
            System.out.println("Student with ID " + studentId + " not found.");
            return;
        }

        List<String> fileLines = new ArrayList<>();
        List<String> currentBlock = new ArrayList<>();
        boolean skipBlock = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("==== STUDENT ====")) {
                    if (!currentBlock.isEmpty() && !skipBlock) {
                        fileLines.addAll(currentBlock);
                    }
                    currentBlock = new ArrayList<>();
                    skipBlock = false;
                }

                currentBlock.add(line);

                if (line.startsWith("ID: ")) {
                    int id = Integer.parseInt(line.substring(4).trim());
                    if (id == studentId) {
                        skipBlock = true;
                    }
                }
            }

            if (!currentBlock.isEmpty() && !skipBlock) {
                fileLines.addAll(currentBlock);
            }

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        List<String> newBlock = new ArrayList<>();
        String type = (student instanceof TelecommunicationsStudent) ? "telecom" : "cyber";
        newBlock.add("==== STUDENT ====");
        newBlock.add("TYPE: " + type);
        newBlock.add("ID: " + student.getId());
        newBlock.add("FirstName: " + student.getFirstName());
        newBlock.add("LastName: " + student.getLastName());
        newBlock.add("BirthYear: " + student.getBirthYear());
        newBlock.add("Grades: " + student.getGrades().toString().replaceAll("[\\[\\]\\s]", ""));

        fileLines.addAll(newBlock);

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (String l : fileLines) {
                pw.println(l);
            }
            System.out.println("Student saved (and replaced if existed) in file: " + filename);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
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

    public void saveAllStudentsToDatabase() {
        database.saveAllToDatabase(telecommunicationsStudents, cybersecurityStudents);
    }

    public void loadAllStudentsFromDatabase() {
        telecommunicationsStudents.clear();
        cybersecurityStudents.clear();
        database.loadAllStudents(telecommunicationsStudents, cybersecurityStudents);
        nextId = nextIdStatic;
    }

	
}