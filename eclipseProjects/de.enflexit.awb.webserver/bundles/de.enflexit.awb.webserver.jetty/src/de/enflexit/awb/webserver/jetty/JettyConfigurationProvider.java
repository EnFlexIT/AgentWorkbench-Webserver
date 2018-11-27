package de.enflexit.awb.webserver.jetty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * The Class JettyConfigurationProvider unpacks required files from 
 * the bundle into the local file system.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class JettyConfigurationProvider {
	
	/**
	 * The enumeration that describes the DirectoryToProvide.
	 */
	public enum DirectoryToProvide {
		ETC("etc"),
		MODULES("modules"),
		WEBAPPS("webapps");
		
		private final String directoryName;
		
		private DirectoryToProvide(final String fileName) {
			this.directoryName = fileName;
		}
		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return directoryName;
		}
	}
	
	private static final String JETTY_CONFIG_SOURCE_LOCATION = "/jetty-distribution/"; 
	
	private boolean debug = false;
	private File homeDirectory;
	
	/**
	 * Instantiates a new property content provider.
	 */
	public JettyConfigurationProvider() {
	}

	/**
	 * Returns the jetty home directory.
	 * @return the jetty home directory
	 */
	public File getJettyHomeDirectory() {
		if (homeDirectory==null) {
			homeDirectory = JettyRuntime.getJettyHomeDirectory();
		}
		return homeDirectory;
	}
	
	/**
	 * Checks and provides the full configuration content.
	 */
	public void checkAndProvideFullConfigurationContent() {
		for (DirectoryToProvide directoryToProvide : DirectoryToProvide.values()) {
			this.checkAndProvideConfigurationContent(directoryToProvide);
		}
	}
	/**
	 * Checks and provides the specified configuration content.
	 * @param directoryToProvide the {@link DirectoryToProvide}
	 */
	public void checkAndProvideConfigurationContent(DirectoryToProvide directoryToProvide) {
		this.checkAndProvideConfigurationContent(directoryToProvide, false);
	}
	/**
	 * Checks and provides the specified configuration content.
	 * @param directoryToProvide the {@link DirectoryToProvide}
	 * @param overwriteExistingFile the overwrite existing file
	 */
	public void checkAndProvideConfigurationContent(DirectoryToProvide directoryToProvide, boolean overwriteExistingFile) {
		
		// --- Try to find the sub directory --------------
		String jettySubDirPath = this.getJettyHomeDirectory().getAbsolutePath() + File.separator + directoryToProvide.toString();
		File jettySubDir = new File(jettySubDirPath); 
		boolean pathExists = jettySubDir.exists(); 
		if (pathExists==false) {
			pathExists = jettySubDir.mkdir();
		}

		// --- Extract directory content from bundle ------
		if (pathExists==true) {
			this.extractDirectoryFromBundle(directoryToProvide, overwriteExistingFile);
		}
	}

	/**
	 * Extract from bundle.
	 *
	 * @param directoryToProvide the file to provide
	 * @param overwriteExistingFile the overwrite existing file
	 */
	private void extractDirectoryFromBundle(DirectoryToProvide directoryToProvide, boolean overwriteExistingFile) {

		String directoryName = directoryToProvide.toString();
		String newDirectoryPath = this.getJettyHomeDirectory().getAbsolutePath() + File.separator + directoryName;
		
		if (this.debug) {
			System.out.println("Extract '" + directoryName + "' to " + newDirectoryPath);
		}
		
		try {
			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
			if (bundleWiring!=null) {
				Vector<String> resources = new Vector<>(bundleWiring.listResources(JETTY_CONFIG_SOURCE_LOCATION + directoryName, "*", BundleWiring.FINDENTRIES_RECURSE));
				for (int i = 0; i < resources.size(); i++) {
					// --- Get the bundle resource found ------------
					String bundleResource = resources.get(i);
					URL bundleFileURL = bundle.getResource(bundleResource);
					
					// --- Configure destination file name ----------
					String filename = bundleFileURL.getFile();
					filename = filename.replaceAll(JETTY_CONFIG_SOURCE_LOCATION, "");
					filename = filename.replaceAll(directoryName, "");
					File destinationFile = new File(newDirectoryPath + filename);
					if (destinationFile.isDirectory()==true) continue;

					// --- Export if not already there --------------
					if (destinationFile.exists()==false || overwriteExistingFile==true) {
						this.writeFile(bundleFileURL, destinationFile);
					}
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Writes the specified file from this bundle to the destination file.
	 *
	 * @param bundFileURL the bundle file URL
	 * @param destinationFile the destination file
	 */
	private void writeFile(URL bundleFileURL, File destinationFile) {
		
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			if (bundleFileURL!=null && destinationFile.isDirectory()==false) {
				
				// --- Check if parent directory exists ---
				File parentDir = destinationFile.getParentFile();
				if (parentDir.exists()==false) {
					parentDir.mkdirs();
				}
				
				// --- Write file to directory ------------
				is = bundleFileURL.openStream();
				fos = new FileOutputStream(destinationFile);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				
			} else {
				// --- Could not find fileURL -------------
				System.err.println("[" + this.getClass().getSimpleName() + "] could not find resource for '" + destinationFile.getName() + "'");
			}
			
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		} finally {
			try {
				if (fos!=null) fos.close();
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			}
			try {
				if (is!=null) is.close();
			} catch (IOException ioEx) {
				ioEx.printStackTrace();
			}
		}
	}
	
}
