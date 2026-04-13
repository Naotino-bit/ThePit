import characters.Archer;
import characters.Warrior;
import items.weapons.Bow;
import items.weapons.Claymore;
import items.weapons.Sword;

public class Game {
    private Warrior player1;
    private Archer player2;
    private Sword spada1;
    private Bow arco1;
    private Claymore claymore1;

    public Game() {
        this.player1 = new Warrior();
        this.player2 = new Archer();

        this.spada1 = new Sword("Debug sword", "*****", 30, "");
        this.arco1 = new Bow("Arco fenomenale", "indefinita", 25);
        this.claymore1 = new Claymore("Spada pesce", "Ultrasonic legend", 250, "");
    }

    public void startMatch() {
        System.out.println("Inizio partita!");
        player1.addToInventory(spada1);
        player1.getInventory();
        player1.equip(claymore1);
        player1.equip(spada1);
        player2.equip(spada1);
        player1.getEquippedItems();
        player2.getHp();
        player1.attack(player2);
        player2.getHp();
    }

}
