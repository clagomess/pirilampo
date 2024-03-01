package br.com.pirilampo.core.compilers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceTest {
    @Test
    public void testAbsolutePathMethod(){
        String result = Resource.absoluteNameFeature("foo\\\\bar\\123", "foo\\bar\\123\\xxx.feature");

        assertEquals("xxx.feature", result);

        result = Resource.absoluteNameFeature("foo//bar/123", "foo/bar/123/xxx.feature");

        assertEquals("xxx.feature", result);
    }
}
