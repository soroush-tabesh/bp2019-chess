package chess.core;

import java.util.ArrayList;

public abstract class Piece {
    protected final ChessBoard board;
    protected final ChessBoard.Color color;
    protected Cell currentCell;
    protected ArrayList<Integer> lastMoveRound;
    protected ArrayList<Cell> locationHistory;

    protected Piece(Cell position, ChessBoard board, ChessBoard.Color color) {
        this.currentCell = position;
        this.board = board;
        this.color = color;
        lastMoveRound = new ArrayList<>();
        locationHistory = new ArrayList<>();
        locationHistory.add(currentCell);
        currentCell.setPiece(this);
    }

    public static Piece pieceFactory(PieceType type, Cell position, ChessBoard board, ChessBoard.Color color) {
        Piece piece = null;
        switch (type) {
            case PAWN:
                piece = new Pawn(position, board, color);
                break;
            case KING:
                piece = new King(position, board, color);
                break;
            case ROOK:
                piece = new Rook(position, board, color);
                break;
            case QUEEN:
                piece = new Queen(position, board, color);
                break;
            case BISHOP:
                piece = new Bishop(position, board, color);
                break;
            case KNIGHT:
                piece = new Knight(position, board, color);
                break;
        }
        return piece;
    }

    public ArrayList<Integer> getLastMoveRound() {
        return lastMoveRound;
    }

    public void setLastMoveRound(ArrayList<Integer> lastMoveRound) {
        this.lastMoveRound = lastMoveRound;
    }

    public Cell getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(Cell destinationCell) {
        currentCell = destinationCell;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public ChessBoard.Color getColor() {
        return color;
    }

    public abstract ArrayList<Operation> getPossibleMoves();

    public abstract ArrayList<Cell> getThreatenedCells();

    @Override
    public abstract String toString();

    public void threaten(int val) {
        if (currentCell == null)
            return;
        ArrayList<Cell> thr = getThreatenedCells();
        for (Cell cell : thr) {
            cell.changeThreatValue(color, val);
        }
    }

    public void move(Cell destinationCell, boolean isRollback) {
        //threaten(-1);
        if (currentCell != null)
            currentCell.setPiece(null);
        if (destinationCell != null)
            destinationCell.setPiece(this);
        currentCell = destinationCell;
        //threaten(1);
        if (!isRollback) {
            lastMoveRound.add(board.getRound());
            locationHistory.add(currentCell);
        } else {
            lastMoveRound.remove(lastMoveRound.size() - 1);
            locationHistory.remove(locationHistory.size() - 1);
        }
    }

    public void removeFromBoard(boolean isRollback) {
        move(null, isRollback);
    }

    public void addToBoard(Cell position, boolean isRollback) {
        move(position, isRollback);
    }

    public boolean isConquerable(Cell destination) {
        return (destination == null
                || destination.getPiece() == null
                || destination.getPiece().color == color.negate())
                && !(destination.getPiece() instanceof King);
    }

    public boolean checkOperationValidity(Operation operation) {
        boolean res;
        operation.apply();
        board.reevaluateThreats();
        res = !board.isCheck(operation.getPiece().getColor());
        operation.revert();
        board.reevaluateThreats();
        return res;
    }

    public enum PieceType {
        KING, QUEEN, ROOK, KNIGHT, BISHOP, PAWN, NULL;

        public static PieceType parsePieceType(char s) {
            switch (Character.toUpperCase(s)) {
                case 'P':
                    return PAWN;
                case 'R':
                    return ROOK;
                case 'N':
                    return KNIGHT;
                case 'B':
                    return BISHOP;
                case 'Q':
                    return QUEEN;
                case 'K':
                    return KING;
                default:
                    return NULL;
            }
        }
    }
}
