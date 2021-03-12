package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.omg.CORBA.portable.InputStream;

public class InputStreamIdentifier {
	public static ImageType getImageType(ByteArrayInputStream byteArrayInputStream) throws IOException {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(byteArrayInputStream);
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

            while (imageReaders.hasNext()) {
                ImageReader reader = imageReaders.next();
                String format = reader.getFormatName();
                format = format.toLowerCase();
                if (format.equals("png")) {
                    return ImageType.PNG;
                }
                if (format.equals("jpg") || format.equals("jpeg")) {
                    return ImageType.JPG;
                }
                if (format.equals("gif")) {
                    return ImageType.GIF;
                }
              
                System.out.println("FORMAT IS " + format);
            }
            return ImageType.OTHER;
        } finally {
            byteArrayInputStream.close();
        }
    }
}
