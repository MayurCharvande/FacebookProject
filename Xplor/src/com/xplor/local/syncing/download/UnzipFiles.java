package com.xplor.local.syncing.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.xplor.common.LogConfig;

import android.util.Log;

public class UnzipFiles {

	 private String _zipFile; 
	 private String _location; 
	 
	  public UnzipFiles(String zipFile, String location) { 
	    _zipFile = zipFile; 
	    _location = location; 
	 
	    _dirChecker(""); 
	  } 
	 
	  @SuppressWarnings("rawtypes")
	public boolean unzipfiles() throws Exception {
			File archive = new File(_zipFile);

			ZipFile zipfile = new ZipFile(archive);
			for(Enumeration e = zipfile.entries(); e.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				unzipEntry(zipfile, entry, _location);
			}

			return true;
		}

		private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir)
				throws IOException {

			if (entry.isDirectory()) {
				createDir(new File(outputDir, entry.getName()));
				return;
			}

			File outputFile = new File(outputDir, entry.getName());
			if(!outputFile.getParentFile().exists()) {
				createDir(outputFile.getParentFile());
			}

			BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

			try {
				 byte[] buffer = new byte[8192];
		         int len;
		         while ((len = inputStream.read(buffer)) != -1) {
		        	 outputStream.write(buffer, 0, len);
		         }
//		         outputStream.close();
			} finally {
				outputStream.flush();
				outputStream.close();
				inputStream.close();
			}
		}

		private void createDir(File dir) {
			if (dir.exists()) {
				return;
			}
			if (!dir.mkdirs()) {
				throw new RuntimeException("Can not create dir " + dir);
			}
		}

	  public void unzip() { 
		  
		  try  { 
		      FileInputStream fin = new FileInputStream(_zipFile); 
		      ZipInputStream zin = new ZipInputStream(fin); 
		      ZipEntry ze = null; 
		      while ((ze = zin.getNextEntry()) != null) { 
		        Log.v("Decompress", "Unzipping " + ze.getName()); 
		 
		        if(ze.isDirectory()) { 
		          _dirChecker(ze.getName()); 
		        } else { 
		          FileOutputStream fout = new FileOutputStream(_location + ze.getName()); 
		          LogConfig.logd("Compress UnZip file =", ""+_location + ze.getName());
		            byte[] buffer = new byte[8192];
			         int len;
			         while ((len = zin.read(buffer)) != -1) {
			        	 fout.write(buffer, 0, len);
			         }
			         fout.close();
		
		          zin.closeEntry(); 
		        }  
		      } 
		      zin.close(); 
		    } catch(Exception e) { 
		      Log.e("Decompress", "unzip", e); 
		    } 
	  } 
	 
	  private void _dirChecker(String dir) { 
	    File f = new File(_location + dir); 
	 
	    if(!f.isDirectory()) { 
	      f.mkdirs(); 
	    } 
	  } 

	  // For unzipping zip file which contain a folder.
	  @SuppressWarnings("resource")
	public File unpackArchive(File theFile, File targetDir)throws IOException ,ZipException{
			// Auto-generated method stub
			if (!theFile.exists()) {
				throw new IOException(theFile.getAbsolutePath()+ " does not Exits..");
			}
			if (!buildDirectory(targetDir)) {
				throw new IOException("Could not Create Directory :" + targetDir);
			}
			
			Log.d("AJAY", "theFile"+ theFile.getAbsolutePath());
			ZipFile zipFile = new ZipFile(theFile);
			for (Enumeration<?> entries = zipFile.entries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				File file = new File(targetDir, File.separator + entry.getName());
				if (!buildDirectory(file.getParentFile())) {
					throw new IOException("Could not Create Directory :"+ file.getParentFile());
				}
				if (!entry.isDirectory()) {
					copyInputStream(zipFile.getInputStream(entry),
							new BufferedOutputStream(new FileOutputStream(file)));
				} else {
					throw new IOException("Could Not Create Directory :" + file);
				}
			}
			zipFile.close();

			return theFile;
		}

	  public static boolean buildDirectory(File file) {
			return file.exists() || file.mkdirs();
		}
	  
	  public static void copyInputStream(InputStream in, OutputStream out)
				throws IOException {
			byte[] buffer = new byte[1024];
			int len = in.read(buffer);
			while (len >= 0) {
				out.write(buffer, 0, len);
				len = in.read(buffer);
			}
			in.close();
			out.close();
		}
}
