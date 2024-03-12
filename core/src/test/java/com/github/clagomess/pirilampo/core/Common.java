package com.github.clagomess.pirilampo.core;

import java.io.File;
import java.util.Objects;

public abstract class Common {
    protected final File featureFolder = new File(Objects.requireNonNull(getClass()
            .getResource("../feature")).getFile());

    protected final File featureMasterFolder = new File(Objects.requireNonNull(getClass()
            .getResource("../master")).getFile());

    protected final File featureFile = new File(Objects.requireNonNull(getClass()
            .getResource("../feature/xxx.Feature")).getFile());

    protected final File featureErrorFolder = new File(Objects.requireNonNull(getClass()
            .getResource("../feature_error")).getFile());

    protected final File featureErrorFile = new File(Objects.requireNonNull(getClass()
            .getResource("../feature_error/yyy.feature")).getFile());
}
