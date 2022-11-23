package com.quick.img2txt;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/6/10 0010.
 */
@Component
public class Img2TxtService {

    public static String toChar = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/|()1{}[]?-_+~<>i!lI;:, ^`'. ";
    public static int width = 500, height = 500; // 大小自己可设置

	private static String FILE_PATH;

	static {
		try {
			FILE_PATH = ResourceUtils.getURL("classpath:").getPath();
			System.out.println(FILE_PATH);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

//    @Value("${upload.file.path}")
//    private String filePath;
//
    @Value("${error.file.path}")
    private String errPath;

    public String getErrorPath(){
        return errPath;
    }

//    public static void main(String[] args) throws IOException {
//        File file = ResourceUtils.getFile("classpath:spider.jpg");
//        File temfile = new File("C:\\Users\\bd2\\Desktop\\sb.png");
//        img2txt(file);
//    }

    public File save(byte[] bytes,String name) throws IOException {
        File newFile = new File(FILE_PATH + File.separator + name);
        if(!newFile.exists()){
            newFile.createNewFile();
        }
        IOUtils.write(bytes,new FileOutputStream(newFile));
        // return img2txt(newFile);
        return img2img(newFile);
    }

    private File img2txt(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        BufferedImage scaled = getScaledImg(image);
        char[][] array = getImageMatrix(scaled);
        StringBuffer sb = new StringBuffer();
        for (char[] cs : array) {
            for (char c : cs) {
                sb.append(c);
//                System.out.print(c);
            }
            sb.append("\r\n");
//            System.out.println();
        }
        String outName = file.getAbsolutePath() + ".txt";
        File outFile = new File(outName);
        IOUtils.write(sb.toString(), new FileOutputStream(outFile));
        return outFile;
    }

    private File img2img(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        BufferedImage scaled = getScaledImg(image);
        char[][] array = getImageMatrix(scaled);
        StringBuffer sb = new StringBuffer();
        String[] strs=new String[array.length];
        for (int i=0;i<array.length;i++) {
            char[] cs =array[i];
            for (char c : cs) {
                sb.append(c);
            }
            strs[i]=sb.toString();
            sb = new StringBuffer();
        }
        BufferedImage images = createImage(strs,image);
        String outName = file.getAbsolutePath()+".jpg";
        File outFile = new File(outName);
        // 创建图片输出流对象，基于文件对象
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outFile);
        // 写入
        ImageIO.write(images,"jpg",imageOutputStream);
        // 关闭流
        imageOutputStream.close();
        return outFile;
    }


    private static char[][] getImageMatrix(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        char[][] rst = new char[h][w];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++) {
                int rgb = img.getRGB(i, j);
                // 注意溢出
                int r = Integer.valueOf(Integer.toBinaryString(rgb).substring(0, 8), 2);
                int g = (rgb & 0xff00) >> 8;
                int b = rgb & 0xff;
                int gray = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);

                // 把int gray转换成char
                int len = toChar.length();
                int base = 256 / len + 1;
                int charIdx = gray / base;
                // 注意i和j的处理顺序，如果是rst[i][j],图像是逆时针90度打印的，仔细体会下getRGB(i，j)这
                rst[j][i] = toChar.charAt(charIdx);
            }
        return rst;
    }

    private static BufferedImage getScaledImg(BufferedImage image) {
        int w=image.getWidth();
        int h=image.getHeight();
        if (w<width||h<height){
            w=width;
            h=height;
        }
        BufferedImage rst = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        rst.getGraphics().drawImage(image, 0, 0, w, h, null);
        return rst;
    }

    public static BufferedImage createImage(String[] strs,BufferedImage img) {
        // 设置背景宽高
        int width2 = img.getWidth();
        int height2 = img.getHeight();
        if (width2<width||height2<height){
            width2=width;
            height2=height;
        }
        BufferedImage image = new BufferedImage(width2, height2, BufferedImage.TYPE_3BYTE_BGR);
        // 获取图形上下文对象
        Graphics graphics = image.getGraphics();
        // 填充
        graphics.fillRect(0, 0, width2, height2);
        // 设定字体大小及样式
        graphics.setFont(new Font("宋体", Font.BOLD,1));
        // 字体颜色
        graphics.setColor(Color.BLACK);
        for (int i = 0; i < strs.length; i=i+2) {
            // 描绘字符串
            graphics.drawString(strs[i], 0,   i+1 );
            graphics.drawString("", 0,   i+2 );
        }
        graphics.dispose();
        return image;
    }
}
