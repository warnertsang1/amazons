package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static org.junit.Assert.*;
import ucb.junit.textui;
import java.util.Iterator;

/** The suite of all JUnit tests for the amazons package.
 *  @author Warner Tsang
 */
public class UnitTest {

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /**
     * Tests basic correctness of put and get on the initialized board.
     */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /**
     * Tests proper identification of legal/illegal queen moves.
     */

    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }
    /**
     * Tests legal positions.
     */
    @Test
    public void testIsLegal() {
        Board b = new Board();
        assertTrue(b.isLegal(Square.sq("d1")));
        assertFalse(b.isLegal(Square.sq("d10")));
        assertTrue(b.isLegal(Square.sq("d1"), Square.sq("d8")));
        assertFalse(b.isLegal(Square.sq("d1"), Square.sq("d10")));
        assertFalse(b.isLegal(Square.sq("a4"), Square.sq("d1")));
        assertFalse(b.isLegal(Square.sq("d1"),
                Square.sq("a1"), Square.sq("a5")));
        assertTrue(b.isLegal(Square.sq("g1"),
                Square.sq("g6"), Square.sq("d9")));
        assertTrue(b.isLegal(Square.sq("g1"), Square.sq("i3")));
        assertTrue(b.isLegal(Square.sq("d1"),
                Square.sq("c2"), Square.sq("j9")));
        assertTrue(b.isLegal(Square.sq("j4"),
                Square.sq("h2"), Square.sq("a9")));
        assertFalse(b.isLegal(Square.sq("a4"),
                Square.sq("g4"), Square.sq("g10")));
        assertFalse(b.isLegal(Square.sq("g1"),
                Square.sq("g7"), Square.sq("d10")));
        assertTrue(b.isLegal(Square.sq("g1"),
                Square.sq("g2"), Square.sq("g1")));
        assertFalse(b.isLegal(Square.sq("i4"),
                Square.sq("j5"), Square.sq("b3")));
        assertTrue(b.isLegal(Move.mv(Square.sq("g1"),
                Square.sq("g2"), Square.sq("g1"))));
        assertTrue(b.isLegal(Move.mv(Square.sq("j4"),
                Square.sq("h2"), Square.sq("a9"))));
        assertFalse(b.isLegal(Move.mv(Square.sq("a4"),
                Square.sq("g4"), Square.sq("g10"))));
        assertFalse(b.isLegal(Move.mv(Square.sq("a4"),
                Square.sq("a7"), Square.sq("a4"))));
    }

    /**
     * Tests legal moves.
     */
    @Test
    public void testMakeMove() {
        Board b = new Board();
        b.makeMove(Square.sq("d1"),
                Square.sq("d8"), Square.sq("i8"));
        assertEquals(Piece.SPEAR,
                b.get(Square.sq("i8")));
        assertEquals(Piece.WHITE,
                b.get(Square.sq("d8")));
        assertEquals(Piece.EMPTY, b.get(Square.sq("d1")));
        b.makeMove(Square.sq("d10"),
                Square.sq("d9"), Square.sq("c9"));
        assertEquals(Piece.SPEAR, b.get(Square.sq("c9")));
        assertEquals(Piece.BLACK, b.get(Square.sq("d9")));
        assertEquals(Piece.EMPTY, b.get(Square.sq("d10")));
        b.makeMove(Square.sq("d8"),
                Square.sq("d1"), Square.sq("d8"));
        b.makeMove(Square.sq("d9"),
                Square.sq("c8"), Square.sq("b8"));
        b.undo();
        assertEquals(Piece.WHITE,
                b.get(Square.sq("d1")));
        assertEquals(Piece.SPEAR,
                b.get(Square.sq("d8")));
        b.undo();
        b.undo();
        b.undo();
        assertEquals(Piece.WHITE, b.get(Square.sq("d1")));
        assertEquals(Piece.BLACK, b.get(Square.sq("d10")));
        b.makeMove(Square.sq("g1"),
                Square.sq("d4"), Square.sq("j10"));
        assertEquals(Piece.SPEAR, b.get(Square.sq("j10")));
        assertEquals(Piece.WHITE, b.get(Square.sq("d4")));
        assertEquals(Piece.EMPTY, b.get(Square.sq("g1")));
        b.undo();
        assertEquals(Piece.WHITE, b.get(Square.sq("g1")));
        b.makeMove(Square.sq("j4"),
                Square.sq("i3"), Square.sq("j4"));
        assertEquals(Piece.SPEAR, b.get(Square.sq("j4")));
        assertEquals(Piece.WHITE, b.get(Square.sq("i3")));
        b.undo();
        assertEquals(Piece.EMPTY, b.get(Square.sq("i3")));
        assertEquals(Piece.WHITE, b.get(Square.sq("j4")));
        b.makeMove(Square.sq("a4"),
                Square.sq("b3"), Square.sq("b2"));
        assertEquals(Piece.SPEAR, b.get(Square.sq("b2")));
        b.undo();
    }

    /**
     * Tests legalMovesIterator.
     */
    @Test
    public void testLegalMovesIterator() {
        Board b = new Board();
        Iterator<Move> iter = b.legalMoves(WHITE);
        for (int i = 0; i < 2176; i++) {
            iter.next();
        }
        assertEquals(null, iter.next());
    }

    /**
     * Tests end game moves.
     */
    @Test
    public void testMakeMoveTwo() {
        Board b = new Board();
        b.put(WHITE, 0, 0);
        b.put(WHITE, 0, 9);
        b.put(WHITE, 9, 0);
        b.put(WHITE, 9, 9);
        b.put(BLACK, 2, 0);
        b.put(BLACK, 2, 2);
        b.put(BLACK, 2, 1);
        b.put(BLACK, 0, 2);
        b.put(BLACK, 8, 0);
        b.put(BLACK, 8, 1);
        b.put(BLACK, 9, 1);
        b.put(BLACK, 9, 8);
        b.put(BLACK, 8, 8);
        b.put(BLACK, 8, 9);
        b.put(BLACK, 0, 8);
        b.put(BLACK, 1, 8);
        b.put(BLACK, 1, 9);
        b.put(EMPTY, 3, 0);
        b.put(EMPTY, 6, 0);
        b.put(EMPTY, 0, 3);
        b.put(EMPTY, 9, 3);
        assertFalse(b.isLegal(Move.mv(Square.sq("a1"),
                Square.sq("a3"), Square.sq("a1"))));
        assertFalse(b.isLegal(Move.mv(Square.sq("a1"),
                Square.sq("c3"), Square.sq("a2"))));
        assertFalse(b.isLegal(Move.mv(Square.sq("a1"),
                Square.sq("a2"), Square.sq("c2"))));
        assertFalse(b.isLegal(Move.mv(Square.sq("j1"),
                Square.sq("a1"), Square.sq("j1"))));
        assertTrue(b.isLegal(Move.mv(Square.sq("a1"),
                Square.sq("a2"), Square.sq("b1"))));
        assertFalse(b.isLegal(Move.mv(Square.sq("a1"),
                Square.sq("b3"), Square.sq("a6"))));
        b.makeMove(Square.sq("a1"), Square.sq("a2"),
                Square.sq("a1"));
    }

    /**
     * Tests legal moves iterator towards end game.
     */
    @Test
    public void legalMoveIteratorCornered() {
        Board b = new Board();
        b.put(WHITE, 0, 0);
        b.put(WHITE, 0, 9);
        b.put(WHITE, 9, 0);
        b.put(WHITE, 9, 9);
        b.put(BLACK, 2, 0);
        b.put(BLACK, 2, 2);
        b.put(BLACK, 1, 2);
        b.put(BLACK, 2, 1);
        b.put(BLACK, 0, 2);
        b.put(SPEAR, 0, 1);
        b.put(SPEAR, 1, 1);
        b.put(SPEAR, 1, 0);
        b.put(BLACK, 8, 0);
        b.put(BLACK, 8, 1);
        b.put(BLACK, 9, 1);
        b.put(BLACK, 9, 8);
        b.put(BLACK, 8, 8);
        b.put(BLACK, 8, 9);
        b.put(BLACK, 0, 8);
        b.put(BLACK, 1, 8);
        b.put(BLACK, 1, 9);
        b.put(EMPTY, 3, 0);
        b.put(EMPTY, 6, 0);
        b.put(EMPTY, 0, 3);
        b.put(EMPTY, 9, 3);
        Iterator<Move> iter = b.legalMoves(WHITE);
        assertFalse(iter.hasNext());
    }

    /**
     * Tests helper function that finds all queens.
     */
    @Test
    public void findNumQueens() {
        Board b = new Board();
        assertEquals(4, b.getWhiteQueens().size());
        assertEquals(4, b.getBlackQueens().size());
    }

    /**
     * Tests if a given piece has a valid move or not.
     */
    @Test
    public void noMoves() {
        Board b = new Board();
        b.put(WHITE, 0, 0);
        b.put(WHITE, 0, 9);
        b.put(WHITE, 9, 0);
        b.put(WHITE, 9, 9);
        b.put(BLACK, 2, 0);
        b.put(BLACK, 2, 2);
        b.put(BLACK, 1, 2);
        b.put(BLACK, 2, 1);
        b.put(BLACK, 0, 2);
        b.put(SPEAR, 0, 1);
        b.put(SPEAR, 1, 1);
        b.put(BLACK, 8, 0);
        b.put(BLACK, 8, 1);
        b.put(BLACK, 9, 1);
        b.put(BLACK, 9, 8);
        b.put(BLACK, 8, 8);
        b.put(BLACK, 8, 9);
        b.put(BLACK, 0, 8);
        b.put(BLACK, 1, 8);
        b.put(BLACK, 1, 9);
        b.put(EMPTY, 3, 0);
        b.put(EMPTY, 6, 0);
        b.put(EMPTY, 0, 3);
        b.put(EMPTY, 9, 3);
        b.makeMove(Square.sq("a1"), Square.sq("b1"), Square.sq("a1"));
        b.makeMove(Square.sq("a3"), Square.sq("a5"), Square.sq("b5"));
        assertTrue(b.noMoves(b.turn()));
        assertEquals(BLACK, b.winner());
        b.undo();
        assertFalse(b.noMoves(b.turn()));
    }
}
