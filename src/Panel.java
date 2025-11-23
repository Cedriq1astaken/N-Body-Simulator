import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Panel extends JPanel implements ActionListener {
    private final int WIDTH = 900;
    private final int HEIGHT = 900;
    private final Particle[] particles = new Particle[]{
        new Particle(100_000, 650, 250, Color.red, new Point2D.Double(71, -71)),
        new Particle(100_000, 250, 650, Color.cyan, new Point2D.Double(-71, 71)),
        new Particle(100_000, 250, 250, Color.green, new Point2D.Double(71, 71)),
        new Particle(100_000, 650, 650, Color.yellow, new Point2D.Double(-71, -71)),
        new Particle(100_000, 450, 167.2, Color.pink, new Point2D.Double(100, 0)),
        new Particle(100_000, 167.2, 450, Color.white, new Point2D.Double(0, 100)),
        new Particle(100_000, 732.8, 450, Color.darkGray, new Point2D.Double(0, -100)),
        new Particle(100_000, 450, 732.8, Color.magenta, new Point2D.Double(-100, 0)),

    };
    private Timer timer;
    private BufferedWriter writer;

    //Variable of simulation
    private final double dt = 0.0001;
    private final double constant = 10;
    private final double epsilon = Math.pow(10, -10);
    private int framerate = 0;
    private Timer frameRateTimer;
    private int framecount = 0;
//    private double energy = 0;

    //Booleans
    private boolean setPath = true;
    private boolean collission = false;
    private boolean axes = false;
    private boolean glow = true;

    public Panel(){
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setLayout(null);
        this.setFocusable(true);

        try {
            writer = new BufferedWriter(new FileWriter("./data/data.csv"));
            writer.write("Energy" + '\n');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.timer = new Timer(1, this);
        this.frameRateTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                framerate = framecount/2;
                framecount = 0;
            }
        });
        timer.start();
        frameRateTimer.start();
    }

    public double[] acceleration(int i){
        double ax = 0;
        double ay = 0;

        for(int j = 0; j < particles.length; j++){
            if(i != j){
                double m = particles[j].getMass();
                double d = Math.sqrt(Math.pow(particles[i].getX() - particles[j].getX(), 2) + Math.pow(particles[i].getY() - particles[j].getY(), 2)) + epsilon;
                double rx = particles[i].getX() - particles[j].getX();
                double ry = particles[i].getY() - particles[j].getY();

                ax += (-constant * m * rx)/Math.pow(d, 3);
                ay += (constant * m * ry)/Math.pow(d, 3);
            }
        }
        return new double[]{ax, ay};
    }

    public void velocity(){
        for(int j = 0; j < particles.length; j++){
            double[] acc = acceleration(j);
            double ax = acc[0];
            double ay = acc[1];

            particles[j].setVelocity(Math.round(ax * 1e7)/1e7, Math.round(ay * 1e7)/1e7, dt);
        }
    }

    public void update(){
        double x_new;
        double y_new;
        velocity();
        for (Particle particle : particles) {
            x_new = particle.getX() + particle.getVelocity().getX() * dt;
            y_new = particle.getY() - particle.getVelocity().getY() * dt;

            particle.setLocation(Math.round(x_new * 1e7)/1e7, Math.round(y_new * 1e7)/1e7);
        }
    }

    public void getEnergy(){
        double K = 0;
        double U = 0;
        for (int i = 0; i < particles.length; i++){
            K += particles[i].getKineticEnergy();
            for(int j = 0; j < particles.length; j++){
                if(i != j){
                    U += particles[i].getPotentialEnergy(particles[j]);
                }
            }
        }

        double energy = (K + U/2)/Math.pow(10, 8);
        try {
            writer.write( energy + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        if(axes){
            g2d.setColor(Color.DARK_GRAY);
            for(int i = 1; i < 30; i++){
                g2d.drawLine(i * WIDTH/30, 0, i * WIDTH/30, HEIGHT);
                g2d.drawLine(0, i * HEIGHT/30, WIDTH, i * HEIGHT/30);
            }
            g2d.setColor(Color.white);
            g2d.drawLine(WIDTH/2, 0, WIDTH/2, HEIGHT);
            g2d.drawLine(0, HEIGHT/2, WIDTH, HEIGHT/2);
        }

        for(Particle particle: particles){
            g2d.setColor(particle.getColor());

            //Trail
            if(setPath){
                particle.addPath(particle.getX() - 10, particle.getY() - 10);
                Path2D path = particle.getPath();
                g2d.draw(path);
            }
            g2d.fill(new Arc2D.Double(particle.getX() - 10, particle.getY() -10, 20.0, 20.0, 0, 360, Arc2D.PIE));

            //GLow
            if(glow){
                for(int i = 0; i < 10; i++){
                    g2d.setColor(new Color(particle.getColor().getRed(), particle.getColor().getGreen(), particle.getColor().getBlue(), 15 - i).brighter());
                    g2d.fill(new Arc2D.Double(particle.getX() - 10 - 2.5*i, particle.getY()- 10 - 2.5*i, 20.0 + 5*i, 20.0 + 5*i, 0, 360, Arc2D.PIE));
                }
            }
        }
        g2d.setColor(Color.green);
        g2d.setFont(new Font(null, Font.PLAIN, 20));
        g2d.drawString("" + framerate, 30, 30);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        for(int i = 0; i < 200; i++)
            update();
        getEnergy();
        repaint();
        framecount++;
    }
}
