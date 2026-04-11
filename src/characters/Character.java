package characters;

import items.Items;

public abstract class Character {
    protected String name;  //classe
    protected int hpMax;
    protected int hp;
    protected int strength; //CRIT DMG Multiplier
    protected int agility; //Precedenza turno, probabilità schivata
    protected int intelligence; //Quantità, velocità recupero mana
    protected int precision; //CRIT RATE Multiplier
    // ↓↓↓↓ stats che variano in base all'equipaggiamento ↓↓↓↓
    protected int damage = 2;
    protected int defence = 0;
    protected int money = 0;
    protected Items[] inventario;
    public Character(){
    }

    public void takeDamage(int receivedDamage) {
        //aggiungere calcolo difese
        hp -= receivedDamage;
    }

    public void attack(Character target) {
        //aggiungere metodo per calcolare il danno in base a armi e oggetti
        target.takeDamage(damage);
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void getHp() {
        System.out.println(hp);
    }
    public void presentation(){
        System.out.println("\n-----STATS-----\n" + "Classe: " + name + "\nPunti vita: " + hpMax + "\nForza: " + strength + "\nAgilità: " + agility + "\nIntelligenza: " + intelligence + "\nPrecisione: " + precision);
    }
}