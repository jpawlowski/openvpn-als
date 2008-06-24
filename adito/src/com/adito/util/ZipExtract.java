package com.adito.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.adito.boot.Util;


public class ZipExtract {

  public static final void copyInputStream(InputStream in, OutputStream out)
  throws IOException
  {
    byte[] buffer = new byte[1024];
    int len;

    while((len = in.read(buffer)) >= 0)
      out.write(buffer, 0, len);

    in.close();
    out.close();
  }


  public static final void extractZipFile(File basedir, InputStream in) throws IOException {
      extractZipFile(basedir, in, true);
  }

  public static final void extractZipFile(File basedir, InputStream in, boolean onlyIfNewer) throws IOException {

    ZipInputStream zin = new ZipInputStream(in);
    try {
      

      ZipEntry entry;
      byte[] buf = new byte[32768];
      int read;
      do {
         entry = zin.getNextEntry();
         
         if(entry==null)
        	 break;
         
         File f =  new File(basedir, entry.getName());
         
         if(entry.isDirectory()) {
             f.mkdirs();
             zin.closeEntry();
             if(entry.getTime() != -1) {
                 f.setLastModified(entry.getTime());
             }
             continue;
         }
         
         if(onlyIfNewer && entry.getTime() != -1 && entry.getTime() == f.lastModified()) {
             continue;
         }
         
         f.getParentFile().mkdirs();
         FileOutputStream out = new FileOutputStream(f);
         
         try {
	         while((read = zin.read(buf, 0, buf.length)) > -1) {
	        	 out.write(buf, 0, read);
	         }
	         
	         zin.closeEntry();
         
         } finally {
        	 Util.closeStream(out);
         }
         if(entry.getTime() != -1) {
             f.setLastModified(entry.getTime());
         }
         
      } while(entry!=null);

    } finally {
    	Util.closeStream(zin);
    }
  }

}

