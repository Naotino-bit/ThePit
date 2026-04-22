package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Bow;

public class Archer extends Character {
    public Archer() {
        name = "Arciere";
        baseHpMax = 75;
        baseStrength = 30;
        baseAgility = 90; //secondaria
        baseIntelligence = 30;
        basePrecision = 105; //primaria

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
        if(item instanceof Bow){
            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);
        } else {
            super.equip(item);
        }
        updateStats();
    }
}
