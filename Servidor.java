import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        
            PrintStream saida = new PrintStream(cliente.getOutputStream());
            PrintStream saidadois = new PrintStream(cliente2.getOutputStream());

            InputStreamReader inputreader = new InputStreamReader(cliente.getInputStream());
            BufferedReader bfreader = new BufferedReader(inputreader);

            InputStreamReader inputreader2 = new InputStreamReader(cliente2.getInputStream());
            BufferedReader bfreader2 = new BufferedReader(inputreader2);

            String x;
            String y;

            while((x = bfreader.readLine()) != null && (y = bfreader2.readLine()) != null){
                saidadois.println("Pessoa 1: " + x);
                saida.println("Pessoa 2: " + y);
            }   

            if(cliente.isConnected() == false || cliente2.isConnected() == false){
                servidor.close();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            
        }
    }
}
