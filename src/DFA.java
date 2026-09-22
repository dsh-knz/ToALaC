/** Вариант 6а: ДКА */
import java.io.IOException;

public class DFA {

    // Q - множество состояний s0, s1, ..., s31
    private static final int[] Q = new int[32];

    // SIGMA - входной алфавит
    private static final char[] SIGMA = {'0', '1'};

    // DELTA[состояние][символ] = следующее состояние
    private static final int[][] DELTA = new int[Q.length][SIGMA.length];

    // Q0 - начальное состояние s0
    private static final int Q0 = 0;

    // F - множество допускающих состояний s16, ..., s31
    private static final boolean[] F = new boolean[Q.length];

    private static void fillTable() {
        for (int state = 0; state < Q.length; state++) {
            Q[state] = state;
            DELTA[state][0] = (2 * state) % Q.length;
            DELTA[state][1] = (2 * state + 1) % Q.length;
            F[state] = state >= 16;
        }
    }

    // Возвращает номер столбца таблицы для входного символа
    private static int symbolIndex(int ch) {
        for (int i = 0; i < SIGMA.length; i++) {
            if (SIGMA[i] == ch) return i;
        }
        return -1;
    }

    public static void main(String[] args) throws IOException {
        fillTable();
        System.out.println("Введите цепочку из 0 и 1:");

        int state = Q0; // s0 соответствует пяти нулям: 00000
        boolean hasCurrentState = true;

        int ch;
        while ((ch = System.in.read()) != -1) {
            if (ch == '\n' || ch == '\r') break;

            int symbol = symbolIndex(ch);
            if (symbol == -1) {
                // В таблице нет перехода по символу вне алфавита
                hasCurrentState = false;
            } else if (hasCurrentState) {
                state = DELTA[state][symbol];
            }
        }

        System.out.println(hasCurrentState && F[state] ? "Accept" : "Reject");
    }
}
