package com.topevery.hybird.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author wujie
 */
public class ImageResizer {
    private final File directory;

    public ImageResizer(File directory) {
        this.directory = directory;
    }

    public String resizeImageIfNeeded(String imagePath, int maxWidth, int maxHeight) throws IOException {
        boolean shouldScale = maxWidth != 0 || maxHeight != 0;

        if (!shouldScale) {
            return imagePath;
        }

        File scaledImage = resizedImage(imagePath, maxWidth, maxHeight);
        copyExifData(imagePath, scaledImage.getPath());
        return scaledImage.getPath();
    }

    private File resizedImage(String path, int maxWidth, int maxHeight) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        int width = maxWidth > 0 ? Math.min(originalWidth, maxWidth) : originalWidth;
        int height = maxHeight > 0 ? Math.min(originalHeight, maxHeight) : originalHeight;

        boolean shouldScaleWidth = (maxWidth > 0) && (maxWidth < originalWidth);
        boolean shouldScaleHeight = (maxHeight > 0) && (maxHeight < originalHeight);
        boolean shouldScale = shouldScaleWidth || shouldScaleHeight;

        if (shouldScale) {
            int scaledWidth = height * originalWidth / originalHeight;
            int scaledHeight = width * originalHeight / originalWidth;

            if (width < height) {
                if (maxWidth == 0) {
                    width = scaledWidth;
                } else {
                    height = scaledHeight;
                }
            } else if (height < width) {
                if (maxHeight == 0) {
                    height = scaledHeight;
                } else {
                    width = scaledWidth;
                }
            } else {
                if (originalWidth < originalHeight) {
                    width = scaledWidth;
                } else if (originalHeight < originalWidth) {
                    height = scaledHeight;
                }
            }
        }

        String[] pathParts = path.split("/");
        String imageName = pathParts[pathParts.length - 1];

        File imageFile = new File(directory, imageName);
        FileOutputStream fileOutput = new FileOutputStream(imageFile);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutput);

        fileOutput.close();

        return imageFile;
    }

    public static void copyExifData(String filePathOri, String filePathDest) {
        try {
            ExifInterface oldExif = new ExifInterface(filePathOri);
            ExifInterface newExif = new ExifInterface(filePathDest);

            List<String> attributes =
                    Arrays.asList(
                            "FNumber",
                            "ExposureTime",
                            "ISOSpeedRatings",
                            "GPSAltitude",
                            "GPSAltitudeRef",
                            "FocalLength",
                            "GPSDateStamp",
                            "WhiteBalance",
                            "GPSProcessingMethod",
                            "GPSTimeStamp",
                            "DateTime",
                            "Flash",
                            "GPSLatitude",
                            "GPSLatitudeRef",
                            "GPSLongitude",
                            "GPSLongitudeRef",
                            "Make",
                            "Model",
                            "Orientation");
            for (String attribute : attributes) {
                setIfNotNull(oldExif, newExif, attribute);
            }

            newExif.saveAttributes();

        } catch (Exception ex) {
            Log.e("ExifDataCopier", "Error preserving Exif data on selected image: " + ex);
        }
    }

    private static void setIfNotNull(ExifInterface oldExif, ExifInterface newExif, String property) {
        if (oldExif.getAttribute(property) != null) {
            newExif.setAttribute(property, oldExif.getAttribute(property));
        }
    }
}
