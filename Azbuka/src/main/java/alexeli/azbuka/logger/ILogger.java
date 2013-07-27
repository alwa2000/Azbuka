package alexeli.azbuka.logger;

/*************************************************************************//**
 * Interface defines logger stuff.
 ****************************************************************************/
public interface ILogger {
    /**********************************************************************//**
     * Message types
     *************************************************************************/
    public static final int INFO    = 0;
    public static final int WARNING = 1;
    public static final int ERROR   = 2;

    /**********************************************************************//**
     * Prints one trace message.
     * @param type Message type: INFO, WARNING or ERROR.
     * @param tag Tag to identify caller module.
     * @param message Trace message.
     *************************************************************************/
    public void print(final int type, final String tag, final String message);

    /**********************************************************************//**
     * Prints one trace message. Assumes INFO type
     * @param tag Tag to identify caller module.
     * @param message Trace message.
     *************************************************************************/
    public void print(final String tag, final String message);


    /**********************************************************************//**
     * Prints exception information.
     * @param exception Exception to be traced.
     *************************************************************************/
    public void printStackTrace(Throwable exception);

    /**********************************************************************//**
     * Sets path of the trace file.
     * @param folder Folder to keep trace file.
     * @param name   File name.
     *************************************************************************/
    public void setLogPath(final String folder, final String name);
}
