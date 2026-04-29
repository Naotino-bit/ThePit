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
        this.baseAgility = 10; //MESSA PER DEBUG A 10 //TODO definire le stats dei nemici
        this.baseStrength = 50;
        this.baseHpMax = 100; //*livello dello zombie




        this.updateStats();
        this.totalHp = this.totalHpMax;

       this.generateLoot(2);
    }

    //metodo per morte del npc
}
