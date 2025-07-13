package chess.logic;

import java.util.ArrayList;

import chess.logic.util.ChessLogic;
import chess.logic.util.GridMath;

public class ChessMove {

    private final char[] FILE_SYMBOLS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    private final char[] RANK_SYMBOLS = {'1', '2', '3', '4', '5', '6', '7', '8'};

    private int start;
    private int end;
    private ChessPiece piece;
    private char color;
    private boolean isLegal;
    private boolean isPromotion;
    private ChessPosition position;

    /**
     * Creates a new ChessMove.
     * @param position the ChessPosition serving as the context of the move
     * @param start index of the piece that is moving
     * @param end index of the destination square
     */
    public ChessMove(ChessPosition position, int start, int end) {
        this.position = position.copy();

        this.start = start;
        this.end = end;
        this.piece = position.getPieceAt(start);
        this.color = this.piece.getColor();

        this.isLegal = ChessLogic.isLegalMove(position, this);

        this.isPromotion = ChessLogic.isPromotion(this);
    }

    /**
     * Creates a new ChessMove.
     * @param position the ChessPosition serving as the context of the move
     * @param start index of the piece that is moving
     * @param end index of the destination square
     * @param promotionType the type of piece to promote to, if applicable.
     */
    public ChessMove(ChessPosition position, int start, int end, char promotionType) {
        this.position = position.copy();

        this.start = start;
        this.end = end;
        this.piece = position.getPieceAt(start);
        this.color = this.piece.getColor();

        this.isLegal = ChessLogic.isLegalMove(position, this);
        
        this.isPromotion = ChessLogic.isPromotion(this);
        if (this.isPromotion) {
            promoteTo(promotionType);
        }
    }

    /**
     * Returns the location on the chess board that should be available for en passant after this move
     * is made.
     * @return the location's index as an integer, or -1 if no en passant opportunity should be made
     * available.
     */
    public int enPassantValue() {
        if (this.piece.getType() == 'P') {
            if (this.end - this.start == 16) {
                return this.start + 8;
            } else if (this.end - this.start == -16) {
                return this.start - 8;
            }
        }
        return -1;
    }

    public void promoteTo(char type) {
        this.piece = ChessPiece.getPiece(this.getColor(), type);
    }

    public boolean isEnPassant() {
        return ChessLogic.isEnPassant(position, this);
    }

    public boolean isPromotion() {
        return this.isPromotion;
    }

    public boolean isKingsideCastle() {
        return ChessLogic.isKingsideCastle(position, this);
    }

    public boolean isQueensideCastle() {
        return ChessLogic.isQueensideCastle(position, this);
    }

    public boolean isLegal() {
        return this.isLegal;
    }

    public boolean isIrreversible() {
        return ChessLogic.isIrreversible(this);
    }

    public ChessPiece getPiece() {
        return this.piece;
    }

    public char getColor() {
        return this.color;
    }

    public char getPieceType() {
        return this.piece.getType();
    }

    public int getPieceID() {
        return this.piece.getID();
    }

    public boolean isCapture() {
        return !position.isEmpty(end) || this.isEnPassant();
    }

    public boolean isCheck() {
        return ChessLogic.isCheck(position.afterMove(this));
    }

    public boolean isCheckmate() {
        return ChessLogic.isCheckmate(position.afterMove(this));
    }

    public boolean isStalemate() {
        return ChessLogic.isStalemate(position.afterMove(this));
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public void printMove() {
        System.out.println(this.getNotation());
    }

    /**
     * Returns the algebraic notation of this move in the context of a specified ChessPosition. If
     * the move is illegal, this method returns null.
     * @return the notation, as a String, or null if the move is illegal.
     */
    public String getNotation() {

        if (this.isLegal) {
            String body;
            if (this.isKingsideCastle()) {
                body = "O-O";
            } else if (this.isQueensideCastle()) {
                body = "O-O-O";
            } else {
                body = "";
                if (this.getPieceType() == 'P' || this.isPromotion()) {
                    if (this.isCapture()) {
                        body += FILE_SYMBOLS[GridMath.getFile(this.getStart()) - 1];
                    }
                } else {
                    body += this.getPieceType();

                    // handle disambiguation
                    boolean fileDisambiguate = false;
                    boolean rankDisambiguate = false;
                    ArrayList<ChessMove> legalMoves = ChessLogic.generateLegalMoves(this.position);
                    for (ChessMove move : legalMoves) {
                        if (this.getEnd() == move.getEnd() && this.getPieceType() == move.getPieceType() && this.getStart() != move.getStart()) {
                            if (GridMath.getFile(this.getStart()) == GridMath.getFile(move.getStart())) {
                                rankDisambiguate = true;
                            } else {
                                fileDisambiguate = true;
                            }
                        }
                    }
                    if (fileDisambiguate) {
                        body += FILE_SYMBOLS[GridMath.getFile(this.getStart()) - 1];
                    }
                    if (rankDisambiguate) {
                        body += RANK_SYMBOLS[GridMath.getRank(this.getStart()) - 1];
                    }
                }

                if (this.isCapture()) {
                    body += 'x';
                }

                body += FILE_SYMBOLS[GridMath.getFile(this.getEnd()) - 1];
                body += RANK_SYMBOLS[GridMath.getRank(this.getEnd()) - 1];

                if (this.isPromotion()) {
                    body += '=';
                    body += this.getPieceType();
                }
            }

            if (this.isCheck()) {
                if (this.isCheckmate()) {
                    body += '#';
                } else {
                    body += '+';
                }
            }

            return body;

        } else {
            return null;
        }
    }
}
