import characters.Archer;
import characters.Warrior;

public class Main {
    public static void main(String[] args) {
        Warrior warrior = new Warrior();
        warrior.presentation();

        Archer archer = new Archer();
        warrior.attack(archer);
        archer.getHp();
    }
}