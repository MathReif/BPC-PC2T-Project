
package students;


import java.io.Serializable;
import java.util.*;

public abstract class Student implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected int id;
    protected String firstName;
    protected String lastName;
    protected int birthYear;
    protected List<Integer> grades;
    
    public Student(int id, String firstName, String lastName, int birthYear) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthYear = birthYear;
        this.grades = new ArrayList<>();
    }
    
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getBirthYear() { return birthYear; }
    
    public void addGrade(int grade) {
        if (grade >= 1 && grade <= 5) {
            grades.add(grade);
        }
    }
    
    public double getAverage() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0;
        for (int grade : grades) {
            sum += grade;
        }
        return sum / grades.size();
    }
    
    public abstract void performSkill();
    
    @Override
    public String toString() {
        return String.format("ID: %d, Name: %s %s, Birth Year: %d, Average: %.2f", 
                            id, firstName, lastName, birthYear, getAverage());
    }
    
    public List<Integer> getGrades() {
        return grades;
    }
}

