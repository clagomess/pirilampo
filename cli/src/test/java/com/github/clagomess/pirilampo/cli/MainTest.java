package com.github.clagomess.pirilampo.cli;

import com.github.clagomess.pirilampo.cli.Main;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.File;
import java.util.Calendar;

@Slf4j
public class MainTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    private File criarPasta(){
        String dir = System.getProperty("java.io.tmpdir");
        dir += File.separator;
        dir += "pirilampo_test";

        File f = new File(dir);

        if (f.isDirectory() || f.mkdir()) {
            dir += File.separator;
            dir += (new Long(Calendar.getInstance().getTime().getTime())).toString();

            f = new File(dir);
            if(f.mkdir()){
                pastas.add(f);
            }

            log.info("Pasta de teste: {}", f.getAbsolutePath());
        }

        return f;
    }

    @Test
    public void testMain() throws Exception {
        exit.expectSystemExit();

        String outDir = criarPasta().getAbsolutePath();
        Main.main(new String[]{
                "-feature_path",
                resourcePath + File.separator + "feature",
                "-name",
                "XXX",
                "-version",
                "1.2.3",
                "-output",
                outDir,
        });
        Assert.assertTrue((new File(outDir + File.separator + "index.html")).isFile());

        outDir = criarPasta().getAbsolutePath();
        Main.main(new String[]{
                "-feature",
                resourcePath + File.separator + "feature/xxx.Feature",
                "-name",
                "XXX",
                "-version",
                "1.2.3",
                "-output",
                criarPasta().getAbsolutePath(),
        });
        Assert.assertTrue((new File(outDir + File.separator + "xxx.html")).isFile());
    }
}
