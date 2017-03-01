package cn.dankal.www.selectandclippic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Dankal on 16/8/4.
 */
public class FileUtil {

    /**
     * 复制图片
     *
     * @param oldPath 旧的图片路径
     * @param newPath 新的图片路径
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                File file = new File(newPath);
                if (file.exists()) {
                    file.delete();
                } else {
                    new File(file.getParent()).mkdirs();
                    file.createNewFile();
                }
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
