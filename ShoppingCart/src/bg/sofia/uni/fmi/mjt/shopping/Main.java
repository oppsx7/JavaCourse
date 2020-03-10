package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;

public class Main {

    public static void main(String[] args) {
        MapShoppingCart mapShoppingCart = new MapShoppingCart();
        Chocolate chocolate1 = new Chocolate("Milka","white",2);
        Chocolate chocolate2 = new Chocolate("Milka","white",2);
        mapShoppingCart.addItem(chocolate1);
        mapShoppingCart.addItem(chocolate2);
        System.out.println(chocolate1.equals(chocolate2));
    }

}
