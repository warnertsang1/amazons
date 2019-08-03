package amazons;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.*;


/** Represents a position on an Amazons board.  Positions are numbered
 *  from 0 (lower-left corner) to 99 (upper-right corner).  Squares
 *  are immutable and unique: there is precisely one square created for
 *  each distinct position.  Clients create squares using the factory method
 *  sq, not the constructor.  Because there is a unique Square object for each
 *  position, you can freely use the cheap == operator (rather than the
 *  .equals method) to compare Squares, and the program does not waste time
 *  creating the same square over and over again.
 *  @author Warner Tsang
 */
final class Square {

    /** The regular expression for a square designation (e.g.,
     *  a3). For convenience, it is in parentheses to make it a
     *  group.  This subpattern is intended to be incorporated into
     *  other pattern that contain square designations (such as
     *  patterns for moves). */
    static final String SQ = "([a-j](?:[1-9]|10))";

    /**
     * Integer to convert columns.
     */
    private static final int MAGICFIXER = 97;

    /** Return my row position, where 0 is the bottom row. */
    int row() {
        return _row;
    }

    /** Return my column position, where 0 is the leftmost column. */
    int col() {
        return _col;
    }

    /** Return my index position (0-99).  0 represents square a1, and 99
     *  is square j10. */
    int index() {
        return _index;
    }

    /** Return true iff THIS - TO is a valid queen move. */
    boolean isQueenMove(Square to) {
        if (this.col() == to.col()
                && this.row() == to.row()) {
            return false;
        }
        if (exists(to.col(), to.row())) {
            if (this.col() == to.col()
                    || this.row() == to.row()
                    || Math.abs(this.col() - to.col())
                    == Math.abs((this.row() - to.row()))) {
                return true;
            }
            return false;
        }
        return false;
    }

    /** Definitions of direction for queenMove. */
    private static final int[][] DIR = {
        { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 },
        { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }
    };

    /** Return the Square that is STEPS>0 squares
     *  away from me in direction DIR,
     *  or null if there is no such square.
     * @param dir is direction.
     * @param steps are steps in the direction.
     */
    Square queenMove(int dir, int steps) {
        if (dir % 2 != 0) {
            return sq(steps * DIR[dir][0] + this.col(),
                    steps * DIR[dir][1] + this.row());
        } else if (dir == 0 || dir == 4) {
            return sq(this.col(),
                    steps * DIR[dir][1] + this.row());
        } else if (dir == 2 || dir == 6) {
            return sq(steps * DIR[dir][0] + this.col(), this.row());
        } else {
            return null;
        }
    }

    /** Return the direction (an int as defined in the documentation
     *  for queenMove) of the queen move THIS-TO. */
    int direction(Square to) {
        assertTrue(isQueenMove(to));
        if (this.col() == to.col()) {
            if (this.row() < to.row()) {
                return 0;
            } else {
                return 4;
            }
        } else if (this.row() == to.row()) {
            if (this.col() < to.col()) {
                return 2;
            } else {
                return 6;
            }
        } else {
            if (this.col() < to.col()) {
                if (this.row() < to.row()) {
                    return 1;
                } else {
                    return 3;
                }
            } else {
                if (this.row() > to.row()) {
                    return 5;
                } else {
                    return 7;
                }
            }
        }
    }

    @Override
    public String toString() {
        return _str;
    }

    /** Return true iff COL ROW is a legal square. */
    static boolean exists(int col, int row) {
        return row >= 0 && col >= 0 && row < Board.SIZE && col < Board.SIZE;
    }

    /** Return the (unique) Square denoting COL ROW. */
    static Square sq(int col, int row) {
        if (!exists(row, col)) {
            return null;
        }
        int parsed = Integer.valueOf(String.valueOf(row) + String.valueOf(col));
        return sq(parsed);
    }

    /** Return the (unique) Square denoting the position with index INDEX. */
    static Square sq(int index) {
        return SQUARES[index];
    }

    /** Return the (unique) Square denoting the position COL ROW, where
     *  COL ROW is the standard text format for a square (e.g., a4). */
    static Square sq(String col, String row) {
        return null;
    }

    /** Return the (unique) Square denoting the position in POSN, in the
     *  standard text format for a square (e.g. a4). POSN must be a
     *  valid square designation. */
    static Square sq(String posn) {
        assertTrue(posn.matches(SQ));
        String rowChar = posn.substring(1);
        int rowInt = Integer.parseInt(rowChar) - 1;
        Character columnChar = posn.charAt(0);
        int columnInt;
        columnInt = (int) columnChar - MAGICFIXER;
        return sq(columnInt, rowInt);
    }

    /** Return an iterator over all Squares. */
    static Iterator<Square> iterator() {
        return SQUARE_LIST.iterator();
    }

    /** Return the Square with index INDEX. */
    private Square(int index) {
        _index = index;
        _row = index / 10;
        _col = index % 10;
        int converter = (int) 'a' + _col;
        Character newColumn = (char) converter;
        _str = String.format("%s%d", newColumn, _row + 1);
    }

    /** The cache of all created squares, by index. */
    private static final Square[] SQUARES =
        new Square[Board.SIZE * Board.SIZE];

    /** SQUARES viewed as a List. */
    private static final List<Square> SQUARE_LIST = Arrays.asList(SQUARES);

    static {
        for (int i = Board.SIZE * Board.SIZE - 1; i >= 0; i -= 1) {
            SQUARES[i] = new Square(i);
        }
    }

    /** My index position. */
    private final int _index;

    /** My row and column (redundant, since these are determined by _index). */
    private final int _row, _col;

    /** My String denotation. */
    private final String _str;

}
