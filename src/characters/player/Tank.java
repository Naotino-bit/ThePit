package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Claymore;

public class Tank extends Character {
    public Tank() {
        name = "Tank";
        baseHpMax = 150;
        baseStrength = 105; //primaria
        baseAgility = 40;
        baseIntelligence = 70; //secondaria
        basePrecision = 40;

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
        if(item instanceof Claymore){
            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);
        } else {
            super.equip(item);
        }
        updateStats();
    }
}
