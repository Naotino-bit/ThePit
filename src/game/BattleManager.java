package game;

import characters.enemies.Enemies;
import characters.Character;

import java.util.ArrayList;
import java.util.Random;

public class BattleManager {
    private Character player;
    private ArrayList<Enemies> enemies;
    private boolean battagliaFinita;

    // Gestione turni avanzata
    private ArrayList<Character> ordineAttacchi;
    private int consecutiveAttacks;
    private int counterRound = 0;
    Random rand = new Random();

    // Costruttore
    public BattleManager(Character player, ArrayList<Enemies> enemies) {
        this.player = player;
        this.enemies = enemies;
        this.battagliaFinita = false;
        preparaOrdineAttacchi();
        this.consecutiveAttacks = getConsecutiveAttack(player);
    }

    private void preparaOrdineAttacchi() {
        // SOLO PER DEBUG DA AGGIORNARE QUANDO SI AVRà L'agilità
        ordineAttacchi = new ArrayList<>();
        ordineAttacchi.add(player);
        for(Enemies enemy: enemies){
            ordineAttacchi.add(enemy);
        }
        counterRound = 0;
    }

    public int getConsecutiveAttack(Character attacker) {
        // viene passato in input chi sta attaccando e in base
        // alla formula: round(agilitàAttacker/minAgilitàBattaglia)
        // restitusice N attacchi consecutivi
        return 2; // DEBUG: 2 attacchi per tutti
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

    public String manageRound(String mossaGiocatore) {
        if (battagliaFinita) return "La battaglia è già finita!";

        StringBuilder logCombattimento = new StringBuilder();

        // 1. TURNO DEL GIOCATORE
        if (ordineAttacchi.get(counterRound) == player) {
            String[] mossaSplit = mossaGiocatore.split(" ");
            int sceltaNemico = 0;

            try {
                if (mossaSplit.length > 1) {
                    sceltaNemico = Integer.parseInt(mossaSplit[1]) - 1;
                }
            } catch (NumberFormatException e) { sceltaNemico = 0; }

            if (sceltaNemico < 0 || sceltaNemico >= enemies.size()) {
                return "Bersaglio non valido. Scegli tra 1 e " + enemies.size();
            }

            if (mossaSplit[0].equalsIgnoreCase("ATTACCA")) {
                Enemies target = enemies.get(sceltaNemico);
                player.attack(target);
                logCombattimento.append("Hai colpito ").append(target.getName()).append("!\n");

                if (target.isDead()) {
                    logCombattimento.append("Hai sconfitto ").append(target.getName()).append("!\n");
                    enemies.remove(target);
                    ordineAttacchi.remove(target);

                    if(enemies.isEmpty()){
                        battagliaFinita = true;
                        return logCombattimento.append("\nLa battaglia è finita! Vittoria!").toString();
                    }
                }

                consecutiveAttacks--;
                if (consecutiveAttacks > 0) {
                    logCombattimento.append("Puoi attaccare di nuovo (Rimanenti: ").append(consecutiveAttacks).append(")\n");
                    logCombattimento.append(getBattleReport());
                    return logCombattimento.toString();
                } else {
                    counterRound++; // Finiti gli attacchi, passa al prossimo
                }
            } else if (mossaSplit[0].equalsIgnoreCase("CURATI")) {
                return "LOGICA PER CURARSI NON IMPLEMENTATA";
            } else if (mossaSplit[0].equalsIgnoreCase("INVENTARIO")) {
                return "LOGICA PER INVENTARIO NON IMPLEMENTATA";
            } else {
                return "Comando non riconosciuto. Usa 'ATTACCA [numero]' o 'CURATI'.\n" + getBattleReport();
            }
        }

        // 2. TURNO DEI NEMICI (Processa tutti i nemici rimasti nella lista ordineAttacchi)
        // Questo loop continua finché non arriviamo alla fine della lista o torna il turno del player
        while (counterRound < ordineAttacchi.size() && !battagliaFinita) {
            Character attuante = ordineAttacchi.get(counterRound);

            if (attuante instanceof Enemies && !attuante.isDead()) {
                logCombattimento.append("\nTurno di ").append(attuante.getName()).append("...\n");

                int attacchiN = getConsecutiveAttack(attuante);
                for (int i = 0; i < attacchiN; i++) {
                    attuante.attack(player);
                    logCombattimento.append("[").append(attuante.getName()).append("] ti attacca!\n");

                    if (player.getHp() <= 0) {
                        battagliaFinita = true;
                        logCombattimento.append("\nSei morto! GAME OVER.");
                        return logCombattimento.toString();
                    }
                }
            }
            counterRound++;
        }

        
        if (counterRound >= ordineAttacchi.size()) {
            preparaOrdineAttacchi();
            consecutiveAttacks = getConsecutiveAttack(player);
        }

        logCombattimento.append(getBattleReport());
        return logCombattimento.toString();
    }

    public boolean isBattagliaFinita() {
        return battagliaFinita;
    }
}