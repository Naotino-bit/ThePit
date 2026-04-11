package items;

public abstract class Items {
    protected String name;
    protected String rarity;
    protected int damage;

    public String getName() {
        return this.name;
    }
    public String getRarity() {
        return this.rarity;
    }
    public int getDamage() {
        return this.damage;
    }
}