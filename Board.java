package amazons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import static amazons.Piece.*;



/** The state of an Amazons Game.
 *  @author Warner Tsang
 */
class Board {

    /**
     * Piece whose turn it is (BLACK or WHITE).
     */
    private Piece _turn;

    /**
     * Cached value of winner on this board, or EMPTY if it has not been
     * computed.
     */

    private Piece _winner;

    /**
     * Stores pieces on the board.
     */
    private Piece[][] _pieceArray;

    /**
     * Tracks all the previous moves for undo.
     */
    private ArrayList<Square> _moveHistory;

    /**
     * Tracks all the current moves for undo.
     */
    private ArrayList<Square> _moveTo;

    /**
     * Tracks all the spears for undo.
     */
    private ArrayList<Square> _spearTracker;

    /**
     * Count of moves.
     */
    private int _numMoves;

    /**
     * Remaining starting squares to consider.
     */
    private Iterator<Square> _startingSquares;

    /**
     * Track the squares of all white queens.
     */
    private ArrayList<Square> _whiteQueens;

    /**
     * Track the squares of all black pieces.
     */
    private ArrayList<Square> _blackQueens;


    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 10;

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        this._turn = model._turn;
        this._winner = model._winner;
        this._pieceArray = model._pieceArray;
        this._moveHistory = model._moveHistory;
        this._moveTo = model._moveTo;
        this._spearTracker = model._spearTracker;
        this._numMoves = model._numMoves;

    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        _turn = WHITE;
        _winner = null;
        _pieceArray = new Piece[SIZE][SIZE];
        _moveHistory = new ArrayList<>();
        _moveTo = new ArrayList<>();
        _spearTracker = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                _pieceArray[j][i] = EMPTY;
            }
        }
        _pieceArray[0][3] = WHITE;
        _pieceArray[3][0] = WHITE;
        _pieceArray[6][0] = WHITE;
        _pieceArray[9][3] = WHITE;
        _pieceArray[0][6] = BLACK;
        _pieceArray[3][9] = BLACK;
        _pieceArray[6][9] = BLACK;
        _pieceArray[9][6] = BLACK;
        _numMoves = 0;
    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    int numMoves() {
        return _numMoves;
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */
    Piece winner() {
        return _winner;
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return _pieceArray[s.col()][s.row()];
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return _pieceArray[col][row];
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        _pieceArray[s.col()][s.row()] = p;
    }

    /**
     * Set square (COL, ROW) to P.
     */
    final void put(Piece p, int col, int row) {
        _pieceArray[col][row] = p;
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (from.isQueenMove(to)
                && get(to) == EMPTY) {
            if (asEmpty == null) {
                int direct = from.direction(to);
                int steps = 0;
                while (from.queenMove(direct, steps) != to) {
                    steps++;
                    Square next = from.queenMove(direct, steps);
                    Piece nextPiece = get(next);
                    if (nextPiece != EMPTY) {
                        return false;
                    }
                }
                return true;
            } else {
                if (isUnblockedMove(from, to, null)
                        && to.isQueenMove(asEmpty)) {
                    int direct = to.direction(asEmpty);
                    int steps = 0;
                    while (to.queenMove(direct, steps) != asEmpty) {
                        steps++;
                        Square next = to.queenMove(direct, steps);
                        Piece nextPiece = get(next);
                        if (next != from) {
                            if (nextPiece != EMPTY) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        return Square.exists(from.col(), from.row())
                && get(from.col(), from.row()) == WHITE
                && turn() == WHITE
                || get(from.col(), from.row()) == BLACK
                && turn() == BLACK;
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    boolean isLegal(Square from, Square to) {
        return isLegal(from)
                && isUnblockedMove(from, to, null);
    }

    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     * position.
     */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from, to)
                && isUnblockedMove(from, to, spear);
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(),
                move.to(), move.spear());
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */

    void makeMove(Square from, Square to, Square spear) {
        if (!isLegal(from, to, spear)) {
            return;
        }
        put(get(from), to);
        put(EMPTY, from);
        put(SPEAR, spear);
        _moveHistory.add(from);
        _moveTo.add(to);
        _spearTracker.add(spear);
        _numMoves++;
        _turn = _turn.opponent();
        if (noMoves(_turn)) {
            _winner = _turn.opponent();
            return;
        }
    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        _numMoves--;
        put(EMPTY, _spearTracker.get(_numMoves));
        _spearTracker.remove(_numMoves);
        Piece undoPiece = get(_moveTo.get(_numMoves));
        Square historyUndo = _moveHistory.get(_numMoves);
        put(undoPiece, historyUndo);
        put(EMPTY, _moveTo.get(_numMoves));
        _moveTo.remove(_numMoves);
        _moveHistory.remove(_numMoves);
        _turn = _turn.opponent();
        _winner = null;
    }

    /**
     * Checks if the given turn has a remaining move to
     * detect a potential winner.
     * @param color is the turn.
     * @return boolean is the value.
     */
    boolean noMoves(Piece color) {
        if (color == WHITE) {
            ArrayList<Square> whites = getWhiteQueens();
            for (Square s : whites) {
                Iterator<Square> iter = reachableFrom(s, null);
                if (iter.next() != null) {
                    return false;
                }
            }
        } else {
            ArrayList<Square> blacks = getBlackQueens();
            for (Square s : blacks) {
                Iterator<Square> iter = reachableFrom(s, null);
                if (iter.next() != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Finds the squares of all the black queens on
     * a given board.
     */
    public void findBlackQueens() {
        _blackQueens = new ArrayList<>();
        _startingSquares = Square.iterator();
        Square check = _startingSquares.next();
        int blackCount = 0;
        while (blackCount < 4) {
            if (get(check) == BLACK) {
                _blackQueens.add(check);
                blackCount++;
                if (blackCount == 4) {
                    return;
                }
            }
            check = _startingSquares.next();
        }
    }

    /**
     * Finds all white queens on a given board.
     */
    public void findWhiteQueens() {
        _whiteQueens = new ArrayList<>();
        _startingSquares = Square.iterator();
        Square check = _startingSquares.next();
        int whiteCount = 0;
        while (whiteCount < 4) {
            if (get(check) == WHITE) {
                _whiteQueens.add(check);
                whiteCount++;
                if (whiteCount == 4) {
                    return;
                }
            }
            check = _startingSquares.next();
        }
    }

    /**
     * Gets an arrayList of the squares of white
     * queens.
     * @return arrayList
     */
    ArrayList<Square> getWhiteQueens() {
        findWhiteQueens();
        return _whiteQueens;
    }

    /**
     * Gets an arrayList of the squares of black
     * queens.
     * @return arrayList
     */
    ArrayList<Square> getBlackQueens() {
        findBlackQueens();
        return _blackQueens;
    }


    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = 0;
            _steps = 0;
            _asEmpty = asEmpty;
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            toNext();
            if (!hasNext()) {
                return null;
            } else {
                return _from.queenMove(_dir, _steps);
            }
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            while (_from.queenMove(_dir, _steps + 1) == null
                    || _from.queenMove(_dir, _steps + 1) != _asEmpty
                    && (get(_from.queenMove(_dir, _steps + 1))
                                    != EMPTY)) {
                if (hasNext()) {
                    _dir++;
                    _steps = 0;
                } else {
                    break;
                }
            }
            _steps++;
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /**
         * All legal moves for SIDE (WHITE or BLACK).
         */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            _start = _startingSquares.next();
            _nextSpear = Square.sq(0);
            _nextSquare = Square.sq(0);
            _queenCount = 0;
            startFinder();
        }

        @Override
        public boolean hasNext() {
            return _queenCount < 4;
        }

        @Override
        public Move next() {
            toNext();
            if (!hasNext()) {
                return null;
            } else {
                return Move.mv(_start, _nextSquare, _nextSpear);
            }
        }

        /**
         * Finds the next valid location of a turn piece.
         */
        private void startFinder() {
            while (get(_start) != _fromPiece
                    && _startingSquares.hasNext()
                    && _start != null) {
                _start = _startingSquares.next();
            }
            if (get(_start) == _fromPiece) {
                _pieceMoves = reachableFrom(_start, null);
                _nextSquare = _pieceMoves.next();
                if (_nextSquare == null) {
                    _queenCount++;
                    if (hasNext()) {
                        _start = _startingSquares.next();
                        startFinder();
                    } else {
                        _start = null;
                        return;
                    }
                }
                _spearThrows = reachableFrom(_nextSquare, _start);
            }
        }

        /**
         * Advance so that the next valid Move is
         * _start-_nextSquare(sp), where sp is the next value of
         * _spearThrows.
         */
        private void toNext() {
            _nextSpear = _spearThrows.next();
            while (!_spearThrows.hasNext()) {
                _nextSquare = _pieceMoves.next();
                while (!_pieceMoves.hasNext()) {
                    _queenCount++;
                    if (hasNext()) {
                        _start = _startingSquares.next();
                        startFinder();
                    } else {
                        _start = null;
                        return;
                    }
                }
                _spearThrows = reachableFrom(_nextSquare, _start);
                _nextSpear = _spearThrows.next();
            }
        }
        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;

        /** Current starting square. */
        private Square _start;

        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;

        /** Current piece's new position. */
        private Square _nextSquare;

        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;

        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;

        /**
         * Square for next spear.
         */
        private Square _nextSpear;

        /**
         * Integer for queen count.
         */
        private int _queenCount;
    }

    @Override
    public String toString() {

        String stringBuild = "";
        for (int row = 9; row >= 0; row--) {
            stringBuild  = stringBuild + "  ";
            for (int col = 0; col < 10; col++) {
                if (_pieceArray[col][row] != WHITE
                        && _pieceArray[col][row] != BLACK
                        && _pieceArray[col][row] != SPEAR) {
                    stringBuild += " " + EMPTY.toString();
                } else if (_pieceArray[col][row] == BLACK) {
                    stringBuild += " " + BLACK.toString();
                } else if (_pieceArray[col][row] == WHITE) {
                    stringBuild += " " + WHITE.toString();
                } else {
                    stringBuild += " " + SPEAR.toString();
                }
            }
            stringBuild += "\n";
        }
        return stringBuild;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();
}
