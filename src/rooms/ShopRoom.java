package rooms;

import characters.Character;
import items.Items;
import game.XmlHandler;

import java.util.ArrayList;
import java.util.Random;

public class ShopRoom extends Room {
    
    private ArrayList<Items> stock;

    public ShopRoom() {
        this.roomName = "Negozio";
        this.stock = new ArrayList<>();
        Random rand = new Random();
        int numItems = rand.nextInt(3) + 3; // Da 3 a 5 oggetti

        // Generiamo gli oggetti, assicurandoci che non siano null
        while (stock.size() < numItems) {
            Items item = XmlHandler.rollRandomItem();
            if (item != null) {
                stock.add(item);
            }
        }
    }

    @Override
    public String enterRoom(Character player) {
        StringBuilder welcome = new StringBuilder();
        welcome.append("🛒 Sei entrato in un Negozio.\n");
        welcome.append("Un mercante ti saluta: 'Benvenuto! Dai un'occhiata alla mia merce.'\n");
        welcome.append("Le tue monete: ").append(player.getMoney()).append("\n\n");
        welcome.append(getStockList());
        welcome.append("\nComandi: 'COMPRA [numero]', 'VENDI [numero_zaino]', 'INVENTARIO', 'ESCI'");
        return welcome.toString();
    }

    private String getStockList() {
        if (stock.isEmpty()) {
            return "Il mercante ha finito la merce!\n";
        }
        StringBuilder list = new StringBuilder("--- MERCE IN VENDITA ---\n");
        for (int i = 0; i < stock.size(); i++) {
            Items item = stock.get(i);
            list.append("[").append(i + 1).append("] ").append(item.getName())
                .append(" (").append(item.getRarity()).append(") - ")
                .append(item.getDetails())
                .append(" | Prezzo: ").append(item.getEconomicValue() * 2).append(" monete\n");
                // Moltiplichiamo il prezzo per 2 per il costo di acquisto
        }
        return list.toString();
    }

    @Override
    public String processCommand(String command, Character player) {
        String[] parts = command.split(" ");
        String action = parts[0].toUpperCase();

        if (action.equals("ESCI")) {
            this.isCleared = true;
            return "Hai salutato il mercante e sei uscito dal Negozio.";
        }

        if (action.equals("INVENTARIO")) {
            ArrayList<Items> inv = player.getInventory();
            if (inv.isEmpty()) {
                return "Il tuo zaino è vuoto.";
            }
            StringBuilder invStr = new StringBuilder("--- IL TUO ZAINO (da vendere) ---\n");
            for (int i = 0; i < inv.size(); i++) {
                Items item = inv.get(i);
                invStr.append("[").append(i + 1).append("] ").append(item.getName())
                      .append(" | Valore di vendita: ").append(item.getEconomicValue()).append(" monete\n");
            }
            return invStr.toString();
        }

        if (action.equals("COMPRA")) {
            if (parts.length < 2) {
                return "Devi specificare l'oggetto. (Es: COMPRA 1)\n\n" + getStockList();
            }
            try {
                int index = Integer.parseInt(parts[1]) - 1;
                if (index < 0 || index >= stock.size()) {
                    return "Oggetto non valido.";
                }

                Items itemToBuy = stock.get(index);
                int cost = itemToBuy.getEconomicValue() * 2; // Prezzo gonfiato per l'acquisto

                if (player.getMoney() >= cost) {
                    if (player.inventoryFull()) {
                        return "Il tuo zaino è pieno! Non puoi comprare altro.";
                    }
                    player.spendMoney(cost); // spende i soldi (che ha sicuramente)
                    player.addToInventory(itemToBuy);
                    stock.remove(index);
                    return "Hai comprato " + itemToBuy.getName() + " per " + cost + " monete!\nMonete rimanenti: " + player.getMoney() + "\n" + getStockList();
                } else {
                    return "Non hai abbastanza monete! Ti servono " + cost + " monete, ma ne hai " + player.getMoney() + ".";
                }
            } catch (NumberFormatException e) {
                return "Indice non valido. Usa 'COMPRA [numero]'.";
            }
        }

        if (action.equals("VENDI")) {
            if (parts.length < 2) {
                return "Devi specificare l'indice dell'oggetto nel tuo zaino. Usa 'INVENTARIO' per vederli. (Es: VENDI 1)";
            }
            try {
                int index = Integer.parseInt(parts[1]) - 1;
                ArrayList<Items> inv = player.getInventory();

                if (index < 0 || index >= inv.size()) {
                    return "Oggetto nello zaino non valido.";
                }

                Items itemToSell = inv.get(index);
                int sellValue = itemToSell.getEconomicValue(); // Il prezzo base come valore di vendita
                
                player.removeFromInventory(itemToSell);
                player.gainMoney(sellValue);

                return "Hai venduto " + itemToSell.getName() + " al mercante per " + sellValue + " monete!\nOra hai " + player.getMoney() + " monete.";
            } catch (NumberFormatException e) {
                return "Indice non valido. Usa 'VENDI [numero_zaino]'.";
            }
        }

        return "Comando non riconosciuto nel Negozio. Usa 'COMPRA [n]', 'VENDI [n]', 'INVENTARIO' o 'ESCI'.";
    }
}
