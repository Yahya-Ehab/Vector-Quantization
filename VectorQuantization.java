import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

public class VectorQuantization {
    static int VECTOR_SIZE = 8;
    static List<int[]> codebook = new ArrayList<>();


    public static void compress(List<int[]> input, int codebookSize, String compressedFileName) {
        buildCodebook(input, codebookSize);
        StringBuilder compressedData = new StringBuilder();

        for (int[] rgb : input) {
            int closestCode = findClosestVector(rgb, codebook);
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

    public static void buildCodebook(List<int[]> input, int codebookSize) {
        codebook.clear(); 
        Random rand = new Random();

        for (int i = 0; i < codebookSize; i++) {
            codebook.add(input.get(rand.nextInt(input.size())));
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            List<List<int[]>> clusters = new ArrayList<>();
            for (int i = 0; i < codebookSize; i++) {
                clusters.add(new ArrayList<>());
            }

            for (int[] rgb : input) {
                int closestIndex = findClosestVector(rgb, codebook);
                clusters.get(closestIndex).add(rgb);
            }

            for (int i = 0; i < codebookSize; i++) {
                if (!clusters.get(i).isEmpty()) {
                    int[] sum = new int[3];
                    for (int[] rgb : clusters.get(i)) {
                        sum[0] += rgb[0];
                        sum[1] += rgb[1];
                        sum[2] += rgb[2];
                    }
                    int[] newCodebookValue = { sum[0] / clusters.get(i).size(), sum[1] / clusters.get(i).size(),
                            sum[2] / clusters.get(i).size() };

                    if (!Arrays.equals(newCodebookValue, codebook.get(i))) {
                        codebook.set(i, newCodebookValue);
                        changed = true;
                    }
                }
            }
        }
    }
    public static BufferedImage decompress(List<Integer> compressedData, List<int[]> codebook) {

        StringBuilder decompressedData = new StringBuilder();

        for (int value : compressedData) {
            String binaryString = Integer.toBinaryString(value);

            while (binaryString.length() < VECTOR_SIZE) {
                binaryString = "0" + binaryString;
            }
            decompressedData.append(binaryString);
        }

        List<int[]> vectorData = new ArrayList<>();
        for (int i = 0; i < decompressedData.length(); i += VECTOR_SIZE) {
            String vectorString = decompressedData.substring(i, i+VECTOR_SIZE);
            int vectorValue = Integer.parseInt(vectorString, 2);
            vectorData.add(codebook.get(vectorValue));
        }

        return ImageVector.vectorToImage(vectorData);
    }

   public static void decompressFile(String compressedFile, String decompressedFile, int codebookSize) {

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
       BufferedImage decompressedImage = decompress(compressedData, codebook);

       try {
           ImageIO.write(decompressedImage, "jpg", new File(decompressedFile));
       } catch (IOException e) {
           e.printStackTrace();
       }
   }


    public static int findClosestVector(int[] rgb, List<int[]> codebook) {
        int closestIndex = 0;
        int closestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < codebook.size(); i++) {
            int distance = calculateDistance(rgb, codebook.get(i));

            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    public static int calculateDistance(int[] a, int[] b) {
        return (a[0] - b[0]) * (a[0] - b[0]) + (a[1] - b[1]) * (a[1] - b[1]) + (a[2] - b[2]) * (a[2] - b[2]);
    }

    public static void compressFile(String inputFile, String compressedFile, int codebookSize) {
        List<int[]> input = ImageVector.ImageToVector(inputFile);
        compress(input, codebookSize, compressedFile);
    }

    public static void main(String[] args) {

        String inputFile = "test.jpg";
        String compressedFile = "compressed.bin";
        String decompressedFile = "decompressed.jpg";
        int codebookSize = 16; // adjustable in GUI
       
        compressFile(inputFile, compressedFile, codebookSize);
        decompressFile(compressedFile, decompressedFile, codebookSize);
    }
}
