package students;

import java.util.*;

public class TelecommunicationsStudent extends Student {

	 private static final long serialVersionUID = 1L;
	 
	public TelecommunicationsStudent(int id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }
    
    @Override
    public void performSkill() {
        System.out.println("Morse code for " + firstName + " " + lastName + ":");
        System.out.println(convertToMorse(firstName + " " + lastName));
    }
    
    private String convertToMorse(String text) {
        Map<Character, String> morseCode = new HashMap<>();
        morseCode.put('A', ".-"); morseCode.put('B', "-..."); morseCode.put('C', "-.-.");
        morseCode.put('D', "-.."); morseCode.put('E', "."); morseCode.put('F', "..-.");
        morseCode.put('G', "--."); morseCode.put('H', "...."); morseCode.put('I', "..");
        morseCode.put('J', ".---"); morseCode.put('K', "-.-"); morseCode.put('L', ".-..");
        morseCode.put('M', "--"); morseCode.put('N', "-."); morseCode.put('O', "---");
        morseCode.put('P', ".--."); morseCode.put('Q', "--.-"); morseCode.put('R', ".-.");
        morseCode.put('S', "..."); morseCode.put('T', "-"); morseCode.put('U', "..-");
        morseCode.put('V', "...-"); morseCode.put('W', ".--"); morseCode.put('X', "-..-");
        morseCode.put('Y', "-.--"); morseCode.put('Z', "--..");
        morseCode.put('0', "-----"); morseCode.put('1', ".----"); morseCode.put('2', "..---");
        morseCode.put('3', "...--"); morseCode.put('4', "....-"); morseCode.put('5', ".....");
        morseCode.put('6', "-...."); morseCode.put('7', "--..."); morseCode.put('8', "---..");
        morseCode.put('9', "----."); morseCode.put(' ', "/");
        
        StringBuilder morse = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            morse.append(morseCode.getOrDefault(c, "")).append(" ");
        }
        return morse.toString();
    }
}
