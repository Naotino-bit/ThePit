package server;

import game.Game;

import java.io.*;
import java.net.*;

public class GameServer {
    public static void main(String[] args) {
        int port = 8080;
        Game game = new Game();

        // 1. Creo il ServerSocket (Mi metto in ascolto)
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server in ascolto sulla porta " + port + "...");

            // 2. accept() blocca il programma finché un Client non si collega
            Socket clientSocket = serverSocket.accept();
            System.out.println("Un giocatore si è connesso!");

            // 3. Preparo i "tubi" per parlare (out) e ascoltare (in)
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // 4. Mando un messaggio di benvenuto al Client
            out.println("Benvenuto nel server!");

            String inputClient;
            while ((inputClient = in.readLine()) != null) {
                // 2. PASSO IL COMANDO AL GIOCO
                // Il server non sa COSA succede, chiede solo al gioco il risultato
                String risultato = game.processCommand(inputClient).toString();

                // 3. SPEDISCO IL RISULTATO AL CLIENT
                out.println(risultato);
                out.println("{END}");
            }
        } catch (IOException e) {
            System.out.println("Errore del Server: " + e.getMessage());
        }
    }
}