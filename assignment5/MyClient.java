package assignment5;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MyClient {
    public static void main(String[] args) {
        // create scanner for user input
        Scanner scn = new Scanner(System.in);

        try{
            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");
            // attempts to establish connection to server on 6666 host
            Socket s = new Socket(ip,1234);
            // this stream is used to send data to server
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos =new DataOutputStream(s.getOutputStream());

            // sendMessage thread
            Thread sendMessage = new Thread(new Runnable()
            {
                @Override
                public void run() {
                    while (true) {

                        // read the message to deliver.
                        String msg = scn.nextLine();

                        try {
                            // write on the output stream
                            dos.writeUTF(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            // readMessage thread
            Thread readMessage = new Thread(new Runnable()
            {
                @Override
                public void run() {

                    while (true) {
                        try {
                            // read the message sent to this client
                            String msg = dis.readUTF();
                            System.out.println(msg);
                            if(msg.equals("Connection closed")){
                                System.out.println("Closing this connection");
                                s.close();
                                break;
                            }
                        }
                        catch (IOException e) {

                            e.printStackTrace();
                        }
                    }
                }
            });
            sendMessage.start();
            readMessage.start();

        }catch(Exception e){
            System.out.println(e);
        }
    }
}
