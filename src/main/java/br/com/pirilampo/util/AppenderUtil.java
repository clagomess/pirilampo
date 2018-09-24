package br.com.pirilampo.util;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class AppenderUtil extends AppenderSkeleton {
    @Override
    protected void append(LoggingEvent event) {
        if((event.getLevel() == Level.ERROR || event.getLevel() == Level.WARN) && event.getThrowableInformation() != null){
            UiConsoleUtil.setLogData(event.getThrowableInformation().getThrowable().getMessage());
        }else{
            UiConsoleUtil.setLogData(event.getRenderedMessage());
        }
    }

    @Override
    public void close() {}

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
