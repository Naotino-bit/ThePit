package game;

import characters.enemies.Enemies;
import characters.Character;
import items.Items;
import items.weapons.Sword;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class BattleManager {
    private Character player;
    private ArrayList<Enemies> enemies;
    private boolean turnoGiocatore; // da sistemare con il peso in base all'agilità
    private boolean battagliaFinita;

    // Costruttore: l'arbitro sale sul ring con i due lottatori
    public BattleManager(Character player, ArrayList<Enemies> enemies) {
        this.player = player;
        this.enemies = enemies;
        this.turnoGiocatore = true; // Magari il giocatore attacca sempre per primo DA LEVARE
        this.battagliaFinita = false;
    }

    public String manageRound(String mossaGiocatore) {

        Items oggettoSword = (new Sword("Spada", "speciale", 5, "Forza", 5));
        player.addToInventory(oggettoSword);
        player.equip(oggettoSword);
        if (battagliaFinita) return "La battaglia è già finita!";
        String[] mossaGiocatoreSplitata = mossaGiocatore.split(" ");
        int sceltaNemico;
        try {
            sceltaNemico = Integer.parseInt(mossaGiocatoreSplitata[1]);
        } catch (IndexOutOfBoundsException e) {
            sceltaNemico = 0;
        }

        String logCombattimento = "";

        // 1. TURNO DEL GIOCATORE
        if (mossaGiocatoreSplitata[0].equalsIgnoreCase("ATTACCA")) {
            player.attack(enemies.get(sceltaNemico));
            logCombattimento += "Hai colpito " + enemies.get(sceltaNemico).getName() + "!\n";
        } else if (mossaGiocatore.equalsIgnoreCase("CURATI")) {
            // logica per curarsi...
        } else {
            return "Mossa non valida nel combattimento.";
        }


        // AGGIUNGERE CHE SE C'è GENTE NELL?ARRAY ENEMIS VUOL DIRE CHE LA BATTAGLIA DEVE CONTINUARE
        // OGNI VOLTA CHE UN NEMICO MUORE VIENE RIMOSSO DALL ARRAY ENEMIES
        // Controllo se il nemico è morto
        if (enemies.get(sceltaNemico).getHp() <= 0) {
            battagliaFinita = true;

            return logCombattimento + "Hai sconfitto " + enemies.get(sceltaNemico).getName() + "!";
        }

        //RIFARE TUTTA LOGICA ATTACCA DAI NEMICI

        // 2. TURNO DEL NEMICO (Automatico)
        logCombattimento += "Turno del nemico...\n";
        enemies.get(sceltaNemico).attack(player);
        logCombattimento += enemies.get(sceltaNemico).getName() + " ti attacca!\n";


        // Controllo se il giocatore è morto
        if (player.getHp() <= 0) {
            battagliaFinita = true;
            logCombattimento += "Sei morto! GAME OVER.";
        }
        // Restituisco la "telecronaca" del turno al Server
        //aggiunta di stats player e nemico
        logCombattimento += "\n-----\n" + player.getName() + ": " + player.getHp() + "/" + player.getHpMax() + " hp\n" + enemies.get(sceltaNemico).getName() + ": " + enemies.get(sceltaNemico).getHp() + "/" + enemies.get(sceltaNemico).getHpMax() + " hp\n";

        return logCombattimento;
    }

    // Un metodo per far sapere a Game se l'arbitro ha finito
    public boolean isBattagliaFinita() {
        return battagliaFinita;
    }

}

