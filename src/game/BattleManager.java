package game;

import characters.enemies.Enemies;
import characters.Character;

public class BattleManager {
    private Character player;
    private Enemies enemy;
    private boolean turnoGiocatore; // da sistemare con il peso in base all'agilità
    private boolean battagliaFinita;

    // Costruttore: l'arbitro sale sul ring con i due lottatori
    public BattleManager(Character player, Enemies enemy) {
        this.player = player;
        this.enemy = enemy;
        this.turnoGiocatore = true; // Magari il giocatore attacca sempre per primo DA LEVARE
        this.battagliaFinita = false;
    }

    public String manageRound(String mossaGiocatore) {
        if (battagliaFinita) return "La battaglia è già finita!";

        String logCombattimento = "";

        // 1. TURNO DEL GIOCATORE
        if (mossaGiocatore.equalsIgnoreCase("ATTACCA")) {
            player.attack(enemy);
            logCombattimento += "Hai colpito " + enemy + "!\n";
        } else if (mossaGiocatore.equalsIgnoreCase("CURATI")) {
            // logica per curarsi...
        } else {
            return "Mossa non valida nel combattimento.";
        }

        // Controllo se il nemico è morto
        if (enemy.getHp() <= 0) {
            battagliaFinita = true;
            return logCombattimento + "Hai sconfitto " + enemy + "!";
        }

        // 2. TURNO DEL NEMICO (Automatico)
        logCombattimento += "Turno del nemico...\n";
        enemy.attack(player);
        logCombattimento += enemy + " ti attacca!\n";

        // Controllo se il giocatore è morto
        if (player.getHp() <= 0) {
            battagliaFinita = true;
            logCombattimento += "Sei morto! GAME OVER.";
        }

        // Restituisco la "telecronaca" del turno al Server
        return "AAAAAAAAAA";//logCombattimento;
    }

    // Un metodo per far sapere a Game se l'arbitro ha finito
    public boolean isBattagliaFinita() {
        return battagliaFinita;
    }
}

