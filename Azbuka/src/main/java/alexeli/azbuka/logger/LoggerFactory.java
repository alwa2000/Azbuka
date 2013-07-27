package alexeli.azbuka.logger;

public class LoggerFactory {
    private static volatile ILogger mLogger = null;

    public static ILogger create() {
        if (mLogger == null) {
            synchronized (ILogger.class) {
                if (mLogger == null) {
                    mLogger = new ConsoleLogger();
                }
            }
        }
        return mLogger;
    }
}
