package spieler.adrian;

import spieler.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mit "spieler.adrian.Spieler:4" starten.
 *
 * Noch keine Zeitüberprüfung. Wenn also eine Zeitüberschreitung eintritt sollte man die Suchtiefe verringern.
 *
 * Es gibt eine "Random" Komponente. Wenn mehrere Züge mit dem gleichen Rating gefunden werden,
 * wird einer davon zufällig ausgewählt.
 * Also gibt es mehrere Ausgangsmöglichkeiten mit den gleichen AI Spielern.
 *
 * Adrian Kaminski
 */
public class Spieler implements OthelloSpieler{

    @Override
    public Zug berechneZug(Zug zug, long l, long l1) throws ZugException {
        // Enemy move
        if (zug != null && !zug.isPassen()){
           move(board,opponent, zug);
        }

        // Forfeit turn if no move is possible
        ArrayList<Zug> possibleMoves = getPossibleMoves(board, player);
        if(possibleMoves.isEmpty()){
            return Zug.passenZug();
        }

        // Find best moves
        ArrayList<Integer> ratings = new ArrayList<>();

        for (Zug move :
                possibleMoves) {
            Farbe[][] copyBoard = copy(board);
            move(copyBoard, player, move);

            int rating;
            //rating = rateBoard(copyBoard, player, opponent);
            //rating = minimax(copyBoard, this.searchDepth, true);
            rating = alphaBeta(copyBoard, this.searchDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            System.out.println("\u001B[31m" + "PosMove: " + move + " " + rating + "\u001B[0m");

            ratings.add(rating);
        }

        // Getting all best moves e.g. when 2 moves have a rating of 42.
        ArrayList<Zug> bestMoves = new ArrayList<>();

        int maxRating = Collections.max(ratings);

        for (int i = 0; i < ratings.size(); i++) {
            if(ratings.get(i) == maxRating){
                bestMoves.add(possibleMoves.get(i));
            }
        }

        int x = 0;
        // (Un)Comment following line to (De)activate picking random move from bestMoves list.
        x = ThreadLocalRandom.current().nextInt(0, bestMoves.size());
        Zug bestMove = bestMoves.get(x);

        System.out.println("\u001B[36m" + "BestMove: " + bestMove + " " + maxRating + "\u001B[0m");
        move(board, player, bestMove);

        return bestMove;
    }

    @Override
    public void neuesSpiel(Farbe farbe, int i) {
        System.out.println("Search depth: " + searchDepth);

        //Arrays.stream(board).forEach(x -> Arrays.fill(x, null));
        board = new Farbe[FIELD_SIZE][FIELD_SIZE];

        // Get the players color
        player = farbe;

        if(player == Farbe.SCHWARZ) {
            opponent = Farbe.WEISS;
        }
        else if(player == Farbe.WEISS) {
            opponent = Farbe.SCHWARZ;
        }

        // Prepare the board
        this.board[3][3] = Farbe.WEISS;
        this.board[3][4] = Farbe.SCHWARZ;
        this.board[4][3] = Farbe.SCHWARZ;
        this.board[4][4] = Farbe.WEISS;
    }

    @Override
    public String meinName() {
        return "adrian";
    }

    private static final int FIELD_SIZE = 8;
    private Farbe[][] board = new Farbe[8][8];

    private Farbe player;
    private Farbe opponent;

    private int searchDepth;

    public Spieler(){

    }

    public Spieler(int depth){
        this.searchDepth = depth;
    }

    /**
     * Simple minimax algorithm
     */
    private int minimax(Farbe[][] board, int searchDepth, boolean player) throws ZugException {
        if(searchDepth == 0 || getPossibleMoves(board, player ? this.player : this.opponent).isEmpty()){
            return rateBoard(board, this.player, this.opponent);
        }

        Farbe[][] copyBoard = copy(board);

        if(player){
            int maxRating = Integer.MIN_VALUE;
            for (Zug zug:
                 getPossibleMoves(copyBoard, this.player)) {
                move(copyBoard, this.player, zug);
                int rating = minimax(copyBoard, searchDepth - 1, false);
                maxRating = Integer.max(maxRating, rating);
            }
            return maxRating;
        }
        else {
            int minRating = Integer.MAX_VALUE;
            for (Zug zug:
                    getPossibleMoves(copyBoard, this.opponent)) {
                move(copyBoard, this.opponent, zug);
                int rating = minimax(copyBoard, searchDepth - 1, true);
                minRating = Integer.min(minRating, rating);
            }
            return minRating;
        }
    }

    /**
     * Simple alpha beta pruning
     */
    private int alphaBeta(Farbe[][] pBoard, int searchDepth, int alpha, int beta, boolean player) throws ZugException {
        if(searchDepth == 0 || getPossibleMoves(pBoard, player ? this.player : this.opponent).isEmpty()){
            return rateBoard(pBoard, this.player, this.opponent);
        }

        Farbe[][] copyBoard = copy(pBoard);

        if(player){
            int maxRating = Integer.MIN_VALUE;
            for (Zug zug:
                    getPossibleMoves(pBoard, this.player)) {
                copyBoard = copy(pBoard);
                move(copyBoard, this.player, zug);
                int rating = alphaBeta(copyBoard, searchDepth - 1, alpha, beta,false);
                maxRating = Integer.max(maxRating, rating);
                alpha = Integer.max(alpha, rating);
                if (beta <= alpha){
                    break;
                }
            }
            return maxRating;
        }
        else {
            int minRating = Integer.MAX_VALUE;
            for (Zug zug:
                    getPossibleMoves(pBoard, this.opponent)) {
                copyBoard = copy(pBoard);
                move(copyBoard, this.opponent, zug);
                int rating = alphaBeta(copyBoard, searchDepth - 1, alpha, beta,true);
                minRating = Integer.min(minRating, rating);
                beta = Integer.min(beta, rating);
                if (beta <= alpha){
                    break;
                }
            }
            return minRating;
        }
    }

    /**
     * Places a piece at a position and flips all other pieces which can be flipped.
     *
     * @param board current board state
     * @param player whose turn it is
     * @param zug position to place piece
     * @return count of pieces flipped
     */
    private int move(Farbe[][] board, Farbe player, Zug zug){
        int row = zug.getZeile();
        int column = zug.getSpalte();

        int count = 0;
        for(int i = 0; i < rowDir.length; i++){
            int rowStep = rowDir[i];
            int columnStep = columnDir[i];
            int currentRow = row + rowStep;
            int currentColumn = column + columnStep;
            // Count of opponent pieces found
            while(currentRow >= 0 && currentRow < 8 && currentColumn >= 0 && currentColumn < 8){
                // Empty cell
                if(board[currentColumn][currentRow] == null){
                    break;
                }
                else if(board[currentColumn][currentRow] != player){
                    currentRow += rowStep;
                    currentColumn += columnStep;
                    count++;
                }
                else{
                    // Conversion is possible
                    if(count > 0){
                        int convertRow = currentRow - rowStep;
                        int convertColumn = currentColumn - columnStep;
                        while(convertRow != row || convertColumn != column){
                            board[convertColumn][convertRow] = player;
                            convertRow = convertRow - rowStep;
                            convertColumn = convertColumn - columnStep;
                        }
                        count++;
                    }
                    break;
                }
            }
        }
        board[column][row] = player;

        return count;
    }

    /**
     * Gets all possible moves for the given player on the given board state,
     *
     * @param board as current board state
     * @param player to check for possible moves
     * @return a list of all possible moves
     */
    private ArrayList<Zug> getPossibleMoves(Farbe[][] board, Farbe player) throws ZugException {
        ArrayList<Zug> list = new ArrayList<>();

        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {

                // No need to check occupied field
                if(board[i][j] != null){
                    continue;
                }

                Zug zug = new Zug(j, i);
                if(isMoveValid(board, player, zug)){
                    list.add(zug);
                }
            }
        }
        return list;
    }

