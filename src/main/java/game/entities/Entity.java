
package game.entities;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public abstract class Entity {
    
    private Image image;
    private ImageView imageView;
    
    private Point2D acceleration;
    private boolean alive;    
    
    private double w;
    private double h;
    
    private Shape defBounds;
    
    private Rectangle middle;
    
    public Entity(String image, int x, int y) {
        this.image = new Image(image);
        this.imageView = new ImageView(this.image);
        this.alive = true;
        
        this.acceleration = new Point2D(0, 0);
        
        this.w = this.image.getWidth();
        this.h = this.image.getHeight();   
        
        this.defBounds = new Circle(w);       
        
        this.imageView.setTranslateX(x);
        this.imageView.setTranslateY(y);        

        this.middle = new Rectangle(5, 5);
        this.middle.setFill(Color.TRANSPARENT);
        this.middle.setTranslateX(this.imageView.getTranslateX() + (this.w / 2));
        this.middle.setTranslateY(this.imageView.getTranslateY() + (this.h / 2));
    }
    
    public ImageView getDisplay() {
        return this.imageView;
    }
    
    public void turnLeft() {
        this.imageView.setRotate(this.imageView.getRotate() - 2);
    }
    
    public void turnRight() {
        this.imageView.setRotate(this.imageView.getRotate() + 2);
    } 
    
    public void update() {
        this.imageView.setTranslateX(this.imageView.getTranslateX() + this.acceleration.getX());
        this.imageView.setTranslateY(this.imageView.getTranslateY() + this.acceleration.getY());
        this.middle.setTranslateX(this.middle.getTranslateX() + this.acceleration.getX());
        this.middle.setTranslateY(this.middle.getTranslateY() + this.acceleration.getY());
    }
    
    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.imageView.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.imageView.getRotate()));
        
        changeX *= 0.015;
        changeY *= 0.015;    
        
        this.acceleration = this.acceleration.add(changeX, changeY);
    }
    
    // Take current speed into consideration
    public void jump() {
        double changeX = Math.cos(Math.toRadians(this.imageView.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.imageView.getRotate()));        
        
        changeX *= 2;
        changeY *= 2;  
        
        setAcceleration(new Point2D(changeX, changeY));
    }
    
    public boolean isAlive() {
        return this.alive;
    }
    
    public void setAlive(boolean state) {
        this.alive = state;
    }
    
    public void setAcceleration(Point2D point) {
        this.acceleration = point;
    }
    
    public Point2D getAcceleration() {
        return this.acceleration;
    }
    
    public double getWidth() {
        return this.w;
    }
    
    public double getHeight() {
        return this.h;
    }
    
    public boolean collidesWith(Entity entity) {
        Shape check = Shape.intersect(this.getBounds(), entity.getBounds());
        return check.getBoundsInLocal().getWidth() != -1;
    }
    
    public Shape getBounds() {
        return defBounds;
    }
    
    public Rectangle getMiddle() {
        return this.middle;
    }
    
    public void setMiddlePointX(double xPos) {
        this.middle.setTranslateX(xPos);
    }
    
    public void setMiddlePointY(double yPos) {
        this.middle.setTranslateY(yPos);
    }
}
