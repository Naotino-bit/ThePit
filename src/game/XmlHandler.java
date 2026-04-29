package game;

import items.Items;
import items.weapons.*;
import items.armors.*; // Fondamentale per vedere le tue 4 sottoclassi!

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHandler {

    public static ArrayList<Items> everyWeapon;
    public static ArrayList<Items> everyArmor;
    public static ArrayList<Items> everyArtifact;
    public static ArrayList<Items> everyItem;

    public static void loadAllItems(){
        everyWeapon = loadWeapons();
        everyArmor = loadArmors();
        everyArtifact = loadArtifacts();

        everyItem = new ArrayList<>();
        if(everyWeapon != null) everyItem.addAll(everyWeapon);
        if(everyArmor != null) everyItem.addAll(everyArmor);
        if(everyArtifact != null) everyItem.addAll(everyArtifact);

    }
    public static ArrayList<Items> loadWeapons() {
        ArrayList<Items> everyWeapon = new ArrayList<>();

        try {
            File fileXML = new File("data/weapons.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fileXML);
            doc.getDocumentElement().normalize();

            NodeList listaNodi = doc.getElementsByTagName("Weapon");

            for (int i = 0; i < listaNodi.getLength(); i++) {
                Node nodo = listaNodi.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    String tipo = elemento.getAttribute("tipo");
                    String nome = elemento.getAttribute("nome");
                    String rarita = elemento.getAttribute("rarita");
                    int dannoBase = Integer.parseInt(elemento.getAttribute("dannoBase"));
                    String stat = elemento.getAttribute("stat");
                    int valStat = Integer.parseInt(elemento.getAttribute("valStat"));

                    switch (tipo) {
                        case "Sword": everyWeapon.add(new Sword(nome, rarita, dannoBase, stat, valStat)); break;
                        case "Dagger": everyWeapon.add(new Dagger(nome, rarita, dannoBase, stat, valStat)); break;
                        case "Bow": everyWeapon.add(new Bow(nome, rarita, dannoBase, stat, valStat)); break;
                        case "Claymore": everyWeapon.add(new Claymore(nome, rarita, dannoBase, stat, valStat)); break;
                        case "Shield": everyWeapon.add(new Shield(nome, rarita, dannoBase, stat, valStat)); break;
                    }
                }
            }
            System.out.println("Caricate " + everyWeapon.size() + " armi dal file XML!");
        } catch (Exception e) {
            System.out.println("Errore nel caricamento del file XML: " + e.getMessage());
            e.printStackTrace();
        }
        return everyWeapon;
    }
    public static ArrayList<Items> loadArmors() {
        ArrayList<Items> everyArmor = new ArrayList<>();

        try {
            File fileXML = new File("data/armors.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fileXML);
            doc.getDocumentElement().normalize();

            NodeList listaNodi = doc.getElementsByTagName("Armor");

            for (int i = 0; i < listaNodi.getLength(); i++) {
                Node nodo = listaNodi.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    String tipo = elemento.getAttribute("tipo");
                    String nome = elemento.getAttribute("nome");
                    String rarita = elemento.getAttribute("rarita");
                    int difesaBase = Integer.parseInt(elemento.getAttribute("difesaBase"));
                    String stat = elemento.getAttribute("stat");
                    int valStat = Integer.parseInt(elemento.getAttribute("valStat"));

                    switch (tipo) {
                        case "Helmet":
                            everyArmor.add(new Helmet(nome, rarita, difesaBase, stat, valStat));
                            break;
                        case "Chestplate":
                            everyArmor.add(new Chestplate(nome, rarita, difesaBase, stat, valStat));
                            break;
                        case "Leggins": // Ho usato il nome che ho visto nel tuo screenshot!
                            everyArmor.add(new Leggins(nome, rarita, difesaBase, stat, valStat));
                            break;
                        case "Boots":
                            everyArmor.add(new Boots(nome, rarita, difesaBase, stat, valStat));
                            break;
                    }
                }
            }
            System.out.println("Caricate " + everyArmor.size() + " armature dal file XML!");
        } catch (Exception e) {
            System.out.println("Errore nel caricamento di armors.xml: " + e.getMessage());
            e.printStackTrace();
        }
        return everyArmor;
    }
    public static ArrayList<Items> loadArtifacts() {
        ArrayList<Items> everyArtifact = new ArrayList<>();

        try {
            File fileXML = new File("data/artifacts.xml"); // Assicurati che il nome combaci con il file!
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fileXML);
            doc.getDocumentElement().normalize();

            // Peschiamo tutti gli artefatti
            NodeList listaNodi = doc.getElementsByTagName("Artefatto");

            for (int i = 0; i < listaNodi.getLength(); i++) {
                Node nodo = listaNodi.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    String tipo = elemento.getAttribute("tipo");
                    String nome = elemento.getAttribute("nome");
                    String rarita = elemento.getAttribute("rarita");
                    String set = elemento.getAttribute("set");

                    Element mainStatNode = (Element) elemento.getElementsByTagName("MainStat").item(0);
                    String mainStat = mainStatNode.getAttribute("stat");
                    int mainStatVal = Integer.parseInt(mainStatNode.getAttribute("valore"));

                    java.util.HashMap<String, Integer> subStatsMap = new java.util.HashMap<>();

                    NodeList subStatsList = elemento.getElementsByTagName("Stat");

                    for (int j = 0; j < subStatsList.getLength(); j++) {
                        Node subNode = subStatsList.item(j);
                        if (subNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element subElement = (Element) subNode;
                            String subStatName = subElement.getAttribute("stat");
                            int subStatVal = Integer.parseInt(subElement.getAttribute("valore"));

                            subStatsMap.put(subStatName, subStatVal);
                        }
                    }

                    switch (tipo) {
                        case "Ring":
                            everyArtifact.add(new items.artefacts.Ring(nome, rarita, set, mainStat, mainStatVal, subStatsMap));
                            break;
                        case "Cloack":
                            everyArtifact.add(new items.artefacts.Cloack(nome, rarita, set, mainStat, mainStatVal, subStatsMap));
                            break;
                        case "Earrings":
                            everyArtifact.add(new items.artefacts.Earrings(nome, rarita, set, mainStat, mainStatVal, subStatsMap));
                            break;
                        case "Necklace":
                            everyArtifact.add(new items.artefacts.Necklace(nome, rarita, set, mainStat, mainStatVal, subStatsMap));
                            break;
                    }
                }
            }
            System.out.println("Caricati " + everyArtifact.size() + " artefatti dal file XML!");

        } catch (Exception e) {
            System.out.println("Errore nel caricamento di artifacts.xml: " + e.getMessage());
            e.printStackTrace();
        }

        return everyArtifact;
    }

    public static Items rollRandomItem(){
        java.util.Random rand = new java.util.Random();

        int roll = rand.nextInt(100) +1;
        String rarity;

        if (roll <= 50) rarity = "Comune";
        else if (roll <= 80) rarity = "Raro";
        else if (roll <= 95) rarity = "Epico";
        else rarity = "Leggendario";

        ArrayList<Items> fullItemPool = new ArrayList<>();
        for (Items item : everyItem) {
            if (item.getRarity().equalsIgnoreCase(rarity)){
                fullItemPool.add(item);
            }
        }

        if(fullItemPool.isEmpty()) return null;
        return fullItemPool.get(rand.nextInt(fullItemPool.size()));


    }

}