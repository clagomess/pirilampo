package br.com.pirilampo.gui.util;

import br.com.pirilampo.core.bind.ConsoleBind;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class AppenderUtil extends AppenderSkeleton {
    @Override
    protected void append(LoggingEvent event) {
        if((event.getLevel() == Level.ERROR || event.getLevel() == Level.WARN) && event.getThrowableInformation() != null){
            ConsoleBind.setLogData(event.getThrowableInformation().getThrowable().getMessage());
        }else{
            ConsoleBind.setLogData(event.getRenderedMessage());
        }
    }

    @Override
    public void close() {}

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
