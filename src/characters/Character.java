package characters;

import items.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
    protected ArrayList<Items> inventory = new ArrayList<Items>();
    protected HashMap<String, Items> equippedItems = new LinkedHashMap<String, Items>();

    public Character(){
        equippedItems.put("Testa", null);
        equippedItems.put("Torso", null);
        equippedItems.put("Gambe", null);
        equippedItems.put("Piedi", null);
        equippedItems.put("Primaria", null);
        equippedItems.put("Secondaria", null);
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

    public void getInventory () { //SISTEMARLO CON LE EXEPTION
        System.out.println("\n----- Inventario -----");
        if(inventory.size() <= 0) {
            System.out.println("Inventario vuoto");
        } else {
            for (Items item : inventory) {
                System.out.println(item.getDetails());
            }
        }
    }

    public void addToInventory (Items item) {
        inventory.add(item);
    }

    public ArrayList<String> getItemSlot(Items item) {
        return item.getEquippedSlot();
    }

    public void equip(Items item) {
//        if(getItemSlot(item).size()==1){
//            deEquip(getItemSlot(item).get(0));
//            deEquip(getItemSlot(item).get(1));
//            equippedItems.replace(getItemSlot(item).get(0), item);
//        } else if(getItemSlot(item).size()==2) {
//            deEquip(getItemSlot(item).get(0));
//            deEquip(getItemSlot(item).get(1));
//            equippedItems.replace(getItemSlot(item).get(0), item);
//            equippedItems.replace(getItemSlot(item).get(1), item);
//        }


    }

    public void deEquip(String target) {
        equippedItems.replace(target, null);
    }

    public void getEquippedItems() {
        System.out.println("----- Equipaggiamento -----");
        for( String i : equippedItems.keySet()) {
            try {
                System.out.println(i + ": " + equippedItems.get(i).getName());
            } catch (NullPointerException e) {
                System.out.println(i + ": " + "Non equipaggiato");
            }

        }
    }
}