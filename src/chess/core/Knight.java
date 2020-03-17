package chess.core;

import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(Cell position, ChessBoard board, ChessBoard.Color color) {
        super(position, board, color);
    }

    @Override
    public ArrayList<Operation> getPossibleMoves() {
        ArrayList<Operation> possibleMoves = new ArrayList<>();
        for (int rank = -2; rank <= 2; ++rank) {
            if (rank == 0)
                continue;
            Cell tcell1 = board.getCell(rank + currentCell.getRank(), currentCell.getFile() + (3 - Math.abs(rank)));
            Cell tcell2 = board.getCell(rank + currentCell.getRank(), currentCell.getFile() - (3 - Math.abs(rank)));
            if (tcell1 != null && isConquerable(tcell1)) {
                Operation operation = Operation.newMoveOrCapture(this, tcell1);
                if (checkOperationValidity(operation))
                    possibleMoves.add(operation);
            }
            if (tcell2 != null && isConquerable(tcell2)) {
                Operation operation = Operation.newMoveOrCapture(this, tcell2);
                if (checkOperationValidity(operation))
                    possibleMoves.add(operation);
            }
        }
        return possibleMoves;
    }

    @Override
    public ArrayList<Cell> getThreatenedCells() {
        ArrayList<Cell> possibleThreats = new ArrayList<>();
        for (int rank = -2; rank <= 2; ++rank) {
            if (rank == 0)
                continue;
            Cell tcell1 = board.getCell(rank + currentCell.getRank(), currentCell.getFile() + (3 - Math.abs(rank)));
            Cell tcell2 = board.getCell(rank + currentCell.getRank(), currentCell.getFile() - (3 - Math.abs(rank)));
            if (tcell1 != null)
                possibleThreats.add(tcell1);
            if (tcell2 != null)
                possibleThreats.add(tcell2);
        }
        return possibleThreats;
    }

    public String getSymbol() {
        return (color == ChessBoard.Color.white ? "♘" : "♞");
    }

    @Override
    public String toString() {
        return ChessBoard.useConsoleIcons ? getSymbol() : color.toString() + "N";
    }
}
