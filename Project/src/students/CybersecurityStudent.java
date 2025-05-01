package students;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CybersecurityStudent extends Student {

	 private static final long serialVersionUID = 1L;
	public CybersecurityStudent(int id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }
    
    @Override
    public void performSkill() {
        System.out.println("SHA-256 hash for " + firstName + " " + lastName + ":");
        System.out.println(calculateHash(firstName + " " + lastName));
    }
    
    private String calculateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
