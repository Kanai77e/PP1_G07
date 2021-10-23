import java.util.ArrayList;

public class State {

    public int[] board;             // int Feld repraesentiert einen Zustand im Spiel
    public int treasureRed;          //Schatzkammern
    public int treasureBlue;
    boolean terminal;               //Terminalzustand
    boolean redPlayer;                // erster / zweiter Spieler


    /**
     * Defaultkonstruktor
     * Erzeugt den Startzustand des Spiels.
     */
    public State() {
        this.board = new int[12];
        for (int i = 0; i < this.board.length; i++) {
            board[i] = 6;
        }
        treasureRed = 0;
        treasureBlue = 0;
        terminal = false;
        redPlayer = true;
    }

    /**
     * Kopierkonstruktor 1
     *
     * @param orig State der kopiert wird
     */
    public State(final State orig) {
        this.board = new int[12];
        this.treasureRed = orig.treasureRed;
        this.treasureBlue = orig.treasureBlue;
        System.arraycopy(orig.board, 0, this.board, 0, this.board.length);
        this.terminal = orig.terminal;
        this.redPlayer = orig.redPlayer;
    }

    /**
     * Kopierkostruktor 2
     *
     * @param board        zu kopierendes Spielbrett.
     * @param treasureRed  zu kopierender Tressor des roten Spielers.
     * @param treasureBlue zu kopierender Tressor des blauen Spielers.
     */
    public State(int[] board, int treasureRed, int treasureBlue) {
        this.treasureRed = treasureRed;
        this.treasureBlue = treasureBlue;
        this.board = new int[12];
        System.arraycopy(board, 0, this.board, 0, this.board.length);
        this.terminal = false;
        this.redPlayer = true;
    }

    /**
     * Methode, die die moeglichen Zuege zurueckgibt, indem die Felder mit mehr als 0 Bohnen
     * in der eigenen Spielfeldhaelfte in eine ArrayList eingefuegt werden
     *
     * @return moves ArrayList mit den Indizes der moeglichen Zuege
     */
    public ArrayList<Integer> getMoves() {
        ArrayList<Integer> moves = new ArrayList<>();
        int offset = redPlayer ? 0 : 6;
        for (int i = offset; i < 6 + offset; i++) {
            if (board[i] > 0) {
                moves.add(i);
            }
        }
        return moves;
    }

    //TODO: bisher noch nicht verwendet. Moegliche Verbesserung
    public void checkTerminal() {
        terminal = false;
    }

    /**
     * Fuehrt einen Zug aus und verteilt dementsprechend Punkte an Spieler
     * @param selection ausgewaehlter Zug
     */
    public void move(int selection) {
        //verteile die Bohnen neu
        int beansSelection = board[selection];
        board[selection] = 0;
        for (int i = 1; i <= beansSelection; i++) {
            board[(selection + i) % 12]++;
        }

        //verteile Punkte
        int checkField = selection + beansSelection;
        while (board[checkField % 12] == 6 || board[checkField % 12] == 4 || board[checkField % 12] == 2) {
            if (redPlayer) {
                treasureRed += board[checkField % 12];
            } else {
                treasureBlue += board[checkField % 12];
            }
            board[checkField % 12] = 0;
            checkField--;
            if(checkField < 0){
                checkField = 11;
            }
        }
        this.redPlayer = !this.redPlayer;
        this.checkTerminal();
    }

    /**
     * Berechnet Heuristikwert.
     *
     * @return Heuristikwert.
     */
    public int getHvalue() {

        //Verhindere auf jeden Fall Zustaende in denen kein Zug mehr moeglich ist
        ArrayList<Integer> availableMoves = getMoves();
        if(availableMoves.isEmpty()){
            return Integer.MIN_VALUE;
        }

        int myScore = redPlayer ? treasureRed : treasureBlue;
        int opponentScore = redPlayer ? treasureBlue : treasureRed;
        int offset = redPlayer ? 0 : 6;
        int myBeans = 0;
        int myWinPits = 0;
        int opponentBeans = 0;
        int opponentWinPits = 0;

        for (int i = offset; i < 6 + offset; i++) {
            myBeans += board[i];
            if(board[i] == 2 || board[i] == 4 || board[i] == 6){
                myWinPits++;
            }
        }

        for (int i = 6 - offset; i < 12 - offset; i++) {
            opponentBeans += board[i];
            if(board[i] == 2 || board[i] == 4 || board[i] == 6){
                opponentWinPits++;
            }
        }

        return (myScore - opponentScore) + ((myBeans - opponentBeans) / 4) + ((myWinPits - opponentWinPits) * 4);


    }

    /**
     * Ueberschreibt die equals-Methode.
     * Es wird bestimmt, ob zwei Zustaende identisch sind.
     *
     * @param obj Zustand welcher verglichen werden soll.
     * @return true, wenn die Zustände identisch sind, false sonst.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj.getClass() != this.getClass()) {
            return false;
        } else {
            final State other = (State) obj;
            if (this.treasureRed != other.treasureRed || this.treasureBlue != other.treasureBlue) {
                return false;
            }
            for (int i = 0; i < this.board.length; i++) {
                if (this.board[i] != other.board[i]) {
                    return false;
                }
            }
        }
        return true;
    }


    //Todo: Bisher noch nicht verwendet. Moegliche Verbesserung
    /**
     * Wendet Hashfunktion auf einen Spielzustand an.
     *
     * @return Hashwert
     */
    @Override
    public int hashCode() {
        int sum = 0;
        sum += treasureRed;
        sum += 4 * treasureBlue;
        for (int i = 0; i < board.length; i++) {
            sum += board[i] * Math.pow(2.0, (i + 2) * 2);
        }
        return sum;
    }
}
