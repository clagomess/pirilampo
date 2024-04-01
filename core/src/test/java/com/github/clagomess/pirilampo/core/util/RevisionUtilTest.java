package com.github.clagomess.pirilampo.core.util;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

@Slf4j
public class RevisionUtilTest {
    @Test
    public void newInstance() {
        val result = RevisionUtil.getInstance();
        log.info("{}", result);
    }
}
