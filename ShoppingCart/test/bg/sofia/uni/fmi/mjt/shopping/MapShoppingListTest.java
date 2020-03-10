package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapShoppingListTest {
    private static MapShoppingCart shoppingCart;

    @Before
    public void setUp() {
        shoppingCart = new MapShoppingCart();
    }

    @Test
    public void testAddNullItem() {
        Item item = null;
        shoppingCart.addItem(item);
        assertNull("Trying to add null item", item);

    }

    @Test
    public void testAddItem() {
        Item item = new Chocolate("milka", "black", 2);
        int size = shoppingCart.getSize();
        shoppingCart.addItem(item);
        assertTrue(size + 1 == shoppingCart.getSize());

    }
}
