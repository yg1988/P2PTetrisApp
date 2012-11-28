package com.example.android.wifidirect.discovery;
import java.util.*;



/**
 CS108 Tetris Game.
 JTetris presents a tetris game in a window.
 It handles the GUI and the animation.
 The Piece and Board classes handle the
 lower-level computations.
 This code is provided in finished, working form for the students.

 Use Keys j-k-l to move, n to drop (or 4-5-6 0)
 During animation, filled rows draw as green.
 Clearing 1-4 rows scores 5, 10, 20, 40 points.
 Clearing 4 rows at a time beeps!
 */

/*
 Implementation notes:
 -The "currentPiece" points to a piece that is
 currently falling, or is null when there is no piece.
 -tick() moves the current piece
 -a timer object calls tick(DOWN) periodically
 -keystrokes call tick() with LEFT, RIGHT, etc.
 -Board.undo() is used to remove the piece from its
 old position and then Board.place() is used to install
 the piece in its new position.
 */

@SuppressWarnings("serial")
public class JTetris {
	// size of the board in blocks
	public static final int WIDTH = 20;
	public static final int HEIGHT = 20;

	// Extra blocks at the top for pieces to start.
	// If a piece is sticking up into this area
	// when it has landed -- game over!
	public static final int TOP_SPACE = 4;

	// When this is true, plays a fixed sequence of 100 pieces
	protected boolean testMode = false;
	public final int TEST_LIMIT = 100;

	// Is drawing optimized
	// (default false, so debugging is easier)
	protected boolean DRAW_OPTIMIZE = false;

	// Board data structures
	protected Board board;
	protected Piece[] pieces;


	// The current piece in play or null
	protected Piece currentPiece;
	protected int currentX;
	protected int currentY;
	protected boolean moved;	// did the player move the piece


	// The piece we're thinking about playing
	// -- set by computeNewPosition
	// (storing this in ivars is slightly questionable style)
	protected Piece newPiece;
	protected int newX;
	protected int newY;

	// State of the game
	protected boolean gameOn;	// true if we are playing
	protected int count;		 // how many pieces played so far
	protected long startTime;	// used to measure elapsed time
	protected Random random;	 // the random generator for new pieces



	protected int score;


	public final int DELAY = 400;	// milliseconds per tick

	/**
	 * Creates a new JTetris where each tetris square
	 * is drawn with the given number of pixels.
	 */
	JTetris(int pixels) {
		super();

		// Set component size to allow given pixels for each block plus
		// a 1 pixel border around the whole thing.
		/*setPreferredSize(new Dimension((WIDTH * pixels)+2,
				(HEIGHT+TOP_SPACE)*pixels+2));*/
		gameOn = false;
		pieces = Piece.getPieces();
		board = new Board(WIDTH, HEIGHT + TOP_SPACE);



	}



	/**
	 Sets the internal state and starts the timer
	 so the game is happening.
	 */
	public void startGame() {
		// cheap way to reset the board state
		board = new Board(WIDTH, HEIGHT + TOP_SPACE);

		// draw the new board state once
		//repaint();

		count = 0;
		score = 0;
		updateCounters();
		gameOn = true;

		// Set mode based on checkbox at start of game
		testMode = true;
		if (testMode) random = new Random(0);	// same seq every time
		else random = new Random(); // diff seq each game

		Piece piece = pickNextPiece();
		addNewPiece(piece);
		startTime = System.currentTimeMillis();
	}




	/**
	 Stops the game.
	 */
	public void stopGame() {
		gameOn = false;


		long delta = (System.currentTimeMillis() - startTime)/10;


	}


	/**
	 Given a piece, tries to install that piece
	 into the board and set it to be the current piece.
	 Does the necessary repaints.
	 If the placement is not possible, then the placement
	 is undone, and the board is not changed. The board
	 should be in the committed state when this is called.
	 Returns the same error code as Board.place().
	 */
	public int setCurrent(Piece piece, int x, int y) {
		int result = board.place(piece, x, y);

		if (result <= Board.PLACE_ROW_FILLED) { // SUCESS
			currentPiece = piece;
			currentX = x;
			currentY = y;
			// repaint the rect where it is now
		}
		else {
			board.undo();
		}

		return(result);
	}


	/**
	 Selects the next piece to use using the random generator
	 set in startGame().
	 */
	public Piece pickNextPiece() {
		int pieceNum;

		pieceNum = (int) (pieces.length * random.nextDouble());

		Piece piece	 = pieces[pieceNum];

		return(piece);
	}


	/**
	 Tries to add a new random piece at the top of the board.
	 Ends the game if it's not possible.
	 */
	public void addNewPiece(Piece piece) {
		count++;
		score++;

		if (testMode && count == TEST_LIMIT+1) {
			stopGame();
			return;
		}

		// commit things the way they are
		board.commit();
		currentPiece = null;

		

		// Center it up at the top
		int px = (board.getWidth() - piece.getWidth())/2;
		int py = board.getHeight() - piece.getHeight();

		// add the new piece to be in play
		int result = setCurrent(piece, px, py);

		// This probably never happens, since
		// the blocks at the top allow space
		// for new pieces to at least be added.
		if (result>Board.PLACE_ROW_FILLED) {
			stopGame();
		}

		updateCounters();
	}

