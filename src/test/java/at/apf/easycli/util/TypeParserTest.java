package at.apf.easycli.util;

import at.apf.easycli.util.enumeration.Material;
import org.junit.Assert;
import org.junit.Test;

public class TypeParserTest {

    private TypeParser tp = new TypeParser();

    @Test
    public void parseEnum_shouldWork() {
        Assert.assertEquals(Material.WOOD, tp.toEnum(Material.class, "WOOD"));
        Assert.assertEquals(Material.COOPER, tp.toEnum(Material.class, "cooper"));
        Assert.assertEquals(Material.STEEL, tp.toEnum(Material.class, "Steel"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseNotExistingEnum_shouldThrowIllegalArgumentException() {
        tp.toEnum(Material.class, "Water");
    }
}
