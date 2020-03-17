package chess.core;

import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(Cell position, ChessBoard board, ChessBoard.Color color) {
        super(position, board, color);
    }

    @Override
    public ArrayList<Operation> getPossibleMoves() {
        ArrayList<Operation> possibleMoves = new ArrayList<>();
        int[][] vectors = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] vector : vectors) {
            for (int rank = currentCell.getRank() + vector[0], file = currentCell.getFile() + vector[1];
                 rank >= 0 && rank < 8 && file >= 0 && file < 8;
                 rank += vector[0], file += vector[1]) {
                Cell tcell = board.getCell(rank, file);
                if (isConquerable(tcell)) {
                    Operation operation = Operation.newMoveOrCapture(this, tcell);
                    if (checkOperationValidity(operation))
                        possibleMoves.add(operation);
                }
                if (tcell.getPiece() != null) {
                    if (tcell.getPiece() instanceof Pawn && isConquerable(tcell)) {
                        Cell tcell2 = board.getCell(rank + vector[0], file + vector[1]);
                        if (tcell2 != null && tcell2.getPiece() instanceof Pawn && isConquerable(tcell2)) {
                            Operation operation = Operation.newMoveOrCapture(this, tcell2);
                            operation.add(new Capture(tcell.getPiece()));
                            if (checkOperationValidity(operation))
                                possibleMoves.add(operation);
                        }
                    }
                    break;
                }
            }
        }
        return possibleMoves;
    }

    @Override
    public ArrayList<Cell> getThreatenedCells() {
        ArrayList<Cell> possibleThreats = new ArrayList<>();
        int[][] vectors = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] vector : vectors) {
            for (int rank = currentCell.getRank() + vector[0], file = currentCell.getFile() + vector[1];
                 rank >= 0 && rank < 8 && file >= 0 && file < 8;
                 rank += vector[0], file += vector[1]) {
                Cell tcell = board.getCell(rank, file);
                possibleThreats.add(tcell);
                if (tcell.getPiece() != null)
                    break;
            }
        }
        return possibleThreats;
    }

    public String getSymbol() {
        return (color == ChessBoard.Color.white ? "♖" : "♜");
    }

    @Override
    public String toString() {
        return ChessBoard.useConsoleIcons ? getSymbol() : color.toString() + "R";
    }
}
