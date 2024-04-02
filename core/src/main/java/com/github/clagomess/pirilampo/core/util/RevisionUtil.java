package com.github.clagomess.pirilampo.core.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
@Getter
public class RevisionUtil {
    private String commitDate = "00/00/0000";
    private String commitId = "0000000";
    private String tag = "0.0.0";

    // singleton
    @Getter
    private static final RevisionUtil instance = new RevisionUtil();

    private RevisionUtil(){
        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));

            commitDate = properties.getProperty("git.build.time");
            commitId = properties.getProperty("git.commit.id.abbrev");
            tag = properties.getProperty("git.closest.tag.name").replace("v", "");
        }catch (Throwable e){
            log.error(log.getName(), e);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Pirilampo - %s-%s",
                tag,
                commitId
        );
    }
}
