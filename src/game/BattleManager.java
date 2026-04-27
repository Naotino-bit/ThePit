package game;

import characters.enemies.Enemies;
import characters.Character;

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
        System.out.println(attackOrder);
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

        StringBuilder fightLog = new StringBuilder();

        // 1. TURNO DEL GIOCATORE
        if (attackOrder.get(counterRound) == player) {
            String[] moveSplit = playerMove.split(" ");
            int enemyChoice = 0;

            try {
                if (moveSplit.length > 1) {
                    enemyChoice = Integer.parseInt(moveSplit[1]) - 1;
                }
            } catch (NumberFormatException e) { enemyChoice = 0; }

            if (enemyChoice < 0 || enemyChoice >= enemies.size()) {
                return "Bersaglio non valido. Scegli tra 1 e " + enemies.size();
            }

            if (moveSplit[0].equalsIgnoreCase("ATTACCA")) {
                Enemies target = enemies.get(enemyChoice);
                player.attack(target);
                fightLog.append("Hai colpito ").append(target.getName()).append("!\n");

                if (target.isDead()) {
                    fightLog.append("Hai sconfitto ").append(target.getName()).append("!\n");
                    enemies.remove(target);
                    attackOrder.remove(target);

                    if(enemies.isEmpty()){
                        battleOver = true;
                        return fightLog.append("\nLa battaglia è finita! Vittoria!").toString();
                    }
                }

                consecutiveAttacks--;
                if (consecutiveAttacks > 0) {
                    fightLog.append("Puoi attaccare di nuovo (Rimanenti: ").append(consecutiveAttacks).append(")\n");
                    fightLog.append(getBattleReport());
                    return fightLog.toString();
                } else {
                    counterRound++; // Finiti gli attacchi, passa al prossimo
                }
            } else if (moveSplit[0].equalsIgnoreCase("CURATI")) {
                return "LOGICA PER CURARSI NON IMPLEMENTATA";
            } else if (moveSplit[0].equalsIgnoreCase("INVENTARIO")) {
                return "LOGICA PER INVENTARIO NON IMPLEMENTATA";
            } else {
                return "Comando non riconosciuto. Usa 'ATTACCA [numero]' o 'CURATI'.\n" + getBattleReport();
            }
        }

        // 2. TURNO DEI NEMICI (Processa tutti i nemici rimasti nella lista attackOrder)
        // Questo loop continua finché non arriviamo alla fine della lista o torna il turno del player
        while (counterRound < attackOrder.size() && !battleOver) {
            Character enemy = attackOrder.get(counterRound);

            if (enemy instanceof Enemies && !enemy.isDead()) {
                fightLog.append("\nTurno di ").append(enemy.getName()).append("...\n");

                int consecutiveAttack = getConsecutiveAttack(enemy);
                for (int i = 0; i < consecutiveAttack; i++) {
                    enemy.attack(player);
                    fightLog.append("[").append(enemy.getName()).append("] ti attacca!\n");

                    if (player.getHp() <= 0) {
                        battleOver = true;
                        fightLog.append("\nSei morto! GAME OVER.");
                        return fightLog.toString();
                    }
                }
            }
            counterRound++;
        }

        
        if (counterRound >= attackOrder.size()) {
            prepareAttackOrder();
            consecutiveAttacks = getConsecutiveAttack(player);
        }

        fightLog.append(getBattleReport());
        return fightLog.toString();
    }

    public boolean isBattleOver() {
        return battleOver;
    }
}