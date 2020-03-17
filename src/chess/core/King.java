package chess.core;

import java.util.ArrayList;

public class King extends Piece {
    public King(Cell position, ChessBoard board, ChessBoard.Color color) {
        super(position, board, color);
    }

    private void checkCastling(ArrayList<Operation> possibleMoves) {
        Cell kingCell = color == ChessBoard.Color.white ? board.getCell(7, 4) : board.getCell(0, 4);
        if (!currentCell.equals(kingCell))
            return;
        //check king-side
        boolean kingsideFlag = true;
        Piece kingsideRook = board.getCell(currentCell.getRank(), 7).getPiece();
        if (!(kingsideRook instanceof Rook) || !kingsideRook.lastMoveRound.isEmpty())
            kingsideFlag = false;
        for (int file = 5; file < 7; ++file) {
            Cell tcell = board.getCell(currentCell.getRank(), file);
            if (tcell.getPiece() != null || tcell.getThreatValue(color.negate()) > 0)
                kingsideFlag = false;
        }
        if (kingsideFlag) {
            Operation operation = new Operation(this);
            operation.addAll(Operation.newMoveOrCapture(this
                    , board.getCell(currentCell.getRank(), currentCell.getFile() + 2)));
            operation.addAll(Operation.newMoveOrCapture(kingsideRook
                    , board.getCell(currentCell.getRank(), currentCell.getFile() + 1)));
            if (checkOperationValidity(operation))
                possibleMoves.add(operation);
        }
        //check queen-side
        boolean queensideFlag = true;
        Piece queensideRook = board.getCell(currentCell.getRank(), 0).getPiece();
        if (!(queensideRook instanceof Rook) || !queensideRook.lastMoveRound.isEmpty())
            queensideFlag = false;
        for (int file = 1; file < 4; ++file) {
            Cell tcell = board.getCell(currentCell.getRank(), file);
            if (tcell.getPiece() != null || tcell.getThreatValue(color.negate()) > 0)
                queensideFlag = false;
        }
        if (queensideFlag) {
            Operation operation = new Operation(this);
            operation.addAll(Operation.newMoveOrCapture(this
                    , board.getCell(currentCell.getRank(), currentCell.getFile() - 2)));
            operation.addAll(Operation.newMoveOrCapture(queensideRook
                    , board.getCell(currentCell.getRank(), currentCell.getFile() - 1)));
            if (checkOperationValidity(operation))
                possibleMoves.add(operation);
        }

    }

    @Override
    public ArrayList<Operation> getPossibleMoves() {
        ArrayList<Operation> possibleMoves = new ArrayList<>();
        int[][] vectors = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {-2, 0}, {-2, -2}, {-2, 2}};
        for (int[] vector : vectors) {
            int coef = color == ChessBoard.Color.white ? -1 : 1;
            Cell tcell = board.getCell(currentCell.getRank() + vector[0] * coef,
                    currentCell.getFile() + vector[1] * coef);
            if (tcell != null && isConquerable(tcell)) {
                Operation operation = Operation.newMoveOrCapture(this, tcell);
                if (checkOperationValidity(operation))
                    possibleMoves.add(operation);
            }
        }
        checkCastling(possibleMoves);
        return possibleMoves;
    }

    @Override
    public ArrayList<Cell> getThreatenedCells() {
        ArrayList<Cell> possibleThreats = new ArrayList<>();
        int[][] vectors = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        for (int[] vector : vectors) {
            int coef = color == ChessBoard.Color.white ? -1 : 1;
            Cell tcell = board.getCell(currentCell.getRank() + vector[0] * coef,
                    currentCell.getFile() + vector[1] * coef);
            if (tcell != null)
                possibleThreats.add(tcell);
        }
        return possibleThreats;
    }

    public String getSymbol() {
        return (color == ChessBoard.Color.white ? "♔" : "♚");
    }

    @Override
    public String toString() {
        return ChessBoard.useConsoleIcons ? getSymbol() : color.toString() + "K";
    }
}
