package assignment5;
import java.util.Arrays;

public class Code {
    private String[] colors;
    private boolean valid_guess = true;
    // have a boolean statement that shows if it is invalid guess or not for original code
    public Code() {

    }
    // check if guess is invalid or not
    public String invalidFeedback(String[] colors, String code){
        String feedback = "";
        if(colors.length != GameConfiguration.pegNumber){
            valid_guess = false;
            System.out.println(code + " -> INVALID GUESS");
            System.out.println();
            feedback = code + " -> INVALID GUESS";
        }
        else {
            for (String color : colors) {
                if (!isValidColor(color)) {
                    valid_guess = false;
                    System.out.println(code + " -> INVALID GUESS");
                    System.out.println();
                    feedback = code + " -> INVALID GUESS";
                    break;
                }
            }
        }
        if(valid_guess){
            this.colors = colors;
        }
        return feedback;
    }
    public String getStringColors(){
        return String.join("", colors);
    }
    public String[] getColors(){
        return colors;
    }

    public boolean get_valid_guess(){
        return valid_guess;
    }

    public boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()){
            return false;
        }
        Code otherCode = (Code) obj;
        return Arrays.equals(this.getColors(), otherCode.colors);
    }

    public boolean isValidColor(String color){
        for(String validColor : GameConfiguration.colors){
            if(validColor.equals(color)){
                return true;
            }
        }
        return false;
    }
}
