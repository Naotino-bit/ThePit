package client;

import game.Game;

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
            //System.out.println(in.readLine()); // Possibile causa di messaggi letti a meta

            String userInput;
            String prefix = "Menu ";
            // 3. Loop del gioco (lato giocatore)
            while (true) {

                // FASE 1: ASCOLTO IL SERVER (Leggo finché non dice {END})
                String rigaDalServer;
                while ((rigaDalServer = in.readLine()) != null) {
                    if (rigaDalServer.startsWith("{END")) {
                        //estrae lo stato es {END:Inventario} estrae Inventario
                        prefix = rigaDalServer.split(":")[1].replace("}","");

                        switch (prefix) {
                            case "MENU":
                            case "CONFIRM_SELECTION":
                                prefix = "Accampamento";
                                break;
                            case "IDLE":
                                prefix = "Esplorazione";
                                break;
                            case "BATTLE":
                                prefix = "Combattimento";
                                break;
                            case "INVENTORY_MAIN":
                            case "INVENTORY_ACTION":
                                prefix = "Zaino";
                                break;
                            case "INVENTORY_OVERFLOW":
                                prefix = "Zaino pieno";
                                break;
                            case "PLAYER_INFO":
                                prefix = "Personaggio";
                                break;
                            default:
                                break;
                        }
                        break;
                    }
                    System.out.println(rigaDalServer);
                }

                // FASE 2: TOCCA A ME PARLARE
                System.out.print(prefix + " > ");
                userInput = scanner.nextLine().trim();

                // SPEDISCO IL COMANDO AL SERVER
                out.println(userInput);

                // FASE 3: CONTROLLO SE DEVO USCIRE
                if (userInput.equalsIgnoreCase("DISCONNETTI")) {
                    System.out.println("Disconnessione in corso...");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Impossibile connettersi al Server: " + e.getMessage());
        }
    }
}