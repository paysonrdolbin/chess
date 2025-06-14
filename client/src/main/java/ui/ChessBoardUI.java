package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;

public class ChessBoardUI {

    public static void main(ChessGame game, Boolean isWhite, ChessPosition pos) {

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ChessBoard board = game.getBoard();

        out.print(ERASE_SCREEN);

        headers(out, isWhite);

        if (pos != null && board.getPiece(pos) != null){
            Collection<ChessMove> moves = game.validMoves(pos);
            ArrayList<ChessPosition> endPositions = new ArrayList<>();
            for(ChessMove move: moves){
                endPositions.add(move.getEndPosition());
            }
            drawHBoard(out, board, isWhite, pos, endPositions);
        } else {
            drawBoard(out, board, isWhite);
        }

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void headers(PrintStream out, boolean isWhite) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        if (isWhite) {
            out.print("    a  b  c  d  e  f  g  h \n");
        } else {
            out.print("    h  g  f  e  d  c  b  a \n");
        }
    }

    public static void drawHBoard(PrintStream out, ChessBoard board, Boolean isWhite,
                                  ChessPosition pos, ArrayList<ChessPosition> endPositions){
        if (isWhite) {
            for (int row = 8; row >= 1; row--) {
                int startCol = (pos.getRow() == row) ? pos.getColumn() : -1;
                Set<Integer> hCols = new HashSet<>();
                for (ChessPosition position : endPositions) {
                    if (position.getRow() == row) {
                        hCols.add(position.getColumn());
                    }
                }
                if (!hCols.isEmpty() || startCol != -1) {
                    drawHRow(out, board, row, true, hCols, startCol);
                } else {
                    drawRow(out, board, row, true);
                }
            }
        } else {
            for (int row = 1; row <= 8; row++) {
                int startCol = (pos.getRow() == row) ? pos.getColumn() : -1;
                Set<Integer> hCols = new HashSet<>();
                for (ChessPosition position : endPositions) {
                    if (position.getRow() == row) {
                        hCols.add(position.getColumn());
                    }
                }
                if (!hCols.isEmpty() || startCol != -1) {
                    drawHRow(out, board, row, false, hCols, startCol);
                } else {
                    drawRow(out, board, row, false);
                }
            }
        }
        setBlack(out);
    }

    private static void drawHRow(PrintStream out, ChessBoard board, int row,
                                 Boolean leftToRight, Set<Integer> hCols, int startCol){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(" " + row + " ");

        int colStart = leftToRight ? 1 : 8;
        int colEnd = leftToRight ? 8 : 1;
        int step = leftToRight ? 1 : -1;

        for (int col = colStart; leftToRight ? col <= colEnd : col >= colEnd; col += step) {
            boolean isWhiteSquare = (row + col) % 2 == 0;

            if (col == startCol){
                setYellow(out);
            } else if (hCols.contains(col) && isWhiteSquare) {
                setGreen(out);
            } else if (hCols.contains(col) && !isWhiteSquare){
                setDarkGreen(out);
            } else if (isWhiteSquare) {
                setWhite(out);
            } else {
                setBlack(out);
            }

            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            if (piece != null) {
                printPiece(out, piece);
            } else {
                out.print("   ");
            }
        }

        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_BG_COLOR_BLACK);
        out.println();

    }

    private static void drawBoard(PrintStream out, ChessBoard board, Boolean isWhite) {
        if (isWhite) {
            for (int row = 8; row >= 1; row--) {
                drawRow(out, board, row, true);
            }
        } else {
            for (int row = 1; row <= 8; row++) {
                drawRow(out, board, row, false);
            }
        }
        setBlack(out);
    }

    private static void drawRow(PrintStream out, ChessBoard board, int row, boolean leftToRight) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(" " + row + " ");

        int colStart = leftToRight ? 1 : 8;
        int colEnd = leftToRight ? 8 : 1;
        int step = leftToRight ? 1 : -1;

        for (int col = colStart; leftToRight ? col <= colEnd : col >= colEnd; col += step) {
            boolean isWhiteSquare = (row + col) % 2 == 0;

            if (isWhiteSquare) {
                setWhite(out);
            } else {
                setBlack(out);
            }

            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            if (piece != null) {
                printPiece(out, piece);
            } else {
                out.print("   ");
            }
        }

        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_BG_COLOR_BLACK);
        out.println();
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }


    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREEN);
    }

    private static void setGreen(PrintStream out){
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_GREEN);
    }

    private static void setYellow(PrintStream out){
        out.print(SET_BG_COLOR_YELLOW);
        out.print(SET_TEXT_COLOR_YELLOW);
    }

    private static void printPiece(PrintStream out, ChessPiece piece) {
        out.print(SET_TEXT_COLOR_BLACK);
        String type = "";
        if(piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            switch(piece.getPieceType()){
                case KING:
                    type = BLACK_KING;
                    break;
                case QUEEN:
                    type = BLACK_QUEEN;
                    break;
                case BISHOP:
                    type = BLACK_BISHOP;
                    break;
                case KNIGHT:
                    type = BLACK_KNIGHT;
                    break;
                case ROOK:
                    type = BLACK_ROOK;
                    break;
                case PAWN:
                    type = BLACK_PAWN;
                    break;
            }
        } if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            switch(piece.getPieceType()){
                case KING:
                    type = WHITE_KING;
                    break;
                case QUEEN:
                    type = WHITE_QUEEN;
                    break;
                case BISHOP:
                    type = WHITE_BISHOP;
                    break;
                case KNIGHT:
                    type = WHITE_KNIGHT;
                    break;
                case ROOK:
                    type = WHITE_ROOK;
                    break;
                case PAWN:
                    type = WHITE_PAWN;
                    break;
            }
        }
        out.print(type);
    }
}