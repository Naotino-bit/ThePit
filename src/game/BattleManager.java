package game;

import characters.enemies.Enemies;
import characters.Character;
import items.Items;
import items.usables.Usables;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class BattleManager {
    private Character player;
    private ArrayList<Enemies> enemies;
    private boolean battleOver;

    private ArrayList<Character> attackOrder;
    private int consecutiveAttacks;
    private int counterRound = 0;
    Random rand = new Random();
    private ArrayList<Items> pendingLoot = new ArrayList<>();
    private ArrayList<Items> itemDropped = new ArrayList<>();
    private enum playerStatus {
        BATTLE,
        INVENTORY
    }
    private playerStatus playerCurrentStatus = playerStatus.BATTLE;

    public BattleManager(Character player, ArrayList<Enemies> enemies) {
        this.player = player;
        this.enemies = enemies;
        this.battleOver = false;
        prepareAttackOrder();
        this.consecutiveAttacks = getConsecutiveAttack(player);
    }

    private void prepareAttackOrder() {
        // L'array dell'ordine attacchi viene prima riempito a caso
        // e poi ordinato con un sort in base all'agilita dei membri
        // della battaglia
        attackOrder = new ArrayList<>();
        attackOrder.add(player);
        for(Enemies enemy: enemies){
            attackOrder.add(enemy);
        }
        attackOrder.sort( Comparator.comparing((Character a) -> a.getStats().get("Agility")).reversed());
        counterRound = 0;
    }

    public int getConsecutiveAttack(Character attacker) {
        // viene passato in input chi sta attaccando e in base
        // alla formula: round(agilitàAttacker/minAgilitàBattaglia)
        // restitusice N attacchi consecutivi
        Integer attAgility = attacker.getStats().get("Agility");
        Integer minAgility = 999;
        Integer temp;
        //troviamo l'agilità minima in battaglia
        for (Character entity: attackOrder) {
            temp = entity.getStats().get("Agility");
            if(temp<minAgility){
                minAgility = temp;
            }
        }
        int attacchiConsecutivi = Math.round((float) attAgility /minAgility);
        //mettiamo un cap massimo a 3 per evitare troppi attacchi consecutivi in boss fight
        if(attacchiConsecutivi > 3) {
            attacchiConsecutivi = 3;
        }
        return attacchiConsecutivi;
    }

    public String getBattleReport() {
        StringBuilder report = new StringBuilder("\n---- STATO BATTAGLIA ----\n");
        report.append(player.getName()).append(": ").append(player.getHp()).append("/").append(player.getHpMax()).append(" hp\n");

        for (int i = 0; i < enemies.size(); i++) {
            Enemies e = enemies.get(i);
            report.append("[").append(i + 1).append("] ")
                    .append(e.getName()).append(": ")
                    .append(e.getHp()).append("/").append(e.getHpMax()).append(" hp\n");
        }
        report.append("--------------------------\n");
        return report.toString();
    }

    public String manageRound(String playerMove) {
        StringBuilder fightLog = new StringBuilder();
        if(playerCurrentStatus == playerStatus.INVENTORY) {
            if (playerMove.equalsIgnoreCase("ESCI") || playerMove.toLowerCase().startsWith("esc")) {
                playerCurrentStatus = playerStatus.BATTLE;
                return "Zaino chiuso";
            }

            try {
                int index = Integer.parseInt(playerMove) - 1; // -1 perché l'utente digita 1 per l'indice 0
                ArrayList<Usables> inventory = player.getInventoryUsables();

                Usables itemToUse = inventory.get(index);
                consecutiveAttacks--;
                return itemToUse.use(player, player);


            } catch (Exception e) {
                return "Inserisci un numero valido o scrivi ESCI.";
            }

        }

        if (battleOver) return "La battaglia è già finita!";

        // --- PRE-VALIDAZIONE INPUT ---
        // Controlliamo subito se il comando ha senso. Se è una parola a caso,
        // blocchiamo tutto prima ancora di far scorrere i turni!
        String[] checkSplit = playerMove.split(" ");
        String cmd = checkSplit[0].toUpperCase();
        if (!cmd.equals("ATTACCA") && !cmd.equals("ZAINO")) {
            return "Comando non riconosciuto. Usa 'ATTACCA [numero]' o 'ZAINO'.\n" + getBattleReport();
        }
        // -----------------------------


        boolean playerActionProcessed = false; // serve per sapere se abbiamo già esegutio l'azione del player



        // Un singolo ciclo che gestisce tutti i turni in sequenza
        while (!battleOver) {

            // 1. FINE DEL ROUND: se abbiamo finito la lista, resettiamo
            if (counterRound >= attackOrder.size()) {
                prepareAttackOrder();
                consecutiveAttacks = getConsecutiveAttack(player);
                //FINE TURNO E CHECK MORTE PER EVENTUALI DANNI DA ABILITA'
                for(Character entity : attackOrder) {
                    entity.endTurn();

                    if(checkDeath(entity, fightLog)){
                        return fightLog.toString();
                    }
                }
                fightLog.append("\n--- NUOVO ROUND ---\n");
            }

            Character currentEntity = attackOrder.get(counterRound);

            // 2. TURNO DEL GIOCATORE
            if (currentEntity == player) {

                // Se abbiamo già processato l'azione del giocatore, significa
                // che i nemici hanno finito e tocca di nuovo a lui.
                // Ci fermiamo e aspettiamo che l'utente inserisca un nuovo comando
                if (playerActionProcessed) {
                    break;
                }

                String[] moveSplit = playerMove.split(" ");
                int enemyChoice = 0;

                try {
                    if (moveSplit.length > 1) {
                        enemyChoice = Integer.parseInt(moveSplit[1]) - 1;
                    }
                } catch (NumberFormatException e) { enemyChoice = 0; }

                if (enemyChoice < 0 || enemyChoice >= enemies.size()) {
                    fightLog.append("Bersaglio non valido. Scegli tra 1 e ").append(enemies.size()).append("\n");
                    break; // Interrompe il ciclo in attesa di un input corretto
                }

                if (moveSplit[0].equalsIgnoreCase("ATTACCA") || moveSplit[0].toLowerCase().startsWith("att")) {
                    Enemies target = enemies.get(enemyChoice);
                    player.attack(target, enemies);
                    fightLog.append("Hai colpito ").append(target.getName()).append("!\n");

                    if(checkDeath(target, fightLog)){
                        return fightLog.toString();
                    }

                    consecutiveAttacks--;
                    if (consecutiveAttacks > 0) {
                        fightLog.append("Puoi attaccare di nuovo (Rimanenti: ").append(consecutiveAttacks).append(")\n");
                        break; // Interrompe il ciclo in attesa del prossimo attacco
                    } else {
                        counterRound++; // Turno del giocatore finito
                        playerActionProcessed = true; // Segniamo che ha già agito
                        // Il ciclo NON si interrompe: ora farà attaccare i nemici successivi!
                    }

                } else if (moveSplit[0].equalsIgnoreCase("ZAINO") || moveSplit[0].toLowerCase().startsWith("za")) {
                    playerCurrentStatus = playerStatus.INVENTORY;
                    fightLog.append("--- Zaino - Consumabili ---\n");
                    for(Items item: player.getInventoryUsables()){
                        fightLog.append(player.getInventory().indexOf(item)+1).append(". ").append(item.getName()).append(" ").append(item.getDetails()).append("\n");
                    }
                    return fightLog.toString();
                } else {
                    fightLog.append("Comando non riconosciuto. Usa 'ATTACCA [numero]' o 'ZAINO'.\n");
                    break;
                }

            }

            // 3. TURNO DEI NEMICI
            else {
                // I nemici morti (non rimossi da attackOrder) verranno ignorati qui
                if (currentEntity instanceof Enemies && !currentEntity.isDead()) {
                    fightLog.append("\nTurno di ").append(currentEntity.getName()).append("...\n");

                    int consAttack = getConsecutiveAttack(currentEntity);
                    for (int i = 0; i < consAttack; i++) {
                        currentEntity.attack(player);
                        fightLog.append("[").append(currentEntity.getName()).append("] ti attacca!\n");

                        if (player.getHp() <= 0) {
                            battleOver = true;
                            fightLog.append("\nSei morto! GAME OVER.");
                            player.handleDeath();
                            return fightLog.toString();
                        }
                    }
                }
                // Il nemico ha finito, passiamo al prossimo
                counterRound++;
            }
        }

        fightLog.append(getBattleReport());
        return fightLog.toString();
    }

    public boolean isBattleOver() {
        return battleOver;
    }

    public ArrayList<Items> getPendingLoot() {
        return pendingLoot;
    }

    public boolean checkDeath(Character entity, StringBuilder fightLog){
        if (entity.isDead()) {
            Enemies deadEnemy = (Enemies) entity;
            int expGained = deadEnemy.getExpReward();
            int moneyGained = deadEnemy.generateMoneyDrop();
            itemDropped = deadEnemy.getDrops();

            boolean hasGoldScarab = false;
            boolean hasSilverScarab = false;
            boolean hasDragonsEgg = false;

            for (Items item : player.getEquippedItemsRaw().values()) {
                if (item != null) {
                    if (item.getName().equals("Gold Scarab")) hasGoldScarab = true;
                    if (item.getName().equals("Silver Scarab")) hasSilverScarab = true;
                    if (item.getName().equals("Dragon's Egg")) hasDragonsEgg = true;
                }
            }

            if (hasDragonsEgg) {
                expGained = (int) (expGained * 1.5);
            }
            if (hasGoldScarab) {
                moneyGained = (int) (moneyGained * 1.5);
            }
            if (hasSilverScarab) {
                Items extraLoot = XmlHandler.rollRandomItem();
                if (extraLoot != null) {
                    itemDropped.add(extraLoot);
                }
            }
            fightLog.append(player.gainExp(expGained));
            player.gainMoney(moneyGained);

            fightLog.append("Hai sconfitto ").append(deadEnemy.getName()).append("!\n");
            fightLog.append("Hai ottenuto ").append(moneyGained).append(" monete.\n");

            for(Items item: itemDropped){
                fightLog.append(deadEnemy.getName()).append(" ha droppato: ").append(item.getName()).append("\n");

                if (!player.addToInventory(item)) {
                    pendingLoot.add(item);
                }
            }
            enemies.remove(deadEnemy);

            // NOTA BENE: Ho rimosso "attackOrder.remove(target)".
            // Rimuovere elementi dalla lista mentre la stiamo scorrendo sballa
            // gli indici e causa bug. Il nemico morto verrà semplicemente
            // "saltato" dal turno nemico e pulito al prossimo "prepareAttackOrder".

            if (enemies.isEmpty()) {
                battleOver = true;
                fightLog.append("\nLa battaglia è finita! Vittoria!").toString();
                return true;
            }
        }
        return false;
    }
}