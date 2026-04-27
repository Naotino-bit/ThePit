package characters.enemies;

import characters.Character;
import items.Items;
import items.weapons.Shield;
import items.weapons.Sword;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

public class Zombie extends Enemies {
    public Zombie() {
        this.name = "Zombie";
        this.totalStrength = 5;
        this.totalAgility = 100; //MESSA PER DEBUG A 10
        this.totalHpMax = 100; //*livello dello zombie
        this.totalHp = this.totalHpMax;

        possibleDrops.add(new Sword("Spada di legno", "Comune", 10, "Forza", 10));
        possibleDrops.add(new Shield("Scudo di legno", "Comune", 10, "Undefined", 0));
    }

    public Items getDrop () {
        Random rand = new Random();
        int randomInt = rand.nextInt(2);
        return possibleDrops.get(randomInt);
    }

    //metodo per morte del npc
}
