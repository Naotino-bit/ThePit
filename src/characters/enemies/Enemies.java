package characters.enemies;

import characters.Character;
import items.Items;

import java.util.ArrayList;

public class Enemies extends Character {
    protected ArrayList<Items> possibleDrops;

    public Enemies() {

        possibleDrops = new ArrayList<Items>();
    }

    @Override
    public boolean canEquipWeapon(Items item) {return false;}
}
