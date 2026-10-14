import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class HypixelApiApp{

    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        String rawJson = fetchBazaar();
        System.out.println(getProductNames(rawJson));
        String itemId = input.next();

        
        if(rawJson != null){
            printBuyInfo(rawJson, itemId);
            printSellInfo(rawJson, itemId);
        }
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

public static void printBuyInfo(String rawJson, String itemId){
    System.out.printf("Buy Price: %.2f", getBuyPrice(rawJson, itemId));
    System.out.print(" | Volume: " + getBuyVolume(rawJson, itemId));
    System.out.println(" | Orders: " + buyOrderCount(rawJson, itemId));
}

public static void printSellInfo(String rawJson, String itemId){
    System.out.printf("Sell price: %.2f", getSellPrice(rawJson, itemId));
    System.out.print(" | Volume: " + getSellVolume(rawJson, itemId));
    System.out.println(" | Orders: " + sellOrderCount(rawJson, itemId));
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



