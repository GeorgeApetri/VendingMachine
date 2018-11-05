import org.omg.CORBA.INTERNAL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VendingMachine {

    private VMType vmType;
    private Currency currency;
    private Map<Product, Integer> productStock;
    private Map<Coin, Integer> coinstock;

    public VendingMachine(String filePath) {
        initialize(filePath);
    }

    public void displayMenu() {
        System.out.println("This is a " + vmType + " vending machine!");
        System.out.println("Cod\t Produs\t Pret\t Gramaj");
        for (Product product : productStock.keySet()) {
            System.out.println(product.getCod() + "\t " + product.getName() + "\t " + product.getPrice() + "\t\t " + product.getSize());
        }
    }

    public void displayCoinStock() {
        System.out.println("This is the " + currency + " coin stock!");
        System.out.println("Cod\t Valoare");
        for (Coin coin : coinstock.keySet()) {
            System.out.println(coin.getCod() + "\t " + coin.getValue());
        }
    }

    public void deliverProduct(Product product){
        productStock.put(product, productStock.get(product) - 1);
    }

    public Integer insertCoins(Integer productPrice) {
        Integer sum = 0;
        Scanner scanner = new Scanner(System.in);
        int option = 1;
        boolean ok = false;
        while (sum < productPrice) {
            System.out.println("Introdu monezi:");
            ok = false;
            option = scanner.nextInt();
            for (Coin coin : coinstock.keySet()) {
                if (coin.getCod() == option) {
                    coinstock.put(coin, 1 + coinstock.get(coin));
                    sum = sum + coin.getValue();
                    System.out.println("Suma introdusa " + sum + " " + currency);
                    Integer toPay = productPrice - sum;
                    System.out.println("Ramas de introdus " + (toPay > 0 ? toPay : 0) + " " + currency);
                    ok = true;
                }
            }
        }
        return sum;
    }

    public Product buyProduct() {
        System.out.println("Alege un produs: ");
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        boolean ok = false;

        for (Product p : productStock.keySet()) {
            if (p.getCod() == option) {
                Integer quantity = productStock.get(p);
                if (quantity > 0) {
                    ok = true;
                    return p;
                } else {
                    System.out.println("Nu sunt produse suficiente!");
                    break;
                }
            }
        }
        if (ok == false) {
            System.out.println("Optiunea introdusa nu este valida.");
            return this.buyProduct();
        }
        return null;
    }

    public void payRest(Integer rest){
        for (Coin coin: coinstock.keySet()){
            while (coin.getValue() <= rest){
                if (coinstock.get(coin) > 0){
                    System.out.println("Paying rest " + coin.getValue());
                    coinstock.put(coin,coinstock.get(coin) - 1);
                    rest = rest - coin.getValue();
                } else {
                    break;
                }
            }
        }
        if (rest == 0) {
            System.out.println("Rest dat cu succes");
        } else {
            System.out.println("Nu sunt destule monezi pentru rest!");
            System.out.println("Rest ramas: " + rest);
        }
    }

    public void start() {
        while (true) {
            this.displayMenu();
            Product product = this.buyProduct();
            this.displayCoinStock();
            Integer sum = this.insertCoins(product.getPrice());
            this.deliverProduct(product);
            this.payRest(sum - product.getPrice());
        }
    }

    public void initialize(String FilePath) {

        Path path = Paths.get(FilePath);
        List<String> lines = null;

        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        vmType = VMType.valueOf(lines.get(0));
        currency = Currency.valueOf(lines.get(1));

        productStock = new LinkedHashMap<>();
        coinstock = new LinkedHashMap<>();

        Integer nrProducts = Integer.valueOf(lines.get(2));
        for (int i = 3; i < 3 + nrProducts; i++) {
            String line = lines.get(i);
            String[] parts = line.split(" ");
            Product product = new Product(parts[0], Integer.valueOf(parts[1]), Integer.valueOf(parts[2]));
            product.setCod(i);
            productStock.put(product, Integer.valueOf(parts[3]));
        }

        Integer currencyLineIndex = 3 + nrProducts;
        Integer nrCoins = Integer.valueOf(lines.get(currencyLineIndex));
        for (int i = currencyLineIndex + 1; i < currencyLineIndex + 1 + nrCoins; i++) {
            String line = lines.get(i);
            String[] parts = line.split(" ");
            Coin coin = new Coin(Integer.valueOf(parts[0]), i);
            coinstock.put(coin, Integer.valueOf(parts[1]));
        }
    }


}


