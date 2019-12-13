package at.apf.easycli.impl;

import at.apf.easycli.exception.MalformedCommandException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CliSplitterTest {

    private CliSplitter splitter = new CliSplitter();

    @Test
    public void splitSingleItem_shouldWork() {
        List<String> result = splitter.split("/bla");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("/bla", result.get(0));
    }

    @Test
    public void splitFiveItems_shouldWork() {
        List<String> result = splitter.split("/bla tra kla wa na");
        Assert.assertEquals(5, result.size());
        Assert.assertEquals("/bla", result.get(0));
        Assert.assertEquals("tra", result.get(1));
        Assert.assertEquals("kla", result.get(2));
        Assert.assertEquals("wa", result.get(3));
        Assert.assertEquals("na", result.get(4));
    }

    @Test
    public void splitWithQuotes_shouldWork() {
        List<String> result = splitter.split("/bla \"tra kla wa\" na");
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("/bla", result.get(0));
        Assert.assertEquals("tra kla wa", result.get(1));
        Assert.assertEquals("na", result.get(2));
    }

    @Test
    public void splitWithQuotesInsideQuotes_shouldWork() {
        List<String> result = splitter.split("/bla \"tra \\\"kla\\\" wa\" na");
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("/bla", result.get(0));
        Assert.assertEquals("tra \"kla\" wa", result.get(1));
        Assert.assertEquals("na", result.get(2));
    }

    @Test(expected = MalformedCommandException.class)
    public void splitWithMissingQuote_shouldThrowMalformedCommandException() {
        splitter.split("/bla \"tra kla wa na");
    }

}
