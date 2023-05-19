import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private static PrintWriter output;
    private static ObjectInputStream objIn;
    private static boolean gamerun = false;
    
    public Client() throws IOException, ClassNotFoundException{
        
        Socket socket = new Socket("localhost", 1234); // เชื่อมต่อ Server ที่เป็น localhost ด้วยพอร์ต 8000

        objIn = new ObjectInputStream(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);
        
        Object resultObj = objIn.readObject();
        if (resultObj instanceof List && ((List) resultObj).size() == 4) {
            @SuppressWarnings("unchecked")
            List<Integer> result = (List<Integer>) resultObj;
            System.out.println("Random numbers: " + result.toString());
      
            new game24(result);
            gamerun = true;
        }

        while (gamerun) {
            while((resultObj = objIn.readObject()) != null) {   
                if (resultObj instanceof List && ((List) resultObj).size() == 4) {
                    @SuppressWarnings("unchecked")
                    List<Integer> result = (List<Integer>) resultObj;
                    System.out.println("New random numbers: " + result.toString());
                    game24.updateNewNumbers(result);
                } else {
                    @SuppressWarnings("unchecked")
                    List<Integer> status = (List<Integer>) resultObj;
                    game24.popup(status.get(0));
                }
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        new Client();
    }

    // เป็น method ที่เอาไว้ให้ไฟล์ game24 ที่เป็น GUI เรียกใช้เพื่อส่งขอความให้ Server เมื่อมาการกดปุ่ม new
    public static void newButtonActive(String message) {
        System.out.println("Sent request to new question");
        output.println(message);
    }

    // เป็น method ที่เอาไว้ให้ไฟล์ game24 ที่เป็น GUI เรียกใช้เพื่อส่งขอความให้ Server เมื่อมาการกดปุ่ม check
    public static void checkButtonActive(String message) {
        System.out.println("Sent request to check answer");
        output.println(message);
    }
}
