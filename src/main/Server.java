package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Server extends Thread {

    ArrayList<Client> clients = new ArrayList<Client>();
    String nomClient = "";
     
    @Override
    public void run() {
        super.run();
        int numClient = 0;
        
        try {
            ServerSocket ss = new ServerSocket(1026);
            System.out.println("serveur en attente");

            while (true) {
                Socket s = ss.accept();
                numClient++;
                nomClient = JOptionPane.showInputDialog("Enter something:");
                Client client = new Client(s, numClient, nomClient);
                clients.add(client);
                sendClientList();
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendClientList() {
        StringBuilder clientList = new StringBuilder();
        clientList.append("Clients:");
        for (Client client : clients) {
            clientList
                    .append("(" + nomClient + ")")
                    .append(client.numClient)
                    .append("|")
                    .append(client.nomClient)
                    .append(",");
        }
        for (Client client : clients) {
            try {
                DataOutputStream out = new DataOutputStream(client.s.getOutputStream());
                out.writeUTF(clientList.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Client extends Thread {

        Socket s;
        int numClient;
        String nomClient;

        public Client(Socket s, int numClient, String nomClient) {
            this.s = s;
            this.numClient = numClient;
            this.nomClient = nomClient;
        }

        @Override
        public void run() {
            super.run();
            try {
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                while (true) {
                    String r = in.readUTF();
                    int numdesti = in.read();
                    sendMessage(r, numdesti);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message, int numd) {
            for (Client client : clients) {
                if (numd == client.numClient) {
                    DataOutputStream out;
                    try {
                        out = new DataOutputStream(client.s.getOutputStream());
                        out.writeUTF(message);
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
