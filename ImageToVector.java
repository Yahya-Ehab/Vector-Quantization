import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageToVector {
   public static List<int[]> ImageToVector(String imagepath) {
        List<int[]> rgbValues = new ArrayList<>();
        try {
            File file = new File(imagepath);
            if (!file.exists()) {
                System.out.println("File does not exist: " + imagepath);
                return rgbValues;
            }
            if (!file.canRead()) {
                System.out.println("File is not readable: " + imagepath);
                return rgbValues;
            }
            BufferedImage image = ImageIO.read(new File(imagepath));
            int width = image.getWidth();
            int height = image.getHeight();
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int color = image.getRGB(j, i);
                    int red = (color >> 16) & 0xff;
                    int green = (color >> 8) & 0xff;
                    int blue = color & 0xff;
                    rgbValues.add(new int[]{red, green, blue});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rgbValues;
    }

    public static BufferedImage vectorToImage(List<int[]> vectorData, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (index < vectorData.size()) {
                    int[] rgb = vectorData.get(index);
                    int color = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];

                    image.setRGB(x, y, color);
                    index++;
                } else {
                    System.out.println("Index exceeds the size of the vectorData.");
                    break;
                }
            }
        }

        return image;
    }
}


