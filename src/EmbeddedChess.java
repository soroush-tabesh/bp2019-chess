import java.util.ArrayList;
import java.util.Scanner;

public class EmbeddedChess {
    public static void main(String[] args) {
        EmbeddedChess embeddedChess = new EmbeddedChess();
        embeddedChess.run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String[] begin = new String[8], end = new String[8];
        for (int i = 0; i < 8; ++i) {
            begin[i] = scanner.nextLine();
        }
        scanner.nextLine();
        for (int i = 0; i < 8; ++i) {
            end[i] = scanner.nextLine();
        }
        ChessBoard beginBoard = new ChessBoard(begin);
        ChessBoard endBoard = new ChessBoard(end);
        boolean possible = false;
        possible |= checkPossibility(beginBoard, 3, endBoard.toString());
        beginBoard.setTurn(ChessBoard.Color.black);
        possible |= checkPossibility(beginBoard, 3, endBoard.toString());
        if (possible) {
            if (endBoard.isCheckmate(ChessBoard.Color.white) || endBoard.isCheckmate(ChessBoard.Color.black)) {
                System.out.println("possible checkmate");
            } else {
                System.out.println("possible no_checkmate");
            }
        } else {
            System.out.println("impossible");
        }
    }

    public boolean checkPossibility(ChessBoard board, int depth, String dest) {
        if (depth < 0)
            return false;
        if (board.toString().equalsIgnoreCase(dest))
            return true;
        if (board.isCheck(board.getTurn().negate()))
            return false;
        for (Operation operation : board.getAllPossibleMoves()) {
            board.addOperation(operation);
            if (checkPossibility(board, depth - 1, dest))
                return true;
            board.revert();
        }
        return false;
    }
}

class Bishop extends Piece {

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

class Capture extends SingleOperation {
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

final class Cell {
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

class ChessBoard {
    public static boolean useConsoleIcons = true;
    private ArrayList<Operation> history;
    private int historyPointer; //points to exclusive end
    private Cell[][] cells;
    private ChessBoard.Color turn;
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
        this.turn = ChessBoard.Color.white;
        setupBoard(boardText);
    }

    public ChessBoard(String[] boardText, ChessBoard.Color turn) {
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
                cells[rank][file].setThreatValue(ChessBoard.Color.white, 0);
                cells[rank][file].setThreatValue(ChessBoard.Color.black, 0);
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
                        , ChessBoard.Color.parseColor(pct[file].charAt(0)));
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

    public King getKing(ChessBoard.Color color) {
        return kings[color.toInt()];
    }

    public boolean isCheckmate(ChessBoard.Color color) {
        return getAllPossibleMoves(color).isEmpty() && isCheck(color);
    }

    public ArrayList<Operation> getAllPossibleMoves() {
        return getAllPossibleMoves(turn);
    }

