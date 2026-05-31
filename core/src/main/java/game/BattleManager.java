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
    private int actionPoints;
    private int counterRound = 0;
    Random rand = new Random();
    private ArrayList<Items> pendingLoot = new ArrayList<>();
    private ArrayList<Items> itemDropped = new ArrayList<>();
    private enum playerStatus {
        BATTLE,
        INVENTORY
    }

    public boolean isPlayerInInventory() { return playerCurrentStatus == playerStatus.INVENTORY; }
    public ArrayList<Enemies> getEnemies() {
        return enemies;
    }

    private playerStatus playerCurrentStatus = playerStatus.BATTLE;

    public BattleManager(Character player, ArrayList<Enemies> enemies) {
        this.player = player;
        this.enemies = enemies;
        this.battleOver = false;
        prepareAttackOrder();
        this.actionPoints = getActionPoints(player);
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

    public int getActionPoints(Character attacker) {
        Integer attAgility = attacker.getStats().get("Agility");
        Integer minAgility = 999;
        for (Character entity: attackOrder) {
            Integer temp = entity.getStats().get("Agility");
            if(temp < minAgility) minAgility = temp;
        }
        if (minAgility < 1) minAgility = 1;
        int actionPoints = Math.round((float) attAgility / minAgility);
        
        if (actionPoints < 2) actionPoints = 2;
        if (actionPoints > 6) actionPoints = 6;
        
        return actionPoints;
    }

    public int getActionPointsLeft() {
        return this.actionPoints;
    }

    public String getBattleReport() {
        StringBuilder report = new StringBuilder("\n---- STATO BATTAGLIA ----\n");
        report.append(player.getName()).append(": ").append(player.getHp()).append("/").append(player.getHpMax()).append(" HP | ")
              .append(player.getCurrentMana()).append("/").append(player.getManaMax()).append(" Mana | ")
              .append("PA Rimanenti: ").append(actionPoints).append("\n");

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
                String[] parts = playerMove.split(" ");
                int index = Integer.parseInt(parts[0]) - 1; // -1 perché l'utente digita 1 per l'indice 0
                ArrayList<Usables> inventory = player.getInventoryUsables();

                if (index < 0 || index >= inventory.size()) return "Oggetto non valido.";
                
                Usables itemToUse = inventory.get(index);
                Character target = player;

                if (itemToUse instanceof items.usables.Throwables && ((items.usables.Throwables) itemToUse).isAoE) {
                    actionPoints--;
                    player.removeFromInventory(itemToUse);
                    StringBuilder aoeResult = new StringBuilder();
                    ArrayList<Enemies> enemiesCopy = new ArrayList<>(enemies);
                    for (Enemies e : enemiesCopy) {
                        aoeResult.append(itemToUse.use(player, e)).append("\n");
                        StringBuilder fakeLog = new StringBuilder();
                        if (checkDeath(e, fakeLog)) {
                            aoeResult.append(fakeLog.toString());
                        }
                    }
                    return aoeResult.toString();
                } else if (itemToUse instanceof items.usables.Throwables) {
                    int targetIndex = 0; // Default to first enemy
                    if (parts.length > 1) {
                        try {
                            targetIndex = Integer.parseInt(parts[1]) - 1;
                        } catch (NumberFormatException e) {}
                    }
                    if (targetIndex < 0 || targetIndex >= enemies.size()) {
                        return "Bersaglio non valido.";
                    }
                    target = enemies.get(targetIndex);
                }

                actionPoints--;
                player.removeFromInventory(itemToUse);
                
                String result = itemToUse.use(player, target);
                
                if (target instanceof Enemies) {
                    StringBuilder fakeLog = new StringBuilder();
                    if (checkDeath(target, fakeLog)) {
                        result += "\n" + fakeLog.toString();
                    }
                }
                
                return result;

            } catch (Exception e) {
                // return "Inserisci un numero valido o scrivi ESCI. (Es: '1' per usare su di te, '1 2' per tirare il primo oggetto al secondo nemico)";
                return "";
            }

        }

        if (battleOver) return "La battaglia è già finita!";

        if (playerMove == null || playerMove.trim().isEmpty()) {
            // return "Inserisci un comando!\n" + getBattleReport();
            return "";
        }

        // --- PRE-VALIDAZIONE INPUT ---
        // Controlliamo subito se il comando ha senso. Se è una parola a caso,
        // blocchiamo tutto prima ancora di far scorrere i turni!
        String[] checkSplit = playerMove.trim().split("\\s+");
        String cmd = checkSplit[0].toUpperCase();
        if (!cmd.equals("F") && !cmd.equals("M") && !cmd.equals("C") && !cmd.equals("PASSA") && !cmd.equals("P") && !cmd.equals("ZAINO") && !cmd.startsWith("ZA")) {
            /*
            return "Comandi:\n" + 
                   "- F L/N/P [nemico] (Fisico Legg 1PA / Norm 2PA / Pes 3PA)\n" +
                   "- M 1/2 [nemico] (Magia Dardo 1PA/5Mana / Esplosione 2PA/15Mana)\n" +
                   "- C (Cura 2PA/10Mana)\n" +
                   "- P (Passa turno)\n" +
                   "- ZAINO\n" + getBattleReport();
            */
            return "";
        }
        // -----------------------------


        boolean playerActionProcessed = false; // serve per sapere se abbiamo già esegutio l'azione del player



        // Un singolo ciclo che gestisce tutti i turni in sequenza
        while (!battleOver) {

            // 1. FINE DEL ROUND: se abbiamo finito la lista, resettiamo
            if (counterRound >= attackOrder.size()) {
                prepareAttackOrder();
                actionPoints = getActionPoints(player);
                //FINE TURNO E CHECK MORTE PER EVENTUALI DANNI DA ABILITA'
                for(Character entity : attackOrder) {
                    fightLog.append(entity.endTurn());

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

                if (moveSplit[0].equalsIgnoreCase("F")) {
                    String attackType = "N";
                    if (moveSplit.length > 1) attackType = moveSplit[1].toUpperCase();

                    int enemyChoice = 0;
                    try {
                        if (moveSplit.length > 2) enemyChoice = Integer.parseInt(moveSplit[2]) - 1;
                    } catch (NumberFormatException e) { enemyChoice = 0; }

                    if (enemyChoice < 0 || enemyChoice >= enemies.size()) {
                        fightLog.append("Bersaglio non valido. Scegli tra 1 e ").append(enemies.size()).append("\n");
                        break;
                    }

                    int apCost = 2;
                    double dmgMod = 1.0;
                    if (attackType.equals("L")) { apCost = 1; dmgMod = 0.5; }
                    else if (attackType.equals("P")) { apCost = 3; dmgMod = 2.0; }

                    if (actionPoints < apCost) {
                        fightLog.append("Non hai abbastanza PA (" + actionPoints + "/" + apCost + "). Usa un attacco più leggero o passa il turno (P).\n");
                        break;
                    }

                    actionPoints -= apCost;
                    Enemies target = enemies.get(enemyChoice);
                    fightLog.append(player.physicalAttack(target, enemies, dmgMod));
                    fightLog.append("Hai usato un Attacco Fisico " + attackType + " su ").append(target.getName()).append("!\n");

                    if(checkDeath(target, fightLog)) return fightLog.toString();

                    if (actionPoints > 0) {
                        fightLog.append("Puoi agire di nuovo (PA Rimanenti: ").append(actionPoints).append(")\n");
                        break;
                    } else {
                        counterRound++;
                        playerActionProcessed = true;
                    }

                } else if (moveSplit[0].equalsIgnoreCase("M") || moveSplit[0].equalsIgnoreCase("C")) {
                    int apCost = 2;
                    int magicType = 2;
                    if (moveSplit[0].equalsIgnoreCase("C")) {
                        apCost = 2; magicType = 3;
                    } else {
                        String mType = "1";
                        if (moveSplit.length > 1) mType = moveSplit[1];
                        if (mType.equals("1")) { apCost = 1; magicType = 1; }
                        else if (mType.equals("2")) { apCost = 2; magicType = 2; }
                    }

                    if (actionPoints < apCost) {
                        fightLog.append("Non hai abbastanza PA (" + actionPoints + "/" + apCost + ").\n");
                        break;
                    }

                    int enemyChoice = 0;
                    try {
                        if (moveSplit[0].equalsIgnoreCase("C")) { }
                        else if (moveSplit.length > 2) enemyChoice = Integer.parseInt(moveSplit[2]) - 1;
                    } catch (NumberFormatException e) { enemyChoice = 0; }

                    if (magicType != 3 && (enemyChoice < 0 || enemyChoice >= enemies.size())) {
                        fightLog.append("Bersaglio non valido.\n");
                        break;
                    }

                    actionPoints -= apCost;
                    Enemies target = magicType == 3 ? null : enemies.get(enemyChoice);
                    
                    int initialMana = player.getCurrentMana();
                    fightLog.append(player.magicalAttack(target, enemies, magicType));
                    if (player.getCurrentMana() == initialMana && magicType != 3) {
                        // Magia fallita per mancanza di mana, restituisco PA
                        actionPoints += apCost;
                        fightLog.append("Magia fallita per mancanza di Mana!\n");
                        break;
                    }

                    if (target != null && checkDeath(target, fightLog)) return fightLog.toString();

                    if (actionPoints > 0) {
                        fightLog.append("Puoi agire di nuovo (PA Rimanenti: ").append(actionPoints).append(")\n");
                        break;
                    } else {
                        counterRound++;
                        playerActionProcessed = true;
                    }

                } else if (moveSplit[0].equalsIgnoreCase("PASSA") || moveSplit[0].equalsIgnoreCase("P")) {
                    counterRound++;
                    playerActionProcessed = true;
                    actionPoints = 0;
                    fightLog.append("Hai passato il turno.\n");
                    
                } else if (moveSplit[0].equalsIgnoreCase("ZAINO") || moveSplit[0].toLowerCase().startsWith("za")) {
                    if (moveSplit.length > 1) {
                        try {
                            int index = Integer.parseInt(moveSplit[1]) - 1;
                            ArrayList<Usables> inventory = player.getInventoryUsables();
                            if (index >= 0 && index < inventory.size()) {
                                Usables itemToUse = inventory.get(index);
                                Character target = player;
                                
                                if (itemToUse instanceof items.usables.Throwables) {
                                    if (!((items.usables.Throwables) itemToUse).isAoE) {
                                        int targetIndex = 0;
                                        if (moveSplit.length > 2) {
                                            targetIndex = Integer.parseInt(moveSplit[2]) - 1;
                                        }
                                        if (targetIndex >= 0 && targetIndex < enemies.size()) {
                                            target = enemies.get(targetIndex);
                                        } else {
                                            fightLog.append("Bersaglio non valido.\n");
                                            break;
                                        }
                                    }
                                }
                                
                                actionPoints--;
                                player.removeFromInventory(itemToUse);
                                
                                if (itemToUse instanceof items.usables.Throwables && ((items.usables.Throwables) itemToUse).isAoE) {
                                    StringBuilder aoeResult = new StringBuilder();
                                    ArrayList<Enemies> enemiesCopy = new ArrayList<>(enemies);
                                    for (Enemies e : enemiesCopy) {
                                        aoeResult.append(itemToUse.use(player, e)).append("\n");
                                        StringBuilder fakeLog = new StringBuilder();
                                        if (checkDeath(e, fakeLog)) {
                                            aoeResult.append(fakeLog.toString());
                                        }
                                    }
                                    fightLog.append(aoeResult.toString());
                                } else {
                                    String result = itemToUse.use(player, target);
                                    if (target instanceof Enemies) {
                                        StringBuilder fakeLog = new StringBuilder();
                                        if (checkDeath(target, fakeLog)) {
                                            result += "\n" + fakeLog.toString();
                                        }
                                    }
                                    fightLog.append(result).append("\n");
                                }

                                if (actionPoints > 0) {
                                    fightLog.append("Puoi agire di nuovo (PA Rimanenti: ").append(actionPoints).append(")\n");
                                    break;
                                } else {
                                    counterRound++;
                                    playerActionProcessed = true;
                                }
                            } else {
                                fightLog.append("Oggetto non valido.\n");
                                break;
                            }
                        } catch (NumberFormatException e) {
                            fightLog.append("Comando zaino non valido.\n");
                            break;
                        }
                    } else {
                        playerCurrentStatus = playerStatus.INVENTORY;
                        fightLog.append("--- Zaino - Consumabili ---\n");
                        for(Items item: player.getInventoryUsables()){
                            fightLog.append(player.getInventory().indexOf(item)+1).append(". ").append(item.getName()).append(" ").append(item.getDetails()).append("\n");
                        }
                        return fightLog.toString();
                    }
                } else {
                    fightLog.append("Comando non riconosciuto.\n");
                    break;
                }

            }

            // 3. TURNO DEI NEMICI
            else {
                // I nemici morti (non rimossi da attackOrder) verranno ignorati qui
                if (currentEntity instanceof Enemies && !currentEntity.isDead()) {
                    fightLog.append("\n--- TURNO DEI NEMICI ---\n");
                    fightLog.append("Turno di ").append(currentEntity.getName()).append("...\n");

                    int ap = getActionPoints(currentEntity);
                    while (ap > 0 && !currentEntity.isDead()) {
                        boolean useMagic = false;
                        if (currentEntity.getStats().get("Intelligence") > currentEntity.getStats().get("Strength") && currentEntity.getCurrentMana() >= 5) {
                            if (rand.nextBoolean()) useMagic = true;
                        }

                        if (useMagic) {
                            if (ap >= 2 && currentEntity.getCurrentMana() >= 15) {
                                fightLog.append(currentEntity.magicalAttack(player, null, 2));
                                ap -= 2;
                                fightLog.append("[").append(currentEntity.getName()).append("] lancia un'Esplosione Arcana!\n");
                            } else {
                                fightLog.append(currentEntity.magicalAttack(player, null, 1));
                                ap -= 1;
                                fightLog.append("[").append(currentEntity.getName()).append("] lancia un Dardo Magico!\n");
                            }
                        } else {
                            if (ap >= 3 && rand.nextBoolean()) {
                                fightLog.append(currentEntity.physicalAttack(player, null, 2.0));
                                ap -= 3;
                                fightLog.append("[").append(currentEntity.getName()).append("] usa un Attacco Pesante!\n");
                            } else if (ap >= 2) {
                                fightLog.append(currentEntity.physicalAttack(player, null, 1.0));
                                ap -= 2;
                                fightLog.append("[").append(currentEntity.getName()).append("] usa un Attacco Normale!\n");
                            } else {
                                fightLog.append(currentEntity.physicalAttack(player, null, 0.5));
                                ap -= 1;
                                fightLog.append("[").append(currentEntity.getName()).append("] usa un Attacco Leggero!\n");
                            }
                        }

                        if (player.getHp() <= 0) {
                            battleOver = true;
                            fightLog.append("\nSei morto! GAME OVER.");
                            // database.DatabaseManager.saveRun(player.getName(), player.getLevel(), player.getMoney());
                            player.handleDeath();
                            return fightLog.toString();
                        }
                    }
                }
                // Il nemico ha finito, passiamo al prossimo
                counterRound++;
            }
        }

        return fightLog.toString();
    }

    public String getTurnOrderString() {
        StringBuilder sb = new StringBuilder();
        int size = attackOrder.size();
        for (int i = 0; i < size; i++) {
            int idx = (counterRound + i) % size;
            Character c = attackOrder.get(idx);
            if (c != null && !c.isDead()) {
                if (sb.length() > 0) sb.append(",");
                sb.append(c.getName());
            }
        }
        return sb.toString();
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