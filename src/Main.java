import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;


public class Main {
    static final int maxDepth = 14;         //maximale Suchtiefe des Algorithmus
    // static String server = "http://127.0.0.1:5000";
    static String server = "http://bohnenspiel.informatik.uni-mannheim.de";
    static String name = "Obin-Wan";
    static int p1 = 0;                      //Schatzkammern
    static int p2 = 0;
    static int bestMove;                    //Zug der vom Algorithmus ueberschrieben wird
    static boolean join = true;            //legt fest ob einem Spiel beigetreten werden soll
    static String gameID = "3534";          //ID die fuer den Beitritt gebraucht wird

    public static void main(String[] args) throws Exception {
        // System.out.println(load(server));
        if (join) {
            openGames();
            joinGame(gameID);
        } else {
            createGame();
        }
    }



    static void createGame() throws Exception {
        String url = server + "/api/creategame/" + name;
        String gameID = load(url);
        System.out.println("Spiel erstellt. ID: " + gameID);

        url = server + "/api/check/" + gameID + "/" + name;
        while (true) {
            Thread.sleep(100);
            String state = load(url);
            System.out.print("." + " (" + state + ")");
            if (state.equals("0") || state.equals("-1")) {
                break;
            } else if (state.equals("-2")) {
                System.out.println("time out");
                return;
            }
        }
        play(gameID, 0);
    }


    static void openGames() throws Exception {
        String url = server + "/api/opengames";
        String[] opengames = load(url).split(";");
        for (int i = 0; i < opengames.length; i++) {
            System.out.println(opengames[i]);
        }
    }


    static void joinGame(String gameID) throws Exception {
        String url = server + "/api/joingame/" + gameID + "/" + name;
        String state = load(url);
        System.out.println("Join-Game-State: " + state);
        if (state.equals("1")) {
            play(gameID, 6);
        } else if (state.equals("0")) {
            System.out.println("error (join game)");
        }
    }


    static void play(String gameID, int offset) throws Exception {
        String checkURL = server + "/api/check/" + gameID + "/" + name;
        String statesMsgURL = server + "/api/statemsg/" + gameID;
        String stateIdURL = server + "/api/state/" + gameID;
        int[] board = {6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6}; // position 1-12
        int start, end;
        if (offset == 0) {
            start = 7;
            end = 12;
        } else {
            start = 1;
            end = 6;
        }

        while (true) {
            Thread.sleep(100);
            int moveState = Integer.parseInt(load(checkURL));
            int stateID = Integer.parseInt(load(stateIdURL));
            if (stateID != 2 && ((start <= moveState && moveState <= end) || moveState == -1)) {
                if (moveState != -1) {
                    int selectedField = moveState - 1;
                    board = updateBoard(board, selectedField);
                    System.out.println("Gegner waehlte: " + moveState + " /\t" + p1 + " - " + p2);
                    System.out.println(printBoard(board) + "\n");
                }
                long timeStart = System.currentTimeMillis();

                // calculate fieldID
                int selectField;

                //Ab hier wird unser Algorithmus aufgerufen
                bestMove = -1;
                State currentState = new State(board, p1, p2);
                System.out.println("Punktestand: Rot - " + currentState.treasureRed + " Blau - " + currentState.treasureBlue);
                if (join) {
                    currentState.redPlayer = false;
                }
                int hValue = max(currentState, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
                selectField = bestMove;
                //__________________________________________

                if (selectField != -1) {
                    board = updateBoard(board, selectField);
                    System.out.println("W�hle Feld: " + (selectField + 1) + " /\t" + p1 + " - " + p2);
                    System.out.println(printBoard(board) + "\n\n");

                    move(gameID, selectField + 1);
                    long timeEnd = System.currentTimeMillis();
                    System.out.println("Zeit: " + (timeEnd - timeStart));
                } else {
                    System.out.println("Fehler kein Zug gefunden");
                }
            } else if (moveState == -2 || stateID == 2) {
                System.out.println("GAME Finished");
                checkURL = server + "/api/statemsg/" + gameID;
                System.out.println(load(checkURL));
                return;
            } else {
                System.out.println("- " + moveState + "\t\t" + load(statesMsgURL));
            }

        }
    }


    static int[] updateBoard(int[] board, int field) {
        int startField = field;

        int value = board[field];
        board[field] = 0;
        while (value > 0) {
            field = (++field) % 12;
            board[field]++;
            value--;
        }

        if (board[field] == 2 || board[field] == 4 || board[field] == 6) {
            do {
                if (startField < 6) {
                    p1 += board[field];
                } else {
                    p2 += board[field];
                }
                board[field] = 0;
                field = (field == 0) ? field = 11 : --field;
            } while (board[field] == 2 || board[field] == 4 || board[field] == 6);
        }
        return board;
    }


    static String printBoard(int[] board) {
        String s = "";
        for (int i = 11; i >= 6; i--) {
            if (i != 6) {
                s += board[i] + "; ";
            } else {
                s += board[i];
            }
        }

        s += "\n";
        for (int i = 0; i <= 5; i++) {
            if (i != 5) {
                s += board[i] + "; ";
            } else {
                s += board[i];
            }
        }

        return s;
    }


    static void move(String gameID, int fieldID) throws Exception {
        String url = server + "/api/move/" + gameID + "/" + name + "/" + fieldID;
        System.out.println(load(url));
    }


    static String load(String url) throws Exception {
        URI uri = new URI(url.replace(" ", ""));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uri.toURL().openConnection().getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        return (sb.toString());
    }

    /**
     * Vorlage Wikipedia. Alpha - Beta. Angepasst fuer unsere Zwecke.
     * Berechnet den bestemoeglichen Zug fuer den Max Spieler.
     *
     * @param n     aktueller Spielzustand
     * @param depth aktuelle Tiefe
     * @return bestmoeglichen Zug Max Spieler
     */
    public static int max(State n, int depth, int alpha, int beta) {
        if (depth == 0 || n.terminal) {
            return n.getHvalue();
        }
        int maxWert = alpha;
        ArrayList<Integer> moves = n.getMoves();
        int wert;

        while (!moves.isEmpty()) {
            State next = new State(n);
            int move = moves.remove(0);
            if (bestMove == -1 && depth == maxDepth) {      //stellt sicher, dass auf jeden Fall ein Zug ausgewaehlt wird
                bestMove = move;
            }
            next.move(move);
            wert = min(next, depth - 1, maxWert, beta);
            if (wert > maxWert) {
                maxWert = wert;
                if (depth == maxDepth) {
                    bestMove = move;
                }
                if (maxWert >= beta) {
                    break;
                }
            }
        }
        return maxWert;
    }


    /**
     * Vorlage Wikipedia. Alpha - Beta. Angepasst fuer unsere Zwecke.
     * Berechnet den bestemoeglichen Zug fuer den Min Spieler.
     *
     * @param n     aktueller Spielzustand
     * @param depth aktuelle Tiefe
     * @return bestmoeglichen Zug Min Spieler
     */
    public static int min(State n, int depth, int alpha, int beta) {
        if (depth == 0 || n.terminal) {
            return n.getHvalue();
        }
        int minWert = beta;
        ArrayList<Integer> moves = n.getMoves();
        int wert;
        while (!moves.isEmpty()) {
            State next = new State(n);
            int move = moves.remove(0);
            next.move(move);
            wert = max(next, depth - 1, alpha, minWert);
            if (wert < minWert) {
                minWert = wert;
                if (minWert <= alpha) {
                    break;
                }
            }
        }
        return minWert;
    }
}
