package chess;

import chess.core.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ApplicationGUI {
    static JFrame f;
    JPanel gui;
    Image[][] chessPieceImages;
    JButton[][] chessBoardCells;
    JLabel moveLabel;
    JPanel chessBoardView;
    ChessBoard chessBoardCore;
    boolean selectionMode = false;
    int selRank, selFile;

    public ApplicationGUI() {
        chessPieceImages = new Image[2][6];
        chessBoardCells = new JButton[8][8];
        initializeGUI();
        newGame();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Runnable r = () -> {
            ApplicationGUI gui = new ApplicationGUI();

            f = new JFrame("Chess Project by Soroush Tabesh");
            f.add(gui.getGui());
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setLocationByPlatform(true);
            f.pack();
            f.setMinimumSize(f.getSize());
            f.setVisible(true);
        };
        SwingUtilities.invokeLater(r);
    }

    public void initializeGUI() {
        //init resources
        createImages();

        //init main gui
        gui = new JPanel(new BorderLayout(0, 0));

        //init toolbar
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_END);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null
                    , "Start a new game? your progress will be lost.") == JOptionPane.YES_OPTION) {
                newGame();
            }
        });
        tools.add(newGameButton);
        tools.addSeparator();

        JButton clearHistoryButton = new JButton("Clear History");
        clearHistoryButton.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null
                    , "Clear history?") == JOptionPane.YES_OPTION) {
                clearHistory();
                JOptionPane.showMessageDialog(null, "History cleared!");
            }
        });
        tools.add(clearHistoryButton);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            undoButtonClick();
        });
        tools.add(undoButton);

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> {
            redoButtonClick();
        });
        tools.add(redoButton);

        tools.addSeparator();
        moveLabel = new JLabel("White to move");
        tools.add(moveLabel);

        chessBoardView = new JPanel(new GridLayout(0, 8)) {
            @Override
            public final Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Dimension prefSize = null;
                Component c = getParent();
                if (c == null)
                    prefSize = new Dimension(
                            (int) d.getWidth(), (int) d.getHeight());
                else if (c.getWidth() > d.getWidth() && c.getHeight() > d.getHeight())
                    prefSize = c.getSize();
                else
                    prefSize = d;
                int s = (int) Math.min(prefSize.getWidth(), prefSize.getHeight());
                return new Dimension(s, s);
            }
        };

        chessBoardView.setBackground(new Color(238, 238, 238));
        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setBackground(new Color(238, 238, 238));
        boardContainer.add(chessBoardView);
        gui.add(boardContainer);

        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int i = 0; i < chessBoardCells.length; i++) {
            for (int j = 0; j < chessBoardCells[i].length; j++) {
                JButton b = new JButton();
                chessBoardCells[i][j] = b;
                b.setMargin(buttonMargin);
                b.setBorder(new EmptyBorder(0, 0, 0, 0));
                b.setIcon(new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)));
                if ((i + j) % 2 == 1)
                    b.setBackground(new Color(108, 107, 106));
                else
                    b.setBackground(new Color(243, 163, 60));
                b.setActionCommand(i + "," + j);
                b.setFocusPainted(false);
                b.addActionListener(e -> {
                    JButton bt = (JButton) e.getSource();
                    cellClick(Integer.parseInt(bt.getActionCommand().split(",")[0])
                            , Integer.parseInt(bt.getActionCommand().split(",")[1]));
                });
            }
        }

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                chessBoardView.add(chessBoardCells[i][j]);
    }

    private void createImages() {
        try {
            BufferedImage bi = ImageIO.read(getClass().getResource("/chess/chesspieces2.png"));
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 6; j++) {
                    chessPieceImages[i][j] = bi.getSubimage(
                            j * 64, i * 64, 64, 64);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void repaintAll() {
        moveLabel.setText((chessBoardCore.getTurn() == ChessBoard.Color.white ? "White" : "Black") + " to move...");
        for (int rank = 0; rank < 8; ++rank) {
            for (int file = 0; file < 8; ++file) {
                chessBoardCells[rank][file].setBorder(new EmptyBorder(0, 0, 0, 0));
                Piece piece = chessBoardCore.getCell(rank, file).getPiece();
                if (piece == null) {
                    chessBoardCells[rank][file].setIcon(null);
                } else {
                    if (selectionMode) {
                        if (selRank == rank && selFile == file) {
                            chessBoardCells[rank][file].setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3), Color.green));
                        }
                    } else {
                        if (piece.getColor() == chessBoardCore.getTurn() && !piece.getPossibleMoves().isEmpty()) {
                            chessBoardCells[rank][file].setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3), Color.green));
                        }
                    }
                    ChessBoard.useConsoleIcons = false;
                    chessBoardCells[rank][file].setIcon(new ImageIcon(
                            chessPieceImages[piece.getColor().toInt()]
                                    [Piece.PieceType.parsePieceType(piece.toString().charAt(1)).ordinal()]));
                }
            }
        }
        if (selectionMode) {
            Piece piece = chessBoardCore.getCell(selRank, selFile).getPiece();
            ArrayList<Operation> possibleMoves = piece.getPossibleMoves();
            for (Operation operation : possibleMoves) {
                chessBoardCells[operation.getMove().getEndCell().getRank()][operation.getMove().getEndCell().getFile()]
                        .setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3), Color.blue));
            }
        }
        if (chessBoardCore.isCheck(chessBoardCore.getTurn())) {
            Cell kingcell = chessBoardCore.getKing(chessBoardCore.getTurn()).getCurrentCell();
            chessBoardCells[kingcell.getRank()][kingcell.getFile()]
                    .setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3), Color.red));
        }
        gui.repaint();
    }

    public void newGame() {
        selectionMode = false;
        chessBoardCore = new ChessBoard();
        repaintAll();
    }

    public void clearHistory() {
        chessBoardCore = new ChessBoard(chessBoardCore.getSetupString());
    }

    public void cellClick(int rank, int file) {
        Piece piece = chessBoardCore.getCell(rank, file).getPiece();
        if (selectionMode) {
            ArrayList<Operation> allPossibleMoves = chessBoardCore.getAllPossibleMoves();
            ArrayList<Operation> desMoves = new ArrayList<>();

            for (Operation operation : allPossibleMoves) {
                Move mv = operation.getMove();
                if (mv.getStartCell().getRank() == selRank && mv.getStartCell().getFile() == selFile
                        && mv.getEndCell().getRank() == rank && mv.getEndCell().getFile() == file) {
                    desMoves.add(operation);
                }
            }

            if (desMoves.size() == 1) {
                chessBoardCore.addOperation(desMoves.get(0));
            } else if (desMoves.size() > 1) {
                f.setEnabled(false);
                final JComboBox comboBox = new JComboBox(new String[]{"Queen", "Rook", "Knight", "Bishop"});
                Object[] options = new Object[]{};
                JOptionPane jop = new JOptionPane("Select your promotion",
                        JOptionPane.QUESTION_MESSAGE,
                        JOptionPane.DEFAULT_OPTION,
                        null, options, null);
                jop.add(comboBox);
                JButton button = new JButton("OK");
                jop.add(button);
                final JDialog diag = new JDialog();
                diag.getContentPane().add(jop);

                button.addActionListener(e -> {
                    f.setEnabled(true);
                    diag.setVisible(false);
                    chessBoardCore.addOperation(desMoves.get(comboBox.getSelectedIndex()));
                    //checkmate
                    if (chessBoardCore.isCheckmate(chessBoardCore.getTurn())) {
                        JOptionPane.showMessageDialog(null
                                , (chessBoardCore.getTurn() == ChessBoard.Color.white ? "Black" : "White")
                                        + " has won the game.");
                    } else if (chessBoardCore.getAllPossibleMoves().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "The game is a draw!");
                    }
                    selectionMode = false;
                    repaintAll();
                });

                diag.pack();
                diag.setVisible(true);
                diag.setAlwaysOnTop(true);
            }

            //checkmate
            if (chessBoardCore.isCheckmate(chessBoardCore.getTurn())) {
                JOptionPane.showMessageDialog(null
                        , (chessBoardCore.getTurn() == ChessBoard.Color.white ? "Black" : "White")
                                + " has won the game.");
            } else if (chessBoardCore.getAllPossibleMoves().isEmpty()) {
                JOptionPane.showMessageDialog(null, "The game is a draw!");
            }
            selectionMode = false;

        } else {
            if (piece != null && piece.getColor() == chessBoardCore.getTurn() && !piece.getPossibleMoves().isEmpty()) {
                selectionMode = true;
                selFile = file;
                selRank = rank;
            }
        }
        repaintAll();
    }

    public void undoButtonClick() {
        selectionMode = false;
        chessBoardCore.revert();
        repaintAll();
    }

    public void redoButtonClick() {
        selectionMode = false;
        chessBoardCore.apply();
        repaintAll();
    }

    private JPanel getGui() {
        return gui;
    }
}
