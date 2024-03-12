package com.github.clagomess.pirilampo.gui.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AppenderUtil extends AppenderSkeleton {
    @Getter
    private static final List<String> events = Collections.synchronizedList(new LinkedList<>());

    @Setter
    private static OnChangeFI onChange = e -> {};

    private final AtomicBoolean canOnChange = new AtomicBoolean(true);

    @Override
    protected void append(LoggingEvent event) {
        String message;

        if((event.getLevel() == Level.ERROR || event.getLevel() == Level.WARN) &&
                event.getThrowableInformation() != null
        ){
            message = event.getThrowableInformation().getThrowable().getMessage();
        }else{
            message = event.getRenderedMessage();
        }

        events.add(String.format(
                "%s %s: - %s",
                event.getLevel(),
                Instant.ofEpochMilli(event.getTimeStamp()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                message
        ));

        if (events.size() > 10) events.remove(0);

        if (canOnChange.get()) {
            canOnChange.set(false);
            onChange.change(String.join("\n", events));
            canOnChange.set(true);
        }
    }

    @Override
    public void close() {}

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @FunctionalInterface
    public interface OnChangeFI {
        void change(String event);
    }
}
