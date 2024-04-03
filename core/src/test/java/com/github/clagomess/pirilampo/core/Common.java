package com.github.clagomess.pirilampo.core;

import java.io.*;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

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

    protected File decompressResource(File target, String resource) throws IOException {
        File outfile = new File(target, resource.replace("/", ""));

        try(
                InputStream is = getClass().getResourceAsStream(resource);
                BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(is));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outfile));
        ){
            byte[] buffer = new byte[1024 * 4];
            int n;

            while ((n = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, n);
            }
        }

        return outfile;
    }
}
