package chess.core;

import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(Cell position, ChessBoard board, ChessBoard.Color color) {
        super(position, board, color);
    }

    @Override
    public ArrayList<Operation> getPossibleMoves() {
        ArrayList<Operation> possibleMoves = new ArrayList<>();
        int[][] vectors = {{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
        for (int[] vector : vectors) {
            int seenEmpty = 0, seenOcc = 0;
            int dist = 0;
            for (int rank = currentCell.getRank() + vector[0], file = currentCell.getFile() + vector[1];
                 rank >= 0 && rank < 8 && file >= 0 && file < 8;
                 rank += vector[0], file += vector[1]) {
                ++dist;
                Cell tcell = board.getCell(rank, file);
                if (tcell.getPiece() == null)
                    ++seenEmpty;
                else
                    ++seenOcc;
                if (seenOcc > 3)
                    break;

                if (isConquerable(tcell)) {
                    Operation operation = Operation.newMoveOrCapture(this, tcell);
                    if (checkOperationValidity(operation))
                        possibleMoves.add(operation);
                }

                if (seenEmpty > 0 && seenOcc > 0)
                    break;
            }

        }
        return possibleMoves;
    }

    @Override
    public ArrayList<Cell> getThreatenedCells() {
        ArrayList<Cell> possibleThreats = new ArrayList<>();
        int[][] vectors = {{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
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
        return (color == ChessBoard.Color.white ? "♗" : "♝");
    }

    @Override
    public String toString() {
        return ChessBoard.useConsoleIcons ? getSymbol() : color.toString() + "B";
    }
}
