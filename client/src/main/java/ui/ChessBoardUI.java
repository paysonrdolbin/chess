package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class ChessBoardUI {

    public static void main(ChessBoard board) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        headers(out);

        drawBoard(out, board);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void headers(PrintStream out){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("    a  b  c  d  e  f  g  h \n");
    }

    private static void drawBoard(PrintStream out, ChessBoard board) {
        for (int row = 8; row >= 1; row--) {
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(" " + Integer.toString(row) + " ");

            for (int col = 1; col <= 8; col++) {
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
        setBlack(out);
    }


    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }


    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
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