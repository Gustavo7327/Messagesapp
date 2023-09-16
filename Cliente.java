import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente{
    public static void main(String[] args) {
        
        try {
            Socket socket = new Socket("localhost",3045);
            System.out.println("Cliente conectado");

            Scanner scanner = new Scanner(System.in);

            ClienteThread clienteThread = new ClienteThread(socket);
            clienteThread.start();

            PrintStream saida = new PrintStream(socket.getOutputStream());

            String teclado = scanner.nextLine();
            
            saida.println(teclado);

            scanner.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}