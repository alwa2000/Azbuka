package alexeli.azbuka.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*************************************************************************//**
 * Logger to trace output to the file. 
 ****************************************************************************/
public class FileLogger extends LoggerBase {
    /*************************************************************************/
    /* Constants                                                             */
    /*************************************************************************/
    private static final int              THRESHOLD         = 2 * 1024 * 1024;
    private static final String           CAUSE             = "Cause:";
    private static final String           NULL_EXCEPTION    = "Null exception received.";
    private static final String           EXT_LOG           = ".log";
    private static final String           EXT_OLD           = ".old";
    private static final SimpleDateFormat FORMATTER         = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");

    /*************************************************************************/
    /* Private members                                                       */
    /*************************************************************************/
    private static BufferedWriter mWriter;
    private String mFolder;
    private File   mLogFile;
    private File   mOldLogFile;

    /**********************************************************************//**
     * From ILogger.
     *************************************************************************/
    @Override
    public void print(int type, String tag, String message) {
        writeLine(FORMATTER.format(new Date()) + " " + getMessageTypeName(type) + "." + tag + " : " + message);
    }

    /**********************************************************************//**
     * From ILogger.
     *************************************************************************/
    @Override
    public void print(String tag, String message) {
        writeLine(FORMATTER.format(new Date()) + " " + getMessageTypeName(ILogger.INFO) + "." + tag + " : " + message);
    }

    /**********************************************************************//**
     * From ILogger.
     *************************************************************************/
    @Override
    public void printStackTrace(Throwable exception) {
        
        if (exception == null)  {
            writeLine(NULL_EXCEPTION);
            return;
        }
        
        printStackTraceToFile(exception.getStackTrace());
        writeLine(CAUSE);
        writeLine(exception.toString());
    }

    /**********************************************************************//**
     * From ILogger.
     *************************************************************************/
    @Override
    public void setLogPath(final String folder, final String name) {
        if (null == folder || null == name) {
            return;
        }

        if (File.separatorChar == folder.charAt(folder.length() - 1)) {
            mFolder = folder;
        } else {
            mFolder = folder + File.separatorChar;
        }

        mLogFile    = new File(mFolder + name + EXT_LOG);
        mOldLogFile = new File(mFolder + name + EXT_OLD);
    }
    
    /**********************************************************************//**
     * Prepares for writing.
     *************************************************************************/
    private void prepareWriter() {
        if (null == mFolder || null == mLogFile) {
            return;
        }

        try {
            if (null == mWriter) {
                File folder = new File(mFolder);

                if (!folder.exists()) {
                    folder.mkdirs();
                }

                if (!mLogFile.exists()) {
                    mLogFile.createNewFile();
                }

                mWriter = new BufferedWriter(new FileWriter(mLogFile, true));
            } else {
                if (mLogFile.length() >= THRESHOLD) {
                    if (mOldLogFile.exists()) {
                        mOldLogFile.delete();
                    }
                    mLogFile.renameTo(mOldLogFile);
                    mLogFile.createNewFile();
                    mWriter = new BufferedWriter(new FileWriter(mLogFile, true));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**********************************************************************//**
     * Writes single line to the trace file.
     *************************************************************************/
    private void writeLine(String line) {
        prepareWriter();

        if (null == mWriter) {
            return;
        }

        try {
            mWriter.write(line);
            mWriter.newLine();
            mWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**********************************************************************//**
     * Prints exception information to the trace file.
     * @param exception Exception to be traced.
     *************************************************************************/
    private void printStackTraceToFile(StackTraceElement[] stackTrace) {
        prepareWriter();

        if (null == mWriter) {
            return;
        }

        try {
            mWriter.write(FORMATTER.format(new Date()) + " ");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int index = 0; index < stackTrace.length; ++index) {
            writeLine(stackTrace[index].toString());
        }
    }
}
