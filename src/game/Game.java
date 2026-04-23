package game;

import characters.enemies.Enemies;
import characters.enemies.Zombie;
import characters.player.Warrior;

import java.util.ArrayList;

public class Game {
    private Warrior player1;
    private BattleManager scontroAttuale;

    public Game() {
        this.player1 = new Warrior();
        this.scontroAttuale = null; // All'inizio non stai combattendo
    }

    public String getEnemies(ArrayList<Enemies> enemies) {
        String string = "E' apparso un nemico: \n";
        if(enemies.size()>1) {
            string = "Sono apparsi dei nemici: \n";
        }
        for(int i=0; i<enemies.size(); i++) {
            string += "[" + (i+1) + "] - " + enemies.get(i).getName() + " " + enemies.get(i).getHp() + "/" + enemies.get(i).getHpMax()+ " hp\n";
        }
        return string;
    }

    public Object processCommand(String comando){
        if(comando.equalsIgnoreCase("spawn zombie")){
            ArrayList<Enemies> enemies = new ArrayList<Enemies>();
            enemies.add(new Zombie());
            enemies.add(new Zombie());
            this.scontroAttuale = new BattleManager(player1, enemies);
            return getEnemies(enemies);

        }

        // SMISTAMENTO: Sto combattendo o no?
        if (scontroAttuale != null && !scontroAttuale.isBattagliaFinita()) {

            // SE STO COMBATTENDO: Passo il comando all'arbitro!
            String risultato = scontroAttuale.manageRound(comando);

            // Se la battaglia è finita, "licenzio" l'arbitro
            if (scontroAttuale.isBattagliaFinita()) {
                scontroAttuale = null;
            }
            return risultato;

        } else {

            // SE NON STO COMBATTENDO: Gestisco esplorazione, inventario, ecc.
            if (comando.equalsIgnoreCase("INVENTARIO")) {
                return "\n----- Inventario -----\n" + player1.getInventory();
            }
        }

        return "Hai a dispozione i seguenti comandi: .....";
    }

}
