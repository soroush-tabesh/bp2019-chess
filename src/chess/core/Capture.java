package chess.core;

public class Capture extends SingleOperation {
    private Cell pos;

    public Capture(Piece piece) {
        super(piece);
        pos = piece.getCurrentCell();
    }

    @Override
    public void apply() {
        getPiece().removeFromBoard(false);
    }

    @Override
    public void revert() {
        getPiece().addToBoard(pos, true);
    }

    @Override
    public String toString() {
        return String.format("Capturing %s at %s", getPiece().toString(), pos.getCellName());
    }
}
