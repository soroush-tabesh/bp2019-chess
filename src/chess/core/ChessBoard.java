package chess.core;

import java.util.ArrayList;

public class ChessBoard {
    public static boolean useConsoleIcons = true;
    private ArrayList<Operation> history;
    private int historyPointer; //points to exclusive end
    private Cell[][] cells;
    private Color turn;
    private int round;
    private King[] kings;

    public ChessBoard(String[] boardText) {
        round = 1;
        history = new ArrayList<>();
        historyPointer = 0;
        cells = new Cell[8][8];
        kings = new King[2];
        for (int rank = 0; rank < 8; ++rank)
            for (int file = 0; file < 8; ++file)
                cells[rank][file] = new Cell(this, rank, file);
        this.turn = Color.white;
        setupBoard(boardText);
    }

    public ChessBoard(String[] boardText, Color turn) {
        this(boardText);
        this.turn = turn;
    }

    public ChessBoard() {
        this(("BR BN BB BQ BK BB BN BR\n" +
                "BP BP BP BP BP BP BP BP\n" +
                "EE EE EE EE EE EE EE EE\n" +
                "EE EE EE EE EE EE EE EE\n" +
                "EE EE EE EE EE EE EE EE\n" +
                "EE EE EE EE EE EE EE EE\n" +
                "WP WP WP WP WP WP WP WP\n" +
                "WR WN WB WQ WK WB WN WR\n").split("\n"));
    }

    public int getRound() {
        return round;
    }

    public Cell getCell(int rank, int file) {
        if (rank >= 8 || rank < 0 || file >= 8 || file < 0)
            return null;
        return cells[rank][file];
    }

    public String[] getSetupString() {
        useConsoleIcons = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (int rank = 0; rank < 8; ++rank) {
            for (int file = 0; file < 8; ++file) {
                stringBuilder.append(getCell(rank, file).toString());
                if (file != 7)
                    stringBuilder.append(" ");
            }
            stringBuilder.append("\n");
        }
        useConsoleIcons = true;
        return stringBuilder.toString().split("\n");
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        //top-border
        stringBuilder.append("   ");
        for (int file = 0; file < 8; ++file) {
            stringBuilder.append((char) ('A' + file) + " ");
            if (!useConsoleIcons) {
                stringBuilder.append(" ");
            }
        }
        stringBuilder.append("\n");
        stringBuilder.append("  ");
        for (int file = 0; file < 8; ++file) {
            stringBuilder.append("--");
            if (!useConsoleIcons) {
                stringBuilder.append("-");
            }
        }
        stringBuilder.append("-\n");

        for (int rank = 0; rank < 8; ++rank) {
            stringBuilder.append((char) ('8' - rank) + "| "); // side-border
            for (int file = 0; file < 8; ++file) {
                stringBuilder.append(getCell(rank, file).toString());
                stringBuilder.append(" ");
            }
            stringBuilder.append("|" + (char) ('8' - rank));// side-border
            stringBuilder.append("\n");
        }

        //bottom-border
        stringBuilder.append("  ");
        for (int file = 0; file < 8; ++file) {
            stringBuilder.append("--");
            if (!useConsoleIcons) {
                stringBuilder.append("-");
            }
        }
        stringBuilder.append("-\n");
        stringBuilder.append("   ");
        for (int file = 0; file < 8; ++file) {
            stringBuilder.append((char) ('A' + file) + " ");
            if (!useConsoleIcons) {
                stringBuilder.append(" ");
            }
        }
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    public void reevaluateThreats() { // should be called after every change in board
        for (int rank = 0; rank < 8; ++rank) {
            for (int file = 0; file < 8; ++file) {
                cells[rank][file].setThreatValue(Color.white, 0);
                cells[rank][file].setThreatValue(Color.black, 0);
            }
        }
        for (int rank = 0; rank < 8; ++rank) {
            for (int file = 0; file < 8; ++file) {
                if (cells[rank][file].getPiece() != null)
                    cells[rank][file].getPiece().threaten(1);
            }
        }
    }

    public void apply() {
        apply(1);
    }

    public void apply(int depth) {
        for (int i = historyPointer; i < history.size() && i < historyPointer + depth; ++i) {
            history.get(i).apply();
            turn = turn.negate();
            ++round;
        }
        historyPointer = Math.min(history.size(), historyPointer + depth);
        reevaluateThreats();
    }

    public void revert() {
        revert(1);
    }

    public void revert(int depth) {
        for (int i = historyPointer - 1; i >= 0 && i >= historyPointer - depth; --i) {
            history.get(i).revert();
            turn = turn.negate();
            --round;
        }
        historyPointer = Math.max(0, historyPointer - depth);
        reevaluateThreats();
    }

    public void setupBoard(String[] boardText) {
        for (int rank = 0; rank < 8; ++rank) {
            String[] pct = boardText[rank].split("\\s");
            for (int file = 0; file < 8; ++file) {
                Piece piece = Piece.pieceFactory(Piece.PieceType.parsePieceType(pct[file].charAt(1))
                        , getCell(rank, file)
                        , this
                        , Color.parseColor(pct[file].charAt(0)));
                if (piece instanceof King)
                    setKing((King) piece);
            }
        }
        reevaluateThreats();
    }

    public void addOperation(Operation oper) {
        while (history.size() > historyPointer)
            history.remove(history.size() - 1);
        history.add(oper);
        apply();
    }

    public King getKing(Color color) {
        return kings[color.toInt()];
    }

    public boolean isCheckmate(Color color) {
        return getAllPossibleMoves(color).isEmpty() && isCheck(color);
    }

    public ArrayList<Operation> getAllPossibleMoves() {
        return getAllPossibleMoves(turn);
    }

    public ArrayList<Operation> getAllPossibleMoves(Color color) {
        ArrayList<Operation> allPossibleMoves = new ArrayList<>();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                Piece piece = cell.getPiece();
                if (piece != null && piece.color == color) {
                    allPossibleMoves.addAll(piece.getPossibleMoves());
                }
            }
        }
        return allPossibleMoves;
    }

    public Color getTurn() {
        return turn;
    }

    public void setTurn(Color turn) {
        this.turn = turn;
    }

    public boolean isCheck(Color color) {
        return getKing(color).getCurrentCell().getThreatValue(color.negate()) > 0;
    }

    private void setKing(King king) {
        kings[king.getColor().toInt()] = king;
    }

    public enum Color {
        white(1), black(0);
        private final int color;

        Color(int color) {
            this.color = color;
        }

        public static Color parseColor(char s) {
            return Character.toUpperCase(s) == 'W' ? white : black;
        }

        public Color negate() {
            return color == 0 ? white : black;
        }

        public int toInt() {
            return color;
        }

        @Override
        public String toString() {
            return color == 0 ? "B" : "W";
        }
    }
}
