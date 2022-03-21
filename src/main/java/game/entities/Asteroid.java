
package game.entities;

import game.graphics.App;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

// Final product will likely have this as abstract class
// And then have three types of asteroids - small, medium, large
public class Asteroid extends Entity {
    
    private double rotationalMotion;
    private Circle bounds;
    private int life;
    private boolean animationStatus;
    
    public Asteroid(int x, int y, String file) {
        super(file, x, y);
        
        Random rnd = new Random();
        super.getDisplay().setRotate(rnd.nextInt(360));
        int acceleration = 15 + rnd.nextInt(15);        
        for (int i = 0; i < acceleration; i++) {
            accelerate();
        }
        
        this.rotationalMotion = 0.5 - rnd.nextDouble();
        
        this.life = 25;
        
        this.bounds = new Circle(super.getWidth() - 65, Color.RED);
        this.bounds.setCenterX(super.getDisplay().getTranslateX() + 65);
        this.bounds.setCenterY(super.getDisplay().getTranslateY() + 65);   
        this.bounds.setFill(Color.TRANSPARENT);
        
        this.animationStatus = false;
    }

    public Asteroid(int x, int y, String file, Boolean rubble) {
        super(file, x, y);
        
        this.life = 25;
        
        this.rotationalMotion = 0;
        
        this.bounds = new Circle(super.getWidth() - 65, Color.RED);
        this.bounds.setCenterX(super.getDisplay().getTranslateX() + 65);
        this.bounds.setCenterY(super.getDisplay().getTranslateY() + 65);   
        this.bounds.setFill(Color.TRANSPARENT);
        
        this.animationStatus = false;       
    }
    
    public void shatter() {
        this.life--;
    } 
    
    @Override
    public void update() {
        super.update();
        super.getDisplay().setRotate(super.getDisplay().getRotate() + this.rotationalMotion);
        this.bounds.setCenterX(this.bounds.getCenterX() + super.getAcceleration().getX());
        this.bounds.setCenterY(this.bounds.getCenterY() + this.getAcceleration().getY());
        
        // Stay on screen
        // To refactor for smooth transistion later with getInBounds
        if (this.getDisplay().getTranslateX() < 0) {
            this.getDisplay().setTranslateX(this.getDisplay().getTranslateX() + App.WIDTH);
            this.bounds.setCenterX(this.bounds.getCenterX() + App.WIDTH);
            super.setMiddlePointX(super.getMiddle().getTranslateX() + App.WIDTH);
        }
        
        if (this.getDisplay().getTranslateX() > App.WIDTH) {
            this.getDisplay().setTranslateX(this.getDisplay().getTranslateX() % App.WIDTH);
            this.bounds.setCenterX(this.bounds.getCenterX() % App.WIDTH);
            super.setMiddlePointX(super.getMiddle().getTranslateX() % App.WIDTH);
        }

        if (this.getDisplay().getTranslateY() < 0) {
            this.getDisplay().setTranslateY(this.getDisplay().getTranslateY() + App.HEIGHT);
            this.bounds.setCenterY(this.bounds.getCenterY() + App.HEIGHT);
            super.setMiddlePointY(super.getMiddle().getTranslateY() + App.HEIGHT);
        }

        if (this.getDisplay().getTranslateY() > App.HEIGHT) {
            this.getDisplay().setTranslateY(this.getDisplay().getTranslateY() % App.HEIGHT);
            this.bounds.setCenterY(this.bounds.getCenterY() % App.HEIGHT);
            super.setMiddlePointY(super.getMiddle().getTranslateY() % App.HEIGHT);
        }
    }
    
    @Override
    public void turnLeft() {
        super.turnLeft();
        this.bounds.setRotate(super.getDisplay().getRotate() - 2);
    }
    
    @Override
    public void turnRight() {
        super.turnRight();
        this.bounds.setRotate(super.getDisplay().getRotate() + 2);
    }
    
    @Override
    public Circle getBounds() {
        return this.bounds;
    }
    
    public int getLife() {
        return this.life;
    }
    
    public boolean getAnimationStatus() {
        return this.animationStatus;
    }
    
    public void setAnimationStatus(boolean setStatus) {
        this.animationStatus = setStatus;
    }
    
    public void setRotationalMotion(double number) {
        this.rotationalMotion = number;
    }
}
