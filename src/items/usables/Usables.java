package items.usables;
import characters.Character;
import items.Items;
import java.util.ArrayList;

public abstract class Usables extends Items {

    public Usables(String name, String rarity, String stat, int value, int economicValue){
        this.name = name;
        this.rarity = rarity;
        this.boostedStat = stat;
        this.boostedStatVal = value;
        this.stat = 0;
        this.economicValue = economicValue;
    }

    public abstract String use(Character caster, Character target);

    @Override
    public ArrayList<String> getEquippedSlot(){
        ArrayList<String> temp = new ArrayList<>();
        temp.add("Inventario");
        return temp;
    }
}
