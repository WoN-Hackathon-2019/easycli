package at.apf.easycli.impl;

import at.apf.easycli.CliEngine;
import at.apf.easycli.annotation.Command;
import at.apf.easycli.annotation.DefaultValue;
import at.apf.easycli.annotation.Flag;
import at.apf.easycli.annotation.Optional;
import at.apf.easycli.exception.CommandNotFoundException;
import at.apf.easycli.exception.MalformedCommandException;
import at.apf.easycli.impl.util.MutableContainer;
import org.junit.Assert;
import org.junit.Test;

public class EasyEngineParseTest {

    private CliEngine engine = new EasyEngine();

    @Test
    public void parseWithoutParams_shouldWork() throws Exception {
        MutableContainer<Boolean> container = new MutableContainer<>(false);
        engine.register(new Object(){
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
        engine.register(new Object(){
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
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Optional int a) {
                container.setValue(a);
            }
        });
        engine.parse("/bla");
        Assert.assertEquals(0, container.getValue().intValue());
    }

    @Test
    public void parseWithDefaultValue_shouldGiveFive() throws Exception {
        MutableContainer<Integer> container = new MutableContainer<>(1);
        engine.register(new Object(){
            @Command("/bla")
            void bla(@DefaultValue("5") int a) {
                container.setValue(a);
            }
        });
        engine.parse("/bla");
        Assert.assertEquals(5, container.getValue().intValue());
    }

    @Test
    public void parseAllTypes_shouldWork() throws Exception {
        engine.register(new Object(){
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

    @Test
    public void parseWithArray_shouldWork() throws Exception {
        MutableContainer<Integer> container = new MutableContainer<>(0);
        engine.register(new Object(){
            @Command("/bla")
            void bla(int[] arr) {
                for (int a: arr) {
                    container.setValue(container.getValue() + a);
                }
            }
        });
        engine.parse("/bla 1 2 3 4 5");
        Assert.assertEquals(15, container.getValue().intValue());
    }

    @Test
    public void parseWithSingleElementArray_shouldWork() throws Exception {
        MutableContainer<Integer> container = new MutableContainer<>(0);
        engine.register(new Object(){
            @Command("/bla")
            void bla(int[] arr) {
                for (int a: arr) {
                    container.setValue(container.getValue() + a);
                }
            }
        });
        engine.parse("/bla 1");
        Assert.assertEquals(1, container.getValue().intValue());
    }

    @Test
    public void parseWithFlag_shouldWork() throws Exception {
        MutableContainer<Boolean> container = new MutableContainer<>(false);
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Flag('c') boolean b) {
                container.setValue(b);
            }
        });
        engine.parse("/bla -c");
        Assert.assertTrue(container.getValue());
    }

    @Test
    public void parseWithMultipleFlags_shouldWork() throws Exception {
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Flag('c') boolean c, @Flag('d') boolean d, @Flag('e') boolean e) {
                Assert.assertTrue(c);
                Assert.assertFalse(d);
                Assert.assertTrue(e);
            }
        });
        engine.parse("/bla -ce");
    }

    @Test
    public void parseLongFlag_shouldWork() throws Exception {
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Flag(value = 'c', alternative = "cdef") boolean c, @Flag('d') boolean d, @Flag('e') boolean e) {
                Assert.assertTrue(c);
                Assert.assertFalse(d);
                Assert.assertTrue(e);
            }
        });
        engine.parse("/bla -e --cdef");
    }

    @Test
    public void parseWithFlagAndArg_shouldWork() throws Exception {
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Flag('c') boolean c, int a) {
                Assert.assertTrue(c);
                Assert.assertEquals(5, a);
            }
        });
        engine.parse("/bla -c 5");
    }

    @Test
    public void parseWithArgAndFlag_shouldWork() throws Exception {
        engine.register(new Object(){
            @Command("/bla")
            void bla(@Flag('c') boolean c, int a) {
                Assert.assertTrue(c);
                Assert.assertEquals(5, a);
            }
        });
        engine.parse("/bla 5 -c");
    }

    @Test
    public void parseWithArgAndFlagAndArg_shouldWork() throws Exception {
        engine.register(new Object(){
            @Command("/bla")
            void bla(int b, @Flag('c') boolean c, int a) {
                Assert.assertEquals(4, b);
                Assert.assertTrue(c);
                Assert.assertEquals(5, a);
            }
        });
        engine.parse("/bla 4 -c 5");
    }

    @Test
    public void parseTwoCommands_shouldWork() throws Exception {
        MutableContainer<Boolean> containerA = new MutableContainer<>(false);
        MutableContainer<Boolean> containerB = new MutableContainer<>(false);
        engine.register(new Object(){
            @Command("/bla")
            void bla() {
                containerA.setValue(true);
            }
            @Command("/tra")
            void tra() {
                containerB.setValue(true);
            }
        });
        engine.parse("/bla");
        Assert.assertTrue(containerA.getValue());
        Assert.assertFalse(containerB.getValue());
        containerA.setValue(false);
        engine.parse("/tra");
        Assert.assertTrue(containerB.getValue());
        Assert.assertFalse(containerA.getValue());
    }

    @Test
    public void parseWithReturn_shouldReturnFive() throws Exception {
        engine.register(new Object(){
            @Command("/add")
            int add(int a, int b) {
                return a + b;
            }
        });
        int result = (int) engine.parse("/add 2 3");
        Assert.assertEquals(5, result);
    }

    @Test(expected = MalformedCommandException.class)
    public void parseWithMissingArgument_shouldThrowMalformedCommandException() throws Exception {
        engine.register(new Object(){
            @Command("/add")
            int add(int a, int b) {
                return a + b;
            }
        });
        engine.parse("/add 2");
    }

    @Test(expected = CommandNotFoundException.class)
    public void parseNotRegisteredCommand_shouldThrowCommandNotFoundException() throws Exception {
        engine.register(new Object(){
            @Command("/add")
            int add(int a, int b) {
                return a + b;
            }
        });
        engine.parse("/register");
    }

    @Test(expected = NumberFormatException.class)
    public void parseWithString_shouldThrowNumberFormatException() throws Exception {
        engine.register(new Object(){
            @Command("/add")
            void add(int a) {

            }
        });
        engine.parse("/add bla");
    }


}
