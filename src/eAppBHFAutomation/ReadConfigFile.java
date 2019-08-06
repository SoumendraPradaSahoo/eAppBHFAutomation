package eAppBHFAutomation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class ReadConfigFile {
static Properties p = new Properties();
public static Properties getObjectRepository() throws IOException{
	        //Read object repository file

	InputStream stream = new FileInputStream(new File(System.getProperty("user.dir")+"\\Object\\Config.Properties"));
	        p.load(stream);
	        return p;
	    }
}
