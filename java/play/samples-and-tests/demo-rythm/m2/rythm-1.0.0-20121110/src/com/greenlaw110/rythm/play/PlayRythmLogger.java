package com.greenlaw110.rythm.play;

import com.greenlaw110.rythm.logger.ILogger;
import play.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 27/01/12
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayRythmLogger implements ILogger {

    public static final PlayRythmLogger instance = new PlayRythmLogger();

    @Override
    public boolean isTraceEnabled() {
        return Logger.isTraceEnabled();
    }

    @Override
    public void trace(String format, Object... args) {
        RythmPlugin.trace(format, args);
    }

    @Override
    public void trace(Throwable t, String format, Object... args) {
        RythmPlugin.debug(t, format, args);
    }

    @Override
    public boolean isDebugEnabled() {
        return Logger.isDebugEnabled();
    }

    @Override
    public void debug(String format, Object... args) {
        RythmPlugin.debug(format, args);
    }

    @Override
    public void debug(Throwable t, String format, Object... args) {
        RythmPlugin.debug(t, format, args);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String format, Object... arg) {
        RythmPlugin.info(format, arg);
    }

    @Override
    public void info(Throwable t, String format, Object... args) {
        RythmPlugin.info(t, format, args);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String format, Object... arg) {
        RythmPlugin.warn(format, arg);
    }

    @Override
    public void warn(Throwable t, String format, Object... args) {
        RythmPlugin.warn(t, format, args);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String format, Object... arg) {
        RythmPlugin.error(format, arg);
    }

    @Override
    public void error(Throwable t, String format, Object... args) {
        RythmPlugin.error(t, format, args);
    }
}
