
package game.entities;

import game.graphics.App;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Projectile extends Entity {    
    
    private Rectangle bounds;
    
    public Projectile(int x, int y, String file) {
        super(file, x, y);
        
        this.bounds = new Rectangle(super.getWidth(), super.getHeight(), Color.BLUE);
        this.bounds.setTranslateX(super.getDisplay().getTranslateX());
        this.bounds.setTranslateY(super.getDisplay().getTranslateY());
        this.bounds.setWidth(super.getWidth());
        this.bounds.setHeight(super.getHeight()); 
        this.bounds.setFill(Color.TRANSPARENT);
    }
    
    @Override
    public void update() {
        super.update();
        this.bounds.setTranslateX(this.bounds.getTranslateX() + super.getAcceleration().getX());
        this.bounds.setTranslateY(this.bounds.getTranslateY() + this.getAcceleration().getY());
        
        // Remove projectile if off screen
        if (this.getDisplay().getTranslateX() < 0 || this.getDisplay().getTranslateX() > App.WIDTH ||
                this.getDisplay().getTranslateY() < 0 || this.getDisplay().getTranslateY() > App.HEIGHT) {
            super.setAlive(false);
        }        
    }
    
    public Rectangle getBounds() {
        return this.bounds;
    }
    
    @Override
    public boolean collidesWith(Entity entity) {
        Shape check = Shape.intersect(this.getBounds(), entity.getBounds());
        return check.getBoundsInLocal().getWidth() != -1;
    }
}
