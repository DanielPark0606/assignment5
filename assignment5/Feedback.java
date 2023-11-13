package assignment5;

public class Feedback {
    private int blackPegs;
    private int whitePegs;

    public Feedback(int blackPegs, int whitePegs) {
        this.blackPegs = blackPegs;
        this.whitePegs = whitePegs;
    }
    public int getBlackPegs(){
        return blackPegs;
    }
    public int getWhitePegs(){
        return whitePegs;
    }
    public String toString() {
        return  blackPegs + "B_" + whitePegs + "W";
    }
}
