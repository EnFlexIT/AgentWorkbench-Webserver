/**
 * ***************************************************************
 * Agent.GUI is a framework to develop Multi-agent based simulation 
 * applications based on the JADE - Framework in compliance with the 
 * FIPA specifications. 
 * Copyright (C) 2010 Christian Derksen and DAWIS
 * http://www.dawis.wiwi.uni-due.de
 * http://sourceforge.net/projects/agentgui/
 * http://www.agentgui.org 
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package de.enflexit.ws.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * The Class JettyHomeContentProvider unpacks required files from 
 * the bundle into the local file system.
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class JettyHomeContentProvider {
	
	/**
	 * The enumeration that describes the FileToProvide.
	 */
	public enum FileToProvide {
		JETTY_XML(WebServerGlobalInfo.JETTY_ETC_RELATIVE_PATH, "jetty.xml"),
		JETTY_DEPLOY_XML(WebServerGlobalInfo.JETTY_ETC_RELATIVE_PATH, "jetty-deploy.xml"),
		JETTY_HTTP_XML(WebServerGlobalInfo.JETTY_ETC_RELATIVE_PATH, "jetty-http.xml"),
		JETTY_SELECTOR_XML(WebServerGlobalInfo.JETTY_ETC_RELATIVE_PATH, "jetty-selector.xml"),
		WEBDEFAULT_XML(WebServerGlobalInfo.JETTY_ETC_RELATIVE_PATH, "webdefault.xml"),
		
		LOGBACK_CONFIGURATION(WebServerGlobalInfo.JETTY_RESOURCES_RELATIVE_PATH, "logback.xml"),
		
		WEBAPP_DIRECTORY(WebServerGlobalInfo.JETTY_WEBAPPS_RELATIVE_PATH, null),
		
		SAMPLE_WEBAPP_ASYNCH_REST(WebServerGlobalInfo.JETTY_WEBAPPS_RELATIVE_PATH, "async-rest.war"),
		SAMPLE_WEBAPP_RAP_DEMO(WebServerGlobalInfo.JETTY_WEBAPPS_RELATIVE_PATH, "rapDemo.war");
		
		private final String subPath;
		private final String fileName;
		
		/**
		 * Instantiates a new FileToProvide.
		 *
		 * @param subPath the sub path
		 * @param fileName the file name
		 */
		private FileToProvide(final String subPath, final String fileName) {
			this.subPath = subPath;
			this.fileName = fileName;
		}
		/**
		 * Returns the sub path.
		 * @return the sub path
		 */
		public String getSubPath() {
			return subPath;
		}
		/**
		 * Returns the file name.
		 * @return the file name
		 */
		public String getFileName() {
			return fileName;
		}
		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return subPath + File.separator + fileName;
		}
	}

	
	private boolean debug = false;
	private String jettyHomeDirectoryInBundle;
	private File jettyHomeDirectory;
	
	/**
	 * Instantiates a new content provider for the Jetty home directory.
	 * @param jettyHomeDirectory the jetty home directory
	 */
	public JettyHomeContentProvider(File jettyHomeDirectory) {
		this.jettyHomeDirectory = jettyHomeDirectory;
		this.getJettyHomeDirectoryInBundle();
	}
	/**
	 * Gets the jetty home directory within the local bundle.
	 * @return the property directory in bundle
	 */
	private String getJettyHomeDirectoryInBundle() {
		if (jettyHomeDirectoryInBundle==null) {
			jettyHomeDirectoryInBundle = "/" + WebServerGlobalInfo.JETTY_HOME_RELATIVE_PATH;
			jettyHomeDirectoryInBundle = jettyHomeDirectoryInBundle.replace("\\", "/");
		}
		return jettyHomeDirectoryInBundle;
	}

	/**
	 * Checks and provides the full jetty home content.
	 */
	public void checkAndProvideFullContent() {
		this.checkAndProvideFullContent(true);
	}
	/**
	 * Checks and provides the full jetty home content.
	 * @param includeExamples the include examples
	 */
	public void checkAndProvideFullContent(boolean includeExamples) {
		for (FileToProvide fileToProvide : FileToProvide.values()) {
			boolean isSampleFile = fileToProvide.name().startsWith("SAMPLE");
			if (isSampleFile==false || (isSampleFile==true && includeExamples==true)) {
				this.checkAndProvideContent(fileToProvide);
			}
		}
	}
	/**
	 * Checks and provides the specified content.
	 * @param fileToProvide the {@link FileToProvide}
	 */
	public void checkAndProvideContent(FileToProvide fileToProvide) {
		this.checkAndProvideContent(fileToProvide, false);
	}
	/**
	 * Checks and provides the specified content.
	 * @param fileToProvide the {@link FileToProvide}
	 * @param overwriteExistingFile indicator to overwrite an existing file
	 */
	public void checkAndProvideContent(FileToProvide fileToProvide, boolean overwriteExistingFile) {
		
		// --- Get file name ------------------------------ 
		final String fileNameToMatch = fileToProvide.getFileName();
		
		// --- In which directory we are? -----------------
		File workDir = this.jettyHomeDirectory;
		if (fileToProvide.getSubPath()!=null) {
			workDir = new File(this.jettyHomeDirectory.getAbsoluteFile() + File.separator + fileToProvide.getSubPath()); 
		}
		
		// --- Try to find that file (if defined) ---------
		File[] fileFound = null;
		if (fileNameToMatch!=null && fileNameToMatch.isEmpty()==false) {
			fileFound = workDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.equals(fileNameToMatch);
				}
			});
		}
		
		// --- Some debug output --------------------------
		if (this.debug) {
			if (fileFound==null || fileFound.length==0) {
				System.out.println("Could not find file " + fileNameToMatch + " in " + this.jettyHomeDirectory.getAbsolutePath() + " => File is to be extracted");	
			} else {
				for (int i = 0; i < fileFound.length; i++) {
					System.out.println("Found file " + fileFound[i].getAbsolutePath());	
				}
			}
		}
		
		// --- Could the file be found? -------------------
		if (overwriteExistingFile==true || fileFound==null || fileFound.length==0) {
			// --- File not found => extract from bundle --
			this.extractFromBundle(fileToProvide);
		}
	}

	/**
	 * Extract from bundle.
	 * @param fileToProvide the file to provide
	 */
	private void extractFromBundle(FileToProvide fileToProvide) {

		// --- Define the source ------------------------------------
		String sourceJettyHomePath = this.getJettyHomeDirectoryInBundle(); 
		String sourceFilePath = sourceJettyHomePath + "/" + fileToProvide.toString();
		if (fileToProvide.getSubPath()!=null) {
			sourceFilePath = sourceJettyHomePath + "/" + fileToProvide.getSubPath() + "/" + fileToProvide.getFileName();
		}
		
		// --- Define the destination -------------------------------
		String destinJettyHomePath = WebServerGlobalInfo.getJettyHomeDirectory(true).getAbsolutePath();
		String destinFilePath = destinJettyHomePath + File.separator + fileToProvide.toString(); // * includes subPath, separator and file name *
		if (fileToProvide.getSubPath()!=null) {
			// --- Check if a sub path needs to be considered -------
			File destinDir = new File(destinJettyHomePath + File.separator + fileToProvide.getSubPath());
			WebServerGlobalInfo.checkDirectoryCreation(destinDir);
			if (fileToProvide.getFileName()==null || fileToProvide.getFileName().isEmpty()==true) {
				return;
			}
		}
		
		if (this.debug) {
			System.out.println("Extract '" + fileToProvide.toString() + "' to " + destinFilePath);
		}
		
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
			URL fileURL = bundle.getResource(sourceFilePath);
			if (fileURL!=null) {
				// --- Write file to directory ------------
				is = fileURL.openStream();
				fos = new FileOutputStream(destinFilePath);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				
			} else {
				// --- Could not find fileURL -------------
				System.err.println(this.getClass().getSimpleName() + " could not find resource for '" + fileToProvide.toString() + "'");
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
