import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

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
        try {
            socket = new Socket(endereco, porta); 
            labelStatus.setText("Conectado ao servidor.");

            // Streams para comunicação
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);

            saida.println(nomeUsuario);

            // Thread para ouvir mensagens do servidor
            new Thread(() -> {
                try {
                    String mensagem;
                    while ((mensagem = entrada.readLine()) != null) {
                        areaChat.append(mensagem + "\n");
                    }
                } catch (IOException e) {
                    labelStatus.setText("Erro na conexão: " + e.getMessage());
                }
            }).start();

            // Método para enviar mensagens ao servidor
            botaoEnviar.addActionListener(e -> {
                String mensagem = campoEntrada.getText();
                if (!mensagem.trim().isEmpty()) {
                    saida.println(mensagem);
                    campoEntrada.setText("");
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
}
