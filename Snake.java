import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;

public class Snake {
  public static void main(String[] args) {
    new Game();
  }
}

class Game extends JFrame implements KeyListener {
  private static final long serialVersionUID = 1L;

  private interface InitConstance {
    int BLOCK_SIZE = 20;
    int FRAME_WIDTH = 30;
    int FRAME_HEIGHT = 30;
    int INIT_X = 5;
    int INIT_Y = 10;
    int COUNT = 4;
    int SPEED = 250;
    Color HEAD = Color.blue;
    Color BODY = Color.white;
    Color BEAN = Color.orange;
    Color BACKGROUND = Color.black;
  }

  private static enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
  };

  private static class Block {
    private int x;
    private int y;

    Block(int x, int y) {
      this.x = x;
      this.y = y;
    }

    int getX() {
      return x;
    }

    int getY() {
      return y;
    }

    boolean isCoincide(Block block) {
      return x == block.getX() && y == block.getY();
    }

    boolean willDash(Direction direction) {
      boolean will = false;
      switch (direction) {
        case UP:
          if (y < 1) {
            will = true;
          }
          break;
        case DOWN:
          if (y > InitConstance.FRAME_HEIGHT - 1) {
            will = true;
          }
          break;
        case LEFT:
          if (x < 1) {
            will = true;
          }
          break;
        default:
          if (x > InitConstance.FRAME_WIDTH - 1) {
            will = true;
          }
      }
      return will;
    }

    Block ahead(Direction direction) {
      int blockX = x;
      int blockY = y;
      switch (direction) {
        case UP:
          blockY -= 1;
          break;
        case DOWN:
          blockY += 1;
          break;
        case LEFT:
          blockX -= 1;
          break;
        default:
          blockX += 1;
      }
      return new Block(blockX, blockY);
    }

    void fill(Graphics graphics) {
      graphics.fill3DRect(
          x * InitConstance.BLOCK_SIZE,
          y * InitConstance.BLOCK_SIZE,
          InitConstance.BLOCK_SIZE,
          InitConstance.BLOCK_SIZE,
          true);
    }
  }

  private JPanel panel;
  private LinkedList<Block> snake;
  private Direction direction;
  private Block bean;
  private boolean isRunning;

  Game() {
    snake = new LinkedList<>();
    isRunning = true;
    initBean();
    initSnake();
    initFrame();
    initThread();
  }

  private void initBean() {
    int x = (int) (Math.random() * InitConstance.FRAME_WIDTH);
    int y = (int) (Math.random() * InitConstance.FRAME_HEIGHT);
    bean = new Block(x, y);
  }

  private void initSnake() {
    snake.clear();
    direction = Direction.UP;
    for (int i = 0; i < InitConstance.COUNT; i++) {
      snake.add(new Block(InitConstance.INIT_X, InitConstance.INIT_Y + i));
    }
  }

  private void initFrame() {
    this.add(
        panel =
            new JPanel() {
              private static final long serialVersionUID = 1L;

              @Override
              public void paint(Graphics graphics) {
                super.paint(graphics);
                graphics.setColor(InitConstance.BACKGROUND);
                graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
                graphics.setColor(InitConstance.HEAD);
                snake.getFirst().fill(graphics);
                graphics.setColor(InitConstance.BODY);
                for (int i = 1; i < snake.size(); i++) {
                  snake.get(i).fill(graphics);
                }
                graphics.setColor(InitConstance.BEAN);
                bean.fill(graphics);
              }
            });
    this.addKeyListener(this);
    this.setSize(
        InitConstance.FRAME_WIDTH * InitConstance.BLOCK_SIZE,
        InitConstance.FRAME_HEIGHT * InitConstance.BLOCK_SIZE);
    this.setTitle("Snake");
    this.setVisible(true);
  }

  private void gameOver() {
    isRunning = false;
    if (JOptionPane.showConfirmDialog(
            panel, "GAME OVER", "Snake", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE)
        == 0) {
      initSnake();
      panel.repaint();
      isRunning = true;
    } else {
      System.exit(0);
    }
  }

  private void initThread() {
    new Thread(
            () -> {
              while (true) {
                if (isRunning) {
                  if (snake.getFirst().willDash(direction)) {
                    gameOver();
                  }
                  snake.addFirst(snake.getFirst().ahead(direction));
                  for (int i = 1; i < snake.size(); i++) {
                    if (snake.getFirst().isCoincide(snake.get(i))) {
                      gameOver();
                    }
                  }
                  if (snake.getFirst().isCoincide(bean)) {
                    initBean();
                  } else {
                    snake.removeLast();
                  }
                  panel.repaint();
                }
                try {
                  Thread.sleep(InitConstance.SPEED);
                } catch (InterruptedException event) {
                  event.printStackTrace();
                }
              }
            })
        .start();
  }

  @Override
  public void keyPressed(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.VK_UP && direction != Direction.DOWN) {
      direction = Direction.UP;
    } else if (event.getKeyCode() == KeyEvent.VK_DOWN && direction != Direction.UP) {
      direction = Direction.DOWN;
    } else if (event.getKeyCode() == KeyEvent.VK_LEFT && direction != Direction.RIGHT) {
      direction = Direction.LEFT;
    } else if (event.getKeyCode() == KeyEvent.VK_RIGHT && direction != Direction.LEFT) {
      direction = Direction.RIGHT;
    }
  }

  @Override
  public void keyTyped(KeyEvent event) {}

  @Override
  public void keyReleased(KeyEvent event) {}
}
