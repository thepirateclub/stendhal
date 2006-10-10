//*****************************************************************************
//*****************************************************************************

//                               Important note

// Compile this file with an older version of java and copy Starter.class to
// data/precompiled. The distribution script will use that file. So we do not
// have to care about old java versions while developing the rest of the game.

// Please note that features of newer java version cannot be used.
// Unfortunatelly the java.lang.reflect.Methods.getMethod()-method uses varargs
// in java 1.5 so it is not possible to compile this file using -target on
// java 1.5. You have to use an old jdk.

//*****************************************************************************
//*****************************************************************************
package games.stendhal.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

/**
 * This class can be compiled with a lower version of Java
 * and will display an error message if the java version
 * is too old.
 *
 * @author hendrik
 */
public class Starter {

	/**
	 * Starts stendhal
	 *
	 * @param args args
	 */
	public static void main(String[] args) {
		try {
			String version = System.getProperty("java.specification.version");
			if (Float.parseFloat(version) < 1.5f) {
				JOptionPane.showMessageDialog(null, "You need at least Java 1.5.0 (also known as 5.0) but you only have " + version + ". You can download it at http://java.sun.com");
			}
		} catch (RuntimeException e) {
			// ignore
		}

		try {
			// invoke real client with reflection in order to prevent
			// a class-load-time dependency.

			// get class
			Class clazz = Class.forName("games.stendhal.client.stendhal");
//			Class clazz = getMainClass("games.stendhal.client.stendhal");

			// get param types of main method
			Class[] paramTypes = new Class[1];
			paramTypes[0] = args.getClass();

			// get param values of main method
			Object[] params = new Object[1];
			params[0] = args;

			// get method and invoke it
			Method method = clazz.getMethod("main", paramTypes);
			method.invoke(null, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Class getMainClass(String className) throws Exception {
		Class clazz = null;
		try {
			// discover folder for .jar-files
			String jarFolder = System.getProperty("user.home") + "/stendhal/jar/";

			// load jar.properties
			String propFile = jarFolder + "jar.properties";
			Properties prop = new Properties();
			InputStream is = new FileInputStream(propFile);
			prop.load(is);
			is.close();

			// get list of .jar-files
			String jarNameString = prop.getProperty("load", "jar.properties does not contain \"load=\" line");
			List jarNameList = new LinkedList();
			StringTokenizer st = new StringTokenizer(jarNameString, ",");
			while (st.hasMoreTokens()) {
				jarNameList.add(st.nextToken());
			}
			// convert .jar-filename-list to URL-array
		    URL[] jarFiles = new URL[jarNameList.size()];
		    Iterator itr = jarNameList.iterator();
		    int i = 0;
		    while (itr.hasNext()) {
		    	jarFiles[i] = new File(jarFolder + itr.next()).toURI().toURL();
		    	i++;
		    }
		    // Create new class loader which the list of .jar-files as classpath 
		    ClassLoader loader = new URLClassLoader(jarFiles);

		    // load class through new loader
		    clazz = loader.loadClass(className);
            		
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Something nasty happend while trying to build classpath.\r\nPlease open a bug report at http://sf.net/projects/arianne with this error message:\r\n" + e);
		}

		if (clazz == null) {
			// fallback to normal classloading
			clazz = Class.forName(className);
		}
		return clazz;
	}
 }

