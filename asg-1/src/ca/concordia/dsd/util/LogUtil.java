package ca.concordia.dsd.util;

import java.io.File;
import java.util.logging.*;

public class LogUtil implements Constants {

    private Handler fH;
    private Logger logger;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        //LOGGER = Logger.getLogger(LogWriter.class.getName());
    }

    public LogUtil(String initiator) {
        logger = Logger.getLogger(initiator);
        try {
            File dir = new File(LOG_DIR + File.separator + initiator);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            fH = new FileHandler(dir.getAbsolutePath() + File.separator + initiator + ".log", true);
            SimpleFormatter sf = new SimpleFormatter();
            fH.setFormatter(sf);
            logger.setUseParentHandlers(false);
            logger.addHandler(fH);
            logger.setLevel(Level.INFO);

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public void log(String message) {
        System.out.println(message);
        logger.log(Level.INFO, message);
    }

    public void log(String manager, String message) {
        System.out.println(message);
        logger.log(Level.INFO, " [" + manager +"] "+message);
    }

    public void close() {
        fH.close();
    }
}