	/**
	 Updates the count/score labels with the latest values.
	 */
	private void updateCounters() {
		//countLabel.setText("Pieces " + count);
		//scoreLabel.setText("Score " + score);
	}


	/**
	 Figures a new position for the current piece
	 based on the given verb (LEFT, RIGHT, ...).
	 The board should be in the committed state --
	 i.e. the piece should not be in the board at the moment.
	 This is necessary so dropHeight() may be called without
	 the piece "hitting itself" on the way down.

	 Sets the ivars newX, newY, and newPiece to hold
	 what it thinks the new piece position should be.
	 (Storing an intermediate result like that in
	 ivars is a little tacky.)
	 */
	public void computeNewPosition(int verb) {
		// As a starting point, the new position is the same as the old
		newPiece = currentPiece;
		newX = currentX;
		newY = currentY;

		// Make changes based on the verb
		switch (verb) {
		case LEFT: newX--; break;

		case RIGHT: newX++; break;

		case ROTATE:
			newPiece = newPiece.fastRotation();

			// tricky: make the piece appear to rotate about its center
			// can't just leave it at the same lower-left origin as the
			// previous piece.
			newX = newX + (currentPiece.getWidth() - newPiece.getWidth())/2;
			newY = newY + (currentPiece.getHeight() - newPiece.getHeight())/2;
			break;

		case DOWN: newY--; break;

		case DROP:
			newY = board.dropHeight(newPiece, newX);

			// trick: avoid the case where the drop would cause
			// the piece to appear to move up
			if (newY > currentY) {
				newY = currentY;
			}
			break;

		default:
			throw new RuntimeException("Bad verb");
		}

	}




	public static final int ROTATE = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int DROP = 3;
	
	public static final int DOWN = 4;
	//public static final int DROP = 3;
	/**
	 Called to change the position of the current piece.
	 Each key press calls this once with the verbs
	 LEFT RIGHT ROTATE DROP for the user moves,
	 and the timer calls it with the verb DOWN to move
	 the piece down one square.

	 Before this is called, the piece is at some location in the board.
	 This advances the piece to be at its next location.

	 Overriden by the brain when it plays.
	 */
	public synchronized void tick(int verb) {
		//if (!gameOn) return;

		if (currentPiece != null) {
			board.undo();	// remove the piece from its old position
		}

		// Sets the newXXX ivars
		computeNewPosition(verb);

		// try out the new position (rolls back if it doesn't work)
		int result = setCurrent(newPiece, newX, newY);

		// if row clearing is going to happen, draw the
		// whole board so the green row shows up
		if (result ==  Board.PLACE_ROW_FILLED) {
			//repaint();
		}


		boolean failed = (result >= Board.PLACE_OUT_BOUNDS);

		// if it didn't work, put it back the way it was
		if (failed) {
			if (currentPiece != null) board.place(currentPiece, currentX, currentY);
		}

		/*
		 How to detect when a piece has landed:
		 if this move hits something on its DOWN verb,
		 and the previous verb was also DOWN (i.e. the player was not
		 still moving it),	then the previous position must be the correct
		 "landed" position, so we're done with the falling of this piece.
		 */
		if (failed && verb==DOWN && !moved) {	// it's landed

			int cleared = board.clearRows();
			if (cleared > 0) {
				// score goes up by 5, 10, 20, 40 for row clearing
				// clearing 4 gets you a beep!
				switch (cleared) {
				case 1: score += 5;	 break;
				case 2: score += 10;  break;
				case 3: score += 20;  break;
				//case 4: score += 40; Toolkit.getDefaultToolkit().beep(); break;
				default: score += 50;  // could happen with non-standard pieces
				}
				updateCounters();
				//repaint();	// repaint to show the result of the row clearing
			}


			// if the board is too tall, we've lost
			if (board.getMaxHeight() > board.getHeight() - TOP_SPACE) {
				stopGame();
			}
			// Otherwise add a new piece and keep playing
			else {
				Piece piece = pickNextPiece();
				addNewPiece(piece);
			}
		}

		// Note if the player made a successful non-DOWN move --
		// used to detect if the piece has landed on the next tick()
		moved = (!failed && verb!=DOWN);
	}

	/**
	 Draws the current board with a 1 pixel border
	 around the whole thing. Uses the pixel helpers
	 above to map board coords to pixel coords.
	 Draws rows that are filled all the way across in green.
	 */
	
	public String paintComponent() {
		StringBuilder result = new StringBuilder();


		// Factor a few things out to help the optimizer

		final int bWidth = board.getWidth();

		int x, y;
		
		
		final int yHeight = this.HEIGHT;
		
		for (y = yHeight-1; y >=0; y--) {
		// Loop through and draw all the blocks
		// left-right, bottom-top
			for (x=0; x<bWidth; x++) {
			
				if (board.getGrid(x, y)) {

					result.append("\u25A9");    //25A0

				}else{
					result.append(" ");
				}
			}
			if(y!=0){
				result.append("\n");
			}
		}
		String str = result.toString();
		return str;
	}


	/**
	 Updates the timer to reflect the current setting of the
	 speed slider.
	 */
	public void updateTimer() {
		//double value = ((double)speed.getValue())/speed.getMaximum();
		//timer.setDelay((int)(DELAY - value*DELAY));
	}


	
	//one of the cheating 
	public synchronized void quake(){
//		int i=10;
//		while(i!=0){
			board.swapRandomCol();
//			i--;
//		}
	}
}

