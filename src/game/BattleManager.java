package game;

import characters.enemies.Enemies;
import characters.Character;
import items.Items;

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
        if (battleOver) return "La battaglia è già finita!";

        // --- PRE-VALIDAZIONE INPUT ---
        // Controlliamo subito se il comando ha senso. Se è una parola a caso,
        // blocchiamo tutto prima ancora di far scorrere i turni!
        String[] checkSplit = playerMove.split(" ");
        String cmd = checkSplit[0].toUpperCase();
        if (!cmd.equals("ATTACCA") && !cmd.equals("CURATI")) {
            return "Comando non riconosciuto. Usa 'ATTACCA [numero]' o 'CURATI'.\n" + getBattleReport();
        }
        // -----------------------------

        StringBuilder fightLog = new StringBuilder();
        boolean playerActionProcessed = false; // serve per sapere se abbiamo già esegutio l'azione del player
        ArrayList<Items> itemDropped = new ArrayList<>();


        // Un singolo ciclo che gestisce tutti i turni in sequenza
        while (!battleOver) {

            // 1. FINE DEL ROUND: se abbiamo finito la lista, resettiamo
            if (counterRound >= attackOrder.size()) {
                prepareAttackOrder();
                consecutiveAttacks = getConsecutiveAttack(player);
                fightLog.append("\n--- NUOVO ROUND ---\n");
            }

            Character currentEntity = attackOrder.get(counterRound);

            // 2. TURNO DEL GIOCATORE
            if (currentEntity == player) {

                // Se abbiamo già processato l'azione del giocatore, significa
                // che i nemici hanno finito e tocca di nuovo a lui.
                // Ci fermiamo e aspettiamo che l'utente inserisca un nuovo comando!
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

                if (moveSplit[0].equalsIgnoreCase("ATTACCA")) {
                    Enemies target = enemies.get(enemyChoice);
                    player.attack(target);
                    fightLog.append("Hai colpito ").append(target.getName()).append("!\n");

                    if (target.isDead()) {
                        fightLog.append("Hai sconfitto ").append(target.getName()).append("!\n");
                        itemDropped = enemies.get(enemyChoice).getDrops();
                        for(Items item: itemDropped){
                            if (!player.addToInventory(item)) {
                                pendingLoot.add(item);
                            }
                        }
                        enemies.remove(target);

                        // NOTA BENE: Ho rimosso "attackOrder.remove(target)".
                        // Rimuovere elementi dalla lista mentre la stiamo scorrendo sballa
                        // gli indici e causa bug. Il nemico morto verrà semplicemente
                        // "saltato" dal turno nemico e pulito al prossimo "prepareAttackOrder".

                        if (enemies.isEmpty()) {
                            battleOver = true;
                            return fightLog.append("\nLa battaglia è finita! Vittoria!").toString();
                        }
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

                } else if (moveSplit[0].equalsIgnoreCase("CURATI")) {
                    fightLog.append("LOGICA PER CURARSI NON IMPLEMENTATA\n");
                    break; // Usa break invece di return!
                } else {
                    fightLog.append("Comando non riconosciuto. Usa 'ATTACCA [numero]' o 'CURATI'.\n");
                    break; // Usa break invece di return!
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
}