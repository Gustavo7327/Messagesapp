import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {

        try {
            ServerSocket servidor = new ServerSocket(3045);
            System.out.println("seerver criado");

            Socket cliente = servidor.accept();
            System.out.println("cliente conectado " + cliente.getInetAddress().getHostAddress());

            System.out.println("Aguardando nova conexao...");

            Socket cliente2 = servidor.accept();
            System.out.println("cliente 2 conectado " + cliente2.getInetAddress().getHostAddress());

            PrintStream saida = new PrintStream(cliente2.getOutputStream());
            PrintStream saida2 = new PrintStream(cliente.getOutputStream());

            ServerThread serverThread = new ServerThread(cliente, cliente2, saida);
            ServerThread serverThread2 = new ServerThread(cliente2, cliente, saida2);
            
            while(true){
                serverThread.start();
                serverThread2.start();

            if(cliente.isConnected() == false || cliente2.isConnected() == false){
                servidor.close();
            }
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }
}
