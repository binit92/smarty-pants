package ca.concordia.dsd.util;

import java.io.File;
import java.util.logging.*;

public class LogUtil implements IConstants{

    private Handler fH ;
    private Logger logger;

    public LogUtil(String initiator){
        logger = Logger.getLogger(initiator);
        try{
            File path = new File(LOG_DIR + File.separator + initiator + File.separator + initiator +".log");
            if (!path.exists()){
                path.mkdirs();
            }
            fH = new FileHandler(path.getAbsolutePath(),true);
            SimpleFormatter sf = new SimpleFormatter();
            fH.setFormatter(sf);
            logger.setUseParentHandlers(false);
            logger.addHandler(fH);
            logger.setLevel(Level.INFO);

        }catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
        }finally {

        }
    }

    public void log(String message){
        logger.log(Level.INFO,message);
    }

}
