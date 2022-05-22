package spieler.adrian;

import spieler.Farbe;
import spieler.OthelloSpieler;
import spieler.Zug;
import spieler.ZugException;

import java.util.ArrayList;

public class Spieler implements OthelloSpieler{

    @Override
    public Zug berechneZug(Zug zug, long l, long l1) throws ZugException {
        // Update the board
        if (zug != null){
            Move previousMove = new Move(zug);
            move(board, previousMove, opponent, player);
        }

        ArrayList<Move> possibleMoves = getPossibleMoves(this.board, this.player, this.opponent);

        if(possibleMoves.isEmpty()){
            return Zug.passenZug();
        }

        Move bestMove = possibleMoves.get(0);
        move(board, bestMove, player, opponent);

        return Move.moveToZug(bestMove);
    }

    @Override
    public void neuesSpiel(Farbe farbe, int i) {
        // Get the players color
        player = Color.farbeToColor(farbe);

        if(player == Color.BLACK) {
            opponent = Color.WHITE;
        }
        else if(player == Color.WHITE) {
            opponent = Color.BLACK;
        }

        // Prepare the board
        board[Column.D.toInt()][row(4)] = Color.WHITE;
        board[Column.D.toInt()][row(5)] = Color.BLACK;
        board[Column.E.toInt()][row(4)] = Color.BLACK;
        board[Column.E.toInt()][row(5)] = Color.WHITE;
    }

    @Override
    public String meinName() {
        return "adrian";
    }

    private enum Column {A(0),B(1),C(2),D(3),E(4),F(5),G(6),H(7);
        private final int toInt;

        Column(int value) {
            this.toInt = value;
        }

        public int toInt() {
            return toInt;
        }

        public static Column toColumn(int x){
            return x == 0 ? Column.A :
                    x == 1 ? Column.B :
                            x == 2 ? Column.C :
                                    x == 3 ? Column.D :
                                            x == 4 ? Column.E :
                                                    x == 5 ? Column.F :
                                                            x == 6 ? Column.G :
                                                                    x == 7 ? Column.H : null;
        }
    }

    public static class Move{
        final Column column;
        final int row; // From 0 to 7

        public Move(Zug zug)  throws ZugException{
            Move move = zugToMove(zug);
            this.column = move.getColumn();
            this.row = move.getRow();
        }

        public Move(Column column, int row) throws ZugException {
            this.column = column;
            // Normal row number - 1 for array indexing.
            this.row = row - 1;
            if (this.row > 7 || this.row < 0){
                throw new ZugException("Row " + row + " is invalid.");
            }
        }

        public static Zug moveToZug(Move move){
            return new Zug(move.getRow(), move.getColumn().toInt());
        }

        public Move zugToMove(Zug zug) throws ZugException {
            Column column = zug.getSpalte() == 0 ? Column.A :
                    zug.getSpalte() == 1 ? Column.B :
                        zug.getSpalte() == 2 ? Column.C :
                            zug.getSpalte() == 3 ? Column.D :
                                zug.getSpalte() == 4 ? Column.E :
                                    zug.getSpalte() == 5 ? Column.F :
                                        zug.getSpalte() == 6 ? Column.G :
                                          zug.getSpalte() == 7 ? Column.H : null;
            int row = zug.getZeile();
            return new Move(column, row + 1);
        }

        public Column getColumn() {
            return column;
        }

        public int getRow() {
            return row;
        }
    }

    private enum Color {BLACK(1),WHITE(2);
        private final int toInt;

        Color(int value) {
            this.toInt = value;
        }

        public int toInt() {
            return toInt;
        }

        public static Color farbeToColor(Farbe farbe){
            return farbe.equals(Farbe.WEISS) ? Color.WHITE :
                    farbe.equals(Farbe.SCHWARZ) ? Color.BLACK : null;
        }
    }

    // Members
    private static final int FIELD_SIZE = 8;

    private final Color[][] board = new Color[8][8];
    private int searchDepth;
    private Color player;
    private Color opponent;

    // Constructors
    public Spieler(){

    }

    public Spieler(int depth){
        this.searchDepth = depth;
    }

