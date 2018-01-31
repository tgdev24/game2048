import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game2048 extends JPanel {
  
	
  /**
	* default serial ID 
	*/
  private static final long serialVersionUID = 1L;
  private static final Color BG_COLOR = new Color(0xbbada0);
  private static final String FONT_NAME = "Arial";
  private static final int TILE_SIZE = 64;
  private static final int TILES_MARGIN = 16;

  private Tile[] myTiles;
  boolean myWin = false;
  boolean myLose = false;
  int myScore = 0;

  //constructor for game
  public Game2048() {
	//gives the maximum size for the panel
    setPreferredSize(new Dimension(340, 400));
    //gives focus to the panel when clicked on
    setFocusable(true);
    //used to get what the user clicks
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
    	//check if what is pressed is escape key then reset game
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          resetGame();
        }
        // can't move then game over
        if (!canMove()) {
          myLose = true;
        }
        // didn't lose or win so moves left to do
        if (!myWin && !myLose) {
          switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
              left();
              break;
            case KeyEvent.VK_RIGHT:
              right();
              break;
            case KeyEvent.VK_DOWN:
              down();
              break;
            case KeyEvent.VK_UP:
              up();
              break;
          }
        }
        //didn't win but cannot move so game over
        if (!myWin && !canMove()) {
          myLose = true;
        }
        
        //refreshes the component if something is changed part of awt
        //handled automatically if using a layout manager, usually not needed 
        repaint();
      }
    });
    // call this just in case. 
    resetGame();
  }

  //reset game
  //reset all private class variables
  //make new tiles 
  public void resetGame() {
    myScore = 0;
    myWin = false;
    myLose = false;
    //makes board
    myTiles = new Tile[4 * 4];
    for (int i = 0; i < myTiles.length; i++) {
      //makes tiles
      myTiles[i] = new Tile();
    }
    //add tile twice because new game starts with 2 tiles
    addTile();
    addTile();
  }

  //main function to move and merge tiles
  public void left() {
    boolean needAddTile = false;
    for (int i = 0; i < 4; i++) {
      Tile[] line = getLine(i);
      Tile[] merged = mergeLine(moveLine(line));
      setLine(i, merged);
      if (!needAddTile && !compare(line, merged)) {
        needAddTile = true;
      }
    }
    if (needAddTile) {
      addTile();
    }
  }

  //move and merge right 
  //delegate to rotate for 
  public void right() {
    myTiles = rotate(180);
    left();
    myTiles = rotate(180);
  }

  //move up
  public void up() {
    myTiles = rotate(270);
    left();
    myTiles = rotate(90);
  }

  //move down
  public void down() {
    myTiles = rotate(90);
    left();
    myTiles = rotate(270);
  }

  //get the content of specific tile
  private Tile tileAt(int x, int y) {
    return myTiles[x + y * 4];
  }

  //add a tile to the board
  private void addTile() {
	//list of tiles that are empty
    List<Tile> list = availableSpace();
    //if there's space on the board, add either a 2 or a 4
    if (!availableSpace().isEmpty()) {
      int index = (int) (Math.random() * list.size()) % list.size();
      //finding out tile to insert at
      Tile emptyTime = list.get(index);
      //give the value of tile either a 2 or a 4
      emptyTime.value = Math.random() < 0.9 ? 2 : 4;
    }
  }

  //make a list of tiles then check if any of the tiles are empty 
  //if so then add them to the list
  private List<Tile> availableSpace() {
    final List<Tile> list = new ArrayList<Tile>(16);
    for (Tile t : myTiles) {
      if (t.isEmpty()) {
        list.add(t);
      }
    }
    return list;
  }

  //if there's available space list is 0, then board is full
  private boolean isFull() {
    return availableSpace().size() == 0;
  }

  //check if a move is possible 
  boolean canMove() {
	// not full so can move
    if (!isFull()) {
      return true;
    }
    // if there are 2 tiles with same values then you can combine them 
    // which means there are moves left to be done
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        Tile t = tileAt(x, y);
        if ((x < 3 && t.value == tileAt(x + 1, y).value)
          || ((y < 3) && t.value == tileAt(x, y + 1).value)) {
          return true;
        }
      }
    }
    // went through the whole board so can't move
    return false;
  }

  //function to see if 2 lines are the same or not
  private boolean compare(Tile[] line1, Tile[] line2) {
    if (line1 == line2) {
      return true;
    } else if (line1.length != line2.length) {
      return false;
    }
    
    for (int i = 0; i < line1.length; i++) {
      if (line1[i].value != line2[i].value) {
        return false;
      }
    }
    return true;
  }

  //called in right, up, and down move
  //rotates the board by the given angle
  private Tile[] rotate(int angle) {
	//creates new board that contains the rotations
    Tile[] newTiles = new Tile[4 * 4];
    int offsetX = 3, offsetY = 3;
    if (angle == 90) {
      offsetY = 0;
    } else if (angle == 270) {
      offsetX = 0;
    }

    double rad = Math.toRadians(angle);
    int cos = (int) Math.cos(rad);
    int sin = (int) Math.sin(rad);
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        int newX = (x * cos) - (y * sin) + offsetX;
        int newY = (x * sin) + (y * cos) + offsetY;
        newTiles[(newX) + (newY) * 4] = tileAt(x, y);
      }
    }
    return newTiles;
  }
  
  //function to make a new line of new tiles given an oldline 
  private Tile[] moveLine(Tile[] oldLine) {
    LinkedList<Tile> l = new LinkedList<Tile>();
    for (int i = 0; i < 4; i++) {
      if (!oldLine[i].isEmpty())
    	//add all elements of the line into the linkedlist if not empty
        l.addLast(oldLine[i]);
    }
    // if all the tiles are empty then return it
    // else make sure the size of the list is 4 then make a new line with new lines.
    if (l.size() == 0) {
      return oldLine;
    } else {
      Tile[] newLine = new Tile[4];
      ensureSize(l, 4);
      for (int i = 0; i < 4; i++) {
        newLine[i] = l.removeFirst();
      }
      return newLine;
    }
  }

  //a function to combine 2 tiles in a given line and return it's result in an array of tiles
  private Tile[] mergeLine(Tile[] oldLine) {
    LinkedList<Tile> list = new LinkedList<Tile>();
    for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
      //get the value of each non-empty tile
      int num = oldLine[i].value;
      //if first 3 tiles and they are 
      if (i < 3 && oldLine[i].value == oldLine[i + 1].value) {
        num *= 2;
        myScore += num;
        int ourTarget = 2048;
        if (num == ourTarget) {
          myWin = true;
        }
        i++;
      }
      //add a new tile containing the combined elements 
      list.add(new Tile(num));
    }
    if (list.size() == 0) {
      return oldLine;
    } else {
      ensureSize(list, 4);
      return list.toArray(new Tile[4]);
    }
  }

  //make sure the size of the list of tiles is the same as the integer given
  private static void ensureSize(java.util.List<Tile> l, int s) {
    while (l.size() != s) {
      l.add(new Tile());
    }
  }
  
  //function to get the elements inside the line horizontally as index stays constant
  private Tile[] getLine(int index) {
    Tile[] result = new Tile[4];
    for (int i = 0; i < 4; i++) {
      result[i] = tileAt(i, index);
    }
    return result;
  }

  //copy the elements of re starting from 0 to the end TO myTiles starting at index*4
  private void setLine(int index, Tile[] re) {
    System.arraycopy(re, 0, myTiles, index * 4, 4);
  }
  
  private int getHighScore() throws IOException {
	  	int highScore = 0;
		File file = new File("scores.txt");
		file.createNewFile();
		BufferedReader reader = new BufferedReader(new FileReader("scores.txt"));
		// initially to get the first line
		String line = reader.readLine();
//		System.out.println("what" + line);
		// get highest score in the file
		while(line != null) {
//			System.out.println("huhhh" + line);
			int scoreLine = Integer.parseInt(line.trim());
			if(scoreLine > highScore) {
				highScore = scoreLine;
			}
			// reads everyline in the file
			line = reader.readLine();
		}
		reader.close();
		
		// handles when user currently playing gets the newest high score
		if(myScore > highScore) {
			BufferedWriter output = new BufferedWriter(new FileWriter("scores.txt", true));
	        output.append(String.valueOf(myScore) + "\n");
	        output.close();
	        //maybe set highscore here
		}
		return highScore;
  }

  //function that calls the EDT for the awt class and places objects on the panel
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setColor(BG_COLOR);
    g.fillRect(0, 0, this.getSize().width, this.getSize().height);
    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < 4; x++) {
        drawTile(g, myTiles[x + y * 4], x, y);
      }
    }
  }

  //a helper function that does the work of creating the tiles'
  //called by paint multiple times 
  private void drawTile(Graphics g2, Tile tile, int x, int y) {
	// inherits graphics classes
	// more things you can do with Graphics2D
    Graphics2D g = ((Graphics2D) g2);
    // aliasing is abrupt changes in color as the information for the GUI is loading, so
    // antialiasing turns that off
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // makes sure that lines and geometry is uniform and aesthetically pleasing
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    int value = tile.value;
    int xOffset = offsetCoors(x);
    int yOffset = offsetCoors(y);
    // set tile background
    g.setColor(tile.getBackground());
    // add a rounrect on the tile
    g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);
    // set color of the rounded rectangle
    g.setColor(tile.getForeground());
    // if value less than 100 then size is 36 if value less than 1000 then 1000 otherwise
    // which means greater than or equal to 1000 then size would be 24
    // makes sense since greater numbers like 2048 need more room to be printed
    final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
    // Arial, bold, size for font object
    final Font font = new Font(FONT_NAME, Font.BOLD, size);
    // g set font
    g.setFont(font);
    // make value of tile into a string
    String s = String.valueOf(value);
    // gives info about rendering of a particular font on a particular screen
    final FontMetrics fm = getFontMetrics(font);
    // size in ints of showing this string with this font
    final int w = fm.stringWidth(s);
    // get height of the 3rd element in line metrics
    final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];
    // if tile is not empty then add the string to the graphic 
    // drawString starts from the bottom left side of the string 
    if (value != 0)
      g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);
    
    if (myWin || myLose) {
      // set color of panel as white
      g.setColor(new Color(255, 255, 255, 30));
      // fillrectangle of whole screen
      g.fillRect(0, 0, getWidth(), getHeight());
      // blueish colors for game over sign or win sign
      g.setColor(new Color(78, 139, 202));
      // font of the sign
      g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
      // winning sign
      if (myWin) {
        g.drawString("You won!", 68, 150);
      }
      // losing sign
      if (myLose) {
        g.drawString("Game over!", 50, 130);
        g.drawString("You lose!", 64, 200);
      }
      // esc sign
      if (myWin || myLose) {
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
        g.setColor(new Color(128, 128, 128, 128));
        g.drawString("Press ESC to play again", 80, getHeight() - 40);
      }
    }
    // score sign
    g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
    g.setColor(new Color(78, 139, 202));
    g.drawString("Score: " + myScore, 200, 365);

    // Display high Score 
    g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
    g.setColor(new Color(78, 139, 202));
    try {
		int score = getHighScore(); 
		if(score > myScore) {
			g.drawString("High Score: " + score, 32, 365);
		}
		else {
			g.drawString("High Score: " + myScore, 32, 365);
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  // function to determine where the tiles will start for location
  private static int offsetCoors(int arg) {
    return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
  }

  // tile class 
  // don't need to instantiate the game2048 class to declare tile
  // static nested inner class becomes its own standalone class
  static class Tile {
    int value;
    
    // no arg instantiation has value of 0
    public Tile() {
      this(0);
    }

    // arg sets the value of the tile
    public Tile(int num) {
      value = num;
    }

    //tile checks if empty
    public boolean isEmpty() {
      return value == 0;
    }

    // less than 16 then get first color, equal or greater, get the second color
    // color of the numbers themselves 
    // turns white at 16
    public Color getForeground() {
      return value < 16 ? new Color(0x776e65) :  new Color(0xf9f6f2);
    }
    
    // color of the background of tile
    public Color getBackground() {
      switch (value) {
        case 2:    return new Color(0xeee4da);
        case 4:    return new Color(0xede0c8);
        case 8:    return new Color(0xf2b179);
        case 16:   return new Color(0xf59563);
        case 32:   return new Color(0xf67c5f);
        case 64:   return new Color(0xf65e3b);
        case 128:  return new Color(0xedcf72);
        case 256:  return new Color(0xedcc61);
        case 512:  return new Color(0xedc850);
        case 1024: return new Color(0xedc53f);
        case 2048: return new Color(0xedc22e);
      }
      
      // background of the board
      return new Color(0xcdc1b4);
    }
  }

  // main function which creates the frame that holds the panel
  public static void main(String[] args) {
    JFrame  game = new JFrame();
    game.setTitle("2048 Game");
    game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    game.setSize(340, 400);
    game.setResizable(false);

    // add the panel to the frame
    game.add(new Game2048());

    // center the frame
    game.setLocationRelativeTo(null);
    // make the frame visible
    game.setVisible(true);
  }
}