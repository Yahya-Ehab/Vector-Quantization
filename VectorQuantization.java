import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class VectorQuantization {
    static final int VECTOR_SIZE = 8;

    public static void compress(List<Integer> input, int codebookSize, String compressedFileName) {
        List<Integer> codebook = buildCodebook(input, codebookSize);
        StringBuilder compressedData = new StringBuilder();

        for (int value : input) {
            int closestCode = findClosestVector(value, codebook);
            String binaryString = Integer.toBinaryString(closestCode);

            while (binaryString.length() < VECTOR_SIZE) {
                binaryString = "0" + binaryString;
            }
            compressedData.append(binaryString);
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(compressedFileName))) {
            for (int i = 0; i < compressedData.length(); i += 8) {
                String byteString = compressedData.substring(i, Math.min(i + 8, compressedData.length()));
                int byteValue = Integer.parseInt(byteString, 2);
                bos.write(byteValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> buildCodebook(List<Integer> input, int codebookSize) {
        List<Integer> codebook = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < codebookSize; i++) {
            codebook.add(input.get(rand.nextInt(input.size())));
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            List<List<Integer>> clusters = new ArrayList<>();

            for (int i = 0; i < codebookSize; i++) {
                clusters.add(new ArrayList<>());
            }

            for (int value : input) {
                int closestIndex = findClosestVector(value, codebook);
                clusters.get(closestIndex).add(value);
            }

            for (int i = 0; i < codebookSize; i++) {
                if (!clusters.get(i).isEmpty()) {
                    int sum = 0;
                    for (int value : clusters.get(i)) {
                        sum += value;
                    }
                    int newCodebookValue = sum / clusters.get(i).size();

                    if (newCodebookValue != codebook.get(i)) {
                        codebook.set(i, newCodebookValue);
                        changed = true;
                    }
                }
            }
        }

        return codebook;
    }

    public static BufferedImage decompress(List<Integer> compressedData, List<Integer> codebook, int width, int height) {
        StringBuilder decompressedData = new StringBuilder();

        for (int value : compressedData) {
            String binaryString = Integer.toBinaryString(value);

            while (binaryString.length() < VECTOR_SIZE) {
                binaryString = "0" + binaryString;
            }
            decompressedData.append(binaryString);
        }

        List<Integer> vectorData = new ArrayList<>();
        for (int i = 0; i < decompressedData.length(); i += VECTOR_SIZE) {
            String vectorString = decompressedData.substring(i, Math.min(i + VECTOR_SIZE, decompressedData.length()));
            int vectorValue = Integer.parseInt(vectorString, 2);
            vectorData.add(codebook.get(vectorValue));
        }

        return vectorToImage(vectorData, width, height);
    }

    public static void decompressFile(String compressedFile, String decompressedFile, int codebookSize, int width,
            int height) {
        List<Integer> compressedData = new ArrayList<>();

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(compressedFile))) {
            int byteRead;

            while ((byteRead = bis.read()) != -1) {
                compressedData.add(byteRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<Integer> codebook = buildCodebook(compressedData, codebookSize);
        BufferedImage decompressedImage = decompress(compressedData, codebook, width, height);

        try {
            ImageIO.write(decompressedImage, "jpg", new File(decompressedFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage vectorToImage(List<Integer> vectorData, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (index < vectorData.size()) {
                    int grayValue = vectorData.get(index);
                    int rgb = (grayValue << 16) | (grayValue << 8) | grayValue;

                    image.setRGB(x, y, rgb);
                    index++;
                } else {
                    System.out.println("Index exceeds the size of the vectorData.");
                    break;
                }
            }
        }

        return image;
    }

    public static int findClosestVector(int value, List<Integer> codebook) {
        int closestIndex = 0;
        int closestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < codebook.size(); i++) {
            int distance = calculateDistance(value, codebook.get(i));

            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    public static int calculateDistance(int a, int b) {
        return (a - b) * (a - b);
    }

    public static void compressFile(String inputFile, String compressedFile, int codebookSize) {
        List<Integer> input = imageToVector(inputFile);
        compress(input, codebookSize, compressedFile);
    }

    public static List<Integer> imageToVector(String imagePath) {
        List<Integer> vectorData = new ArrayList<>();

        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int gray = (int) (0.299 * ((rgb >> 16) & 0xFF) + 0.587 * ((rgb >> 8) & 0xFF)
                            + 0.114 * (rgb & 0xFF));
                    vectorData.add(gray);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vectorData;
    }

    public static void main(String[] args) {
        String inputFile = "test.jpg";
        String compressedFile = "compressed.bin";
        String decompressedFile = "decompressed.jpg";
        int codebookSize = 16; // adjustable in GUI
        int width = 332;
        int height = 300;

        compressFile(inputFile, compressedFile, codebookSize);
        decompressFile(compressedFile, decompressedFile, codebookSize, width, height);
    }
}
