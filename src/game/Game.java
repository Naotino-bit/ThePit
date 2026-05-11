package game;

import characters.Character;
import characters.enemies.Enemies;
import characters.enemies.Zombie;
import items.Items;
import items.armors.Armors;
import items.armors.Helmet;
import items.usables.HealthPotion;
import items.usables.Usables;
import items.weapons.Claymore;
import items.weapons.Sword;
import items.weapons.Weapons;

import java.sql.SQLData;
import java.util.ArrayList;

public class Game {
    private Character player;
    private BattleManager currentBattle;
    public enum playerView {
        IDLE,
        BATTLE,
        INVENTORY_MAIN,
        INVENTORY_ACTION,
        INVENTORY_OVERFLOW,
        PLAYER_INFO
    }
    public playerView currentView = playerView.IDLE;
    private Items selectedItem = null;
    private ArrayList<Items> pendingLoot = new ArrayList<>();

    public Game(Character playerClass) {
        this.player = playerClass;
        this.currentBattle = null; // All'inizio non stai combattendo

        player.addToInventory(new HealthPotion("Pozione vita", "LEGGENDARIO", "Vitalità", 25, 5000));
        player.addToInventory(new Helmet("Franco ", " ", 1, "Forza", 10, 5000));
        player.addToInventory(new Helmet("Gianni ", " ", 1, "Forza", 10, 5000));

        XmlHandler.loadAllItems();
    }

    public String getEnemies(ArrayList<Enemies> enemies) {
        String string = "E' apparso un nemico: \n";
        if(enemies.size()>1) {
            string = "Sono apparsi dei nemici: \n";
        }
        string += player.getName() + ": " + player.getHp() +"/" + player.getHpMax() + " hp\n";
        for(int i=0; i<enemies.size(); i++) {
            string += "[" + (i+1) + "] - " + enemies.get(i).getName() + " " + enemies.get(i).getHp() + "/" + enemies.get(i).getHpMax()+ " hp\n";
        }
        return string;
    }

