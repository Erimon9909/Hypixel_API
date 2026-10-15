import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.BorderLayout;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HypixelApiApp{
    String rawJson = fetchBazaar();
    public static void main(String[] args){

        Scanner input = new Scanner(System.in);
        String rawJson = fetchBazaar();

        createFrame(formatList(getProductNames(rawJson)), rawJson);
        
        
    }

    public static List<String> formatList(List<String> items){
        return items.stream().map(item -> {
            if (item == null || item.isBlank()){
                return "";
            }
            return Arrays.stream(item.split("_")).map(word -> word.isEmpty() ? "" :
            word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
            .collect(Collectors.joining(" "));
        }).toList();
    }

    public static void createFrame(List<String> formattedList, String rawJson){
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        panel.setLayout(null);
        JList<String> list = new JList<>(formattedList.toArray(new String[0]));
        JScrollPane scrollPane = new JScrollPane(list);
        JLabel label = new JLabel();
        JTextArea buyInfo = new JTextArea();
        JTextArea sellInfo = new JTextArea();

        JButton button = new JButton("Check Price");
        button.addActionListener(e -> {
            String selectedItem = list.getSelectedValue();

            if(selectedItem != null){
                String itemId = selectedItem.toUpperCase().replace(" ", "_");
                buyInfo.setText(printBuyInfo(rawJson, itemId));
                sellInfo.setText(printSellInfo(rawJson, itemId));
            }
        });
        panel.setSize(1000, 200);
        button.setBounds(0,450,150, 40);
        scrollPane.setBounds(0,0, 500,450);
        buyInfo.setBounds(500, 0, 350, 40);
        sellInfo.setBounds(500, 100, 350, 40);

        panel.add(button);
        panel.add(scrollPane);
        panel.add(buyInfo);
        panel.add(sellInfo);
        frame.add(panel);

        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static String fetchBazaar(){
    HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    HttpRequest req = HttpRequest.newBuilder().uri(URI.create("https://api.hypixel.net/skyblock/bazaar")).header("Accept", "application/json").GET().build();

    try{
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200){
            return response.body();
        }else{
            System.err.println("HTTP error code: " + response.statusCode());
        }
    }catch(Exception e){
        System.err.println("Network error: " + e.getMessage());
    }

    return null;
}

public static double getBuyPrice(String jsonBody, String itemId){
    JsonObject root = JsonParser.parseString(jsonBody).getAsJsonObject();

    if(!root.has("products")){
        System.err.println("Missing 'product' key.");
        return 0;
    }

    JsonObject products = root.getAsJsonObject("products");
    if(!products.has(itemId)){
        System.err.println("Item: " + itemId + " not found!");
        return 0;
    }
    JsonObject itemData = products.getAsJsonObject(itemId);

    JsonObject quickStatus = itemData.getAsJsonObject("quick_status");
    double buyPrice = quickStatus.get("buyPrice").getAsDouble();

    return buyPrice;
}

public static double getSellPrice(String jsonBody, String itemId){
    JsonObject root = JsonParser.parseString(jsonBody).getAsJsonObject();

    JsonObject products = root.getAsJsonObject("products");
    JsonObject itemData = products.getAsJsonObject(itemId);

    JsonObject quickstatus = itemData.getAsJsonObject("quick_status");
    double sellPrice = quickstatus.get("sellPrice").getAsDouble();

    return sellPrice;
}

public static int getBuyVolume(String jsonBody, String itemId){
    JsonObject root = JsonParser.parseString(jsonBody).getAsJsonObject();

    JsonObject products = root.getAsJsonObject("products");
    JsonObject itemData = products.getAsJsonObject(itemId);

    JsonObject quickstatus = itemData.getAsJsonObject("quick_status");
    int buyVolume = quickstatus.get("buyVolume").getAsInt();

    return buyVolume;
}

public static int getSellVolume(String jsonBody, String itemId){
    JsonObject root = JsonParser.parseString(jsonBody).getAsJsonObject();

    JsonObject products = root.getAsJsonObject("products");
    JsonObject itemData = products.getAsJsonObject(itemId);

    JsonObject quickstatus = itemData.getAsJsonObject("quick_status");
    int sellVolume = quickstatus.get("sellVolume").getAsInt();

    return sellVolume;
}

public static int buyOrderCount(String jsonBody, String itemId){
    JsonObject root = JsonParser.parseString(jsonBody).getAsJsonObject();

    JsonObject products = root.getAsJsonObject("products");
    JsonObject itemData = products.getAsJsonObject(itemId);

    JsonObject quickstatus = itemData.getAsJsonObject("quick_status");
    int orderCount = quickstatus.get("buyOrders").getAsInt();

    return orderCount;
}

public static int sellOrderCount(String jsonBody, String itemId){
    JsonObject root = JsonParser.parseString(jsonBody).getAsJsonObject();

    JsonObject products = root.getAsJsonObject("products");
    JsonObject itemData = products.getAsJsonObject(itemId);

    JsonObject quickstatus = itemData.getAsJsonObject("quick_status");
    int orderCount = quickstatus.get("sellOrders").getAsInt();

    return orderCount;
}

public static String printBuyInfo(String rawJson, String itemId){
    double buyPrice = getBuyPrice(rawJson, itemId);
    double sellOrders = sellOrderCount(rawJson, itemId);
    int buyVolume = getBuyVolume(rawJson, itemId);

    return String.format("Buy: %.2f | Volume: %d | Orders: %.2f", buyPrice, buyVolume, sellOrders);
}

public static String printSellInfo(String rawJson, String itemId){
    double sellPrice = getSellPrice(rawJson, itemId);
    double buyOrder = buyOrderCount(rawJson, itemId);
    int sellVolume = getSellVolume(rawJson, itemId);

    return String.format("Sell: %.2f | Volume: %d, Orders: %.2f", sellPrice, sellVolume, buyOrder);
}

public static List<String> getProductNames(String jsonBody){
    List<String> productIds = new ArrayList<>();
    JsonObject root = JsonParser.parseString(jsonBody).getAsJsonObject();

    if(root.has("products")){
        JsonObject products = root.getAsJsonObject("products");

        for(String key : products.keySet()){
            productIds.add(key);
        }
    }

    Collections.sort(productIds);
    return productIds;
}


}



