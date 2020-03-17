package chess;

import chess.core.ChessBoard;
import chess.core.Move;
import chess.core.Operation;

import java.util.ArrayList;
import java.util.Scanner;

public class ApplicationConsole {
    public static void main(String[] args) {
        ChessBoard chessBoard = new ChessBoard();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(chessBoard);
            System.out.println(chessBoard.getTurn() + " to move, enter your desired move: ");
            String[] input = scanner.nextLine().trim().split(" ");
            ArrayList<Operation> allPossibleMoves = chessBoard.getAllPossibleMoves();
            if (input.length != 2) {
                chessBoard.revert();
                continue;
            }
            boolean flag = false;
            for (Operation operation : allPossibleMoves) {
                Move mv = operation.getMove();
                if (mv.getStartCell().getCellName().equalsIgnoreCase(input[0])
                        && mv.getEndCell().getCellName().equalsIgnoreCase(input[1])) {
                    chessBoard.addOperation(operation);
                    flag = true;
                    break;
                }
            }
            if (!flag)
                System.out.println("Illegal move...");
        }
    }
}
