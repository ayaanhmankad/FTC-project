import java.util.ArrayList;
import java.util.Random;
import java.awt.geom.Rectangle2D; 

public class Firstproject {
    public static void main(String[] args) {

    Firstproject robot = new Firstproject();

    robot.obstaclelocs();

    for (int i = 0; i < 1000; i++) {
        robot.robodrivetrain();

        robot.checkBorders();

        // simple “animation” in console
        System.out.println("x: " + robot.x + " y: " + robot.y + " heading: " + robot.heading);

        try {
            Thread.sleep(50); // slows it down so you can see movement
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
    public ArrayList<Rectangle2D> obstacles = new ArrayList<>();
     public double x = 50, y = 50, heading = 0; 
      int mode= 1;
     //we are in the center of a 100 square feet(in real life) field. Speed will be slowed down to show a true proportionla relationship of speed. I used these measurements to make sure the drive train doesn't go too fast since it is fast and the field is too small. 
     public void checkBorders() {
        if (x < 0) {
             x = 0;
         } else if (x > 100) {
             x = 100;
         }
         if (y < 0) {
             y = 0;
         } else if (y > 100) {
             y = 100;
             }
            }
    public void obstaclelocs() {

    Random rand = new Random();
    int attempts = 0;

    while (obstacles.size() < 5 && attempts < 100) {

        double obsX = rand.nextDouble() * 99;
        double obsY = rand.nextDouble() * 99;

        Rectangle2D candidate =
            new Rectangle2D.Double(obsX, obsY, 1.0, 1.0);

        boolean valid = true;

        if (Math.abs(obsX - 50) < 2 && Math.abs(obsY - 50) < 2) {
            valid = false;
        }

        for (Rectangle2D existing : obstacles) {
            if (existing.intersects(candidate)) {
                valid = false;
            }
        }

        if (valid) {
            obstacles.add(candidate);
        }

        attempts++;
    }
}
    

        public void robodrivetrain() {

         if (mode == 1) {

        double step = 1.0;

        double nextX = x + step * Math.cos(Math.toRadians(heading));
        double nextY = y + step * Math.sin(Math.toRadians(heading));

        boolean blocked = false;

        for (Rectangle2D obstacle : obstacles) {
            if (obstacle.intersects(nextX, nextY, 1.0, 1.0)) {
                blocked = true;
                break;
            }
        }

        if (!blocked) {
            x = nextX;
            y = nextY;
        } else {
            heading += 10;
        }
    }
  }
}
    
    