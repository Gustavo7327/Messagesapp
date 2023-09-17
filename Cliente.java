import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class Cliente implements ActionListener{
    public static void main(String[] args) {
        
        // JFrame frame = new JFrame();
        // JTextField field = new JTextField();
        
        // frame.setPreferredSize(new Dimension(300, 300));
        // frame.setLocationRelativeTo(null);
        // field.setBounds(100, 100, 100, 100);

        Scanner scanner = new Scanner(System.in);

        try {
            Socket socket = new Socket("localhost",3045);
            System.out.println("Cliente conectado");

            

            ClienteThread clienteThread = new ClienteThread(socket);
            clienteThread.start();

            PrintStream saida = new PrintStream(socket.getOutputStream());

            while(true){
                String teclado = scanner.nextLine();
            
                saida.println(teclado);
            }
            
            
            
            //frame.setVisible(true);
            
        } catch (UnknownHostException e) {
            e.printStackTrace();
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
            scanner.close();
        }
        
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        
    }
}