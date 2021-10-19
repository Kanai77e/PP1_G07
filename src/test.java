import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
        State testState = new State();
        testState.redPlayer = true;
        testState.treasureRed = 0;
        testState.treasureBlue = 46;
        testState.board[0] = 1;
        testState.board[1] = 1;
        testState.board[2] = 0;
        testState.board[3] = 0;
        testState.board[4] = 0;
        testState.board[5] = 4; //0
        testState.board[6] = 3; //+1
        testState.board[7] = 3; //+1
        testState.board[8] = 3; //+1
        testState.board[9] = 3; //+1
        testState.board[10] = 3;
        testState.board[11] = 0;

        System.out.println("Rot " + testState.treasureRed);
        System.out.println("Blau " + testState.treasureBlue);
        System.out.println(testState.getHvalue());
        System.out.println(testState.getMoves());
        for (int i = 0; i < 12; i++) {
            System.out.println(i + " - " + testState.board[i]);
        }
        testState.move(5);
        System.out.println("Rot " + testState.treasureRed);
        System.out.println("Blau " + testState.treasureBlue);
        System.out.println(testState.getHvalue());
        for (int i = 0; i < 12; i++) {
            System.out.println(i + " - " + testState.board[i]);
        }
    }
}