    private static final int[] rowDir = {0,1,1,1,0,-1,-1,-1};
    private static final int[] columnDir = {-1,-1,0,1,1,1,0,-1};

    /**
     * Checks if a given move is valid.
     *
     * @param pBoard as current board state
     * @param player to check if the move is possible
     * @param zug as the position of the move
     * @return true if the move is possible
     */
    private static boolean isMoveValid(Farbe[][] pBoard, Farbe player, Zug zug){
        int row = zug.getZeile();
        int column = zug.getSpalte();

        if(row < 0 || row >= 8 || column < 0 || column >= 8 || pBoard[column][row] != null){
            return false;
        }
        boolean movePossible = false;
        for(int i = 0; i < rowDir.length; i++){
            int rowStep = rowDir[i];
            int columnStep = columnDir[i];
            int currentRow = row + rowStep;
            int currentColumn = column + columnStep;
            int count = 0;
            // Count of opponent pieces encountered
            while(currentRow >= 0 && currentRow < 8 && currentColumn >= 0 && currentColumn < 8){
                if(pBoard[currentColumn][currentRow] == null){
                    break;
                }
                else if(pBoard[currentColumn][currentRow] != player){
                    currentRow += rowStep;
                    currentColumn += columnStep;
                    count++;
                }
                else{
                    // Conversion is possible
                    if(count > 0){
                        movePossible = true;
                    }
                    break;
                }
            }
        }
        return movePossible;
    }

    private static final int[][] matrix = {{50,-1,5,2,2,5,-1,50},{-1,-10,1,1,1,1,-10,-1},{5,1,1,1,1,1,1,5},{2,1,1,0,0,1,1,2},{2,1,1,0,0,1,1,2},{5,1,1,1,1,1,1,5},{-1,-10,1,1,1,1,-10,-1},{50,-1,5,2,2,5,-1,50}};

    /**
     * Rates the current board.
     *
     * @param pBoard as current board state
     * @param player
     * @param opponent
     * @return rating of the board
     */
    private int rateBoard(Farbe[][] pBoard, Farbe player, Farbe opponent){
        int rating = 0;
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                if(pBoard[i][j] == player){
                    rating += matrix[i][j];
                    //rating++;
                }
                else if(pBoard[i][j] == opponent){
                    rating -= matrix[i][j];
                    //rating--;
                }
            }
        }
        return rating;
    }

    private Farbe[][] copy(Farbe[][] board){
        Farbe[][] arrayCopy = new Farbe[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, arrayCopy[i], 0, board[i].length);
        }
        return arrayCopy;
    }

    public void printBoard(Farbe[][] board){
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                System.out.print(board[i][j] == null ? " " : board[i][j]);
            }
            System.out.println();
        }
    }
}
