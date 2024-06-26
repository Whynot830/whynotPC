package com.example.whynotpc.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Utility class for compressing and decompressing image data.
 */
public class ImageUtils {
    /**
     * Compresses the specified image data using the Deflate algorithm with the best compression level.
     *
     * @param data The input image data to compress.
     * @return compressed image data.
     * @throws IOException If an I/O error occurs.
     */
    public static byte[] compressImage(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

        byte[] tmp = new byte[4096];

        int size;
        while (!deflater.finished()) {
            size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    /**
     * Decompresses the specified compressed image data using the Inflater algorithm.
     *
     * @param data The input compressed image data to decompress.
     * @return decompressed image data.
     * @throws DataFormatException If the compressed data format is invalid.
     * @throws IOException         If an I/O error occurs.
     */
    public static byte[] decompressImage(byte[] data) throws DataFormatException, IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4096];

        int count;
        while (!inflater.finished()) {
            count = inflater.inflate(tmp);
            outputStream.write(tmp, 0, count);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }
}
