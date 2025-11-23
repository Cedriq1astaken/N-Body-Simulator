import javax.swing.*;

public class Frame extends JFrame {

    public Frame(){
        System.setProperty("sun.java2d.opengl", "true");
        this.setTitle("Gravity Field");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new Panel());

        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

}
