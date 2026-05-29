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
        WELCOME_MENU,
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

            GameStatus currentState = GameStatus.WELCOME_MENU;
            Game game = null;
            int targetSaveSlot = 1;

            String slotsInfo = database.DatabaseManager.getSaveSlotsInfo();
            String topRuns = database.DatabaseManager.getTopRuns().replace("\n", "\\n");
            out.println("{WELCOME_INFO:" + slotsInfo + "|||" + topRuns + "}");

            String inputClient;
            Character temp = null;


            while ((inputClient = in.readLine()) != null) {

                inputClient = inputClient.trim();
                if (inputClient.isEmpty()) continue;

                if (currentState == GameStatus.WELCOME_MENU) {
                    if (inputClient.startsWith("NEW_GAME")) {
                        try {
                            targetSaveSlot = Integer.parseInt(inputClient.split(" ")[1]);
                        } catch (Exception e) {
                            targetSaveSlot = 1;
                        }
                        currentState = GameStatus.MAIN_MENU;
                        out.println("Scegli la tua classe:");
                        for (int i = 0; i < availableClass.size(); i++) {
                            out.println((i + 1) + ". " + availableClass.get(i).getName());
                        }
                        out.println("{END:MENU}");
                    } else if (inputClient.startsWith("RESET_SLOT")) {
                        try {
                            int slotId = Integer.parseInt(inputClient.split(" ")[1]);
                            database.DatabaseManager.deleteSaveState(slotId);
                            out.println("{WELCOME_INFO:" + database.DatabaseManager.getSaveSlotsInfo() + "|||" + database.DatabaseManager.getTopRuns().replace("\n", "\\n") + "}");
                        } catch (Exception e) {
                            out.println("Comando non valido.");
                            out.println("{WELCOME_INFO:" + database.DatabaseManager.getSaveSlotsInfo() + "|||" + database.DatabaseManager.getTopRuns().replace("\n", "\\n") + "}");
                        }
                    } else if (inputClient.startsWith("LOAD_GAME")) {
                        try {
                            int slotId = Integer.parseInt(inputClient.split(" ")[1]);
                            Character loadedPlayer = database.DatabaseManager.loadGameState(slotId);
                            if (loadedPlayer != null) {
                                game = new Game(loadedPlayer);
                                game.setSaveSlot(slotId);
                                currentState = GameStatus.IN_GAME;
                                out.println("{MSG:Partita caricata con successo! Bentornato, " + loadedPlayer.getName() + "!} " +
                                            "{CLASS_INFO:" + loadedPlayer.getClass().getSimpleName() + "} " +
                                            game.getStatsData() + " " + game.getEquippedData() +
                                            " {END:" + game.getCurrentState() + "}");
                            } else {
                                out.println("Slot vuoto o salvataggio corrotto.");
                                out.println("{WELCOME_INFO:" + database.DatabaseManager.getSaveSlotsInfo() + "|||" + database.DatabaseManager.getTopRuns().replace("\n", "\\n") + "}");
                            }
                        } catch (Exception e) {
                            out.println("Comando non valido.");
                            out.println("{WELCOME_INFO:" + database.DatabaseManager.getSaveSlotsInfo() + "|||" + database.DatabaseManager.getTopRuns().replace("\n", "\\n") + "}");
                        }
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
                        out.println("{END:CONFIRM_SELECTION}");
                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                        out.println("Scelta non valida");
                        out.println("{END:MENU}");
                    }

                } else if (currentState == GameStatus.CONFIRM_SELECTION) {
                    if (inputClient.equalsIgnoreCase("CONFERMA")) {
                        game = new Game(temp);
                        game.setSaveSlot(targetSaveSlot); 
                        database.DatabaseManager.saveGameState(targetSaveSlot, temp);
                        currentState = GameStatus.IN_GAME;
                        out.println("{MSG:Hai scelto " + temp.getName() + "! Che l'avventura abbia inizio} " +
                                    "{CLASS_INFO:" + temp.getClass().getSimpleName() + "} " +
                                    game.getStatsData() + " " + game.getEquippedData() +
                                    " {END:" + game.getCurrentState() + "}");
                    } else if (inputClient.equalsIgnoreCase("ANNULLA")) {
                        currentState = GameStatus.MAIN_MENU;
                        out.println("Scegli la tua classe");
                        out.println("{END:MENU}");
                    } else {
                        out.println("Comando non riconosciuto. Digita 'conferma' o 'annulla'.");
                        out.println("{END:CONFIRM_SELECTION}");
                    }

                } else if (currentState == GameStatus.IN_GAME) {
                    if (inputClient.equalsIgnoreCase("DISCONNECT")) {
                        if (game != null) {
                            game.saveGame();
                        }
                        currentState = GameStatus.WELCOME_MENU;
                        game = null;
                        out.println("{WELCOME_INFO:" + database.DatabaseManager.getSaveSlotsInfo() + "|||" + database.DatabaseManager.getTopRuns().replace("\n", "\\n") + "}");
                        continue;
                    }
                    String risultato = game.processCommand(inputClient).toString();
                    risultato = wrapPlainMessages(risultato);
                    risultato = risultato.replace("\r\n", "\\n").replace("\n", "\\n");
                    out.println(risultato + " {END:" + game.getCurrentState() + "}");
                }
            }

        } catch (Throwable e) {
            System.out.println("Errore non gestito nel ClientHandler: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Un giocatore si è disconnesso o c'è stato un errore di rete/gioco.");
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

    public static String wrapPlainMessages(String text) {
        StringBuilder result = new StringBuilder();
        StringBuilder plain = new StringBuilder();
        int braceCount = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                if (braceCount == 0) {
                    String plainStr = plain.toString().trim();
                    if (!plainStr.isEmpty()) {
                        result.append("{MSG:").append(plainStr).append("}");
                    }
                    plain.setLength(0);
                }
                braceCount++;
                result.append(c);
            } else if (c == '}') {
                braceCount--;
                result.append(c);
            } else {
                if (braceCount == 0) {
                    plain.append(c);
                } else {
                    result.append(c);
                }
            }
        }
        String plainStr = plain.toString().trim();
        if (!plainStr.isEmpty()) {
            result.append("{MSG:").append(plainStr).append("}");
        }
        return result.toString();
    }
}