package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Shield;
import items.weapons.Sword;

public class Warrior extends Character {
    public Warrior(){
        name = "Guerriero";
        baseHpMax = 120;
        baseStrength = 90; //primaria
        baseAgility = 85; //secondaria
        baseIntelligence = 30;
        basePrecision = 50;

        updateStats();
        this.totalHp = this.totalHpMax;
    }

    @Override
    public void equip(Items item) {
        if(!inInventory(item)) {
            System.out.println("Non hai questo oggetto nell'inventario");
            return;
        }
        inventory.remove(item);
        if(item instanceof Sword){
            equippedItems.replace("Primaria", item);
        } else if (item instanceof Shield) {
            equippedItems.replace("Secondaria", item);
        } else {
            super.equip(item);
        }
        updateStats();
    }
}
