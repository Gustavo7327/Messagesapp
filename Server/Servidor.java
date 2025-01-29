import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {
    // Mapa para armazenar nomes de usuários e seus respectivos sockets
    private static ConcurrentHashMap<String, PrintWriter> usuarios = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int porta = 12345; // Porta do servidor

        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor rodando na porta " + porta + "...");

            while (true) {
                Socket socket = servidor.accept(); // Aceita uma nova conexão
                System.out.println("Novo cliente conectado: " + socket.getInetAddress());

                // Cria uma nova thread para tratar o cliente
                new Thread(new ManipuladorCliente(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    // Classe interna para manipular os clientes
    static class ManipuladorCliente implements Runnable {
        private Socket socket;
        private String nomeUsuario;

        public ManipuladorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter saida = new PrintWriter(socket.getOutputStream(), true)
            ) {

                // Solicita o nome do usuário
                saida.println("Digite seu nome:");
                nomeUsuario = entrada.readLine();

                if (nomeUsuario == null || nomeUsuario.isEmpty()) {
                    saida.println("Nome inválido. Conexão encerrada.");
                    socket.close();
                    return;
                }

                // Adiciona o usuário à lista
                synchronized (usuarios) {
                    if (usuarios.containsKey(nomeUsuario)) {
                        saida.println("Nome já está em uso. Conexão encerrada.");
                        socket.close();
                        return;
                    }
                    usuarios.put(nomeUsuario, saida);
                    System.out.println(nomeUsuario + " entrou no chat.");
                    enviarMensagemGlobal(nomeUsuario + " entrou no chat.");
                }

                // Loop para troca de mensagens
                String mensagem;
                while ((mensagem = entrada.readLine()) != null) {
                    if (mensagem.equalsIgnoreCase("sair")) {
                        break;
                    }
                    enviarMensagemGlobal(nomeUsuario + ": " + mensagem);
                }

            } catch (IOException e) {
                System.out.println("Erro ao comunicar com o cliente: " + e.getMessage());
            } finally {
                // Remove o usuário ao desconectar
                if (nomeUsuario != null) {
                    usuarios.remove(nomeUsuario);
                    System.out.println(nomeUsuario + " saiu do chat.");
                    enviarMensagemGlobal(nomeUsuario + " saiu do chat.");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Erro ao fechar socket: " + e.getMessage());
                }
            }
        }

        // Envia uma mensagem para todos os usuários conectados
        private void enviarMensagemGlobal(String mensagem) {
            synchronized (usuarios) {
                for (PrintWriter saidaUsuario : usuarios.values()) {
                    saidaUsuario.println(mensagem);
                }
            }
        }
    }
}
