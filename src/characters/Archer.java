package characters;

import items.Items;
import items.weapons.Bow;

public class Archer extends Character{
    public Archer() {
        name = "Arciere";
        hpMax = 75;
        hp = hpMax;
        strength = 30;
        agility = 90; //secondaria
        intelligence = 30;
        precision = 105; //primaria
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
    }
}
