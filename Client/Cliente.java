import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        String endereco = "localhost"; // Endereço do servidor
        int porta = 12345; // Porta do servidor

        try (Socket socket = new Socket(endereco, porta)) {
            System.out.println("Conectado ao servidor!");

            // Streams para comunicação
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);

            // Thread para ouvir mensagens do servidor
            new Thread(() -> {
                try {
                    String mensagem;
                    while ((mensagem = entrada.readLine()) != null) {
                        System.out.println(mensagem);
                    }
                } catch (IOException e) {
                    System.out.println("Conexão encerrada.");
                }
            }).start();

            // Envia mensagens para o servidor
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            String mensagem;
            while (true) {
                mensagem = teclado.readLine();
                saida.println(mensagem);
                if (mensagem.equalsIgnoreCase("sair")) {
                    System.out.println("Você saiu do chat.");
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Erro no cliente: " + e.getMessage());
        }
    }
}
