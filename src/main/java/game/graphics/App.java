package game.graphics;

import game.entities.Asteroid;
import game.entities.Entity;
import game.entities.Projectile;
import game.entities.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {
    
    public static int WIDTH = 0;
    public static int HEIGHT = 0;
    int redPoints = 0;
    int bluePoints = 0;
            
    @Override
    public void start(Stage stage) {

        // Auto detect screen size
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        this.WIDTH = (int) screenBounds.getWidth();
        this.HEIGHT = (int) screenBounds.getHeight();
        System.out.println(screenBounds);

        // Entities
        Entity shipRed = new Ship(WIDTH / 4, HEIGHT / 4, "/game_redPlayer.png");
        Entity shipBlue = new Ship(WIDTH - 200, HEIGHT - 200, "/game_bluePlayer.png");
        
        List<Projectile> projectilesRed = new ArrayList<>();
        List<Projectile> projectilesBlue = new ArrayList<>();
        
        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), "/game_asteroid_1.png");
            if (!asteroid.collidesWith(shipRed) && !asteroid.collidesWith(shipBlue)) {
                asteroids.add(asteroid);
            }
        }

        List<Asteroid> rubbles = new ArrayList<>();               
               
        // Graphic and animation
        List<Image> images = new ArrayList<>();
        for (int i = 1; i < 6; i++) { //should be 7 for full sprite
            images.add(new Image("/explosion_" + i + ".png"));
        }

        List<Image> asteroidExplosion = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            asteroidExplosion.add(new Image("/asteroidExplode_" + i + ".png"));
        }
        
        List<Image> transitionExplosion = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            transitionExplosion.add(new Image("/asteroidTransition_" + i + ".png"));
        }
        
        // Background
        BackgroundImage background = new BackgroundImage(new Image("/game_background.png", WIDTH, HEIGHT, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

        // Layout
        Pane gamePane = new Pane();
        gamePane.setBackground(new Background(background));
        gamePane.setPrefSize(WIDTH, HEIGHT);        
        gamePane.getChildren().add(shipRed.getBounds());
        gamePane.getChildren().add(shipRed.getDisplay());        
        
        gamePane.getChildren().add(shipBlue.getDisplay()); 
        gamePane.getChildren().add(shipBlue.getBounds());
        
        asteroids.forEach(asteroid -> {
            gamePane.getChildren().add(asteroid.getBounds());
            gamePane.getChildren().add(asteroid.getDisplay());       
                });
        
        Scene gameScene = new Scene(gamePane);

        // Input controller
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        
        Map<KeyCode,Boolean> buttonController = new HashMap<>();
        gameScene.setOnKeyPressed((key) -> {
            buttonController.put(key.getCode(), Boolean.TRUE);
            
            // Avoid burst spamming of projectiles
            if (key.getCode() == KeyCode.ENTER || key.getCode() == KeyCode.SPACE) {
                executorService.schedule(() -> {
                    buttonController.put(key.getCode(), Boolean.FALSE);
                }, 25, TimeUnit.MILLISECONDS);
            }
        });
        
        gameScene.setOnKeyReleased((key) -> {
            buttonController.put(key.getCode(), Boolean.FALSE);
        });        
        
        // AnimationTimer
        new AnimationTimer() {
            @Override
            public void handle(long present) {
                // Exit
                if (buttonController.getOrDefault(KeyCode.ESCAPE, false)) {
                    Platform.exit();
                }
                
                // Red player controls
                if (buttonController.getOrDefault(KeyCode.LEFT, false)) {
                    shipRed.turnLeft();
                }
                
                if (buttonController.getOrDefault(KeyCode.RIGHT, false)) {
                    shipRed.turnRight();
                }
                
                if (buttonController.getOrDefault(KeyCode.UP, false)) {
                    shipRed.accelerate();
                }
                
                if (buttonController.getOrDefault(KeyCode.SHIFT, false)) {
                    // TODO: Add visual effect for this
                    shipRed.jump();
                }                    
                
                if (buttonController.getOrDefault(KeyCode.ENTER, false)) {                        
                    Projectile projectile = new Projectile((int) shipRed.getMiddle().getTranslateX() - 9, (int) shipRed.getMiddle().getTranslateY() - 9, "/projectile_longRed.png");
                   
                    projectile.getDisplay().setRotate(shipRed.getDisplay().getRotate());
                    projectile.getBounds().setRotate(shipRed.getDisplay().getRotate());
                    
                    projectilesRed.add(projectile);

                    projectile.accelerate();
                    projectile.setAcceleration(projectile.getAcceleration().normalize().multiply(8));
                    
                    gamePane.getChildren().add(projectile.getBounds());
                    gamePane.getChildren().add(projectile.getDisplay());
                }
                
                // Blue player controls
                if (buttonController.getOrDefault(KeyCode.D, false)) {
                    shipBlue.turnRight();
                }
                
                if (buttonController.getOrDefault(KeyCode.A, false)) {
                    shipBlue.turnLeft();
                }
                
                if (buttonController.getOrDefault(KeyCode.W, false)) {
                    shipBlue.accelerate();
                }
                
                if (buttonController.getOrDefault(KeyCode.E, false)) {
                    shipRed.jump();
                }        
                
                if (buttonController.getOrDefault(KeyCode.SPACE, false)) {
                    Projectile projectile = new Projectile((int) shipBlue.getDisplay().getTranslateX(),
                    (int) shipBlue.getDisplay().getTranslateY(), "/projectile_longBlue.png");
                    
                    projectile.getDisplay().setRotate(shipBlue.getDisplay().getRotate());
                    
                    projectilesBlue.add(projectile);

                    projectile.accelerate();
                    projectile.setAcceleration(projectile.getAcceleration().normalize().multiply(6));
                    
                    gamePane.getChildren().add(projectile.getBounds());
                    gamePane.getChildren().add(projectile.getDisplay());
                }

                // Red projectile hit detection on asteroids
                projectilesRed.forEach(projectile -> {
                    asteroids.forEach(asteroid -> {
                        if (projectile.collidesWith(asteroid)) {
                            // Set projectile dead
                            projectile.setAlive(false);
                            asteroid.shatter();
                                            
                            // Play explosion animation for each removed projectile
                            ImageView exp = new ImageView();
                            Transition projectileExplode = new Transition() {
                                {
                                setCycleDuration(Duration.millis(450)); // total time for animation

                                setOnFinished(handler -> {
                                    gamePane.getChildren().remove(exp);             
                                });
                                }

                                @Override
                                protected void interpolate(double fraction) {
                                    int index = (int) (fraction*(images.size()-1));
                                    exp.setImage(images.get(index));
                                }
                            };                                                        

                            gamePane.getChildren().add(exp);
                            // Tweak this, explosion doesn't draw exactly on the tip of projectile
                            exp.setTranslateX(projectile.getMiddle().getTranslateX() - 50);
                            exp.setTranslateY(projectile.getMiddle().getTranslateY() - 50);
                            projectileExplode.play();
                        }

                        if (!asteroid.isAlive()) {   
                            Random rnd = new Random();                            
                            Asteroid rubble = new Asteroid((int) asteroid.getMiddle().getTranslateX() - 64 + (10 - rnd.nextInt(20)),
                            (int) asteroid.getMiddle().getTranslateY() - 64 + (10 - rnd.nextInt(20)), "/game_asteroid_1.png", false);
                            
                            rubble.getDisplay().setRotate(projectile.getDisplay().getRotate() + rnd.nextInt(40));
                            
                            // Placeholder
                            rubble.getDisplay().setFitHeight(64);
                            rubble.getDisplay().setFitWidth(64);

                            for (int i = 0; i < 40; i++) {
                                rubble.accelerate();
                            }
                            
                            rubble.setRotationalMotion(1.5 - (rnd.nextInt(300) / 100));
                            rubble.getBounds().setRadius(20);

                            gamePane.getChildren().add(rubble.getDisplay());
                            gamePane.getChildren().add(rubble.getBounds());
                            // Added to a separate list to avoid concurrent modification error
                            rubbles.add(rubble);
                        }
                    });
                });

                // Blue projectile hit detection on asteroids
                projectilesBlue.forEach(projectile -> {
                    asteroids.forEach(asteroid -> {
                        if (projectile.collidesWith(asteroid)) {
                            // Set projectile dead
                            projectile.setAlive(false);
                            asteroid.shatter();

                            // Play explosion animation for each removed projectile
                            ImageView exp = new ImageView();
                            Transition projectileExplode = new Transition() {
                                {
                                    setCycleDuration(Duration.millis(450));

                                    setOnFinished(handler -> {
                                        gamePane.getChildren().remove(exp);
                                    });
                                }

                                @Override
                                protected void interpolate(double fraction) {
                                    int index = (int) (fraction*(images.size()-1));
                                    exp.setImage(images.get(index));
                                }
                            };

                            gamePane.getChildren().add(exp);
                            exp.setTranslateX(projectile.getMiddle().getTranslateX() - 50);
                            exp.setTranslateY(projectile.getMiddle().getTranslateY() - 50);
                            projectileExplode.play();
                        }

                        if (!asteroid.isAlive()) {
                            Random rnd = new Random();
                            Asteroid rubble = new Asteroid((int) asteroid.getMiddle().getTranslateX() - 64 + (10 - rnd.nextInt(20)),
                                    (int) asteroid.getMiddle().getTranslateY() - 64 + (10 - rnd.nextInt(20)), "/game_asteroid_1.png", false);

                            rubble.getDisplay().setRotate(projectile.getDisplay().getRotate() + rnd.nextInt(40));

                            // Placeholder
                            rubble.getDisplay().setFitHeight(64);
                            rubble.getDisplay().setFitWidth(64);

                            for (int i = 0; i < 40; i++) {
                                rubble.accelerate();
                            }

                            rubble.setRotationalMotion(1.5 - (rnd.nextInt(300) / 100));
                            rubble.getBounds().setRadius(20);

                            gamePane.getChildren().add(rubble.getDisplay());
                            gamePane.getChildren().add(rubble.getBounds());

                            // Add to a separate list to avoid concurrent modification exception
                            rubbles.add(rubble);
                        }
                    });
                });
                
                // Removing dead projectiles
                projectilesRed.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .forEach(projectile -> {
                            gamePane.getChildren().remove(projectile.getDisplay());
                            gamePane.getChildren().remove(projectile.getBounds());
                });
                            
                projectilesRed.removeAll(projectilesRed.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .collect(Collectors.toList()));
                
                projectilesBlue.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .forEach(projectile -> {
                            gamePane.getChildren().remove(projectile.getDisplay());
                            gamePane.getChildren().remove(projectile.getBounds());
                });
                
                projectilesBlue.removeAll(projectilesBlue.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .collect(Collectors.toList()));
                
                asteroids.stream()
                        .forEach(asteroid -> {
                            if (asteroid.getLife() == 20 || asteroid.getLife() == 15 || asteroid.getLife() == 10 || asteroid.getLife() == 5) {
                                // One additional shatter to prevent looped animations
                                asteroid.shatter();
                                Transition transitionExplode = new Transition() {
                                {
                                setCycleDuration(Duration.millis(400));

                                setOnFinished(handler -> {
                                    asteroid.setAnimationStatus(false);
                                    if (asteroid.getLife() >= 15) {
                                        asteroid.getDisplay().setImage(new Image("/game_asteroid_2.png"));
                                    } else if (asteroid.getLife() > 10) {
                                        asteroid.getDisplay().setImage(new Image("/game_asteroid_3.png"));
                                    } else if (asteroid.getLife() > 5) {
                                        asteroid.getDisplay().setImage(new Image("/game_asteroid_4.png"));
                                    } else {
                                        asteroid.setAlive(false);
                                    }                 
                                });
                                }

                                @Override
                                protected void interpolate(double fraction) {
                                    int index = (int) (fraction*(transitionExplosion.size()-1));
                                    asteroid.getDisplay().setImage(transitionExplosion.get(index));
                            }
                            };
                                if (asteroid.getAnimationStatus() == false) {
                                    transitionExplode.play();
                                    asteroid.setAnimationStatus(true);
                                }
                            } 
                        });
                
                // Removing dead asteroids
                asteroids.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .forEach(asteroid -> {
                            
                            ImageView expAsteroid = new ImageView();
                            Transition asteroidExplode = new Transition() {
                                {
                                setCycleDuration(Duration.millis(450));

                                setOnFinished(handler -> {
                                    gamePane.getChildren().remove(expAsteroid);
                                });
                                }

                                @Override
                                protected void interpolate(double fraction) {
                                    int index = (int) (fraction*(asteroidExplosion.size()-1));
                                    expAsteroid.setImage(asteroidExplosion.get(index));
                                }
                            };
                            
                            gamePane.getChildren().add(expAsteroid);

                            expAsteroid.setTranslateX(asteroid.getMiddle().getTranslateX() - 128);
                            expAsteroid.setTranslateY(asteroid.getMiddle().getTranslateY() - 128);
                            
                            // Removing dead asteroids
                            gamePane.getChildren().remove(asteroid.getDisplay());
                            gamePane.getChildren().remove(asteroid.getBounds());
                            
                            // Playing the animation
                            asteroidExplode.play();
                });
                
                asteroids.removeAll(asteroids.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .collect(Collectors.toList()));

                for (int i = 0; i < 2; i++) {
                    for (Asteroid placeholder : rubbles) {
                        if (!(asteroids.contains(placeholder))) {
                            asteroids.add(placeholder);
                        }
                    }
                    rubbles.clear();
                }
                
                // Updating projectile positions
                projectilesRed.forEach((projectile) -> projectile.update());
                projectilesBlue.forEach((projectile) -> projectile.update());
                asteroids.forEach((asteroid) -> asteroid.update());

                // Stop the application on asteroid collision
                asteroids.forEach((asteroid) -> {
                    if (asteroid.collidesWith(shipBlue)) {
                        stop();
                    }

                    if (asteroid.collidesWith(shipRed)) {
                        stop();
                    }
                });

                // Projectile hit detection for blue ship
                projectilesRed.forEach((projectile) -> {
                    if (projectile.collidesWith(shipBlue)) {
                        // Play explosion animation on hit
                        ImageView exp = new ImageView();
                        Transition projectileExplode = new Transition() {
                            {
                                setCycleDuration(Duration.millis(450));

                                setOnFinished(handler -> {
                                    gamePane.getChildren().remove(exp);
                                });
                            }

                            @Override
                            protected void interpolate(double fraction) {
                                int index = (int) (fraction*(images.size()-1));
                                exp.setImage(images.get(index));
                            }
                        };

                        gamePane.getChildren().add(exp);
                        exp.setTranslateX(projectile.getMiddle().getTranslateX() - 50);
                        exp.setTranslateY(projectile.getMiddle().getTranslateY() - 50);
                        projectileExplode.play();

                        projectile.setAlive(false);
                        redPoints++;

                        // Print points
                        System.out.println("RED PLAYER POINTS: " + redPoints);
                    }
                });

                // Projectile hit detection for red ship
                projectilesBlue.forEach((projectile) -> {
                    if (projectile.collidesWith(shipRed)) {
                        // Explosion on collision
                        ImageView exp = new ImageView();
                        Transition projectileExplode = new Transition() {
                            {
                                setCycleDuration(Duration.millis(450));

                                setOnFinished(handler -> {
                                    gamePane.getChildren().remove(exp);
                                });
                            }

                            @Override
                            protected void interpolate(double fraction) {
                                int index = (int) (fraction*(images.size()-1));
                                exp.setImage(images.get(index));
                            }
                        };

                        gamePane.getChildren().add(exp);
                        // Tweak this, explosion doesn't draw exactly on the tip of projectile
                        exp.setTranslateX(projectile.getMiddle().getTranslateX() - 50);
                        exp.setTranslateY(projectile.getMiddle().getTranslateY() - 50);
                        projectileExplode.play();

                        projectile.setAlive(false);
                        bluePoints++;
                        System.out.println("RED PLAYER POINTS: " + bluePoints);
                    }
                });

                if (bluePoints >= 10) {
                    stop();
                }

                if (redPoints >= 10) {
                    stop();
                }

                shipRed.update();
                shipBlue.update();
            }
        }.start();

        // Stage
        stage.setScene(gameScene);
        stage.setFullScreen(true);
        stage.show();
    }
}