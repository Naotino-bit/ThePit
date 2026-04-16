import characters.enemies.Zombie;
import characters.player.Archer;
import characters.player.Warrior;
import items.Items;
import items.armors.Boots;
import items.artefacts.Necklace;
import items.weapons.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    private Warrior player1;
    private Archer player2;
    private Sword spada1;
    private Bow arco1;
    private Claymore claymore1;
    private Necklace collana;
    private Boots stivali;
    private Shield sudo;
    private Staff staffa;
    private Zombie zombie1;

    public Game() {
        this.player1 = new Warrior();

        this.spada1 = new Sword("Debug sword", "*****", 30, "");
        this.zombie1 = new Zombie();
    }

    public void startMatch() {
        System.out.println("Inizio partita!");

        zombie1.getHp();
        player1.addToInventory(spada1);
        player1.addToInventory(spada1);
        player1.getInventory();
        player1.attack(zombie1);
        player1.attack(zombie1);
        player1.attack(zombie1);
        player1.attack(zombie1);
        player1.attack(zombie1);
        System.out.println("Hai trovato: " + zombie1.getDrop().getName()); // da sistemare e aggiungere la possibilità di rifiutare il loot
        zombie1.getHp();

    }

}
