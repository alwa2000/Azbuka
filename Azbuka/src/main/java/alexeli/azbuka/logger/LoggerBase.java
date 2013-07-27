package alexeli.azbuka.logger;

public abstract class LoggerBase implements ILogger {
    protected String getMessageTypeName(final int type) {
        String typeName = null;
        switch (type) {
        case WARNING:
            typeName = "WARNING";
            break;
        case ERROR:
            typeName = "ERROR";
            break;
        default: // includes case INFO
            typeName = "INFO";
            break;
        }
        return typeName;
    }
}
