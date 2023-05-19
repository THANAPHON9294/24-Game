import java.io.*;
import java.net.*;
import java.util.*;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;



public class Server {
    private List<Socket> sockets;
    private static ThreadLocal<List<Integer>> questionThreadLocal = new ThreadLocal<>();

    public Server() throws IOException {
        sockets = new ArrayList<>();

        ServerSocket ss = new ServerSocket(1234); // สร้าง ServerSocket ด้วยพอร์ต 1234
        System.out.println("Server is running...");
        
        while (true) { // วนลูปรอรับการเชื่อมต่อจาก Client
            Socket socket = ss.accept(); // รับการเชื่อมต่อจาก Client
            sockets.add(socket);
            System.out.println("Client connected: " + socket);
            
            Thread clientThread = new Thread(()  -> {
                try {
                    ObjectOutputStream objoutput = new ObjectOutputStream(socket.getOutputStream());
                    List<Integer> fristquestion = generateRandomNumbers();
                    saveQuestion(fristquestion);
                    objoutput.writeObject(fristquestion); // ส่งผลลัพธ์ให้กับ Client
                
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                    String message ;
                    while ((message = input.readLine()) != null) {
                        System.out.println(message + " from " + socket);
                        if (message.equals("request new") ) {
                            List<Integer> question = generateRandomNumbers();
                            saveQuestion(question);
                            objoutput.writeObject(question);
                            System.out.println("Server new generate random numbers to " + socket);
                        } else {
                            if (isBalanced(message)) {
                                double result = evaluateMathExpression(message);
                                List<Integer> q = getQuestion();
                                System.out.println(result);
                                System.out.println(q);
                                objoutput.writeObject(correctCheck(result, message, q));
                            } else {
                                List<Integer> failparameter = new ArrayList<>();
                                failparameter.add(0);
                                objoutput.writeObject(failparameter);;
                            }
                            
                            
                        }   
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }); 
            
            clientThread.start();   
        }
    
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

    private static List<Integer> generateRandomNumbers() {
        Random rand = new Random();
        List<Integer> numbers = new ArrayList<Integer>();
        
        // สุ่มเลขสี่ตัว ตั้งแต่ 1-9 โดยใช้ Random class
        while (numbers.size() < 4) {
            int num = rand.nextInt(9) + 1;
            if (!numbers.contains(num)) {
                numbers.add(num);
            }
        }
        
        // ตรวจสอบว่าผลคูณของเลขใน list เท่ากับหรือมากกว่า 24 หรือไม่
        int product = 1;
        for (int i : numbers) {
            product *= i;
        }
        while (product < 24) {
            numbers.clear();
            while (numbers.size() < 4) {
                int num = rand.nextInt(9) + 1;
                if (!numbers.contains(num)) {
                    numbers.add(num);
                }
            }
            product = 1;
            for (int i : numbers) {
                product *= i;
            }
        }
        return numbers;
    }

    // ใช้ GraalVM ที่เป็น JavaScript engine ช่วยในการนำ String มาคำนวณหาผลลัพท์
    private static double evaluateMathExpression(String expression) {
        try (Context context = Context.create()) {
            Value result = context.eval("js", expression);
            return result.asDouble();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid math expression: " + expression);
        }
    }

    // method สำหรับเช็คสถานะของ String ที่นำมาคำนวณ
    private static List<Integer> correctCheck(double result, String massage, List<Integer> question) {
        List<Integer> status = new ArrayList<>();
        if (result == 24) {
            // System.out.println(question);
            if (cheatCheck(massage, question)) {
                status.add(1);
            } else {
                status.add(3);
            }
            
        } else {
            status.add(2);
            
        }
        return status;
    }
    
    // เป็น method ที่เอาไว้เช็คตัวเลขจาก String ที่รับมาว่าตรงกับค่า List ที่สุ่มให้หรือไม่
    private static boolean cheatCheck(String massage, List<Integer> question) {
        Map<Integer, Integer> countMap = new HashMap<>(); // ใช้ Map เก็บจำนวนครั้งของแต่ละตัวเลข

        // นับจำนวนครั้งของแต่ละตัวเลขในสตริง
        StringBuilder currentNumber = new StringBuilder();
        for (char c : massage.toCharArray()) {
            if (Character.isDigit(c)) {
                currentNumber.append(c);
            } else {
                if (currentNumber.length() > 0) {
                    int number = Integer.parseInt(currentNumber.toString());
                    countMap.put(number, countMap.getOrDefault(number, 0) + 1);
                    currentNumber.setLength(0);
                }
            }
        }
        // ตรวจสอบตัวเลขที่เหลือหลังจากจบสตริง
        if (currentNumber.length() > 0) {
            int number = Integer.parseInt(currentNumber.toString());
            countMap.put(number, countMap.getOrDefault(number, 0) + 1);
        }

        // เปรียบเทียบจำนวนครั้งของแต่ละตัวเลขในสตริงกับลิสต์
        for (int num : question) {
            if (!countMap.containsKey(num) || countMap.get(num) == 0) {
                return false; // หากจำนวนครั้งของตัวเลขในสตริงไม่ตรงกับลิสต์ จะคืนค่า false
            }
            countMap.put(num, countMap.get(num) - 1);
        }

        // ตรวจสอบว่าจำนวนครั้งของตัวเลขในสตริงและลิสต์เท่ากันหรือไม่
        for (int count : countMap.values()) {
        	if (count != 0) {
        	return false;
        	}
        }
        return true;
    }

    // เป็น method ที่เอาไว้เช็คความสมดุลของวงเล็บ เพื่อป้องกัน Error เวลานำค่า String ไปใช้ GraalVM
    private static boolean isBalanced(String input) {
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '(' ) {
                stack.push(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) {
                    return false;
                }

                char top = stack.pop();

                if ((ch == ')' && top != '(')) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    // เป็น method เก็บค่าของโจทย์ที่ server สุ่ม เมื่อมีการเชื่อมต่อมากกว่า 1 client
    private static ThreadLocal<List<Integer>> saveQuestion(List<Integer> question) {
        questionThreadLocal.set(question);
        return questionThreadLocal;
    }
 
    // เป็น method อ่านค่าของโจทย์ที่ server สุ่ม เมื่อมีการเชื่อมต่อมากกว่า 1 client
    private static List<Integer> getQuestion() { 
        return questionThreadLocal.get();
    }
}

