package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType pieceType;
    private final ChessGame.TeamColor color;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor; this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves;
        switch (this.pieceType) {
            case KING:
                moves = getKingMoves(board, myPosition);
                break;
            case QUEEN:
                moves = getQueenMoves(board, myPosition);
                break;
            case ROOK:
                moves = getRookMoves(board, myPosition);
                break;
            case BISHOP:
                moves = getBishopMoves(board, myPosition);
                break;
            default:
                moves = new ArrayList<>();
        }
        return moves;
    }

    // checks the King's moves
    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        // checks positions surrounding the king
        for (int col = pos.getColumn()-1; col < pos.getColumn()+2; col++){
            for (int row = pos.getRow()-1; row < pos.getRow()+2; row++){
                // except for the current position
                if(row != pos.getRow() || col != pos.getColumn()){
                    ChessPosition newPos = new ChessPosition(row, col);
                    // makes sure the position is in bounds
                    if (inBounds(newPos)){
                        // makes sure the piece at the new position isn't friendly
                        if(!colorCheck(this.getTeamColor(), newPos, board)){
                            ChessMove move = new ChessMove(pos, newPos, null);
                            moves.add(move);
                        }
                    }
                }
            }
        }
        return moves;
    }

    // checks the Queen's moves
    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        // checks the vertical and horizontal moves using Rook's function
        Collection<ChessMove> rookMoves = getRookMoves(board, pos);
        // checks diagonal moves using Bishop's function
        Collection<ChessMove> bishopMoves = getBishopMoves(board, pos);
        moves.addAll(rookMoves);
        moves.addAll(bishopMoves);

        return moves;
    }

    // checks the Rook's moves
    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();

        // check vertical moves above
        int rowAbove = pos.getRow() + 1;
        while(rowAbove <= 8){
            ChessPosition moveCheck= new ChessPosition(rowAbove, pos.getColumn());
            if(board.getPiece(moveCheck) != null){
                break;
            }
            if(!colorCheck(this.getTeamColor(), moveCheck, board)){
                ChessMove move = new ChessMove(pos, moveCheck, null);
                moves.add(move);
            }
            rowAbove++;
        }

        // check vertical moves below
        int rowBelow = pos.getRow()-1;
        while(rowBelow >= 1){
            ChessPosition moveCheck= new ChessPosition(rowBelow, pos.getColumn());
            if(board.getPiece(moveCheck) != null){
                break;
            }
            if(!colorCheck(this.getTeamColor(), moveCheck, board)){
                ChessMove move = new ChessMove(pos, moveCheck, null);
                moves.add(move);
            }
            rowBelow--;
        }

        // check horizontal moves to the right
        int colRight = pos.getColumn()+1;
        while(colRight >= 1){
            ChessPosition moveCheck= new ChessPosition(pos.getRow(), colRight);
            ChessMove move = new ChessMove(pos, moveCheck, null);
            if(board.getPiece(moveCheck) != null){
                if(!colorCheck(this.getTeamColor(), moveCheck, board)){
                    moves.add(move);
                }
                break;
            } else{
                moves.add(move);
            }
            colRight++;
        }

        // check horizontal moves to the left
        int colLeft = pos.getColumn()-1;
        while(colLeft >= 1){
            ChessPosition moveCheck= new ChessPosition(pos.getRow(), colLeft);
            ChessMove move = new ChessMove(pos, moveCheck, null);
            if(board.getPiece(moveCheck) != null){
                if(!colorCheck(this.getTeamColor(), moveCheck, board)){
                    moves.add(move);
                }
                break;
            } else{
                moves.add(move);
            }
            colLeft--;
        }

        return moves;
    }

    // checks the Bishop's moves
    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        // diagonal down-left
        diagonalMoves(-1,-1, board, pos, moves);
        // diagonal down-right
        diagonalMoves(-1, +1, board, pos, moves);
        // diagonal up-left
        diagonalMoves(+1, -1, board, pos, moves);
        // diagonal up-right
        diagonalMoves(+1, +1, board, pos, moves);
        return moves;
    }

    // helper function to do diagonal checking much quicker
    private void diagonalMoves(int rowChange, int colChange, ChessBoard board, ChessPosition pos, Collection<ChessMove> moves){
        ChessPosition pos1 = new ChessPosition(pos.getRow()+rowChange, pos.getColumn()+colChange);
        while(inBounds(pos1)){
            ChessMove move1 = new ChessMove(pos, pos1, null);
            // if the move hits another piece, break.
            if(board.getPiece(pos1) != null){
                // if the move hits a friendly color, don't add the move.
                if(!colorCheck(this.getTeamColor(), pos1, board)){
                    moves.add(move1);
                }
                break;
                // if the space is empty, add the move
            } else {
                moves.add(move1);
            }
            pos1 = new ChessPosition(pos1.getRow()+rowChange, pos1.getColumn()+colChange);
        }
    }


    // helper function to check if the position being checked contains a piece of the same color
    private boolean colorCheck(ChessGame.TeamColor color, ChessPosition pos, ChessBoard board){
        if(board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() == color){
            return true;
        }
        return false;
    }

    // helper function to check if the position being checked is in bounds
    private boolean inBounds(ChessPosition pos){
        if (pos.getRow() >= 1 && pos.getRow() < 9 && pos.getColumn() >= 1 && pos.getColumn() < 9){
            return true;
        }
        return false;
    }
}
