import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
        ArrayList<Integer> testList = new ArrayList<>();

        testList.add(1);
        testList.add(2);
        testList.add(3);
        testList.add(4);
        System.out.println(testList.toString());
        while(!testList.isEmpty()){
            System.out.println(testList.remove(0));
        }
        System.out.println(-1 % 12);
    }
}
