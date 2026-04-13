package characters;

import items.Items;

public class Mage extends Character{
    public Mage() {
        name = "Mago";
        hpMax = 75;
        hp = hpMax;
        strength = 30;
        agility = 30;
        intelligence = 115; //primaria
        precision = 80; //secondaria
    }

    @Override
    public void equip(Items item) {
        String classe = item.getClass().toString();
        if(classe.contains("Staff")){
            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);
        } else {
            System.out.println("Non puoi equipaggiare " + item.getName());
        }
    }
}
