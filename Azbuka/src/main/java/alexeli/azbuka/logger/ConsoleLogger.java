package alexeli.azbuka.logger;

import android.util.Log;

/*************************************************************************//**
 * Logger to trace output to the console. 
 ****************************************************************************/
public class ConsoleLogger extends LoggerBase {
    /**********************************************************************//**
     * From ILogger.
     *************************************************************************/
    @Override
    public void print(final int type, final String tag, final String message) {
        switch (type) {
        case INFO:
        default:
            Log.i(tag, message);
            break;
        case WARNING:
            Log.w(tag, message);
            break;
        case ERROR:
            Log.e(tag, message);
            break;
        }
    }

    @Override
    public void print(final String tag, final String message) {
        Log.i(tag, message);
    }

    /**********************************************************************//**
     * From ILogger.
     *************************************************************************/
    @Override
    public void printStackTrace(Throwable exception) {
        printStackTrace(exception.getStackTrace());
    }

    /**********************************************************************//**
     * From ILogger.
     *************************************************************************/
    @Override
    public void setLogPath(String folder, String name) {
    }

    /**********************************************************************//**
     * Prints exception information to the console.
     * @param exception Exception to be traced.
     *************************************************************************/
    private void printStackTrace(StackTraceElement[] stackTrace) {
        for (int index = 0; index < stackTrace.length; ++index) {
            Log.e(getMessageTypeName(ERROR), stackTrace[index].toString());
        }
    }
}
