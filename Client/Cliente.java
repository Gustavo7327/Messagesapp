import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Cliente {
    public static void main(String[] args) {
        String endereco = "localhost"; 
        int porta = 12345;

        // Configuração da interface gráfica
        JFrame janela = new JFrame("Chat");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setPreferredSize(new Dimension(400, 300));
        janela.setLocationRelativeTo(null);
        janela.setLayout(new BorderLayout(10, 5));
        janela.setResizable(false);

        JLabel labelStatus = new JLabel("Aguardando conexão...");
        labelStatus.setSize(300, 20);
        labelStatus.setHorizontalAlignment(SwingConstants.CENTER);
        labelStatus.setOpaque(true);
        labelStatus.setBackground(new Color(230, 230, 230));
        janela.add(labelStatus, BorderLayout.NORTH);

        JTextArea areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setFont(new Font("Verdana", 1, 12));
        areaChat.setSize(400, 250);
        areaChat.setAutoscrolls(true);
        janela.add(areaChat, BorderLayout.CENTER);

        JPanel panelEntrada = new JPanel(new FlowLayout());
        JTextField campoEntrada = new JTextField(20);
        panelEntrada.add(campoEntrada);
        JButton botaoEnviar = new JButton("Enviar");
        panelEntrada.add(botaoEnviar);
        janela.add(panelEntrada, BorderLayout.SOUTH);

        janela.pack();
        janela.setVisible(true);

        // Solicitar nome
        String nomeUsuario = JOptionPane.showInputDialog(janela, "Digite seu nome:");

        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            JOptionPane.showMessageDialog(janela, "Nome inválido. O programa será encerrado.");
            System.exit(0);
        }

        Socket socket = null;
        PrintWriter saida = null;

        try {
            socket = new Socket(endereco, porta); 
            labelStatus.setText("Conectado ao servidor.");

            // Streams para comunicação
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            saida = new PrintWriter(socket.getOutputStream(), true);

            saida.println(nomeUsuario);

            // Thread para ouvir mensagens do servidor
            BufferedReader reader = entrada;
            new Thread(() -> {
                try {
                    String mensagem;
                    while ((mensagem = reader.readLine()) != null) {
                        if (!mensagem.equals("Digite seu nome:")) {
                            areaChat.append(mensagem + "\n");
                        }  
                    }
                } catch (IOException e) {
                    labelStatus.setText("Erro na conexão: " + e.getMessage());
                }
            }).start();

            // Thread para monitorar a conexão periodicamente
            Socket s = socket;
            JLabel status = labelStatus;
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(3000); // Verifica a cada 3 segundos
                        if (!verificarConexao(s)) {
                            status.setText("Conexão perdida!");
                            break;
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }).start();

            // Método para enviar mensagens ao servidor
            PrintWriter pw = saida;
            botaoEnviar.addActionListener(e -> enviarMensagem(pw, campoEntrada, labelStatus, s));

            campoEntrada.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        enviarMensagem(pw, campoEntrada, labelStatus, s);
                    }
                }
            });

        } catch (IOException e) {
            labelStatus.setText("Erro na conexão: " + e.getMessage());
            if (socket != null) {
                try {
                    socket.close(); 
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Método para enviar mensagens ao servidor
    private static void enviarMensagem(PrintWriter saida, JTextField campoEntrada, JLabel labelStatus, Socket socket) {
        if (verificarConexao(socket)) {
            String mensagem = campoEntrada.getText();
            if (!mensagem.trim().isEmpty()) {
                saida.println(mensagem);
                campoEntrada.setText("");
            }
        } else {
            labelStatus.setText("Conexão encerrada.");
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                labelStatus.setText("Erro ao fechar o socket: " + ex.getMessage());
            }
        }
    }

    // Método para verificar se o socket ainda está ativo
    private static boolean verificarConexao(Socket socket) {
        try {
            if (socket == null || socket.isClosed()) {
                return false;
            }
            socket.sendUrgentData(0xFF); // Envia um byte fora de banda para testar a conexão
            return true;
        } catch (IOException e) {
            return false; // Se houver erro, o socket está desconectado
        }
    }
}
