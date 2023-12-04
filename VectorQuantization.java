import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.*;



public class VectorQuantization {
    static final int VECTOR_SIZE = 8; // Size of each vector in bits


    public static void compress(List<Integer> input, int codebookSize, String compressedFileName){
        List<Integer> codebook = buildCodebook(input, codebookSize);
        StringBuilder compressedData = new StringBuilder();
        for (int value : input){
            int closestCode = findClosestVector(value, codebook);
            String binaryString = Integer.toBinaryString(closestCode);
            while (binaryString.length() < VECTOR_SIZE){
                binaryString = "0" + binaryString;
            }
            compressedData.append(binaryString);
        }
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(compressedFileName))){
            for (int i = 0; i < compressedData.length(); i+=8){
                String byteString = compressedData.substring(i, Math.min(i + 8, compressedData.length()));
                int byteValue = Integer.parseInt(byteString,2);
                bos.write(byteValue);
            }
    } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static List<Integer> decompress(List<Integer> compressedFileName, List<Integer> codebook,String decompressedFileName){
        //implement decompression logic
        List<Integer> decompressedData = new ArrayList<>();
        return decompressedData;
    }

    public static List<Integer> buildCodebook(List<Integer> input, int codebookSize){
        // Initialize the codebook with random values from the input
        List<Integer> codebook = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < codebookSize; i++){
            codebook.add(input.get(rand.nextInt(input.size())));
        }

        // Repeat the process until the codebook stops changing
        boolean changed = true;
        while(changed) {
            changed = false;
            List<List<Integer>> clusters = new ArrayList<>();
            for (int i = 0; i < codebookSize; i++) {
                clusters.add(new ArrayList<>());
            }

            // Assign each input value to the closest codebook value
            for (int value : input) {
                int closestIndex = 0;
                int closestDistance = Integer.MAX_VALUE;
                for (int i = 0; i < codebookSize; i++) {
                    int distance = calculateDistance(value, codebook.get(i));
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestIndex = i;
                    }
                }
                clusters.get(closestIndex).add(value);
            }

            // Update each codebook value to be the average of its cluster
            for (int i = 0; i < codebookSize; i++){
                int sum = 0;
                for (int value : clusters.get(i)){
                    sum+= value;
                }
                int newCodebookValue = sum /clusters.get(i).size();
                if (newCodebookValue != codebook.get(i)){
                    codebook.set(i,newCodebookValue);
                    changed = true;
                }
            }
        }
        return codebook;
    }




    public static int findClosestVector(int value, List<Integer> codebook){
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

    public static int calculateDistance(int a , int b){
       return Math.abs(a - b);
    }

    public static void reconstructcodebook(List<Integer> decompressedData, List<Integer> codebook){
        //implement codebook reconstruction logic
    }

    public static void compressFile(String inputFile, String compressedFile, int codebookSize){
        List<Integer> input = ImageToVector.ImageToVector(inputFile);
        compress(input, codebookSize, compressedFile);
    }
    public static void decompressFile(String compressedFile, String decompressedFile, int codebookSize){
        //implement File decompression logic
    }

    public static void main(String[] args) {
        String inputFile = "Cute_Dog.jpg";
        String compressedFile = "compressed.bin";
        String decompressedFile = "decompressed.jpg";
        int codebookSize = 16; //adjustable in GUI

        compressFile(inputFile, compressedFile, codebookSize);
        decompressFile(compressedFile, decompressedFile, codebookSize);
    }

}