package game;

import characters.enemies.Zombie;
import characters.player.Warrior;

public class Game {
    private Warrior player1;
    private BattleManager scontroAttuale;

    public Game() {
        this.player1 = new Warrior();
        this.scontroAttuale = null; // All'inizio non stai combattendo
    }

    public Object processCommand(String comando){
        if(comando.equalsIgnoreCase("spawn zombie")){
            Zombie zombie = new Zombie();
            this.scontroAttuale = new BattleManager(player1, zombie);
            return "E' apparso uno zombie!";

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
