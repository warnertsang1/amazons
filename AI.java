package amazons;
import java.util.ArrayList;
import java.util.Iterator;
import static amazons.Piece.*;

/** A Player that automatically generates moves.
 *  @author Warner Tsang
 */

class AI extends Player {

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * Integer for max depth.
     */
    private static final int DEEPNESS = 19;

    /**
     * Increment max depth.
     */
    private static final int INC = 1;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = _controller.board();
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        Move bestMove = null;
        int bestScore;
        if (sense == 1) {
            bestScore = -INFTY;
            Iterator<Move> iterWhite = board.legalMoves();
            for (Move step = iterWhite.next();
                 step != null; step = iterWhite.next()) {
                board.makeMove(step);
                int result = findMove(board, depth - 1, false, -1,
                        alpha, beta);
                board.undo();
                if (result >= bestScore) {
                    bestMove = step;
                    bestScore = result;
                    alpha = Math.max(alpha, result);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        } else {
            bestScore = INFTY;
            Iterator<Move> iterBlack = board.legalMoves();
            for (Move step = iterBlack.next();
                 step != null; step = iterBlack.next()) {
                board.makeMove(step);
                int result = findMove(board, depth - 1, false, 1,
                        alpha, beta);
                board.undo();
                if (result <= bestScore) {
                    bestMove = step;
                    bestScore = result;
                    beta = Math.min(beta, result);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }
        if (saveMove) {
            _lastFoundMove = bestMove;
        }
        return bestScore;
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private int maxDepth(Board board) {
        int k = board.numMoves();
        return (k / DEEPNESS) + INC;
    }

    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }
        ArrayList<Square> whiteList = board.getWhiteQueens();
        ArrayList<Square> blackList = board.getBlackQueens();
        int whiteQueenMoves = 0;
        int blackQueenMoves = 0;
        for (Square q : whiteList) {
            Iterator<Square> iterWhite = board.reachableFrom(q, null);
            while (iterWhite.next() != null) {
                whiteQueenMoves++;
            }
        }
        for (Square q : blackList) {
            Iterator<Square> iterBlack = board.reachableFrom(q, null);
            while (iterBlack.next() != null) {
                blackQueenMoves++;
            }
        }
        return whiteQueenMoves - blackQueenMoves;
    }
}
