package assignment5;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    private Code secretCode;
    private List<Code> playerGuesses;
    private List<Feedback> feedbackHistory;
    private int remainingGuesses;
    //public boolean gameOver = false;

    public GameBoard(Code secretCode, int maxGuesses) {
        this.secretCode = secretCode;
        this.remainingGuesses = maxGuesses;
        playerGuesses = new ArrayList<>();
        feedbackHistory = new ArrayList<>();
    }

    // generate feedback through string output

    public String generateFeedback(String playerGuessInput) {
        String feedback;
        if(playerGuessInput.equals("HISTORY")){
            feedback = getFeedbackHistory();
            //System.out.println();
        }
        else {
            // Parse the player's input into a Code object (R,G,B,O,...)
            String[] playerGuessColors = new String[playerGuessInput.length()];
            for (int i = 0; i < playerGuessInput.length(); i++) {
                playerGuessColors[i] = String.valueOf(playerGuessInput.charAt(i));
            }
            // checks if code is valid or not
            Code playerGuess = new Code(playerGuessColors, playerGuessInput);
            // check error message when it is invalid guess
            if(!playerGuess.get_valid_guess()) {
                feedback = playerGuess.invalidFeedback(playerGuessColors, playerGuessInput);
            }
            // checks what peg it generates if code is valid
            else {
                feedback = generatePegs(playerGuess);
            }
        }
        return feedback;
    }



    // Implement methods to add guesses, generate feedback, and check for game over
    // game end when player correctly guesses code on last attempt
    public boolean isGameOver(){
        return isOutOfGuesses();
    }
    public boolean didPlayerLose(){
        return isOutOfGuesses() && (!playerGuesses.isEmpty() && !isGuessCorrect(playerGuesses.get(playerGuesses.size() - 1)));
    }
    public boolean isOutOfGuesses() {
        return remainingGuesses == 0;
    }

    public boolean isGuessCorrect(Code guess){
        return secretCode.equals(guess);
    }
    public void addGuess(Code guess) {
        // if guess is valid add to history
        remainingGuesses--;
        playerGuesses.add(guess);
    }
    public String getFeedbackHistory() {
        // print out code, tabs, feedback
        String output = "";
        for(int i = 0; i <feedbackHistory.size(); i++){
            output += playerGuesses.get(i).getStringColors() + "\t\t" + feedbackHistory.get(i) + "\n";
        }
        return output;
    }
    public int getRemaining_guesses(){
        return remainingGuesses;
    }

    public String generatePegs(Code playerGuess) {
        String output = "";
        // check if code is valid
        if (playerGuess.get_valid_guess()) {
            // if valid add guess and give feedback
            addGuess(playerGuess);
            // Implement logic to provide feedback (black and white pegs)
            int blackPegs = 0;
            int whitePegs = 0;
            // array keep track pegs
            boolean[] guessMatched = new boolean[GameConfiguration.pegNumber];
            boolean[] secretCodeMatched = new boolean[GameConfiguration.pegNumber];
            // check black pegs (correct color and position)
            for (int i = 0; i < GameConfiguration.pegNumber; i++) {
                if (playerGuess.getColors()[i].equals(secretCode.getColors()[i])) {
                    blackPegs++;
                    guessMatched[i] = true;
                    secretCodeMatched[i] = true;
                }
            }
            //check white pegs (correct color)
            for (int i = 0; i < GameConfiguration.pegNumber; i++) {
                if (!guessMatched[i]) {
                    for (int j = 0; j < GameConfiguration.pegNumber; j++) {
                        if (!secretCodeMatched[j] && playerGuess.getColors()[i].equals(secretCode.getColors()[j])) {
                            whitePegs++;
                            guessMatched[i] = true;
                            secretCodeMatched[j] = true;
                            break;
                        }
                    }
                }
            }
            // Update feedbackHistory with the new feedback.
            Feedback feedback = new Feedback(blackPegs, whitePegs);
            feedbackHistory.add(feedback);
            if (blackPegs == GameConfiguration.pegNumber) {
                // flag for winner
                output =  "You win !!";
                remainingGuesses = 0;
            } else {
                if (remainingGuesses >= 0) {
                    output = playerGuess.getStringColors() + " -> Result: " + feedback + "\n";
                }
            }
        }
        return output;
    }
}
