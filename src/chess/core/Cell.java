package chess.core;

public final class Cell {
    private final int rank, file;
    private int[] threatValue;
    private Piece piece;
    private ChessBoard board;

    Cell(ChessBoard board, int rank, int file) {
        this.board = board;
        this.rank = rank;
        this.file = file;
        threatValue = new int[2];
    }

    public static Cell parseCell(ChessBoard board, String cellName) {
        cellName = cellName.toUpperCase();
        return new Cell(board, cellName.charAt(0) - 'A', cellName.charAt(1) - '0');
    }

    public ChessBoard getBoard() {
        return board;
    }

    public int getThreatValue(ChessBoard.Color color) {
        return threatValue[color.toInt()];
    }

    public void setThreatValue(ChessBoard.Color color, int threatValue) {
        this.threatValue[color.toInt()] = threatValue;
    }

    public void changeThreatValue(ChessBoard.Color color, int change) {
        threatValue[color.toInt()] += change;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell))
            return false;
        Cell t = (Cell) obj;
        return t.file == file && t.rank == rank;
    }

    public int getRank() {
        return rank;
    }

    public int getFile() {
        return file;
    }

    @Override
    public String toString() {
        return piece != null ? piece.toString() : (ChessBoard.useConsoleIcons ? "•" : "EE");
    }

    public String getCellName() {
        return "" + (char) ('A' + getFile()) + (char) ('8' - getRank());
    }
}
