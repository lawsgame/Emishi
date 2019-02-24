package com.lawsgame.emishitactics.engine.utils;

/**
 * simple
 */
public class Lawgger {
    public enum LogLevel{
        DEBUG(0),
        INFO(1),
        WARN(2),
        ERROR(3),
        NO_LOG(4);

        int priority;

        LogLevel(int priority) {
            this.priority = priority;
        }
    }

    private static LogLevel logLevel = LogLevel.ERROR;
    private StringBuilder logBuilder = new StringBuilder();
    private Class<?> loggedClass;

    private Lawgger(Class<?> loggedClass){
        this.loggedClass = loggedClass;
    }


    public static Lawgger createInstance(Class<?> loggedClass){
        return new Lawgger(loggedClass);
    }
    public static void setLogLevel(LogLevel wantedlogLevel){
        logLevel = wantedlogLevel;
    }

    public void debug(Object... messages){
        if(logLevel.priority <= LogLevel.DEBUG.priority){
            showMessage(ConsoleColor.BLUE_BOLD, messages);
        }
    }

    public void info(Object... messages){
        if(logLevel.priority <= LogLevel.INFO.priority){
            showMessage(ConsoleColor.GREEN_BOLD, messages);
        }
    }

    public void warn(Object... messages){
        if(logLevel.priority <= LogLevel.WARN.priority){
            showMessage(ConsoleColor.YELLOW_BOLD, messages);
        }
    }

    public void error(Object... messages){
        if(logLevel.priority <= LogLevel.ERROR.priority){
            showMessage(ConsoleColor.RED_BOLD, messages);
        }
    }

    public void showMessage(ConsoleColor cc, Object[] messages){
        synchronized(this) {
            logBuilder.setLength(0);
            logBuilder.append(cc.code());
            logBuilder.append(loggedClass.getSimpleName());
            logBuilder.append(ConsoleColor.RESET.code());
            logBuilder.append(" : ");
            for (int i = 0; i < messages.length; i++) {
                logBuilder.append(messages[i].toString());
            }
            System.out.println(logBuilder.toString());
        }
    }
}
