
package game.entities;

import game.graphics.App;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class Ship extends Entity {
       
    private Circle bounds;
        
    public Ship(int x, int y, String file) {
        super(file, x, y);
        this.bounds = new Circle(super.getWidth() - 30, Color.BLUE);
        this.bounds.setCenterX(super.getDisplay().getTranslateX() + 30);
        this.bounds.setCenterY(super.getDisplay().getTranslateY() + 30);
        this.bounds.setFill(Color.TRANSPARENT);
    }    
    
    @Override
    public void update() {
        super.update();
        this.bounds.setCenterX(this.bounds.getCenterX() + super.getAcceleration().getX());
        this.bounds.setCenterY(this.bounds.getCenterY() + this.getAcceleration().getY());
        
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
    
    public Circle getBounds() {
        return this.bounds;
    }
}