    public ArrayList<Operation> getAllPossibleMoves(ChessBoard.Color color) {
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

    public ChessBoard.Color getTurn() {
        return turn;
    }

    public void setTurn(ChessBoard.Color turn) {
        this.turn = turn;
    }

    public boolean isCheck(ChessBoard.Color color) {
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

        public static ChessBoard.Color parseColor(char s) {
            return Character.toUpperCase(s) == 'W' ? white : black;
        }

        public ChessBoard.Color negate() {
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

class King extends Piece {
    public King(Cell position, ChessBoard board, ChessBoard.Color color) {
        super(position, board, color);
    }

    private void checkCastling(ArrayList<Operation> possibleMoves) {
        Cell kingCell = color == ChessBoard.Color.white ? board.getCell(7, 4) : board.getCell(0, 4);
        if (!currentCell.equals(kingCell))
            return;
        //check king-side
        boolean kingsideFlag = true;
        Piece kingsideRook = board.getCell(currentCell.getRank(), 7).getPiece();
        if (!(kingsideRook instanceof Rook) || !kingsideRook.lastMoveRound.isEmpty())
            kingsideFlag = false;
        for (int file = 5; file < 7; ++file) {
            Cell tcell = board.getCell(currentCell.getRank(), file);
            if (tcell.getPiece() != null /*|| tcell.getThreatValue(color.negate()) > 0*/)
                kingsideFlag = false;
        }
        if (kingsideFlag) {
            Operation operation = new Operation(this);
            operation.addAll(Operation.newMoveOrCapture(this
                    , board.getCell(currentCell.getRank(), currentCell.getFile() + 2)));
            operation.addAll(Operation.newMoveOrCapture(kingsideRook
                    , board.getCell(currentCell.getRank(), currentCell.getFile() + 1)));
            if (checkOperationValidity(operation))
                possibleMoves.add(operation);
        }
        //check queen-side
        boolean queensideFlag = true;
        Piece queensideRook = board.getCell(currentCell.getRank(), 0).getPiece();
        if (!(queensideRook instanceof Rook) || !queensideRook.lastMoveRound.isEmpty())
            queensideFlag = false;
        for (int file = 1; file < 4; ++file) {
            Cell tcell = board.getCell(currentCell.getRank(), file);
            if (tcell.getPiece() != null/* || tcell.getThreatValue(color.negate()) > 0*/)
                queensideFlag = false;
        }
        if (queensideFlag) {
            Operation operation = new Operation(this);
            operation.addAll(Operation.newMoveOrCapture(this
                    , board.getCell(currentCell.getRank(), currentCell.getFile() - 2)));
            operation.addAll(Operation.newMoveOrCapture(queensideRook
                    , board.getCell(currentCell.getRank(), currentCell.getFile() - 1)));
            if (checkOperationValidity(operation))
                possibleMoves.add(operation);
        }

    }

    @Override
    public ArrayList<Operation> getPossibleMoves() {
        ArrayList<Operation> possibleMoves = new ArrayList<>();
        int[][] vectors = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {-2, 0}, {-2, -2}, {-2, 2}};
        for (int[] vector : vectors) {
            int coef = color == ChessBoard.Color.white ? -1 : 1;
            Cell tcell = board.getCell(currentCell.getRank() + vector[0] * coef,
                    currentCell.getFile() + vector[1] * coef);
            if (tcell != null && isConquerable(tcell)) {
                Operation operation = Operation.newMoveOrCapture(this, tcell);
                if (checkOperationValidity(operation))
                    possibleMoves.add(operation);
            }
        }
        checkCastling(possibleMoves);
        return possibleMoves;
    }

    @Override
    public ArrayList<Cell> getThreatenedCells() {
        ArrayList<Cell> possibleThreats = new ArrayList<>();
        int[][] vectors = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        for (int[] vector : vectors) {
            int coef = color == ChessBoard.Color.white ? -1 : 1;
            Cell tcell = board.getCell(currentCell.getRank() + vector[0] * coef,
                    currentCell.getFile() + vector[1] * coef);
            if (tcell != null)
                possibleThreats.add(tcell);
        }
        return possibleThreats;
    }

    public String getSymbol() {
        return (color == ChessBoard.Color.white ? "♔" : "♚");
    }

    @Override
    public String toString() {
        return ChessBoard.useConsoleIcons ? getSymbol() : color.toString() + "K";
    }
}

class Knight extends Piece {
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

class Move extends SingleOperation {
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

class Operation extends ArrayList<SingleOperation> {

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

class Pawn extends Piece {
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
                    && sidePiece.color == color.negate()
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

abstract class Piece {
    protected final ChessBoard board;
    protected final ChessBoard.Color color;
    protected Cell currentCell;
    protected ArrayList<Integer> lastMoveRound;
    protected ArrayList<Cell> locationHistory;

    protected Piece(Cell position, ChessBoard board, ChessBoard.Color color) {
        this.currentCell = position;
        this.board = board;
        this.color = color;
        lastMoveRound = new ArrayList<>();
        locationHistory = new ArrayList<>();
        locationHistory.add(currentCell);
        currentCell.setPiece(this);
    }

    public static Piece pieceFactory(Piece.PieceType type, Cell position, ChessBoard board, ChessBoard.Color color) {
        Piece piece = null;
        switch (type) {
            case PAWN:
                piece = new Pawn(position, board, color);
                break;
            case KING:
                piece = new King(position, board, color);
                break;
            case ROOK:
                piece = new Rook(position, board, color);
                break;
            case QUEEN:
                piece = new Queen(position, board, color);
                break;
            case BISHOP:
                piece = new Bishop(position, board, color);
                break;
            case KNIGHT:
                piece = new Knight(position, board, color);
                break;
        }
        return piece;
    }

    public ArrayList<Integer> getLastMoveRound() {
        return lastMoveRound;
    }

    public void setLastMoveRound(ArrayList<Integer> lastMoveRound) {
        this.lastMoveRound = lastMoveRound;
    }

    public Cell getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(Cell destinationCell) {
        currentCell = destinationCell;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public ChessBoard.Color getColor() {
        return color;
    }

    public abstract ArrayList<Operation> getPossibleMoves();

    public abstract ArrayList<Cell> getThreatenedCells();

    @Override
    public abstract String toString();

    public void threaten(int val) {
        if (currentCell == null)
            return;
        ArrayList<Cell> thr = getThreatenedCells();
        for (Cell cell : thr) {
            cell.changeThreatValue(color, val);
        }
    }

    public void move(Cell destinationCell, boolean isRollback) {
        //threaten(-1);
        if (currentCell != null)
            currentCell.setPiece(null);
        if (destinationCell != null)
            destinationCell.setPiece(this);
        currentCell = destinationCell;
        //threaten(1);
        if (!isRollback) {
            lastMoveRound.add(board.getRound());
            locationHistory.add(currentCell);
        } else {
            lastMoveRound.remove(lastMoveRound.size() - 1);
            locationHistory.remove(locationHistory.size() - 1);
        }
    }

    public void removeFromBoard(boolean isRollback) {
        move(null, isRollback);
    }

    public void addToBoard(Cell position, boolean isRollback) {
        move(position, isRollback);
    }

    public boolean isConquerable(Cell destination) {
        return (destination == null
                || destination.getPiece() == null
                || destination.getPiece().color == color.negate())
                && !(destination.getPiece() instanceof King);
    }

    public boolean checkOperationValidity(Operation operation) {
        boolean res;
        operation.apply();
        board.reevaluateThreats();
        res = !board.isCheck(operation.getPiece().getColor());
        operation.revert();
        board.reevaluateThreats();
        return res;
    }

    public enum PieceType {
        KING, QUEEN, ROOK, KNIGHT, BISHOP, PAWN, NULL;

        public static Piece.PieceType parsePieceType(char s) {
            switch (Character.toUpperCase(s)) {
                case 'P':
                    return PAWN;
                case 'R':
                    return ROOK;
                case 'N':
                    return KNIGHT;
                case 'B':
                    return BISHOP;
                case 'Q':
                    return QUEEN;
                case 'K':
                    return KING;
                default:
                    return NULL;
            }
        }
    }
}

class Promote extends SingleOperation {
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

class Queen extends Piece {
    public Queen(Cell position, ChessBoard board, ChessBoard.Color color) {
        super(position, board, color);
    }

    @Override
    public ArrayList<Operation> getPossibleMoves() {
        ArrayList<Operation> possibleMoves = new ArrayList<>();
        int[][] vectors = {{1, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] vector : vectors) {
            for (int rank = currentCell.getRank() + vector[0], file = currentCell.getFile() + vector[1];
                 rank >= 0 && rank < 8 && file >= 0 && file < 8;
                 rank += vector[0], file += vector[1]) {
                Cell tcell = board.getCell(rank, file);
                if (isConquerable(tcell)) {
                    Operation operation = Operation.newMoveOrCapture(this, tcell);
                    if (checkOperationValidity(operation))
                        possibleMoves.add(operation);
                }
                if (tcell.getPiece() != null)
                    break;
            }
        }
        return possibleMoves;
    }

    @Override
    public ArrayList<Cell> getThreatenedCells() {
        ArrayList<Cell> possibleThreats = new ArrayList<>();
        int[][] vectors = {{1, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};
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
        return (color == ChessBoard.Color.white ? "♕" : "♛");
    }

    @Override
    public String toString() {
        return ChessBoard.useConsoleIcons ? getSymbol() : color.toString() + "Q";
    }
}

class Rook extends Piece {
    public Rook(Cell position, ChessBoard board, ChessBoard.Color color) {
        super(position, board, color);
    }

    @Override
    public ArrayList<Operation> getPossibleMoves() {
        ArrayList<Operation> possibleMoves = new ArrayList<>();
        int[][] vectors = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] vector : vectors) {
            for (int rank = currentCell.getRank() + vector[0], file = currentCell.getFile() + vector[1];
                 rank >= 0 && rank < 8 && file >= 0 && file < 8;
                 rank += vector[0], file += vector[1]) {
                Cell tcell = board.getCell(rank, file);
                if (isConquerable(tcell)) {
                    Operation operation = Operation.newMoveOrCapture(this, tcell);
                    if (checkOperationValidity(operation))
                        possibleMoves.add(operation);
                }
                if (tcell.getPiece() != null) {
                    if (tcell.getPiece() instanceof Pawn && isConquerable(tcell)) {
                        Cell tcell2 = board.getCell(rank + vector[0], file + vector[1]);
                        if (tcell2 != null && tcell2.getPiece() instanceof Pawn && isConquerable(tcell2)) {
                            Operation operation = Operation.newMoveOrCapture(this, tcell2);
                            operation.add(new Capture(tcell.getPiece()));
                            if (checkOperationValidity(operation))
                                possibleMoves.add(operation);
                        }
                    }
                    break;
                }
            }
        }
        return possibleMoves;
    }

    @Override
    public ArrayList<Cell> getThreatenedCells() {
        ArrayList<Cell> possibleThreats = new ArrayList<>();
        int[][] vectors = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
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
        return (color == ChessBoard.Color.white ? "♖" : "♜");
    }

    @Override
    public String toString() {
        return ChessBoard.useConsoleIcons ? getSymbol() : color.toString() + "R";
    }
}

abstract class SingleOperation {
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
