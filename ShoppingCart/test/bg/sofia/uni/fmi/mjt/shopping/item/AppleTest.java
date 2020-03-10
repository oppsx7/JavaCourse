package bg.sofia.uni.fmi.mjt.shopping.item;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AppleTest {
    private static Apple apple;
    @BeforeClass
    public static void setUp() {
        apple = new Apple("milka","white",2);
    }

    @Test
    public void testGetName(){
        assertEquals("milka",apple.getName());
    }
    @Test
    public void testGetDescription(){
        assertEquals("white",apple.getDescription());
    }
    @Test
    public void testGetPrice(){
        assertEquals(2,apple.getPrice(),0.001);
    }
}
