package chess.core;

import java.util.ArrayList;

public class Operation extends ArrayList<SingleOperation> {

    private Piece piece;

    public Operation(Piece piece) {
        this.piece = piece;
    }

    public static Operation newMoveOrCapture(Piece piece, Cell destination) {
        Operation operation = new Operation(piece);
        Piece destPiece = destination.getPiece();
        if (destPiece != null) {
            operation.add(new Capture(destPiece));
        }
        operation.add(new Move(piece, destination));
        return operation;
    }

    public Move getMove() {
        for (SingleOperation singleOperation : this) {
            if (singleOperation instanceof Move)
                return (Move) singleOperation;
        }
        return null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void apply() {
        for (int i = 0; i < this.size(); i++) {
            get(i).apply();
        }
    }

    public void revert() {
        for (int i = this.size() - 1; i >= 0; i--) {
            get(i).revert();
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(piece.getCurrentCell().getCellName())
                .append(" : ")
                .append(piece.toString())
                .append("\n");
        for (SingleOperation singleOperation : this) {
            stringBuilder.append(singleOperation.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
