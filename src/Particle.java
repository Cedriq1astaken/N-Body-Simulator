import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Random;


public class Particle extends Point2D {
    private final Random rand = new Random();
    private double mass;
    private double x;
    private double y;
    private Point2D velocity;
    private Color color;
    private Path2D path = new Path2D.Double();
    private LinkedList<Point2D> tailPoints = new LinkedList<Point2D>();
    private final int maxLen = 350;

    public Particle(double mass, double x, double y, Color color){
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.color = color;
        this.velocity = new Point2D.Double(0, 0);
        this.path.moveTo(x + 10, y + 10);
    }

    public Particle(double mass, Color color){
        this.mass = mass;
        this.x = rand.nextDouble(1,900);
        this.y = rand.nextDouble(1,900);
        this.color = color;
        this.velocity = new Point2D.Double(rand.nextDouble(-100,100), rand.nextDouble(-100,100));
        this.path.moveTo(x, y);
    }

    public Particle(double mass, double x, double y,  Color color, Point2D velocity){
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.color = color;
        this.velocity = velocity;
        this.path.moveTo(x, y);
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Color getColor(){
        return color;
    }

    public Point2D getVelocity() {
        return velocity;
    }
    public double getMass(){
        return mass;
    }

    public void setVelocity(double x, double y, double dt) {
        double vx_new = velocity.getX() + x * dt;
        double vy_new = velocity.getY() + y * dt;
        this.velocity = new Point2D.Double(vx_new, vy_new) ;
    }

    public Path2D getPath() {
        return path;
    }

    public void addPath(double x, double y) {
        tailPoints.add(new Point2D.Double(x, y));
        path.lineTo(x + 10, y + 10);
        if(tailPoints.size() >= maxLen){
            tailPoints.removeFirst();
            path.reset();
            path.moveTo(tailPoints.getFirst().getX() + 10, tailPoints.getFirst().getY() + 10);
            for(int i = 1; i < tailPoints.size(); i++){
                Point2D p = tailPoints.get(i);
                path.lineTo(p.getX() + 10, p.getY() + 10);
            }
        }
    }

    public double getKineticEnergy(){
        double velocity = Math.sqrt(Math.pow(getVelocity().getY(), 2) + Math.pow(getVelocity().getX(), 2));
        return 0.5 * mass * velocity * velocity;
    }

    public double getPotentialEnergy(Particle particle2){
        double x = this.getX() - particle2.getX();
        double y = this.getY() - particle2.getY();

        double distance  = Math.sqrt(x*x + y*y);

        return this.mass* particle2.mass/Math.abs(distance);
    }
}
