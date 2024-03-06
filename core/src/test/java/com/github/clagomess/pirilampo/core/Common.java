package com.github.clagomess.pirilampo.core;

import java.io.File;
import java.util.Objects;

public abstract class Common {
    protected final File featureFolder = new File(Objects.requireNonNull(Thread.currentThread()
            .getContextClassLoader()
            .getResource("feature")).getFile());

    protected final File featureMasterFolder = new File(Objects.requireNonNull(Thread.currentThread()
            .getContextClassLoader()
            .getResource("master")).getFile());

    protected final File featureFile = new File(Objects.requireNonNull(Thread.currentThread()
            .getContextClassLoader()
            .getResource("feature/xxx.Feature")).getFile());
}
