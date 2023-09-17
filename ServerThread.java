import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ServerThread extends Thread{
    
    private Socket cliente;
    private PrintStream saida;
    private Socket cliente2;

    public ServerThread(Socket cliente, Socket cliente2, PrintStream saida){
        this.cliente = cliente;
        this.saida = saida;
        this.cliente2 = cliente2;
    }

    @Override
    public void run(){

        try{
            saida = new PrintStream(cliente2.getOutputStream());
            InputStreamReader inputreader = new InputStreamReader(cliente.getInputStream());
            BufferedReader bfreader = new BufferedReader(inputreader);
            String x;
            
            while((x = bfreader.readLine()) != null ){
                saida.println("Pessoa: " + x);
                
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }

}
