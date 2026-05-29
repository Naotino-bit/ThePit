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

    private String getOverflowData() {
        if (pendingLoot.isEmpty()) return "";
        Items item = pendingLoot.getFirst();
        String type = (item instanceof Usables) ? "USABLE" : "EQUIP";
        String slot = (item.getEquippedSlot() != null && !item.getEquippedSlot().isEmpty()) ? item.getEquippedSlot().getFirst() : "NONE";
        return "{OVERFLOW_INFO:" + item.getName() + "," + type + "," + item.getDetails() + "," + slot + "}";
    }

    private String getInventoryData() {
        StringBuilder data = new StringBuilder("{INVENTORY_INFO:");
        java.util.ArrayList<Items> inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            Items item = inventory.get(i);
            String type = (item instanceof Usables) ? "USABLE" : "EQUIP";
            String slot = (item.getEquippedSlot() != null && !item.getEquippedSlot().isEmpty()) ? item.getEquippedSlot().getFirst() : "NONE";
            data.append(item.getName()).append(",").append(type).append(",").append(item.getDetails()).append(",").append(slot).append(",").append(item.getEconomicValue());
            if (i < inventory.size() - 1) data.append(";");
        }
        data.append("}");
        return data.toString();
    }

    public String getEquippedData() {
        StringBuilder data = new StringBuilder("{EQUIPPED_INFO:");
        java.util.Map<String, Items> equipped = player.getEquippedItemsRaw();
        boolean first = true;
        for (java.util.Map.Entry<String, Items> entry : equipped.entrySet()) {
            if (entry.getValue() != null) {
                if (!first) data.append(";");
                data.append(entry.getKey()).append(",").append(entry.getValue().getName()).append(",").append(entry.getValue().getDetails());
                first = false;
            }
        }
        data.append("}");
        return data.toString();
    }

    private String getShopData() {
        if (currentRoom instanceof ShopRoom) {
            ShopRoom shop = (ShopRoom) currentRoom;
            StringBuilder data = new StringBuilder("{SHOP_INFO:");
            ArrayList<Items> stock = shop.getStock();
            for (int i = 0; i < stock.size(); i++) {
                Items item = stock.get(i);
                int price = item.getEconomicValue() * 2;
                String type = (item instanceof Usables) ? "USABLE" : "EQUIP";
                String slot = (item.getEquippedSlot() != null && !item.getEquippedSlot().isEmpty()) ? item.getEquippedSlot().getFirst() : "NONE";
                data.append(item.getName()).append(",")
                    .append(price).append(",")
                    .append(item.getDetails()).append(",")
                    .append(type).append(",")
                    .append(slot);
                if (i < stock.size() - 1) data.append(";");
            }
            data.append("}");
            return data.toString();
        }
        return "";
    }

    public String getStatsData() {
        StringBuilder data = new StringBuilder("{BATTLE_INFO:");
        data.append(player.getLevel()).append(",")
            .append(player.getHp()).append(",")
            .append(player.getHpMax())
            .append(",").append(player.getStats().get("Strength")).append(",").append(player.getStats().get("Agility"))
            .append(",").append(player.getStats().get("Intelligence")).append(",").append(player.getStats().get("Precision"))
            .append(",").append(player.getCurrentMana()).append(",").append(player.getManaMax())
            .append(",").append(player.getStats().getOrDefault("Defense", 0))
            .append(",").append(player.getCritRate()).append(",").append(player.getDodgeChance());
        
        int currentPa = 0;
        if (currentRoom instanceof BattleRoom) {
            BattleManager battle = ((BattleRoom) currentRoom).getBattleManager();
            if (battle != null && !battle.isBattleOver()) {
                currentPa = battle.getActionPointsLeft();
            }
        }
        data.append(",").append(currentPa);
        data.append(",").append(player.getMoney()); // 14th field: player gold
 
        if (currentRoom instanceof BattleRoom) {
            BattleManager battle = ((BattleRoom) currentRoom).getBattleManager();
            for(characters.enemies.Enemies e : battle.getEnemies()) {
                data.append(";").append(e.getName()).append(",").append(e.getHp()).append(",").append(e.getHpMax());
            }
        }
        data.append("}");
        if (currentRoom instanceof BattleRoom) {
            BattleManager battle = ((BattleRoom) currentRoom).getBattleManager();
            if (battle != null) {
                data.append("{TURN_ORDER:").append(battle.getTurnOrderString()).append("}");
            }
        }
        return data.toString();
    }

    private Character player;
    private Room currentRoom;
    private int lastBossDefeatedLevel = 0;

    public enum playerView {
        IDLE,
        ROOM, // non abbiamo più battle ma abbiamo room
        INVENTORY_MAIN,
        INVENTORY_ACTION,
        INVENTORY_OVERFLOW,
        INVENTORY_BATTLE,
        PLAYER_INFO
    }

    public playerView currentView = playerView.IDLE;
    private Items selectedItem = null;
    private ArrayList<Items> pendingLoot = new ArrayList<>();
    private int saveSlot = -1;

    public void setSaveSlot(int slot) {
        this.saveSlot = slot;
    }

    public void saveGame() {
        if (this.saveSlot > 0) database.DatabaseManager.saveGameState(this.saveSlot, this.player);
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
                return "{MSG:Zaino aperto}\n" + getInventoryData();
            }

            if (command.equalsIgnoreCase("AVANZA") || command.toLowerCase().startsWith("ava")) {
                generateNextRoom();
                currentView = playerView.ROOM;
                String res = "{MSG:" + currentRoom.enterRoom(player) + "}" + getEquippedData() + getStatsData();
                if (currentRoom instanceof ShopRoom) {
                    res += getShopData() + getInventoryData();
                }
                return res;
            }

            // DEBUG ONLY
            if (command.equalsIgnoreCase("spawn") || command.toLowerCase().startsWith("sp")) {
                ArrayList<Enemies> enemies = new ArrayList<Enemies>();
                enemies.add(spawnEnemy(new Zombie()));
                enemies.add(spawnEnemy(new Zombie()));

                this.currentRoom = new BattleRoom(player, enemies);
                currentView = playerView.ROOM;
                return "{MSG:" + this.currentRoom.enterRoom(player) + "}" + getEquippedData() + getStatsData();
            }

            // mostra quello che hai equipaggiato attualmente
            if (command.equalsIgnoreCase("PERSONAGGIO") || command.toLowerCase().startsWith("per")) {
                currentView = playerView.PLAYER_INFO;
                return "{MSG:Scheda personaggio aperta}\n" + getStatsData() + getEquippedData();
            }

            if (command.equalsIgnoreCase("CLASSIFICA") || command.toLowerCase().startsWith("cla")) {
                return database.DatabaseManager.getTopRuns();
            }

            // return "Sei nel corridoio. Comandi disponibili: 'AVANZA', 'ZAINO', 'PERSONAGGIO', 'CLASSIFICA'";
            return "Sei nel corridoio.";
        }

        // inventario, scelta oggetto
        else if (currentView == playerView.INVENTORY_MAIN) {
            if (command.equalsIgnoreCase("ESCI") || command.toLowerCase().startsWith("esc")) {
                // If we have an active room that isn't cleared, we go back to ROOM state
                if (currentRoom != null && !currentRoom.isCleared()) {
                    currentView = playerView.ROOM;
                    return "{MSG:Zaino chiuso. Sei ancora nella stanza.}";
                } else {
                    currentView = playerView.IDLE;
                    return "{MSG:Zaino chiuso}";
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
                // return "Inserisci un numero valido o scrivi ESCI.";
                return "";
            }
        }

        // azione sul'oggetto selezionato
        else if (currentView == playerView.INVENTORY_ACTION) {
            if (command.equalsIgnoreCase("EQUIPAGGIA") || command.toLowerCase().startsWith("equ")) {
                if (selectedItem instanceof Usables) {
                    return "{MSG:Non puoi equipaggiare questo oggetto}\n" + getInventoryData();
                }
                String equipResult = player.equip(selectedItem);
                if (equipResult.startsWith("Non puoi") || equipResult.startsWith("Non hai")) {
                    // Se l'equipaggiamento fallisce, rimaniamo in INVENTORY_ACTION per mostrare l'errore!
                    return "{MSG:" + equipResult + "}\n" + getInventoryData();
                }
                currentView = playerView.INVENTORY_MAIN;
                return "{MSG:" + equipResult + "}\n" + getInventoryData();
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
                return "{MSG:" + useResult + "}\n" + getInventoryData();

            } else if (command.equalsIgnoreCase("BUTTA") || command.toLowerCase().startsWith("bu")) {
                player.removeFromInventory(selectedItem);
                currentView = playerView.INVENTORY_MAIN;
                return "{MSG:Hai buttato " + selectedItem.getName() + "}\n" + getInventoryData();
            } else if (command.equalsIgnoreCase("ANNULLA") || command.toLowerCase().startsWith("ann")) {
                selectedItem = null;
                currentView = playerView.INVENTORY_MAIN;
                return "{MSG:Azione annullata}\n" + getInventoryData();
            }

            // Permetti di selezionare un altro oggetto direttamente senza ANNULLA
            try {
                int index = Integer.parseInt(command) - 1;
                ArrayList<Items> inventory = (ArrayList<Items>) player.getInventory();
                selectedItem = inventory.get(index);
                // Rimaniamo in INVENTORY_ACTION con il nuovo oggetto selezionato
                if (selectedItem instanceof Usables) {
                    return "{MSG:Hai selezionato: " + selectedItem.getName() + "}\n" + getInventoryData();
                } else {
                    return "{MSG:Hai selezionato: " + selectedItem.getName() + "}\n" + getInventoryData();
                }
            } catch (Exception e) {
                // Numero non valido, ignoriamo
            }
            // return "Comando non valido. 'EQUIPAGGIA', 'BUTTA', 'ANNULLA' ";
            return "";
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
                // return "Comando non valido.\nA terra c'è: " + item.getName() + " " + item.getDetails() + "\nScrivi un NUMERO per sostituirlo o LASCIA.";
                return "A terra c'è: " + item.getName() + " " + item.getDetails();
            }
        }

        // vista personaggio (stats e equippedItems)
        else if (currentView == playerView.PLAYER_INFO) {
            if (command.equalsIgnoreCase("ESCI") || command.toLowerCase().startsWith("esc")) {
                // If we have an active room that isn't cleared, we go back to ROOM state
                if (currentRoom != null && !currentRoom.isCleared()) {
                    currentView = playerView.ROOM;
                    return "{MSG:Scheda personaggio chiusa. Sei ancora nella stanza}";
                } else {
                    currentView = playerView.IDLE;
                    return "{MSG:Scheda personaggio chiusa}";
                }
            } else if (command.equalsIgnoreCase("Statistiche") || command.toLowerCase().startsWith("sta")) {
                final String[] statsInfo = { "" }; // renamed temp to statsInfo
                statsInfo[0] += "Livello: " + player.getLevel() + "\n";
                player.getStats().forEach((stat, value) -> {
                    statsInfo[0] += stat + ": " + value + "\n";
                });

                return "{MSG:" + statsInfo[0] + "}\n" + getStatsData() + getEquippedData();
            } else if (command.equalsIgnoreCase("Equipaggiamento") || command.toLowerCase().startsWith("equ")) {
                return "{MSG:" + player.getEquippedItems() + "}\n" + getStatsData() + getEquippedData();
            } else if (command.toLowerCase().startsWith("disequipaggia") || command.toLowerCase().startsWith("dis")) {
                try {
                    command = command.split(" ")[1];
                    // così che l'input viene a prescindere reso valido con la prima lettera
                    // maiuscola e il resto minuscolo
                    String deEquipResult = player.deEquip(command.substring(0, 1).toUpperCase() + command.substring(1).toLowerCase());
                    return "{MSG:" + deEquipResult + "}\n" + getStatsData() + getEquippedData();
                } catch (Exception e) {
                    // return "Errore durante il comando. Uso Disequipaggia [SLOT] Es: Disequipaggia torso";
                    return "";
                }

            } else {
                // return "Comando non valido. 'Equipaggiamento', 'Statistiche', 'Disequipaggia', 'Esci' ";
                return "";
            }

        }

        // nella stanza attiva (battaglia, negozio, fontana)
        if (currentView == playerView.ROOM) {
            if (!(currentRoom instanceof BattleRoom)) {
                if (command.equalsIgnoreCase("ZAINO") || command.toLowerCase().startsWith("za")) {
                    currentView = playerView.INVENTORY_MAIN;
                    return "{MSG:Zaino aperto}\n" + getInventoryData();
                }
                if (command.equalsIgnoreCase("PERSONAGGIO") || command.toLowerCase().startsWith("per")) {
                    currentView = playerView.PLAYER_INFO;
                    return "{MSG:Scheda personaggio aperta}\n" + getStatsData() + getEquippedData();
                }
            }

            // Pass the command to the active room logic
            String result = "{MSG:" + currentRoom.processCommand(command, player) + "}" + getEquippedData() + getStatsData(); // renamed risultato to result
            if (currentRoom instanceof ShopRoom) {
                result += getShopData() + getInventoryData();
            }

            if (result.contains("GAME OVER")) {
                if (this.saveSlot > 0) {
                    database.DatabaseManager.saveRun(this.player.getClass().getSimpleName(), this.player.getLevel(), this.player.getMoney());
                    database.DatabaseManager.deleteSaveState(this.saveSlot);
                }
            }

            // Se la stanza è stata superata/conclusa
            if (currentRoom.isCleared()) {
                pendingLoot = currentRoom.getPendingLoot();
                if (!pendingLoot.isEmpty()) {
                    currentView = playerView.INVENTORY_OVERFLOW;
                    result += "\n[!] Zaino pieno! [!] \nA terra c'è: " + pendingLoot.getFirst().getName() + " "
                            + pendingLoot.getFirst().getDetails();
                } else {
                    currentView = playerView.IDLE;
                    if (this.saveSlot > 0) database.DatabaseManager.saveGameState(this.saveSlot, this.player);
                }
                currentRoom = null;
            }
            return result;
        }

        // return "Comando non riconosciuto.";
        return "";
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
        if (currentView == playerView.ROOM) {
            if (currentRoom instanceof BattleRoom) {
                BattleManager battle = ((BattleRoom) currentRoom).getBattleManager();
                if (battle.isPlayerInInventory()) return playerView.INVENTORY_BATTLE.name();
                return "BATTLE";
            }
            if (currentRoom instanceof ShopRoom) {
                return "SHOP";
            }
        }
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
