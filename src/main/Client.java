package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import raven.chat.component.ChatBox;
import raven.chat.model.ModelMessage;
import raven.chat.swing.ChatEvent;

public class Client extends javax.swing.JFrame {

    static DataInputStream in;
    static DataOutputStream out;
    static String[] clients;
    static ChatClient selectedClient;
    static ArrayList<ChatClient> chatClients = new ArrayList<>();

    String currectClientName = "";

    public Client() {
        initComponents();

        ImageIcon transparentIcon = new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        setIconImage(transparentIcon.getImage());

        try {
            Socket socketClient = new Socket("localhost", 1026);
            in = new DataInputStream(socketClient.getInputStream());
            out = new DataOutputStream(socketClient.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        chatArea1.addChatEvent(new ChatEvent() {
            @Override
            public void mousePressedSendButton(ActionEvent evt) {
                Thread envoyer = new Thread(new Runnable() {
                    public void run() {
                        String message = chatArea1.getText();
                        int numdest = selectedClient.numClient;
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy, hh:mmaa");
                        Icon icon = new ImageIcon();
                        String date = df.format(new Date());

                        chatArea1.addChatBox(new ModelMessage(icon, "Moi", date, message), ChatBox.BoxType.LEFT);
                        try {
                            out.writeUTF(message);
                            out.write(numdest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                envoyer.start();
            }

            public void mousePressedFileButton(ActionEvent evt) {
            }

            public void keyTyped(KeyEvent evt) {
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background1 = new raven.chat.swing.Background();
        chatArea1 = new raven.chat.component.ChatArea();
        ListeDesClients = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout background1Layout = new javax.swing.GroupLayout(background1);
        background1.setLayout(background1Layout);
        background1Layout.setHorizontalGroup(
            background1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chatArea1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
            .addGroup(background1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(ListeDesClients, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        background1Layout.setVerticalGroup(
            background1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, background1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ListeDesClients, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chatArea1, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Client client = new Client();
                Thread recevoir = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            String r;
                            try {
                                r = in.readUTF();
                                if (r.contains("Clients:")) {
                                    int startIndex = r.indexOf('(') + 1;
                                    int endIndex = r.indexOf(')');
                                    if("".equals(client.currectClientName))
                                        client.currectClientName = r.substring(startIndex, endIndex);

                                    client.chatArea1.setTitle("Votre nom: " + client.currectClientName + " chat avec: Unknown");

                                    client.ListeDesClients.removeAll();
                                    r = r.replace("(" + client.currectClientName + ")", "");
                                    r = r.replace("Clients:", "");
                                    clients = r.split(",");
                                    for (String cl : clients) {
                                        String[] ccl = cl.split("\\|");
                                        ChatClient chatClient = new ChatClient(Integer.parseInt(ccl[0]), ccl[1]);
                                        chatClients.add(chatClient);
                                        client.ListeDesClients.addItem(chatClient);
                                    }
                                } else {
                                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy, hh:mmaa");
                                    Icon icon = new ImageIcon();
                                    String date = df.format(new Date());
                                    client.chatArea1.addChatBox(new ModelMessage(icon, selectedClient.nomClient, date, r), ChatBox.BoxType.RIGHT);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                client.ListeDesClients.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        selectedClient = (ChatClient) client.ListeDesClients.getSelectedItem();
                        client.chatArea1.setTitle("Votre nom: " + client.currectClientName + " chat avec: "+ selectedClient.nomClient);
                    }
                });

                recevoir.start();
                client.setVisible(true);
                client.ListeDesClients.removeAllItems();
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<ChatClient> ListeDesClients;
    private raven.chat.swing.Background background1;
    private raven.chat.component.ChatArea chatArea1;
    // End of variables declaration//GEN-END:variables

}

class ChatClient {

    int numClient;
    String nomClient;

    public ChatClient(int numClient, String nomClient) {
        this.numClient = numClient;
        this.nomClient = nomClient;
    }

    public String toString() {
        return nomClient;
    }
}
