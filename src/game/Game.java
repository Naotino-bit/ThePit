package game;

import characters.Character;
import characters.enemies.Enemies;
import characters.enemies.Zombie;
import characters.enemies.Skeleton;
import characters.enemies.Goblin;
import characters.enemies.Witch;
import characters.enemies.Orc;
import items.Items;
import items.armors.Armors;
import items.armors.Helmet;
import items.usables.HealthPotion;
import items.usables.Usables;
import items.weapons.Claymore;
import items.weapons.Sword;
import items.weapons.Weapons;
import rooms.Room;
import rooms.BattleRoom;
import rooms.FountainRoom;
import rooms.ShopRoom;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    private Character player;
    private Room currentRoom;
    private int lastBossDefeatedLevel = 0;

    public enum playerView {
        IDLE,
        ROOM, // non abbiamo più battle ma abbiamo room
        INVENTORY_MAIN,
        INVENTORY_ACTION,
        INVENTORY_OVERFLOW,
        PLAYER_INFO
    }

    public playerView currentView = playerView.IDLE;
    private Items selectedItem = null;
    private ArrayList<Items> pendingLoot = new ArrayList<>();
    private int saveSlot = -1;

    public void setSaveSlot(int slot) {
        this.saveSlot = slot;
    }

    public Game(Character playerClass) {
        this.player = playerClass;
        this.currentRoom = null;

        XmlHandler.loadAllItems();
    }

    public String getEnemies(ArrayList<Enemies> enemies) {
        String enemyInfo = "E' apparso un nemico: \n"; /* l'avevi chiamato string, l'ho chiamato enemyInfo */
        if (enemies.size() > 1) {
            enemyInfo = "Sono apparsi dei nemici: \n";
        }
        enemyInfo += player.getName() + ": " + player.getHp() + "/" + player.getHpMax() + " hp\n";
        for (int i = 0; i < enemies.size(); i++) {
            enemyInfo += "[" + (i + 1) + "] - " + enemies.get(i).getName() + " " + enemies.get(i).getHp() + "/"
                    + enemies.get(i).getHpMax() + " hp\n";
        }
        return enemyInfo;
    }

    public Object processCommand(String command) {

        // se l'utente è in freeroam
        if (currentView == playerView.IDLE) {
            if (command.equalsIgnoreCase("ZAINO") || command.toLowerCase().startsWith("za")) {
                currentView = playerView.INVENTORY_MAIN;
                return getInventoryString() + "Selezionare un oggetto oppure 'ESCI'";
            }

            if (command.equalsIgnoreCase("AVANZA") || command.toLowerCase().startsWith("ava")) {
                generateNextRoom();
                currentView = playerView.ROOM;
                return currentRoom.enterRoom(player);
            }

            // DEBUG ONLY
            if (command.equalsIgnoreCase("spawn") || command.toLowerCase().startsWith("sp")) {
                ArrayList<Enemies> enemies = new ArrayList<Enemies>();
                enemies.add(spawnEnemy(new Zombie()));
                enemies.add(spawnEnemy(new Zombie()));

                this.currentRoom = new BattleRoom(player, enemies);
                currentView = playerView.ROOM;
                return this.currentRoom.enterRoom(player);
            }

            // mostra quello che hai equipaggiato attualmente
            if (command.equalsIgnoreCase("PERSONAGGIO") || command.toLowerCase().startsWith("per")) {
                currentView = playerView.PLAYER_INFO;
                return "Mostra le statistiche con 'Statistiche'\nL'equipaggiamento attuale con 'Equipaggiamento'\nDisequipaggia qualcosa con 'Disequipaggia [SLOT]'";
            }

            if (command.equalsIgnoreCase("CLASSIFICA") || command.toLowerCase().startsWith("cla")) {
                return database.DatabaseManager.getTopRuns();
            }

            return "Sei nel corridoio. Comandi disponibili: 'AVANZA', 'ZAINO', 'PERSONAGGIO', 'CLASSIFICA'";
        }

        // inventario, scelta oggetto
        else if (currentView == playerView.INVENTORY_MAIN) {
            if (command.equalsIgnoreCase("ESCI") || command.toLowerCase().startsWith("esc")) {
                // If we have an active room that isn't cleared, we go back to ROOM state
                if (currentRoom != null && !currentRoom.isCleared()) {
                    currentView = playerView.ROOM;
                    return "Zaino chiuso. Sei ancora nella stanza.";
                } else {
                    currentView = playerView.IDLE;
                    return "Zaino chiuso.";
                }
            }
            try {
                int index = Integer.parseInt(command) - 1; // -1 perché l'utente digita 1 per l'indice 0
                ArrayList<Items> inventory = (ArrayList<Items>) player.getInventory();

                selectedItem = inventory.get(index); // Salvo l'oggetto scelto!
                currentView = playerView.INVENTORY_ACTION; // Cambio stato!

                if (selectedItem instanceof Usables) {
                    return "Hai selezionato: " + selectedItem.getDetails() + "\nVuoi USARE, BUTTARE o ANNULLARE?";
                } else {
                    String slot = selectedItem.getEquippedSlot().getFirst();
                    if (player.getEquippedItemsRaw().get(slot) != null) {
                        return "Hai selezionato: " + selectedItem.getDetails() + "\n[!] Attualmente hai equipaggiato: "
                                + player.getEquippedItemsRaw().get(slot).getDetails()
                                + "\nVuoi EQUIPAGGIARE, BUTTARE o ANNULLARE?";
                    }
                    return "Hai selezionato: " + selectedItem.getDetails()
                            + "\nVuoi EQUIPAGGIARE, BUTTARE o ANNULLARE?";
                }

            } catch (Exception e) {
                return "Inserisci un numero valido o scrivi ESCI.";
            }
        }

        // azione sul'oggetto selezionato
        else if (currentView == playerView.INVENTORY_ACTION) {
            if (command.equalsIgnoreCase("EQUIPAGGIA") || command.toLowerCase().startsWith("equ")) {
                if (selectedItem instanceof Usables) {
                    return "Non puoi equipaggiare";
                }
                String equipResult = player.equip(selectedItem);
                currentView = playerView.INVENTORY_MAIN;
                return equipResult + getInventoryString();
            } else if (command.equalsIgnoreCase("USA")) {
                if (!(selectedItem instanceof Usables)) {
                    return "Non è un consumabile";
                }
                if (selectedItem instanceof items.usables.Throwables) {
                    return "Puoi usare questo oggetto solo durante una battaglia!";
                }
                String useResult = ((Usables) selectedItem).use(player, player);
                player.removeFromInventory(selectedItem);
                currentView = playerView.INVENTORY_MAIN;
                return useResult + "\n" + getInventoryString();

            } else if (command.equalsIgnoreCase("BUTTA") || command.toLowerCase().startsWith("bu")) {
                player.removeFromInventory(selectedItem);
                currentView = playerView.INVENTORY_MAIN;
                return "Hai buttato " + selectedItem.getName() + getInventoryString();
            } else if (command.equalsIgnoreCase("ANNULLA") || command.toLowerCase().startsWith("ann")) {
                selectedItem = null;
                currentView = playerView.INVENTORY_MAIN;
                return "Azione annullata" + getInventoryString();
            }
            return "Comando non valido. 'EQUIPAGGIA', 'BUTTA', 'ANNULLA' ";
        }

        // vista per gestione oggetti overflow
        else if (currentView == playerView.INVENTORY_OVERFLOW) {
            Items item = pendingLoot.getFirst();
            if (command.equalsIgnoreCase("LASCIA") || command.toLowerCase().startsWith("la")) {
                pendingLoot.removeFirst();
                if (pendingLoot.isEmpty()) {
                    currentView = playerView.IDLE;
                    if (this.saveSlot > 0) database.DatabaseManager.saveGameState(this.saveSlot, this.player);
                    return "Hai buttato l'oggetto. Tornato in esplorazione";
                } else {
                    return "Hai abbandonato l'oggetto. C'è ancora altro a terra:\nVuoi prendere ["
                            + pendingLoot.getFirst().getName() + " " + pendingLoot.getFirst().getDetails()
                            + "]?\nScrivi un NUMERO per sostituirlo, o LASCIA.";
                }
            }

            try {
                int indexToDrop = Integer.parseInt(command) - 1;
                ArrayList<Items> inventory = player.getInventory();

                Items droppedItem = inventory.get(indexToDrop);

                inventory.remove(indexToDrop);
                inventory.add(item);
                pendingLoot.removeFirst();

                String dropMessage = "Hai gettato " + droppedItem.getName() + " e raccolto " + item.getName() + "\n"; // renamed
                                                                                                                      // msg
                                                                                                                      // to
                                                                                                                      // dropMessage

                // controllo di nuovo se c'è altra roba in coda
                if (pendingLoot.isEmpty()) {
                    currentView = playerView.IDLE;
                    if (this.saveSlot > 0) database.DatabaseManager.saveGameState(this.saveSlot, this.player);
                    return dropMessage + "Non c'è altro a terra. Tornato in esplorazione.";
                } else {
                    return dropMessage + "C'è ancora altro a terra:\nVuoi prendere " + pendingLoot.getFirst().getName()
                            + " " + pendingLoot.getFirst().getDetails()
                            + "?\nScrivi un NUMERO per sostituirlo, o LASCIA.";
                }

            } catch (Exception e) {
                return "Comando non valido.\nA terra c'è: " + item.getName() + " " + item.getDetails()
                        + "\nScrivi un NUMERO per sostituirlo o LASCIA.";
            }
        }

        // vista personaggio (stats e equippedItems)
        else if (currentView == playerView.PLAYER_INFO) {
            if (command.equalsIgnoreCase("ESCI") || command.toLowerCase().startsWith("esc")) {
                // If we have an active room that isn't cleared, we go back to ROOM state
                if (currentRoom != null && !currentRoom.isCleared()) {
                    currentView = playerView.ROOM;
                    return "Scheda personaggio chiusa. Sei ancora nella stanza.";
                } else {
                    currentView = playerView.IDLE;
                    return "Scheda personaggio chiusa.";
                }
            } else if (command.equalsIgnoreCase("Statistiche") || command.toLowerCase().startsWith("sta")) {
                final String[] statsInfo = { "" }; // renamed temp to statsInfo
                statsInfo[0] += "Livello: " + player.getLevel() + "\n";
                player.getStats().forEach((stat, value) -> {
                    statsInfo[0] += stat + ": " + value + "\n";
                });

                return statsInfo[0];
            } else if (command.equalsIgnoreCase("Equipaggiamento") || command.toLowerCase().startsWith("equ")) {
                return player.getEquippedItems();
            } else if (command.toLowerCase().startsWith("disequipaggia") || command.toLowerCase().startsWith("dis")) {
                try {
                    command = command.split(" ")[1];
                    // così che l'input viene a prescindere reso valido con la prima lettera
                    // maiuscola e il resto minuscolo
                    String deEquipResult = player.deEquip(command.substring(0, 1).toUpperCase() + command.substring(1).toLowerCase());
                    return deEquipResult;
                } catch (Exception e) {
                    return "Errore durante il comando. Uso Disequipaggia [SLOT] Es: Disequipaggia torso";
                }

            } else {
                return "Comando non valido. 'Equipaggiamento', 'Statistiche', 'Disequipaggia', 'Esci' ";
            }

        }

        // nella stanza attiva (battaglia, negozio, fontana)
        if (currentView == playerView.ROOM) {
            if (!(currentRoom instanceof BattleRoom)) {
                if (command.equalsIgnoreCase("ZAINO") || command.toLowerCase().startsWith("za")) {
                    currentView = playerView.INVENTORY_MAIN;
                    return getInventoryString() + "Selezionare un oggetto oppure 'ESCI'";
                }
                if (command.equalsIgnoreCase("PERSONAGGIO") || command.toLowerCase().startsWith("per")) {
                    currentView = playerView.PLAYER_INFO;
                    return "Mostra le statistiche con 'Statistiche'\nL'equipaggiamento attuale con 'Equipaggiamento'\nDisequipaggia qualcosa con 'Disequipaggia [SLOT]'";
                }
            }

            // Pass the command to the active room logic
            String result = currentRoom.processCommand(command, player); // renamed risultato to result

            if (result.contains("GAME OVER")) {
                if (this.saveSlot > 0) {
                    database.DatabaseManager.deleteSaveState(this.saveSlot);
                }
            }

            // Se la stanza è stata superata/conclusa
            if (currentRoom.isCleared()) {
                pendingLoot = currentRoom.getPendingLoot();
                if (!pendingLoot.isEmpty()) {
                    currentView = playerView.INVENTORY_OVERFLOW;
                    result += "\n[!] Zaino pieno! [!] \nCi sono degli oggetti che puoi prendere.\n"
                            + getInventoryString() + "\nScrivi un NUMERO per sostituirlo o LASCIA.";
                    result += "\nA terra c'è: " + pendingLoot.getFirst().getName() + " "
                            + pendingLoot.getFirst().getDetails();
                } else {
                    currentView = playerView.IDLE;
                    if (this.saveSlot > 0) database.DatabaseManager.saveGameState(this.saveSlot, this.player);
                }
                currentRoom = null;
            }
            return result;
        }

        return "Comando non riconosciuto.";
    }

    private void generateNextRoom() {
        Random rand = new Random();

        // Controllo Boss (Ogni 10 livelli)
        if (player.getLevel() >= 10 && player.getLevel() % 10 == 0 && player.getLevel() > lastBossDefeatedLevel) {
            lastBossDefeatedLevel = player.getLevel();
            ArrayList<Enemies> enemies = new ArrayList<>();
            Enemies boss = getRandomEnemy(rand);
            boss = spawnEnemy(boss);
            boss.applyBossBuff();
            enemies.add(boss);
            currentRoom = new BattleRoom(player, enemies);
            return; // Se è la stanza del boss, non facciamo roll per shop o fontana
        }

        int roll = rand.nextInt(100);

        // 70% Battle, 20% Shop, 10% Fountain
        if (roll < 70) {
            ArrayList<Enemies> enemies = new ArrayList<>();
            // Spawn 1 to 3 enemies based on random chance
            int numEnemies = rand.nextInt(3) + 1;
            for (int i = 0; i < numEnemies; i++) {
                enemies.add(spawnEnemy(getRandomEnemy(rand)));
            }
            currentRoom = new BattleRoom(player, enemies);
        } else if (roll < 90) {
            currentRoom = new ShopRoom();
        } else {
            currentRoom = new FountainRoom();
        }
    }

    private String getInventoryString() {
        String inventoryStr = "\n----- Zaino -----\n"; // renamed temp to inventoryStr
        ArrayList<Items> inventory = player.getInventory();
        if (inventory.isEmpty()) {
            return inventoryStr + "Zaino vuoto\n";
        }
        for (int i = 0; i < inventory.size(); i++) {
            inventoryStr += "[" + (i + 1) + "] " + inventory.get(i).getName() + "\n";
        }
        return inventoryStr;
    }

    public String getCurrentState() {
        return currentView.name();
    }

    private Enemies spawnEnemy(Enemies enemy) {
        enemy.setLevelAndScale(player.getLevel());
        return enemy;
    }

    private Enemies getRandomEnemy(Random rand) {
        int r = rand.nextInt(5);
        switch (r) {
            case 0: return new Zombie();
            case 1: return new Skeleton();
            case 2: return new Goblin();
            case 3: return new Orc();
            case 4: return new Witch();
            default: return new Zombie();
        }
    }
}
