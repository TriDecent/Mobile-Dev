package com.example.kiotz.utilities;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;

import java.nio.ByteBuffer;

public class ImageUtils {

    public static String getImageNameFromUri(String uri) {
        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null or empty");
        }
        return uri.substring(uri.lastIndexOf("/") + 1);
    }

    public static String getImageExtension(String uri) {
        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null or empty");
        }
        return uri.substring(uri.lastIndexOf(".") + 1);
    }

    public static int getImageFormat(String uri) {
        String extension = getImageExtension(uri).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> ImageFormat.JPEG;
            case "png" -> ImageFormat.FLEX_RGBA_8888;
            default -> throw new IllegalArgumentException("Unsupported image format: " + extension);
        };
    }

    public static Image convertBitmapToImage(Bitmap bitmap, String imageUri) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int format = getImageFormat(imageUri);

        ImageReader imageReader = ImageReader.newInstance(width, height, format, 1);
        Image image = imageReader.acquireNextImage();

        try {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            buffer.rewind();
            bitmap.copyPixelsToBuffer(buffer);
            return image;
        } catch (Exception e) {
            image.close();
            imageReader.close();
            throw new RuntimeException("Failed to convert bitmap to image", e);
        }
    }
}