package at.apf.easycli.impl;

import at.apf.easycli.CliEngine;
import at.apf.easycli.annotation.Command;
import at.apf.easycli.annotation.DefaultValue;
import at.apf.easycli.annotation.Flag;
import at.apf.easycli.annotation.Optional;
import at.apf.easycli.annotation.Usage;
import org.junit.Assert;
import org.junit.Test;

public class EasyEngineUsageTest {

    private CliEngine engine = new EasyEngine();

    @Test
    public void usageWithoutUsage_shouldWork() {
        engine.register(new Object() {
            @Command("/add")
            void add(int a, int b) { }
        });
        String usage = engine.usage("/add 5 1");
        Assert.assertEquals("Usage: /add <int> <int>", usage.trim());
    }

    @Test
    public void usageUsageAndNoParameters_shouldWork() {
        engine.register(new Object() {
            @Usage("Adds two numbers")
            @Command("/add")
            void add(int a, int b) { }
        });
        String usage = engine.usage("/add 5 1");
        Assert.assertEquals("Usage: /add <int> <int>\nAdds two numbers", usage.trim());
    }

    @Test
    public void usageOptionalParam_shouldWork() {
        engine.register(new Object() {
            @Command("/add")
            void add(int a, @Optional int b) { }
        });
        String usage = engine.usage("/add 5 1");
        Assert.assertEquals("Usage: /add <int> [<int>]", usage.trim());
    }

    @Test
    public void usageTwoOptionalParam_shouldWork() {
        engine.register(new Object() {
            @Command("/add")
            void add(@DefaultValue("8") int a, @Optional int b) { }
        });
        String usage = engine.usage("/add 5 1");
        Assert.assertEquals("Usage: /add [<int> [<int>]]", usage.trim());
    }

    @Test
    public void usageWithArray_shouldWork() {
        engine.register(new Object() {
            @Command("/add")
            void add(int[] a) { }
        });
        String usage = engine.usage("/add 5 1");
        Assert.assertEquals("Usage: /add <int>...", usage.trim());
    }

    @Test
    public void usageWithFlags_shouldWork() {
        engine.register(new Object() {
            @Command("/add")
            void add(@Flag('a') boolean a) { }
        });
        String usage = engine.usage("/add 5 1");
        Assert.assertEquals("Usage: /add [FLAG...]\n\nFLAGS:\n  -a", usage.trim());
    }

    @Test
    public void usageWithFourFlags_shouldWork() {
        engine.register(new Object() {
            @Command("/add")
            void add(@Flag('c') boolean c, @Flag('a') boolean a, @Flag('d') boolean d, @Flag('b') boolean b) { }
        });
        String usage = engine.usage("/add 5 1");
        Assert.assertEquals("Usage: /add [FLAG...]\n\nFLAGS:\n  -a  \n  -b  \n  -c  \n  -d", usage.trim());
    }

    @Test
    public void usageWithFlagWithAlternative_shouldWork() {
        engine.register(new Object() {
            @Command("/add")
            void add(@Flag(value = 'a', alternative = "add") boolean a) { }
        });
        String usage = engine.usage("/add 5 1");
        Assert.assertEquals("Usage: /add [FLAG...]\n\nFLAGS:\n  -a, --add", usage.trim());
    }

    @Test
    public void usageWithFlagAndUsage_shouldWork() {
        engine.register(new Object() {
            @Command("/add")
            void add(@Usage("increments the value by one") @Flag(value = 'i', alternative = "increment") boolean a) { }
        });
        String usage = engine.usage("/add 5 1");
        Assert.assertEquals("Usage: /add [FLAG...]\n\nFLAGS:\n  -i, --increment  increments the value by one", usage.trim());
    }

    @Test
    public void allTogether_shouldWork() {
        engine.register(new Object() {
            @Usage("Adds two or more numbers")
            @Command("/add")
            void add(
                    @Usage("increments the value by one")
                    @Flag(value = 'i', alternative = "increment")
                    boolean increment,
                    int a,
                    int b,
                    @Optional
                    int c,
                    @DefaultValue("1")
                    int d,
                    @Usage("something other")
                    @Flag('b')
                    boolean x
            ) { }
        });
        String usage = engine.usage("/add 5 1");
        Assert.assertEquals("Usage: /add [FLAG...] <int> <int> [<int> [<int>]]\n" +
                "Adds two or more numbers\n" +
                "\n" +
                "FLAGS:\n" +
                "  -b               something other\n" +
                "  -i, --increment  increments the value by one", usage.trim());
    }

    @Test
    public void listCommands_shouldWork() {
        engine.register(new Object() {
            @Usage("negates a number")
            @Command("/negate")
            public void negate(int a) {}

            @Usage("Adds two numbers")
            @Command("/add")
            public void add(int a, int b) {}

            @Command("/hello")
            public void add() {}
        });
        String usage = engine.listCommands();
        Assert.assertEquals("/add     Adds two numbers\n" +
                "/hello   \n" +
                "/negate  negates a number", usage.trim());
    }

}
