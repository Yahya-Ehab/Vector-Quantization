import java.io.File;
import java.awt.Font;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GUI implements ActionListener {
    File imageFile;
    VectorQuantization vectorquantization = new VectorQuantization();
    JPanel panel= new JPanel();
    JButton select = new JButton();
    JLabel textLabel = new JLabel();
    JLabel codebook_label = new JLabel();
    JLabel vector_label = new JLabel();
    JButton compress = new JButton();
    JButton decompress = new JButton();
    JSpinner codebook_size = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    JSpinner vector_size = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    JFrame frame = new JFrame("VQ Image Compression");
    JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir")); // GUI to select files
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "bmp"); // Filter to choose specific files only

    public GUI(){
        panel.setLayout(null);
        select.setText("Select File");
        compress.setText("Compress");
        decompress.setText("Decompress");
        codebook_size.setValue(16);
        vector_size.setValue(8);
        codebook_label.setText("Codebook Size");
        vector_label.setText("Vector Size");
        textLabel.setText("Select a file to proceed");

        textLabel.setBounds(300, 50, 250, 60);
        textLabel.setFont(new Font("", Font.BOLD, 16));
        select.setBounds(30, 160, 100, 30);
        compress.setBounds(230, 160, 100, 30);
        decompress.setBounds(350, 160, 110, 30);
        codebook_size.setBounds(500, 160, 110, 30);
        codebook_label.setBounds(500, 130, 110, 30);

        // Listens for button press and calls actionPerformed()
        select.addActionListener(this);
        compress.addActionListener(this);
        decompress.addActionListener(this);

        panel.add(textLabel);
        panel.add(select);
        panel.add(compress);
        panel.add(decompress);
        panel.add(codebook_size);
        panel.add(codebook_label);

        // Hides buttons till you select a file
        compress.setVisible(false);
        decompress.setVisible(false);
        codebook_size.setVisible(false);
        codebook_label.setVisible(false);


        frame.add(panel);
        frame.setSize(800, 250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public void actionPerformed(ActionEvent actionEvent){
        if (actionEvent.getActionCommand().equals("Select File")) {
            if (selectFile()) {
                compress.setVisible(true);
                decompress.setVisible(true);
                codebook_size.setVisible(true);
                vector_size.setVisible(true);
                codebook_label.setVisible(true);
                textLabel.setText("File selected: " + imageFile.getName());
            }
        }
        else if (actionEvent.getActionCommand().equals("Compress")) {
            try{
                int codebookSize = (Integer) codebook_size.getValue();
                VectorQuantization.compressFile(imageFile.getAbsolutePath(), "compressed.bin", codebookSize);
                textLabel.setBounds(300, 50, 250, 60);
                textLabel.setText("Compression completed");
            }
            catch(Exception e){
                textLabel.setBounds(300, 50, 250, 60);
                textLabel.setText("File Error");
            }
        }
        else if (actionEvent.getActionCommand().equals("Decompress")) {
            try{
                int codebookSize = (Integer) codebook_size.getValue();
                VectorQuantization.decompressFile("compressed.bin", "decompressed.jpg", codebookSize);
                textLabel.setBounds(300, 50, 250, 60);
                textLabel.setText("Decompression completed");
            }
            catch(Exception e){
                textLabel.setBounds(300, 50, 250, 60);
                textLabel.setText("File Error");
            }
        }
    }

    public Boolean selectFile(){
        fileChooser.setFileFilter(filter);
        fileChooser.setApproveButtonText("Select");
        int returnVal = fileChooser.showOpenDialog(fileChooser);

        // If you press "Select"
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            imageFile = fileChooser.getSelectedFile();
            return true;
        }
        return false;
    }

    public static void main(String[] args){
        new GUI();
    }
}
