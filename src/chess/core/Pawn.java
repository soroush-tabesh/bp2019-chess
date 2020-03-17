package chess.core;

import java.util.ArrayList;

public class Pawn extends Piece {
    public Pawn(Cell position, ChessBoard board, ChessBoard.Color color) {
        super(position, board, color);
    }

    private void checkPromotion(ArrayList<Operation> possibleMoves) {
        int coef = color == ChessBoard.Color.white ? -1 : 1;
        Cell frontCell = board.getCell(currentCell.getRank() + coef, currentCell.getFile());
        if (frontCell != null
                && ((frontCell.getRank() == 7 && color == ChessBoard.Color.black) || (frontCell.getRank() == 0 && color == ChessBoard.Color.white))
                && frontCell.getPiece() == null) {
            for (PieceType type : PieceType.values()) {
                if (type == PieceType.PAWN || type == PieceType.KING || type == PieceType.NULL)
                    continue;
                Operation operation = Operation.newMoveOrCapture(this, frontCell);
                operation.add(new Promote(this, type));
                if (checkOperationValidity(operation))
                    possibleMoves.add(operation);
            }
        }
        Cell attackr = board.getCell(currentCell.getRank() + coef, currentCell.getFile() + 1);
        if (attackr != null
                && attackr.getPiece() != null
                && ((attackr.getRank() == 7 && color == ChessBoard.Color.black) || (attackr.getRank() == 0 && color == ChessBoard.Color.white))
                && isConquerable(attackr)) {
            for (PieceType type : PieceType.values()) {
                if (type == PieceType.PAWN || type == PieceType.KING || type == PieceType.NULL)
                    continue;
                Operation operation = Operation.newMoveOrCapture(this, attackr);
                operation.add(new Promote(this, type));
                if (checkOperationValidity(operation))
                    possibleMoves.add(operation);
            }
        }
        Cell attackl = board.getCell(currentCell.getRank() + coef, currentCell.getFile() - 1);
        if (attackl != null
                && attackl.getPiece() != null
                && ((attackl.getRank() == 7 && color == ChessBoard.Color.black) || (attackl.getRank() == 0 && color == ChessBoard.Color.white))
                && isConquerable(attackl)) {
            for (PieceType type : PieceType.values()) {
                if (type == PieceType.PAWN || type == PieceType.KING || type == PieceType.NULL)
                    continue;
                Operation operation = Operation.newMoveOrCapture(this, attackl);
                operation.add(new Promote(this, type));
                if (checkOperationValidity(operation))
                    possibleMoves.add(operation);
            }
        }
    }

    private void checkEnPassant(ArrayList<Operation> possibleMoves) {
        for (int file = -1; file <= 1; file += 2) {
            Cell sideCell = board.getCell(currentCell.getRank(), currentCell.getFile() + file);
            if (sideCell == null)
                continue;
            Piece sidePiece = sideCell.getPiece();
            if (sidePiece instanceof Pawn
                    && sidePiece.lastMoveRound.size() == 1
                    && sidePiece.lastMoveRound.get(0) == board.getRound() - 1 // also ensures it's the opponent
                    && sidePiece.locationHistory.get(sidePiece.locationHistory.size() - 2).getRank()
                    == (sidePiece.color == ChessBoard.Color.white ? 6 : 1)
                    && (sideCell.getRank() == 4 || sideCell.getRank() == 3)) {
                Operation operation = Operation.newMoveOrCapture(this, sideCell);
                int coef = color == ChessBoard.Color.white ? -1 : 1;
                Cell tcell = board.getCell(sideCell.getRank() + coef, sideCell.getFile());
                Move mv = new Move(this, tcell);
                operation.set(1, mv);
                if (checkOperationValidity(operation))
                    possibleMoves.add(operation);
            }
        }
    }

    public void checkDoubleForward(ArrayList<Operation> possibleMoves) {
        int coef = color == ChessBoard.Color.white ? -1 : 1;
        Cell frontCell = board.getCell(currentCell.getRank() + coef, currentCell.getFile());
        if (frontCell == null)
            return;
        Cell secFrontCell = board.getCell(frontCell.getRank() + coef, frontCell.getFile());
        if (secFrontCell == null)
            return;
        if (currentCell.getRank() == (color == ChessBoard.Color.white ? 6 : 1)
                && frontCell.getPiece() == null
                && secFrontCell.getPiece() == null) {
            Operation operation = Operation.newMoveOrCapture(this, secFrontCell);
            if (checkOperationValidity(operation))
                possibleMoves.add(operation);
        }
    }

    @Override
    public ArrayList<Operation> getPossibleMoves() {
        ArrayList<Operation> possibleMoves = new ArrayList<>();
        int coef = color == ChessBoard.Color.white ? -1 : 1;
        Cell frontCell = board.getCell(currentCell.getRank() + coef, currentCell.getFile());
        if (frontCell != null
                && frontCell.getPiece() == null
                && frontCell.getRank() != 7 && frontCell.getRank() != 0) {
            Operation operation = Operation.newMoveOrCapture(this, frontCell);
            if (checkOperationValidity(operation))
                possibleMoves.add(operation);
        }
        Cell attackCell1 = board.getCell(currentCell.getRank() + coef, currentCell.getFile() - 1);
        Cell attackCell2 = board.getCell(currentCell.getRank() + coef, currentCell.getFile() + 1);
        if (attackCell1 != null && attackCell1.getPiece() != null && isConquerable(attackCell1)) {
            Operation operation = Operation.newMoveOrCapture(this, attackCell1);
            if (checkOperationValidity(operation))
                possibleMoves.add(operation);
        }
        if (attackCell2 != null && attackCell2.getPiece() != null && isConquerable(attackCell2)) {
            Operation operation = Operation.newMoveOrCapture(this, attackCell2);
            if (checkOperationValidity(operation))
                possibleMoves.add(operation);
        }
        checkPromotion(possibleMoves);
        checkEnPassant(possibleMoves);
        checkDoubleForward(possibleMoves);

        //remove unwanted
        possibleMoves.removeIf(operation -> {
            Move move = operation.getMove();
            if (move.getStartCell().equals(move.getEndCell()))
                return true;
            if ((color == ChessBoard.Color.white && move.getEndCell().getRank() == 0)
                    || (color == ChessBoard.Color.black && move.getEndCell().getRank() == 7)) {
                boolean haspromo = false;
                for (SingleOperation singleOperation : operation) {
                    if (singleOperation instanceof Promote)
                        haspromo = true;
                }
                if (!haspromo)
                    return true;
            }
            return false;
        });
        return possibleMoves;
    }

    @Override
    public ArrayList<Cell> getThreatenedCells() {
        ArrayList<Cell> possibleThreats = new ArrayList<>();
        int coef = color == ChessBoard.Color.white ? -1 : 1;
        Cell attackCell1 = board.getCell(currentCell.getRank() + coef, currentCell.getFile() - 1);
        Cell attackCell2 = board.getCell(currentCell.getRank() + coef, currentCell.getFile() + 1);
        if (attackCell1 != null)
            possibleThreats.add(attackCell1);
        if (attackCell2 != null)
            possibleThreats.add(attackCell2);
        return possibleThreats;
    }

    public String getSymbol() {
        return (color == ChessBoard.Color.white ? "♙" : "♟");
    }

    @Override
    public String toString() {
        return ChessBoard.useConsoleIcons ? getSymbol() : color.toString() + "P";
    }
}
