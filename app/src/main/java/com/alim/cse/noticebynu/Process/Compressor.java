package com.alim.cse.noticebynu.Process;

import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Compressor {

    private byte[] zipFile;

    public Compressor(byte[] Zip) {
        this.zipFile = Zip;
    }

    public String Unzip() {
        int len;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(zipFile);
        ZipInputStream zis = new ZipInputStream(bais);

        try {
            while (null != zis.getNextEntry()){
                byte [] buffer = new byte[1024];
                while (0<(len=zis.read(buffer))) {
                    baos.write(buffer,0,len); }
                Log.println(Log.ASSERT,"FOS",baos.toString());
                zis.closeEntry();
            }
            return baos.toString();
        } catch (Exception e) {
            Log.println(Log.ASSERT,"Exception",e.toString());
            return baos.toString();
        }
    }

    public byte[] Zip(String Name) {
        byte[] bytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        ZipEntry entry = new ZipEntry(Name);
        entry.setSize(zipFile.length);
        try {
            zos.putNextEntry(entry);
            zos.write(zipFile);
            zos.closeEntry();
            zos.close();
            bytes = baos.toByteArray();
            Log.println(Log.ASSERT,"DONE",baos.toString());
            return bytes;
        } catch (Exception e) {
            bytes = "Exception".getBytes();
            Log.println(Log.ASSERT,"Exception",e.toString());
            return bytes;
        }
    }
}
