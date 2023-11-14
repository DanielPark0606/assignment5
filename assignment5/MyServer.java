package assignment5;
import java.io.*;
import java.util.*;
import java.net.*;

public class MyServer {
    // Vector store active clients (broadcast message to each object)
    static Vector<ClientHandler> ar = new Vector<>();

    // counter for clients
    static int i = 0;

    public static void main(String[] args) throws IOException{
        //server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);
        //running infinite loop for client request
        while(true){
            Socket s;
            try {
                //establishes connection to receive client request
                s = ss.accept();

                System.out.println("New client request received : " + s);

                // obtain input and output stream
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                // create a new handler object for handling request
                ClientHandler mtch = new ClientHandler(s, "client " + i, dis, dos);

                System.out.println("Creating a new handler for this client ...");

                // create new thread with object
                Thread t = new Thread(mtch);

                System.out.println("Adding this client to active client list");

                // add client to active client list
                ar.add(mtch);
                // start the thread
                t.start();
                i++;
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
    }
}

// ClientHandler class
class ClientHandler implements Runnable
{
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    String name;
    boolean isloggedin;

    private GameBoard gameBoard;
    Scanner scn = new Scanner(System.in);

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;
    }

    @Override
    public void run() {
        String received;
        String feedback;
        //boolean continueGame;
        // keep receiving guesses from client
        while (true)
        {
            try {
                System.out.println();
                System.out.println("Welcome to Mastermind.");
                dos.writeUTF("Welcome to Mastermind.");
                System.out.println();
                System.out.println("This is a text version of the classic board game Mastermind.");
                dos.writeUTF("This is a text version of the classic board game Mastermind.");
                System.out.println();

                // initial start
                System.out.print("You have " + GameConfiguration.guessNumber + " guesses to figure out the secret code or you lose the\n" +
                        "game. Are you ready to play? (Y/N): ");
                dos.writeUTF("You have " + GameConfiguration.guessNumber + " guesses to figure out the secret code or you lose the\n" +
                        "game. Are you ready to play? (Y/N): ");
                received = dis.readUTF();
                if (received.equals("Y")) {
                    isloggedin = true;
                    //dos.writeUTF("Y");
                    System.out.println("Y");
                } else {
                    isloggedin = false;
                    //dos.writeUTF("N");
                    System.out.println("N");
                }
                // if continueGame is true suspend and output the end message and close

                String code = SecretCodeGenerator.getInstance().getNewSecretCode();
                if(isloggedin) {
                    System.out.print("Generating secret code ... (for this example the secret code is " + code + ")");
                    dos.writeUTF("Generating secret code ... (for this example the secret code is " + code + ")");
                    System.out.println();
                }

                // get the secret code
                String[] codeColors = new String[code.length()];
                for (int i = 0; i < code.length(); i++) {
                    codeColors[i] = String.valueOf(code.charAt(i));
                }

                // Initialize the game components
                Code secretCode = new Code(codeColors, code);  // checks if guess is valid
                gameBoard = new GameBoard(secretCode, GameConfiguration.guessNumber); // set the game board


                // keep looping until game is not over and still have guesses
                while (!gameBoard.isGameOver() && isloggedin) {

                    System.out.println("You have " + gameBoard.getRemaining_guesses() + " guesses left.");
                    System.out.println("What is your next guess?");
                    System.out.println("Type in the characters for your guess and press enter.");
                    System.out.println();
                    dos.writeUTF("You have " + gameBoard.getRemaining_guesses() + " guesses left.");
                    dos.writeUTF("What is your next guess?");
                    // receive the guess (String) from client
                    received = dis.readUTF();

                    // check for logout
                    if (received.equals("logout")) {
                        this.isloggedin = false;
                        this.s.close();
                        break;
                    }

                    // print the received guess to the server console
                    System.out.println("Guess from " + name + ": " + received);
                    // help generate the feedback
                    feedback = gameBoard.generateFeedback(received);
                    System.out.println(feedback);
                    dos.writeUTF(feedback);
                    // out of guess
                    if(gameBoard.didPlayerLose()) {
                        String lose = "Sorry, you are out of guesses. You lose, boo-hoo.";
                        System.out.println(lose);
                        dos.writeUTF(lose);
                    }
                }
                // if not logged in connection closed
                System.out.println("Connection closed");
                dos.writeUTF("Connection closed");
                this.s.close();
                break;


            } catch (EOFException eofException){
                System.out.println("Server closed connection");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            //closing resources
            this.dis.close();
            this.dos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
