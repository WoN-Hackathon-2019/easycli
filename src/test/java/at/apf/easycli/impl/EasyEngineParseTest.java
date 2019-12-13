package at.apf.easycli.impl;

import at.apf.easycli.CliEngine;
import at.apf.easycli.annotation.Command;
import at.apf.easycli.annotation.DefaultValue;
import at.apf.easycli.annotation.Optional;
import at.apf.easycli.impl.util.MutableContainer;
import org.junit.Assert;
import org.junit.Test;

public class EasyEngineParseTest {

    private CliEngine engine = new EasyEngine();

    @Test
    public void parseWithoutParams_shouldWork() throws Exception {
        MutableContainer<Boolean> container = new MutableContainer<>(false);
        engine.add(new Object(){
            @Command("/bla")
            void bla() {
                container.setValue(true);
            }
        });
        engine.parse("/bla");
        Assert.assertTrue(container.getValue());
    }

    @Test
    public void parseSingleParam_shouldWork() throws Exception {
        MutableContainer<Integer> container = new MutableContainer<>(0);
        engine.add(new Object(){
            @Command("/bla")
            void bla(int a) {
                container.setValue(a);
            }
        });
        engine.parse("/bla 5");
        Assert.assertEquals(5, container.getValue().intValue());
    }

    @Test
    public void parseWithOptional_shouldGiveZero() throws Exception {
        MutableContainer<Integer> container = new MutableContainer<>(1);
        engine.add(new Object(){
            @Command("/bla")
            void bla(@Optional int a) {
                container.setValue(a);
            }
        });
        engine.parse("/bla");
        Assert.assertEquals(0, container.getValue().intValue());
    }

    @Test
    public void parseWithDefaultValue_shouldGiveZero() throws Exception {
        MutableContainer<Integer> container = new MutableContainer<>(1);
        engine.add(new Object(){
            @Command("/bla")
            void bla(@DefaultValue("5") int a) {
                container.setValue(a);
            }
        });
        engine.parse("/bla");
        Assert.assertEquals(5, container.getValue().intValue());
    }

    @Test
    public void parseAllTypes_shouldGiveZero() throws Exception {
        engine.add(new Object(){
            @Command("/bla")
            void bla(int a, long b, float c, double d, boolean e, String f) {
                Assert.assertEquals(1, a);
                Assert.assertEquals(10000000000L, b);
                Assert.assertEquals(1.1, c, 0.0001);
                Assert.assertEquals(-4.543, d, 0.0000001);
                Assert.assertTrue(e);
                Assert.assertEquals("jaja", f);
            }
        });
        engine.parse("/bla 1 10000000000 1.1 -4.543 true jaja");
    }


}
