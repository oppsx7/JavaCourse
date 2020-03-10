package bg.sofia.uni.fmi.mjt.shopping.item;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChocolateTest {
    private static Chocolate chocolate;
    @BeforeClass
    public static void setUp() {
        chocolate = new Chocolate("milka","white",2);
    }

    @Test
    public void testGetName(){
        assertEquals("milka",chocolate.getName());
    }
    @Test
    public void testGetDescription(){
        assertEquals("white",chocolate.getDescription());
    }
    @Test
    public void testGetPrice(){
        assertEquals(2,chocolate.getPrice(),0.001);
    }
}
