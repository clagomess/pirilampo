package br.com.pirilampo.core.core;

import br.com.pirilampo.core.core.Resource;
import org.junit.Assert;
import org.junit.Test;

public class ResourceTest {
    @Test
    public void testAbsolutePathMethod(){
        String result = Resource.absoluteNameFeature("foo\\\\bar\\123", "foo\\bar\\123\\xxx.feature");

        Assert.assertEquals("xxx.feature", result);

        result = Resource.absoluteNameFeature("foo//bar/123", "foo/bar/123/xxx.feature");

        Assert.assertEquals("xxx.feature", result);
    }
}