    // Methods
    private ArrayList<Move> getPossibleMoves(Color[][] board, Color player, Color opponent) throws ZugException {
        ArrayList<Move> list = new ArrayList<Move>();

        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {

                // No need to check occupied field
                if(board[i][j] == opponent || board[i][j] == player){
                    continue;
                }

                Move move = new Move(Column.toColumn(i), j + 1);
                if(isMoveValid(board, move, player, opponent)){
                    list.add(move);
                }
            }
        }
        return list;
    }

    private boolean isMoveValid(Color[][] board, Move move, Color player, Color opponent) {
        // Positions of the move
        int column = move.getColumn().toInt();
        int row = move.getRow();

        int opponentPieces = 0;

        // Check north
        for (int j = row - 1; j >= 0; j--) {
            if(board[column][j] == null || board[column][j] == player && opponentPieces == 0){
                break;
            }
            else if(board[column][j] == opponent){
                opponentPieces++;
            }
            else if(board[column][j] == player && opponentPieces > 0){
                return true;
            }
        }
        // Check north-east
        opponentPieces = 0;
        int i = column + 1;
        for (int j = row - 1; j >= 0 && i < FIELD_SIZE; j--) {
            if(board[i][j] == null || board[i][j] == player && opponentPieces == 0){
                break;
            }
            else if(board[i][j] == opponent){
                opponentPieces++;
            }
            else if(board[i][j] == player && opponentPieces > 0){
                return true;
            }
            i++;
        }
        // Check east
        opponentPieces = 0;
        for (i = column + 1; i < FIELD_SIZE; i++) {
            if(board[i][row] == null || board[i][row] == player && opponentPieces == 0){
                break;
            }
            else if(board[i][row] == opponent){
                opponentPieces++;
            }
            else if(board[i][row] == player && opponentPieces > 0){
                return true;
            }
        }
        // Check south-east
        opponentPieces = 0;
        i = column + 1;
        for (int j = row + 1; j < FIELD_SIZE && i < FIELD_SIZE; j++) {
            if(board[i][j] == null || board[i][j] == player && opponentPieces == 0){
                break;
            }
            else if(board[i][j] == opponent){
                opponentPieces++;
            }
            else if(board[i][j] == player && opponentPieces > 0){
                return true;
            }
            i++;
        }
        // Check south
        opponentPieces = 0;
        for (int j = row + 1; j < FIELD_SIZE ; j++) {
            if(board[column][j] == null || board[column][j] == player && opponentPieces == 0){
                break;
            }
            else if(board[column][j] == opponent){
                opponentPieces++;
            }
            else if(board[column][j] == player && opponentPieces > 0){
                return true;
            }
        }
        // Check south-west
        opponentPieces = 0;
        i = column - 1;
        for (int j = row + 1; j < FIELD_SIZE && i >= 0; j++) {
            if(board[i][j] == null || board[i][j] == player && opponentPieces == 0){
                break;
            }
            else if(board[i][j] == opponent){
                opponentPieces++;
            }
            else if(board[i][j] == player && opponentPieces > 0){
                return true;
            }
            i--;
        }
        // Check west
        opponentPieces = 0;
        for (i = column - 1; i >= 0; i--) {
            if(board[i][row] == null || board[i][row] == player && opponentPieces == 0){
                break;
            }
            else if(board[i][row] == opponent){
                opponentPieces++;
            }
            else if(board[i][row] == player && opponentPieces > 0){
                return true;
            }
        }
        // Check north-west
        opponentPieces = 0;
        i = column - 1;
        for (int j = row - 1; j >= 0 &&  i >= 0; j--) {
            if(board[i][j] == null || board[i][j] == player && opponentPieces == 0){
                break;
            }
            else if(board[i][j] == opponent){
                opponentPieces++;
            }
            else if(board[i][j] == player && opponentPieces > 0){
                return true;
            }
            i--;
        }
        return false;
    }

    private int move(Color[][] board, Move move, Color player, Color opponent) throws ZugException {
        ArrayList<Move> toFlip = new ArrayList<Move>();
        ArrayList<Move> tmp = new ArrayList<Move>();

        int column = move.getColumn().toInt();
        int row = move.getRow();

        board[column][row] = player;

        // Check pieces to flip
        int opponentPieces = 0;
        tmp.clear();
        // Check north
        for (int j = row - 1; j >= 0; j--) {
            if(board[column][j] == null){
                break;
            }
            else if(board[column][j] == opponent){
                opponentPieces++;
                tmp.add(new Move(Column.toColumn(column), j + 1));
            }
            else if(board[column][j] == player && opponentPieces > 0){
                toFlip.addAll(tmp);
                tmp.clear();
            }
        }
        // Check north-east
        opponentPieces = 0;
        tmp.clear();
        int i = column + 1;
        for (int j = row - 1; j >= 0 && i < FIELD_SIZE; j--) {
            if(board[i][j] == null){
                break;
            }
            else if(board[i][j] == opponent){
                opponentPieces++;
                tmp.add(new Move(Column.toColumn(i), j + 1));
            }
            else if(board[i][j] == player && opponentPieces > 0){
                toFlip.addAll(tmp);
                tmp.clear();
            }
            i++;
        }
        // Check east
        opponentPieces = 0;
        tmp.clear();
        for (i = column + 1; i < FIELD_SIZE; i++) {
            if(board[i][row] == null){
                break;
            }
            else if(board[i][row] == opponent){
                opponentPieces++;
                tmp.add(new Move(Column.toColumn(i), row + 1));
            }
            else if(board[i][row] == player && opponentPieces > 0){
                toFlip.addAll(tmp);
                tmp.clear();
            }
        }
        // Check south-east
        opponentPieces = 0;
        tmp.clear();
        i = column + 1;
        for (int j = row + 1; j < FIELD_SIZE && i < FIELD_SIZE; j++) {
            if(board[i][j] == null){
                break;
            }
            else if(board[i][j] == opponent){
                opponentPieces++;
                tmp.add(new Move(Column.toColumn(i), j + 1));
            }
            else if(board[i][j] == player && opponentPieces > 0){
                toFlip.addAll(tmp);
                tmp.clear();
            }
            i++;
        }
        // Check south
        opponentPieces = 0;
        tmp.clear();
        for (int j = row + 1; j < FIELD_SIZE ; j++) {
            if(board[column][j] == null){
                break;
            }
            else if(board[column][j] == opponent){
                opponentPieces++;
                tmp.add(new Move(Column.toColumn(column), j + 1));
            }
            else if(board[column][j] == player && opponentPieces > 0){
                toFlip.addAll(tmp);
                tmp.clear();
            }
        }
        // Check south-west
        opponentPieces = 0;
        tmp.clear();
        i = column - 1;
        for (int j = row + 1; j < FIELD_SIZE && i >= 0; j++) {
            if(board[i][j] == null){
                break;
            }
            else if(board[i][j] == opponent){
                opponentPieces++;
                tmp.add(new Move(Column.toColumn(i), j + 1));
            }
            else if(board[i][j] == player && opponentPieces > 0){
                toFlip.addAll(tmp);
                tmp.clear();
            }
            i--;
        }
        // Check west
        opponentPieces = 0;
        tmp.clear();
        for (i = column - 1; i >= 0; i--) {
            if(board[i][row] == null){
                break;
            }
            else if(board[i][row] == opponent){
                opponentPieces++;
                tmp.add(new Move(Column.toColumn(i), row + 1));
            }
            else if(board[i][row] == player && opponentPieces > 0){
                toFlip.addAll(tmp);
                tmp.clear();
            }
        }
        // Check north-west
        opponentPieces = 0;
        tmp.clear();
        i = column - 1;
        for (int j = row - 1; j >= 0 &&  i >= 0; j--) {
            if(board[i][j] == null){
                break;
            }
            else if(board[i][j] == opponent){
                opponentPieces++;
                tmp.add(new Move(Column.toColumn(i), j + 1));
            }
            else if(board[i][j] == player && opponentPieces > 0){
                toFlip.addAll(tmp);
                tmp.clear();
            }
            i--;
        }

        // Flip pieces
        int points = 0;

        for (Move flip_move :
                toFlip) {
            int flip_column = flip_move.getColumn().toInt();
            int flip_row = flip_move.getRow();

            if(board[flip_column][flip_row] == opponent){
                points++;
                board[flip_column][flip_row] = player;
            } else {
                // Flip error
                System.out.println("ASD");
            }
        }
        return points;
    }

    private Move getBestMove(Color[][] board, int depth, Color player, Color opponent) throws ZugException {
        Move bestMove = null;
        ArrayList<Move> possibleMoves = getPossibleMoves(board, player, opponent);
        return bestMove;
    }

    /**
     * Converts normal row to array index.
     *
     * @param row as number from 1 to 8
     * @return array index from 0 to
     */
    private int row(int row){
        return row - 1;
    }
}
