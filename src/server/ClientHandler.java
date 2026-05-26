package server;

import characters.Character;
import characters.player.*;
import game.Game;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ClientHandler implements Runnable {

    private Socket clientSocket; // Il tubo di rete di QUESTO specifico giocatore

    private enum GameStatus {
        SLOT_SELECTION,
        MAIN_MENU,
        CONFIRM_SELECTION,
        IN_GAME
    }


    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }


    @Override
    public void run() {

        ArrayList<Character> availableClass = new ArrayList<>();
        availableClass.add(new Warrior());
        availableClass.add(new Archer());
        availableClass.add(new Assassin());
        availableClass.add(new Mage());
        availableClass.add(new Tank());

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {

            GameStatus currentState = GameStatus.SLOT_SELECTION;
            Game game = null;
            int selectedSlot = -1;

            out.println("Benvenuto in ThePit!");
            out.println(database.DatabaseManager.getSaveSlotsInfo());
            out.println("Seleziona uno slot (1, 2, 3) per caricare o iniziare una nuova partita:");
            out.println("{END:MENU}");

            String inputClient;
            Character temp = null;


            while ((inputClient = in.readLine()) != null) {

                inputClient = inputClient.trim();

                if (currentState == GameStatus.SLOT_SELECTION) {
                    try {
                        int slot = Integer.parseInt(inputClient);
                        if (slot < 1 || slot > 3) throw new NumberFormatException();
                        
                        Character loadedPlayer = database.DatabaseManager.loadGameState(slot);
                        selectedSlot = slot;
                        
                        if (loadedPlayer != null) {
                            game = new Game(loadedPlayer);
                            game.setSaveSlot(selectedSlot);
                            currentState = GameStatus.IN_GAME;
                            out.println("Partita caricata con successo! Bentornato " + loadedPlayer.getName());
                            out.println("{END:" + game.getCurrentState() + "}");
                        } else {
                            out.println("Slot vuoto. Creazione nuovo personaggio...");
                            out.println("Scegli la tua classe:");
                            for (int i = 0; i < availableClass.size(); i++) {
                                out.println((i + 1) + ". " + availableClass.get(i).getName());
                            }
                            currentState = GameStatus.MAIN_MENU;
                            out.println("{END:MENU}");
                        }
                    } catch (NumberFormatException e) {
                        out.println("Scelta non valida. Scegli 1, 2 o 3.");
                        out.println("{END:MENU}");
                    }
                } else if (currentState == GameStatus.MAIN_MENU) {
                    try {
                        Character chosenClass = availableClass.get(Integer.parseInt(inputClient) - 1);
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
                    if (inputClient.equalsIgnoreCase("CONFERMA")) {
                        game = new Game(temp);
                        game.setSaveSlot(selectedSlot);
                        currentState = GameStatus.IN_GAME;
                        out.println("Hai scelto " + temp.getName() + "! Che l'avventura abbia inzio");
                        out.println("{END:" + game.getCurrentState() + "}");
                    } else if (inputClient.equalsIgnoreCase("ANNULLA")) {
                        currentState = GameStatus.MAIN_MENU;
                        out.println("Scegli la tua classe");
                        out.println("{END:MENU}");
                    } else {
                        out.println("Comando non riconosciuto. Digita 'conferma' o 'annulla'.");
                        out.println("{END:MENU}");
                    }

                } else if (currentState == GameStatus.IN_GAME) {
                    String viewState = game.getCurrentState();
                    String risultato = game.processCommand(inputClient).toString();
                    String[] lines = risultato.split("\n");
                    for (String line : lines) {
                        out.println(line);
                        // Pokémon-style text delay for descriptive battle lines
                        if ((viewState.equals("ROOM") || game.getCurrentState().equals("ROOM")) && !line.trim().isEmpty() 
                            && !line.startsWith("-") && !line.matches("^\\[\\d+\\].*") 
                            && !line.contains("PA Rimanenti") && !line.contains("HP |") 
                            && !line.contains("Comandi:")) {
                            
                            try { Thread.sleep(600); } catch (InterruptedException e) {}
                        }
                    }
                    out.println("{END:" + game.getCurrentState() + "}");
                }
            }

        } catch (IOException e) {
            // Se entriamo in questo catch, significa che il client si è disconnesso brutalmente o c'è un errore di rete
            System.out.println("Un giocatore si è disconnesso o c'è stato un errore di rete.");
        } finally {
            // Chiudo il socket in modo pulito alla fine
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}