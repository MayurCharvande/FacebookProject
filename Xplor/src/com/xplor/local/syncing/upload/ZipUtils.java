package com.xplor.local.syncing.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class ZipUtils {

	public static File unpackArchive(InputStream stream, File targetDir,Context ctx) throws IOException {

		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		try {
			
			InputStream in = new BufferedInputStream(stream, 1024);
			File zip = File.createTempFile("src", ".zip", targetDir);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(zip));
			copyInputStream(in, out);
			out.close();
			Log.d("Jitendra", "process completed....");
			return unpackArchive(zip, targetDir);
		} catch (Exception e) {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
			alertDialog
					.setCancelable(true)
					.setInverseBackgroundForced(true)
					// .setIcon(R.drawable.iconapp)
					.setMessage("Problem in downloading.Try again later.")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									// Auto-generated method stub
									dialog.dismiss();
								}
							});

			return null;
		}
	}

	@SuppressWarnings("resource")
	public static File unpackArchive(File theFile, File targetDir) throws IOException ,ZipException{
		// Auto-generated method stub
		if (!theFile.exists()) {
			Log.d("Jitendra", "yahi hai exception");
			throw new IOException(theFile.getAbsolutePath()+ " does not Exits..");
		}
		if (!buildDirectory(targetDir)) {
			Log.d("Jitendra", "yahi hai exception");
			throw new IOException("Could not Create Directory :" + targetDir);
		}
		
		Log.d("Jitendra", "theFile"+ theFile.getAbsolutePath());
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
				throw new IOException("Could Not Create Directory :" + file);			}
		}
		zipFile.close();

		return theFile;
	}

	public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len = in.read(buffer);
		while (len >= 0) {
			out.write(buffer, 0, len);
			len = in.read(buffer);
		}
		in.close();
		out.close();
	}

	public static boolean buildDirectory(File file) {
		return file.exists() || file.mkdirs();
	}

}
