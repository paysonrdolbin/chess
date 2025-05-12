package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamColor;
    private chess.ChessBoard board;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        if(allMoves.isEmpty()){
            return null;
        }
        TeamColor color = piece.getTeamColor();
        ChessBoard oldBoard = board;
        for(ChessMove move: allMoves){
            makeMove(move);
            if(!isInCheck(color)){
                validMoves.add(move);
            }
            board = oldBoard;
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor oppColor;
        if(teamColor == TeamColor.WHITE){
            oppColor = TeamColor.BLACK;
        } else {
            oppColor = TeamColor.WHITE;
        }
        Map<ChessPosition, ChessPiece> team = teamPieces(teamColor);
        Map<ChessPosition, ChessPiece> oppPieces = teamPieces(oppColor);
        for (Map.Entry<ChessPosition, ChessPiece> entry : oppPieces.entrySet()) {
            ChessPosition pos = entry.getKey();
            ChessPiece piece = entry.getValue();
            for(ChessMove move: piece.pieceMoves(board, pos)){
                if(team.get(pos) != null && team.get(pos).getPieceType() == ChessPiece.PieceType.KING){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Map<ChessPosition, ChessPiece> pieces = teamPieces(teamColor);
        if(isInCheck(teamColor)){
            for(Map.Entry<ChessPosition, ChessPiece> entry: pieces.entrySet()){
                if(validMoves(entry.getKey()) != null){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();
        teamColor = TeamColor.WHITE;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public Map<ChessPosition, ChessPiece> teamPieces(TeamColor color) {
        Map<ChessPosition, ChessPiece> pieces = new HashMap<>();
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == color) {
                    pieces.put(pos, piece);
                }
            }
        }
        return pieces;
    }
}
