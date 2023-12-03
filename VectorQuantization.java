import java.util.ArrayList;
import java.util.List;
import java.io.*;



public class VectorQuantization {
    static final int VECTOR_SIZE = 8; // Size of each vector in bits


    public static List<Integer> compress(List<Integer> input, int codebookSize, String compressedFileName){
        //implement compression logic
    }
    public static List<Integer> decompress(List<Integer> compressedFileName, List<Integer> codebook,String decompressedFileName){
        //implement decompression logic
    }

    public static List<Integer> buildCodebook(List<Integer> input, int codebookSize){
        //implement codebook building logic
    }
    public static int findClosestVector(int value, List<Integer> codebook){
        //implement logic to find closest vector
    }

    public static int calculateDistance(int a , int b){
       return Math.abs(a-b);
    }

    public static void reconstructcodebook(List<Integer> decompressedData, List<Integer> codebook){
        //implement codebook reconstruction logic
    }

    public static void compressFile(String inputFile, String compressedFile, int codebookSize){
        //implement File compression logic
    }
    public static void decompressFile(String compressedFile, String decompressedFile, int codebookSize){
        //implement File decompression logic
    }
    
    public static void main(String[] args) {
        String inputFile = "input.txt";
        String compressedFile = "compressed.bin";
        String decompressedFile = "decompressed.txt";
        int codebookSize = 16; //adjustable in GUI

        compressFile(inputFile, compressedFile, codebookSize);
        decompressFile(compressedFile, decompressedFile, codebookSize);
    }

}