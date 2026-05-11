package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    public static void main(String[] args) {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server in ascolto sulla porta " + port + "...");


            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Un nuovo giocatore si è connesso: " + clientSocket.getInetAddress());


                ClientHandler clientHandler = new ClientHandler(clientSocket);

                // nuova istanza per ogni player
                Thread threadPlayer = new Thread(clientHandler);
                threadPlayer.start();
            }

        } catch (IOException e) {
            System.out.println("Errore irreversibile del Server: " + e.getMessage());
        }
    }
}