    public Object processCommand(String comando){

        // se l'utente è in freeroam
        if(currentView == playerView.IDLE) {
            if(comando.equalsIgnoreCase("ZAINO") || comando.toLowerCase().startsWith("za")){
                currentView = playerView.INVENTORY_MAIN;
                return getInventoryString() + "Selezionare un oggetto oppure 'ESCI'";
            }

            //DEBUG ONLY
            if(comando.equalsIgnoreCase("spawn") || comando.toLowerCase().startsWith("sp")){
                ArrayList<Enemies> enemies = new ArrayList<Enemies>();

                enemies.add(spawnEnemy(new Zombie()));
                enemies.add(spawnEnemy(new Zombie()));

                this.currentBattle = new BattleManager(player, enemies);
                currentView = playerView.BATTLE;
                return getEnemies(enemies);
            }

            //mostra quello che hai equipaggiato attualmente
            if(comando.equalsIgnoreCase("PERSONAGGIO") || comando.toLowerCase().startsWith("per")){
                currentView = playerView.PLAYER_INFO;
                return "Mostra le statistiche con 'Statistiche'\nL'equipaggiamento attuale con 'Equipaggiamento'\nDisequipaggia qualcosa con 'Disequipaggia [SLOT]'";
            }
        }

        // inventario, scelta oggetto
        else if (currentView == playerView.INVENTORY_MAIN) {
            if (comando.equalsIgnoreCase("ESCI") || comando.toLowerCase().startsWith("esc")) {
                currentView = playerView.IDLE;
                return "Zaino chiuso";
            }
            try {
                int index = Integer.parseInt(comando) - 1; // -1 perché l'utente digita 1 per l'indice 0
                ArrayList<Items> inventory = (ArrayList<Items>) player.getInventory();

                selectedItem = inventory.get(index); // Salvo l'oggetto scelto!
                currentView = playerView.INVENTORY_ACTION; // Cambio stato!

                if (selectedItem instanceof Usables) {
                    return "Hai selezionato: "  + selectedItem.getDetails() + "\nVuoi USARE, BUTTARE o ANNULLARE?";
                } else {
                    String slot = selectedItem.getEquippedSlot().getFirst();
                    if (player.getEquippedItemsRaw().get(slot) != null) {
                        return "Hai selezionato: "  + selectedItem.getDetails() + "\n[!] Attualmente hai equipaggiato: " + player.getEquippedItemsRaw().get(slot).getDetails() +"\nVuoi EQUIPAGGIARE, BUTTARE o ANNULLARE?";
                    }
                    return "Hai selezionato: "  + selectedItem.getDetails() + "\nVuoi EQUIPAGGIARE, BUTTARE o ANNULLARE?";
                }

            } catch (Exception e) {
                return "Inserisci un numero valido o scrivi ESCI.";
            }
        }

        //azione sul'oggetto selezionato
        else if (currentView == playerView.INVENTORY_ACTION) {
            if(comando.equalsIgnoreCase("EQUIPAGGIA") || comando.toLowerCase().startsWith("equ")) {
                if (selectedItem instanceof Usables) {
                    return "Non puoi equipaggiare";
                }
                player.equip(selectedItem);
                currentView = playerView.INVENTORY_MAIN;
                return "Hai equipaggiato " + selectedItem.getName() + getInventoryString();
            } else if (comando.equalsIgnoreCase("USA")) {
                if (!(selectedItem instanceof Usables)) {
                    return "Non è un consumabile";
                }
                ((Usables) selectedItem).use(player,player);
                currentView = playerView.INVENTORY_MAIN;
                return "Hai usato " + selectedItem.getName() + getInventoryString();

            } else if (comando.equalsIgnoreCase("BUTTA") || comando.toLowerCase().startsWith("bu")) {
                player.removeFromInventory(selectedItem);
                currentView = playerView.INVENTORY_MAIN;
                return "Hai buttato " + selectedItem.getName() + getInventoryString();
            } else if (comando.equalsIgnoreCase("ANNULLA") || comando.toLowerCase().startsWith("ann")) {
                selectedItem = null;
                currentView = playerView.INVENTORY_MAIN;
                return "Azione annullata" + getInventoryString();
            }
            return "Comando non valido. 'EQUIPAGGIA', 'BUTTA', 'ANNULLA' ";
        }
        
        //vista per gestione oggetti overflow durante battaglia
        else if (currentView == playerView.INVENTORY_OVERFLOW) {
            Items item = pendingLoot.getFirst();
            if (comando.equalsIgnoreCase("LASCIA") || comando.toLowerCase().startsWith("la")) {
                pendingLoot.removeFirst();
                if (pendingLoot.isEmpty()) {
                    currentView = playerView.IDLE;
                    return "Hai buttato l'oggetto. Tornato in esplorazione";
                } else {
                    return "Hai abbandonato l'oggetto. C'è ancora altro a terra:\nVuoi prendere [" + pendingLoot.getFirst().getName() + " " + pendingLoot.getFirst().getDetails() + "]?\nScrivi un NUMERO per sostituirlo, o LASCIA.";
                }
            }

            try {
                int indexToDrop = Integer.parseInt(comando) - 1;
                ArrayList<Items> inventory = player.getInventory();

                Items droppedItem = inventory.get(indexToDrop);

                inventory.remove(indexToDrop);
                inventory.add(item);
                pendingLoot.removeFirst();

                String msg = "Hai gettato " + droppedItem.getName() + " e raccolto " + item.getName() + "\n";

                // controllo di nuovo se c'è altra roba in coda
                if (pendingLoot.isEmpty()) {
                    currentView = playerView.IDLE;
                    return msg + "Non c'è altro a terra. Tornato in esplorazione.";
                } else {
                    return msg + "C'è ancora altro a terra:\nVuoi prendere " + pendingLoot.getFirst().getName() + " " + pendingLoot.getFirst().getDetails() + "?\nScrivi un NUMERO per sostituirlo, o LASCIA.";
                }

            } catch (Exception e) {
                return "Comando non valido.\nA terra c'è: " + item.getName() + " " + item.getDetails() + "\nScrivi un NUMERO per sostituirlo o LASCIA.";
            }
        }

        //vista personaggio (stats e equippedItems)
        else if (currentView == playerView.PLAYER_INFO) {
            if (comando.equalsIgnoreCase("ESCI") || comando.toLowerCase().startsWith("esc")) {
                currentView = playerView.IDLE;
                return "Scheda personaggio chiusa";
            } else if (comando.equalsIgnoreCase("Statistiche") || comando.toLowerCase().startsWith("sta")){
                final String[] temp = {""}; //fatto ad array e non stringa perchè dava errore non so il motivo
                temp[0] += "Livello: " + player.getLevel() + "\n";
                player.getStats().forEach((stat, value) -> {
                    temp[0] += stat + ": " + value + "\n";
                });

                return temp[0];
            } else if (comando.equalsIgnoreCase("Equipaggiamento") || comando.toLowerCase().startsWith("equ")) {
                return player.getEquippedItems();
            } else if (comando.toLowerCase().startsWith("disequipaggia") || comando.toLowerCase().startsWith("dis")) {
                try {
                    comando = comando.split(" ")[1];
                    //così che l'input viene a prescindere reso valido con la prima lettera maiuscola e il resto minuscolo
                    player.deEquip(comando.substring(0,1).toUpperCase() + comando.substring(1).toLowerCase());
                    return "Hai disequipaggiato " + comando.toLowerCase();
                } catch (Exception e) {
                    return "Errore durante il comando. Uso Disequipaggia [SLOT] Es: Disequipaggia torso";
                }

            } else {
                return "Comando non valido. 'Equipaggiamento', 'Statistiche', 'Disequipaggia', 'Esci' ";
            }


        }
        
        

        // in battaglia?
        if (currentView == playerView.BATTLE) {

            // SE STO COMBATTENDO: Passo il comando all'arbitro!
            String risultato = currentBattle.manageRound(comando);

            // Se la battaglia è finita, "licenzio" l'arbitro
            if (currentBattle.isBattleOver()) {

                pendingLoot = currentBattle.getPendingLoot();
                if (!pendingLoot.isEmpty()) {
                    currentView = playerView.INVENTORY_OVERFLOW;
                    risultato += "\n[!] Zaino pieno! [!] \nCi sono degli oggetti che puoi prendere.\n" + getInventoryString() + "\nScrivi un NUMERO per sostituirlo o LASCIA.";
                    risultato += "\nA terra c'è: " + pendingLoot.getFirst().getName() + " " +pendingLoot.getFirst().getDetails();
                } else {
                    currentView = playerView.IDLE;
                }
                currentBattle = null;

            }
            return risultato;
        }

        return "Comando non riconosciuto. Esegui 'ZAINO', 'SPAWN', 'PERSONAGGIO' ";
    }

    private String getInventoryString() {
        String temp = "\n----- Zaino -----\n";
        ArrayList<Items> inventory = player.getInventory();
        if(inventory.isEmpty()){
            return temp + "Zaino vuoto\n";
        }
        for (int i = 0; i < inventory.size(); i++) {
            temp += (i + 1) + ": " + inventory.get(i).getName() + "\n";
        }
        return temp;
    }

    public String getCurrentState() {
        return currentView.name();
    }

    private Enemies spawnEnemy(Enemies enemy) {
        enemy.setLevelAndScale(player.getLevel());
        return enemy;
    }
}
