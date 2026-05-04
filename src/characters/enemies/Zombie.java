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
        this.baseHpMax = 1000; // scala sul livello del player
        this.baseAgility = 10; //MESSA PER DEBUG A 10 TODO definire le stats dei nemici
        this.baseStrength = 5;
        this.baseIntelligence = 0;
        this.basePrecision = 15;
        


        //OGNI TIPO DI DROP
        this.expReward = 50; //exp droppata
        this.minMoneyDrop = 15;
        this.maxMoneyDrop = 30;
        this.generateLoot(2);


        this.updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;


    }

    //metodo per morte del npc
}
