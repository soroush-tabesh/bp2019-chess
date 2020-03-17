package chess.core;

public class Promote extends SingleOperation {
    private Piece.PieceType destinationType;
    private Piece newPiece;

    public Promote(Piece piece, Piece.PieceType destinationType) {
        super(piece);
        this.destinationType = destinationType;
    }

    @Override
    public void apply() {
        Cell pos = getPiece().getCurrentCell();
        Piece piece = getPiece();
        piece.setCurrentCell(null);
        newPiece = Piece.pieceFactory(destinationType, pos, piece.getBoard(), piece.getColor());
        newPiece.setLastMoveRound(piece.getLastMoveRound());
    }

    @Override
    public void revert() {
        Cell pos = newPiece.getCurrentCell();
        Piece piece = getPiece();
        newPiece.setCurrentCell(null);
        piece.setCurrentCell(pos);
    }

    @Override
    public String toString() {
        return String.format("Promoting PAWN at %s to %s", getPiece().getCurrentCell().getCellName(), destinationType.toString());
    }
}
