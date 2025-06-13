package utils;

import chess.ChessPosition;

public class MoveNotation {
    public static String posToString(ChessPosition pos){
        char colChar = (char) ('a' + (pos.getColumn() - 1));
        int row = pos.getRow();
        return "" + colChar + row;
    }

    public static ChessPosition stringToPos(String pos){
            int row = Integer.parseInt(pos.substring(1));
            int col;
            switch(pos.charAt(0)){
                case 'a': col = 1; break;
                case 'b': col = 2; break;
                case 'c': col = 3; break;
                case 'd': col = 4; break;
                case 'e': col = 5; break;
                case 'f': col = 6; break;
                case 'g': col = 7; break;
                case 'h': col = 8; break;
                default: col = -1;
            }
            return new ChessPosition(row, col);
    }
}
