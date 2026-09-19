/** Вариант 6а: ДКА */
import java.io.IOException;

public class DFA {
    // table[состояние][0 или 1] = следующее состояние
    private static final int[][] table = new int[32][2];

    private static void fillTable() {
        for (int state = 0; state < 32; state++) {
            table[state][0] = (2 * state) % 32;
            table[state][1] = (2 * state + 1) % 32;
        }
    }

    public static void main(String[] args) throws IOException {
        fillTable();
        System.out.println("Введите цепочку из 0 и 1:");

        int state = 0; // s0 соответствует пяти нулям: 00000
        int symbol;
        while ((symbol = System.in.read()) != -1) {
            if (symbol == '\n' || symbol == '\r') break;
            if (symbol != '0' && symbol != '1') {
                System.out.println("Ошибка: нужны только 0 и 1.");
                return;
            }
            state = table[state][symbol - '0'];
        }

        // У состояний s16–s31 самый левый из пяти битов равен 1
        System.out.println(state >= 16 ? "Accept" : "Reject");
    }
}
