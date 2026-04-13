package characters;

import items.Items;

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
        String classe = item.getClass().toString();
        if(classe.contains("Bow")){
            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);
        } else {
            System.out.println("Non puoi equipaggiare " + item.getName());
        }
    }
}
