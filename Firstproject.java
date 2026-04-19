import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class Firstproject extends JPanel {

    // =========================
    // ROBOT STATE
    // =========================
    double x = 100;
    double y = 100;
    double heading = 0;
    double desiredHeading = 0;

    final int FIELD_SIZE = 650;
    final int ROBOT_SIZE = 15;
    final int RADIUS = ROBOT_SIZE / 2;

    // =========================
    // MODES
    // 0 = AUTONOMOUS
    // 1 = TELEOP
    // =========================
    int opMode = 0;
  
    // =========================
    // INPUT
    // =========================
    boolean forward, back, turnLeft, turnRight;

    // =========================
    // WORLD
    // =========================
    List<Rectangle> obstacles = new ArrayList<>();
    Random rand = new Random();

    // =========================
    // AUTON STATE
    // =========================
    int steps = 0;
    double targetAngle = 0;

    public Firstproject() {
        obstaclelocs();
        setFocusable(true);
    }

    // =========================
    // OBSTACLES
    // =========================
    private void obstaclelocs() {
        obstacles.add(new Rectangle(250, 250, 60, 60));
        obstacles.add(new Rectangle(400, 300, 70, 70));
        obstacles.add(new Rectangle(150, 420, 90, 40));
        obstacles.add(new Rectangle(500, 100, 80, 80));
        obstacles.add(new Rectangle(180, 320, 90, 70));
    }

    // =========================
    // UPDATE LOOP
    // =========================
    public void update() {
        if (opMode == 0) autonomous();
        else teleop();

        repaint();
    }

    // =========================
    // AUTONOMOUS (RANDOM + SMOOTH)
    // =========================
    public void autonomous() {

        double speed = 2;

        if (steps <= 0) {
            targetAngle = rand.nextInt(360);
            steps = 50 + rand.nextInt(50);
        }

        steps--;

        double rad = Math.toRadians(targetAngle);

        double nextX = x + Math.cos(rad) * speed;
        double nextY = y + Math.sin(rad) * speed;

        Rectangle future = new Rectangle((int) nextX, (int) nextY, ROBOT_SIZE, ROBOT_SIZE);

        for (Rectangle r : obstacles) {
            if (future.intersects(r)) {
                targetAngle += 120;
                steps = 0;
                return;
            }
        }

        if (!inBounds(nextX, nextY)) {
            targetAngle += 120;
            steps = 0;
            return;
        }

        // smooth heading change
        desiredHeading = targetAngle;

        double diff = normalizeAngle(desiredHeading - heading);
        double turnSpeed = 5;

        diff = Math.max(-turnSpeed, Math.min(turnSpeed, diff));
        heading += diff;

        x = nextX;
        y = nextY;
    }

    // =========================
    // TELEOP
    // =========================
    public void teleop() {

        double speed = 3;

        if (turnLeft) heading -= 4;
        if (turnRight) heading += 4;

        double rad = Math.toRadians(heading);

        double moveX = 0;
        double moveY = 0;

        if (forward) {
            moveX += Math.cos(rad) * speed;
            moveY += Math.sin(rad) * speed;
        }

        if (back) {
            moveX -= Math.cos(rad) * speed;
            moveY -= Math.sin(rad) * speed;
        }

        double nextX = x + moveX;
        double nextY = y + moveY;

        Rectangle future = new Rectangle((int) nextX, (int) nextY, ROBOT_SIZE, ROBOT_SIZE);

        for (Rectangle r : obstacles) {
            if (future.intersects(r)) return;
        }

        if (inBounds(nextX, nextY)) {
            x = nextX;
            y = nextY;
        }
    }

    // =========================
    // BOUNDARY CHECK
    // =========================
    private boolean inBounds(double nx, double ny) {
        return nx - RADIUS >= 0 &&
               nx + RADIUS <= FIELD_SIZE &&
               ny - RADIUS >= 0 &&
               ny + RADIUS <= FIELD_SIZE;
    }

    // =========================
    // ANGLE FIX
    // =========================
    private double normalizeAngle(double a) {
        while (a > 180) a -= 360;
        while (a < -180) a += 360;
        return a;
    }

    // =========================
    // DRAW
    // =========================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        g2.drawRect(0, 0, FIELD_SIZE, FIELD_SIZE);

        g2.setColor(Color.RED);
        for (Rectangle r : obstacles) {
            g2.fillRect(r.x, r.y, r.width, r.height);
        }

        g2.setColor(Color.GREEN);
        g2.fillOval((int) x, (int) y, ROBOT_SIZE, ROBOT_SIZE);

        g2.setColor(Color.WHITE);
        int lx = (int) (x + Math.cos(Math.toRadians(heading)) * 25);
        int ly = (int) (y + Math.sin(Math.toRadians(heading)) * 25);
        g2.drawLine((int) x + RADIUS, (int) y + RADIUS, lx, ly);

        g2.drawString("MODE: " + (opMode == 0 ? "AUTONOMOUS" : "TELEOP"), 10, 20);
        g2.drawString("SPACE = TeleOp | ENTER = Auto", 10, 40);
    }

    // =========================
    // MAIN
    // =========================
    public static void main(String[] args) {

        Firstproject panel = new Firstproject();

        JFrame frame = new JFrame("FTC Robot Simulator");
        frame.setSize(700, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setVisible(true);

        // IMPORTANT: focus fix
        SwingUtilities.invokeLater(panel::requestFocusInWindow);

        panel.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                    case KeyEvent.VK_W -> panel.forward = true;
                    case KeyEvent.VK_S -> panel.back = true;
                    case KeyEvent.VK_A -> panel.turnLeft = true;
                    case KeyEvent.VK_D -> panel.turnRight = true;

                    case KeyEvent.VK_SPACE -> panel.opMode = 1;
                    case KeyEvent.VK_ENTER -> panel.opMode = 0;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

                switch (e.getKeyCode()) {

                    case KeyEvent.VK_W -> panel.forward = false;
                    case KeyEvent.VK_S -> panel.back = false;
                    case KeyEvent.VK_A -> panel.turnLeft = false;
                    case KeyEvent.VK_D -> panel.turnRight = false;
                }
            }
        });

        Timer timer = new Timer(50, e -> panel.update());
        timer.start();
    }
}