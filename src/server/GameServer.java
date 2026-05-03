package server;

import characters.Character;
import characters.player.*;
import game.Game;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Set;

public class GameServer {
    public static void main(String[] args) {
        int port = 8080;
        String string;
        ArrayList<Character> availableClass = new ArrayList<>();
        availableClass.add(new Warrior());
        availableClass.add(new Archer());
        availableClass.add(new Assassin());
        availableClass.add(new Mage());
        availableClass.add(new Tank());

        enum GameStatus {
                MAIN_MENU,
                CONFIRM_SELECTION,
                IN_GAME
        }

        // 1. Creo il ServerSocket (Mi metto in ascolto)
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server in ascolto sulla porta " + port + "...");

            // 2. accept() blocca il programma finché un Client non si collega
            Socket clientSocket = serverSocket.accept();
            System.out.println("Un giocatore si è connesso!");

            // 3. Preparo i "tubi" per parlare (out) e ascoltare (in)
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            //INIZIO A GESTIONE DEL MENU PRINCIPALE
            GameStatus currentState = GameStatus.MAIN_MENU;
            Game game = null; //ancora non creiamo la partita

            out.println("Benvenuto in ThePit!");
            out.println("Scegli la tua classe");

            for (int i=0; i<availableClass.size(); i++) {
               out.println( (i+1) + ". " + availableClass.get(i).getName());

            }
            out.println("{END:MENU}");



            String inputClient;
            Character temp = null; // per temporanea conferma della classe
            while ((inputClient = in.readLine()) != null) {
                if(currentState == GameStatus.MAIN_MENU) {
                    try {
                        Character chosenClass = availableClass.get(Integer.parseInt(inputClient)-1);
                        out.println("--- " + chosenClass.getName() + " ---");
                        chosenClass.getStats().forEach((stat, value) -> {
                            out.println(stat + ": " + value);
                        });
                        temp = chosenClass;
                        out.println("Digita 'conferma' per confermare questa classe o 'annulla' per annullare la scelta");
                        currentState = GameStatus.CONFIRM_SELECTION;
                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                        out.println("Scelta non valida");
                    }
                    out.println("{END:MENU}");

                } else if (currentState == GameStatus.CONFIRM_SELECTION) {
                    if(inputClient.equalsIgnoreCase("CONFERMA")){
                        game = new Game(temp);
                        currentState = GameStatus.IN_GAME;
                        out.println("Hai scelto " + temp.getName() + "! Che l'avventura abbia inzio");
                        out.println("{END:" + game.getCurrentState() + "}");
                    } else if (inputClient.equalsIgnoreCase("ANNULLA")) {
                        currentState = GameStatus.MAIN_MENU;
                        out.println("Scegli la tua classe");
                        out.println("{END:MENU}");
                    }else {
                        out.println("Comando non riconosciuto. Digita 'conferma' o 'annulla'.");
                        out.println("{END:MENU}");
                    }


                } else if (currentState == GameStatus.IN_GAME) {
                    // 2. PASSO IL COMANDO AL GIOCO
                    // Il server non sa COSA succede, chiede solo al gioco il risultato
                    String risultato = game.processCommand(inputClient).toString();

                    // 3. SPEDISCO IL RISULTATO AL CLIENT
                    out.println(risultato);
                    out.println("{END:" + game.getCurrentState() + "}");
                }

            }
        } catch (IOException e) {
            System.out.println("Errore del Server: " + e.getMessage());
        }
    }
}