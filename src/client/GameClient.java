package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameClient {
    public static void main(String[] args) {
        String ipAddress = "127.0.0.1"; // Chiama se stesso (Localhost)
        int port = 8080;

        // 1. Chiamo il Server (Socket) e preparo lo Scanner per la tastiera
        try (Socket socket = new Socket(ipAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connesso al server!");

            // 2. Leggo e stampo subito il messaggio di benvenuto del Server
            System.out.println(in.readLine());

            String userInput;
            // 3. Loop del gioco (lato giocatore)
            while (true) {
                System.out.print("Scegli la tua mossa: ");
                userInput = scanner.nextLine(); // Leggo la tastiera

                out.println(userInput); // SPEDISCO IL COMANDO AL SERVER

                // Se ho scritto ESCI, chiudo il client
                if (userInput.equalsIgnoreCase("ESCI")) {
                    System.out.println(in.readLine()); // Leggo il saluto finale
                    break;
                }

                // 4. Aspetto che il server calcoli il risultato e lo stampo a schermo!
                String rigaDalServer;
                // Continua a leggere finché la riga non è uguale a "[END]"
                while ((rigaDalServer = in.readLine()) != null) {
                    if (rigaDalServer.equals("{END}")) {
                        break; // Il server ha finito, esco dal mini-ciclo di lettura!
                    }
                    System.out.println(rigaDalServer); // Stampa la riga normalmente
                }
            }
        } catch (IOException e) {
            System.out.println("Impossibile connettersi al Server: " + e.getMessage());
        }
    }
}