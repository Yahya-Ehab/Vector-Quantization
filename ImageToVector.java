import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageToVector {
    public  static List<Integer> ImageToVector(String imagepath){
        List<Integer> grayscaleValues = new ArrayList<>();
        try{
            File file = new File(imagepath);
            if (!file.exists()) {
                System.out.println("File does not exist: " + imagepath);
                return grayscaleValues;
            }
            if (!file.canRead()){
                System.out.println("File is not readable: " + imagepath);
                return grayscaleValues;
            }
            BufferedImage image = ImageIO.read(new File(imagepath));
            int width = image.getWidth();
            int height = image.getHeight();
            for (int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    int color = image.getRGB(j, i);
                    int red = (color >> 16) & 0xff;
                    int green = (color >> 8)  & 0xff;
                    int blue = color & 0xff;
                    int gray = (red + green + blue) / 3;
                    grayscaleValues.add(gray);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return grayscaleValues;
    }
}
