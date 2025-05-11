package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceType == that.pieceType && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, color);
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
            case KNIGHT:
                moves = getKnightMoves(board, myPosition);
                break;
            case PAWN:
                moves = getPawnMoves(board, myPosition);
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
        // left
        diagonalMoves(0,-1, board, pos, moves);
        // right
        diagonalMoves(0, +1, board, pos, moves);
        // down
        diagonalMoves(-1, 0, board, pos, moves);
        // up
        diagonalMoves(+1, 0, board, pos, moves);
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

    // checks the Knight's moves
    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] positions = new int[][]{
            {2,-1}, {2,1}, {-2,-1}, {-2,1},
            {1,-2}, {1,2}, {-1,-2}, {-1,2}
        };
        // cycles through each possible move for a knight and adds moves if it meets condiitons.
        for(int[] dir: positions){
            ChessPosition position = new ChessPosition(pos.getRow()+dir[0], pos.getColumn()+dir[1]);
            ChessMove move = new ChessMove(pos, position, null);
            // if the position is in bounds
            if(inBounds(position) &&
            // if the position has a piece, and it's not the same color
            ((board.getPiece(position) != null && !colorCheck(this.getTeamColor(), position, board)
            // or the position doesn't have a piece on it
            || board.getPiece(position) == null))){
                moves.add(move);
            }
        }

        return moves;
    }

    // checks the Pawn's moves
    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();
        // list of all potential promotion piece types
        PieceType[] promotions = new PieceType[] {PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
        int[] diagonals = new int[] {-1, 1};

        // Black pawn's potential moves
        if(this.getTeamColor() == ChessGame.TeamColor.BLACK){
            // move 2 spaces starting move
            ChessPosition oneBelow = new ChessPosition(pos.getRow()-1, pos.getColumn());
            if(pos.getRow() == 7){
                ChessPosition twoBelow = new ChessPosition(5, pos.getColumn());
                if(board.getPiece(twoBelow) == null && board.getPiece(oneBelow) == null){
                    ChessMove move2Below = new ChessMove(pos, twoBelow, null);
                    moves.add(move2Below);
                }
            }
            // move 1 space
            if(board.getPiece(oneBelow) == null && inBounds(oneBelow)){
                // if it's the last row, then adds move for each type of piece promotion
                if(oneBelow.getRow() == 1){
                    for(PieceType type: promotions){
                        ChessMove move = new ChessMove(pos, oneBelow, type);
                        moves.add(move);
                    }
                } else{
                    ChessMove move = new ChessMove(pos, oneBelow, null);
                    moves.add(move);
                }
            }
            // diagonal kill
            for(int i : diagonals){
                ChessPosition diagonal = new ChessPosition(pos.getRow()-1, pos.getColumn()+i);
                if(inBounds(diagonal) && board.getPiece(diagonal) != null && board.getPiece(diagonal).getTeamColor() != ChessGame.TeamColor.BLACK){
                    if(diagonal.getRow() == 1){
                        for(PieceType type: promotions){
                            ChessMove killMove = new ChessMove(pos, diagonal, type);
                            moves.add(killMove);
                        }
                    } else{
                        ChessMove killMove = new ChessMove(pos, diagonal, null);
                        moves.add(killMove);
                    }
                }
            }
        }

        // White pawn's potential moves
        if(this.getTeamColor() == ChessGame.TeamColor.WHITE){
            // move 2 spaces starting move
            ChessPosition oneAbove = new ChessPosition(pos.getRow()+1, pos.getColumn());
            if(pos.getRow() == 2){
                ChessPosition twoAbove = new ChessPosition(4, pos.getColumn());
                if(board.getPiece(twoAbove) == null && board.getPiece(oneAbove) == null){
                    ChessMove move2Above = new ChessMove(pos, twoAbove, null);
                    moves.add(move2Above);
                }
            }
            // move 1 space
            if(board.getPiece(oneAbove) == null && inBounds(oneAbove)){
                // if it's the last row, then adds move for each type of piece promotion
                if(oneAbove.getRow() == 8){
                    for(PieceType type: promotions){
                        ChessMove move = new ChessMove(pos, oneAbove, type);
                        moves.add(move);
                    }
                } else{
                    ChessMove move = new ChessMove(pos, oneAbove, null);
                    moves.add(move);
                }
            }
            // diagonal kill
            for(int i : diagonals){
                ChessPosition diagonal = new ChessPosition(pos.getRow()+1, pos.getColumn()+i);
                if(inBounds(diagonal) && board.getPiece(diagonal) != null && board.getPiece(diagonal).getTeamColor() != ChessGame.TeamColor.WHITE){
                    if(diagonal.getRow() == 8){
                        for(PieceType type: promotions){
                            ChessMove killMove = new ChessMove(pos, diagonal, type);
                            moves.add(killMove);
                        }
                    } else{
                        ChessMove killMove = new ChessMove(pos, diagonal, null);
                        moves.add(killMove);
                    }
                }
            }
        }
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
