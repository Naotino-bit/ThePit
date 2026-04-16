package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Shield;
import items.weapons.Sword;

public class Warrior extends Character {
    public Warrior(){
        name = "Guerriero";
        hpMax = 120;
        hp = hpMax;
        strength = 90; //primaria
        agility = 85; //secondaria
        intelligence = 30;
        precision = 50;
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
    }
}
