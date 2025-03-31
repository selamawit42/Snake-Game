package snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    private int[] snakexlength = new int[750];
    private int[] snakeylength = new int[750];
    private Random random = new Random();
    private int enemyx, enemyy;
    private int bonex, boney; 
    private boolean boneVisible = false;
    private Timer boneTimer; 
    private int boneTimeLeft = 8; 

    private boolean right = true;
    private boolean left = false;
    private boolean up = false;
    private boolean down = false;
    private int moves = 0;
    private int score = 0;
    private int highestScore = 0; 

    private ImageIcon rightmouth = new ImageIcon(getClass().getResource("rightmouth.png"));
    private ImageIcon leftmouth = new ImageIcon(getClass().getResource("leftmouth.png"));
    private ImageIcon upmouth = new ImageIcon(getClass().getResource("upmouth.png"));
    private ImageIcon downmouth = new ImageIcon(getClass().getResource("downmouth.png"));
    private ImageIcon snaketitle = new ImageIcon(getClass().getResource("snaketitle.jpg"));
    private ImageIcon enemy = new ImageIcon(getClass().getResource("enemy.png"));
    private ImageIcon snakebody = new ImageIcon(getClass().getResource("snakeimage.png"));
    private ImageIcon bone = new ImageIcon(getClass().getResource("ftb65ypf.png")); 

    private Timer timer;
    private int delay = 200;
    private int lengthofsnake = 2;
    private boolean gameOver = false;
    private int enemiesEaten = 0;

    public SnakeGame() {
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
        newEnemy();
        snakexlength[0] = 100; 
        snakeylength[0] = 100; 
        
        // Initialize bone timer
        boneTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (boneTimeLeft > 0) {
                    boneTimeLeft--;
                } else {
                    boneVisible = false; 
                    boneTimer.stop();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.white);
        g.drawRect(24, 10, 851, 55);
        g.drawRect(24, 10, 851, 576);
        snaketitle.paintIcon(this, g, 25, 12);
        g.setColor(Color.black);
        g.fillRect(25, 75, 850, 575);

        // Draw the snake
        for (int n = 0; n < lengthofsnake; n++) {
            if (n == 0) {
                if (right) {
                    rightmouth.paintIcon(this, g, snakexlength[n], snakeylength[n]);
                } else if (left) {
                    leftmouth.paintIcon(this, g, snakexlength[n], snakeylength[n]);
                } else if (up) {
                    upmouth.paintIcon(this, g, snakexlength[n], snakeylength[n]);
                } else if (down) {
                    downmouth.paintIcon(this, g, snakexlength[n], snakeylength[n]);
                }
            } else {
                snakebody.paintIcon(this, g, snakexlength[n], snakeylength[n]);
            }
        }

        // Draw the enemy
        enemy.paintIcon(this, g, enemyx, enemyy);

        // Draw the bone if visible
        if (boneVisible) {
            bone.paintIcon(this, g, bonex, boney);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Time left for bone: " + boneTimeLeft + "s", 20, 50);
        }

        // Game over logic
        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Over", 300, 200);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Press Enter To Restart", 250, 350);
        }

        // Score display
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 710, 35);
        g.drawString("Highest Score: " + highestScore, 710, 55);
    }

    public void actionPerformed(ActionEvent e) {
        for (int i = lengthofsnake - 1; i > 0; i--) {
            snakexlength[i] = snakexlength[i - 1];
            snakeylength[i] = snakeylength[i - 1];
        }

        if (left) {
            snakexlength[0] -= 25;
        }
        if (right) {
            snakexlength[0] += 25;
        }
        if (up) {
            snakeylength[0] -= 25;
        }
        if (down) {
            snakeylength[0] += 25;
        }

        // Boundary conditions
        if (snakexlength[0] > 850) snakexlength[0] = 25;
        if (snakexlength[0] < 25) snakexlength[0] = 850;
        if (snakeylength[0] > 625) snakeylength[0] = 75;
        if (snakeylength[0] < 75) snakeylength[0] = 625;

        checkCollision();
        collidesWithEnemy();
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT && (!right)) {
            left = true;
            right = false;
            up = false;
            down = false;
            moves++;
        }
        if (key == KeyEvent.VK_RIGHT && (!left)) {
            left = false;
            right = true;
            up = false;
            down = false;
            moves++;
        }
        if (key == KeyEvent.VK_UP && (!down)) {
            left = false;
            right = false;
            up = true;
            down = false;
            moves++;
        }
        if (key == KeyEvent.VK_DOWN && (!up)) {
            left = false;
            right = false;
            up = false;
            down = true;
            moves++;
        }
        if (key == KeyEvent.VK_ENTER && gameOver) {
            resetGame();
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public void checkCollision() {
        if (snakexlength[0] < 25 || snakexlength[0] > 850 || snakeylength[0] < 75 || snakeylength[0] > 625) {
            gameOver = true;
            timer.stop();
        }

        for (int i = 1; i < lengthofsnake; i++) {
            if (snakexlength[0] == snakexlength[i] && snakeylength[0] == snakeylength[i]) {
                gameOver = true;
                timer.stop();
            }
        }
    }

    public void newEnemy() {
        enemyx = 25 + (random.nextInt(34) * 25);
        enemyy = 75 + (random.nextInt(22) * 25);

        for (int i = 0; i < lengthofsnake; i++) {
            if (snakexlength[i] == enemyx && snakeylength[i] == enemyy) {
                newEnemy();
                return;
            }
        }
    }

    public void collidesWithEnemy() {
        if (snakexlength[0] == enemyx && snakeylength[0] == enemyy) {
            newEnemy();
            lengthofsnake++;
            enemiesEaten++;

            // Generate a bone randomly after every 5 enemies eaten
            if (enemiesEaten % 5 == 0) {
                generateBone();
            }

            // Increment score by 1 for eating an enemy
            score++;

            // Update highest score if necessary
            if (score > highestScore) {
                highestScore = score; 
            }

            snakexlength[lengthofsnake - 1] = snakexlength[lengthofsnake - 2];
            snakeylength[lengthofsnake - 1] = snakeylength[lengthofsnake - 2];
        }

        // Check if the snake eats the bone
        if (boneVisible && snakexlength[0] == bonex && snakeylength[0] == boney) {
            // Score based on remaining time
            if (boneTimeLeft >= 6) {
                score += 5; // 6-8 seconds left
            } else if (boneTimeLeft >= 4) {
                score += 4; // 4-5 seconds left
            } else if (boneTimeLeft >= 2) {
                score += 2; // 2-3 seconds left
            } else {
                score += 1; // 0-1 seconds left
            }

            boneVisible = false; // Hide the bone after eating
            boneTimer.stop(); // Stop the timer
        }
    }

    public void generateBone() {
        bonex = 25 + (random.nextInt(34) * 25);
        boney = 75 + (random.nextInt(22) * 25);
        boneVisible = true; 
        boneTimeLeft = 8; 
        boneTimer.restart(); // Ensure the timer is restarted
    }

    public void resetGame() {
        score = 0;
        highestScore = Math.max(highestScore, score); 
        lengthofsnake = 2; 
        moves = 0;
        gameOver = false;
        enemiesEaten = 0; 
        delay = 200; 
        timer.setDelay(delay); 
        timer.start();
        newEnemy();
        snakexlength[0] = 100; 
        snakeylength[0] = 100; 
        boneVisible = false; 
        boneTimer.stop(); 
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Snake Game");
        frame.setBounds(10, 10, 900, 700);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SnakeGame play = new SnakeGame();
        frame.add(play);

        frame.setVisible(true);
    }
}