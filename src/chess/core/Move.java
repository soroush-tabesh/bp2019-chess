package chess.core;

public class Move extends SingleOperation {
    private Cell startCell, endCell;

    public Move(Piece piece, Cell destination) {
        super(piece);
        startCell = piece.getCurrentCell();
        endCell = destination;
    }

    @Override
    public void apply() {
        getPiece().move(endCell, false);
    }

    public Cell getStartCell() {
        return startCell;
    }

    public Cell getEndCell() {
        return endCell;
    }

    @Override
    public void revert() {
        getPiece().move(startCell, true);
    }

    @Override
    public String toString() {
        return String.format("Move from %s to %s", startCell.getCellName(), endCell.getCellName());
    }
}
