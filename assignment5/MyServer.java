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
        String received = null;
        String feedback = "";
        // initial start
        System.out.print("You have " + GameConfiguration.guessNumber + " guesses to figure out the secret code or you lose the\n" +
                "game. Are you ready to play?: ");
        String code = SecretCodeGenerator.getInstance().getNewSecretCode();
        //String code = "RBYR";
        System.out.println();
        //generate  secret code
        System.out.print("Generating secret code ...");

        System.out.println(" (for this example the secret code is " + code + ")");

        System.out.println();

        // get the secret code
        String[] codeColors = new String[code.length()];
        for(int i = 0; i < code.length(); i++ ){
            codeColors[i] = String.valueOf(code.charAt(i));
        }

        // Initialize the game components
        Code secretCode = new Code();  // checks if guess is valid
        gameBoard = new GameBoard(secretCode, GameConfiguration.guessNumber); // set the game board

        // keep receiving guesses from client
        while (true)
        {
            try {
                // keep looping until game is not over and still have guesses
                while (!gameBoard.isGameOver()) {
                    // receive the guess (String) from client
                    received = dis.readUTF();

                    // check for logout
                    if (received.equals("logout")) {
                        this.isloggedin = false;
                        this.s.close();
                        break;
                    }

                    System.out.println("You have " + gameBoard.getRemaining_guesses() + " guesses left.");
                    System.out.println("What is your next guess?");
                    System.out.println("Type in the characters for your guess and press enter.");
                    System.out.println();
                    // print the received guess to the server console
                    System.out.println("Guess from " + name + ": " + received);
                    // help generate the feedback
                    feedback = gameBoard.generateFeedback(received);
                    System.out.println(feedback);
                    dos.writeUTF(feedback);
                }
                if (gameBoard.didPlayerLose()) {
                    // receive the guess (String) from client
                    String lose = "Sorry, you are out of guesses. You lose, boo-hoo.";
                    // help generate the feedback
                    feedback = gameBoard.generateFeedback(received);
                    System.out.println(feedback);
                    System.out.println(lose);
                    dos.writeUTF(lose);
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }

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
