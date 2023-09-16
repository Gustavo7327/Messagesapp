import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClienteThread extends Thread{

    private Socket socket;

    public ClienteThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){

        try {
            InputStreamReader inputreader = new InputStreamReader(socket.getInputStream());
            BufferedReader bfreader = new BufferedReader(inputreader);
            String x;
            while((x = bfreader.readLine()) != null){
                System.out.println(x);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}