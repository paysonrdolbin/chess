package chess;

import java.util.*;

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
        this.board = new ChessBoard(); board.resetBoard(); teamColor = TeamColor.WHITE;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamColor == chessGame.teamColor && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, board);
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
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            return validMoves;
        }
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        if(allMoves.isEmpty()){
            return validMoves;
        }
        TeamColor color = piece.getTeamColor();
        for(ChessMove move: allMoves){
            // keeps track of the piece at the end position to restore the board.
            ChessPiece endPiece = board.getPiece(move.getEndPosition());
            // implements the move, if it's not in check, adds it to validMoves
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
            if(!isInCheck(color)){
                validMoves.add(move);
            }
            // correctly reverses the move
            board.addPiece(move.getStartPosition(), piece);
            board.addPiece(move.getEndPosition(), endPiece);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        // makes sure there's actually a piece at the start location
        if(board.getPiece(move.getStartPosition()) == null){
            throw new InvalidMoveException("No piece at this location");
        // makes sure not trying to make a move out of turn
        } else if(board.getPiece(move.getStartPosition()).getTeamColor() != this.teamColor){
            throw new InvalidMoveException("Move cannot be made out of turn.");
        } else{
            ChessPiece piece = board.getPiece(move.getStartPosition());
            Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());

            // won't allow an invalid move
            if(legalMoves == null || !legalMoves.contains(move)){
                throw new InvalidMoveException("This move isn't valid");
            }

            // changes the piece if it's a promotion
            if(move.getPromotionPiece() != null){
                ChessPiece promotePiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotePiece);
            } else{
                board.addPiece(move.getEndPosition(), piece);
            }
            // changes the piece at starting position back to null
            board.addPiece(move.getStartPosition(), null);

            // changes the turn
            if(teamColor == TeamColor.BLACK){
                teamColor = TeamColor.WHITE;
            } else{
                teamColor = TeamColor.BLACK;
            }
        }
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
        // makes maps for the player's team's pieces, and the opponent's team's pieces
        Map<ChessPosition, ChessPiece> team = teamPieces(teamColor);
        Map<ChessPosition, ChessPiece> oppPieces = teamPieces(oppColor);
        ChessPosition posKing;

        // searches for the king and get's its location
        for (Map.Entry<ChessPosition, ChessPiece> entry : team.entrySet()) {
            if(entry.getValue().getPieceType() == ChessPiece.PieceType.KING){
                posKing = entry.getKey();

                // searches through each of the opponent's pieces
                for (Map.Entry<ChessPosition, ChessPiece> entry1: oppPieces.entrySet()){
                    Collection<ChessMove> moves = entry1.getValue().pieceMoves(board, entry1.getKey());

                    // checks each of their moves for potential check causing moves
                    for(ChessMove move: moves){
                        if(move.getEndPosition().equals(posKing)){
                            return true;
                        }
                    }
                }
                break;
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
            return hasNoValidMoves(pieces);
        }
        return false;
    }

    public boolean hasNoValidMoves(Map<ChessPosition, ChessPiece> pieces){
        for(Map.Entry<ChessPosition, ChessPiece> entry: pieces.entrySet()){
            if(!validMoves(entry.getKey()).isEmpty()){
                return false;
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
        Map<ChessPosition, ChessPiece> pieces = teamPieces(teamColor);
        if(!isInCheck(teamColor)){
            return hasNoValidMoves(pieces);
        }
        return false;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

// shuffles through the whole board and adds the pieces of a certain color and their relative locations to a map
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
