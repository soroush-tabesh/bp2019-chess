package chess.core;

public abstract class SingleOperation {
    private Piece piece;

    public SingleOperation(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }

    public abstract void apply();

    public abstract void revert();

    @Override
    public abstract String toString();
}
