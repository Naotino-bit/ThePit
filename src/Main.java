import characters.Archer;
import characters.Warrior;
import items.weapons.Sword;


public class Main {
    public static void main(String[] args) {
        Warrior warrior = new Warrior();
        warrior.presentation();
        Sword spadaDiLegno = new Sword("Spada di legno", "Leggendaria", 10);
        spadaDiLegno.getInfo();
        warrior.getInventory();
        warrior.addToInventory(spadaDiLegno);
        warrior.getInventory();
        Archer archer = new Archer();
        warrior.attack(archer);
    }
}