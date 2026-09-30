/** Вариант 6б: НКА */
import java.io.IOException;

public class NFA {
    private static final int R0 = 0;
    private static final int R1 = 1;

    // Q - множество состояний {r0, r1}
    private static final int[] Q = {R0, R1};

    // SIGMA - входной алфавит; ε в алфавит не входит
    private static final char[] SIGMA = {'a', 'b'};

    // Дополнительный столбец таблицы для ε-переходов
    private static final int EPSILON = SIGMA.length;

    // DELTA[откуда][a, b или ε][куда] = существует ли переход
    private static final boolean[][][] DELTA =
            new boolean[Q.length][SIGMA.length + 1][Q.length];

    // Q0 - начальное состояние r0
    private static final int Q0 = R0;

    // F - множество допускающих состояний {r0, r1}
    private static final boolean[] F = {true, true};

    private static void buildTable() {
        DELTA[R0][1][R0] = true;       // b: остаёмся в r0
        DELTA[R1][0][R1] = true;       // a: остаёмся в r1
        DELTA[R0][EPSILON][R1] = true; // ε: начинаем блок a без чтения символа
    }

    /** Добавляем к множеству все состояния, достижимые без чтения символа */
    private static void closure(boolean[] states) {
        boolean changed;
        do {
            changed = false;
            for (int from = 0; from < Q.length; from++) {
                for (int to = 0; to < Q.length; to++) {
                    if (states[from] && DELTA[from][EPSILON][to] && !states[to]) {
                        states[to] = true;
                        changed = true;
                    }
                }
            }
        } while (changed);
    }

    /** Выполняем все возможные переходы по одному символу */
    private static boolean[] step(boolean[] current, int symbol) {
        boolean[] next = new boolean[Q.length];
        for (int from = 0; from < Q.length; from++) {
            for (int to = 0; to < Q.length; to++) {
                if (current[from] && DELTA[from][symbol][to]) {
                    next[to] = true;
                }
            }
        }
        closure(next);
        return next;
    }

    private static void printStates(boolean[] states) {
        System.out.print("{");
        boolean first = true;
        for (int state = 0; state < Q.length; state++) {
            if (states[state]) {
                if (!first) System.out.print(", ");
                System.out.print("r" + state);
                first = false;
            }
        }
        System.out.print("}");
    }

    /** Печатаем таблицу, которой пользуется метод step */
    private static void printTable() {
        System.out.println("Таблица переходов:\nсостояние | a | b | ε");
        for (int from = 0; from < Q.length; from++) {
            System.out.print("r" + from + " | ");
            for (int symbol = 0; symbol < SIGMA.length; symbol++) {
                boolean[] targets = new boolean[Q.length];
                for (int to = 0; to < Q.length; to++) {
                    targets[to] = DELTA[from][symbol][to];
                }
                printStates(targets);
                System.out.print(" | ");
            }
            printStates(DELTA[from][EPSILON]);
            System.out.println();
        }
    }

    /** Возвращает номер столбца таблицы для входного символа */
    private static int symbolIndex(int ch) {
        for (int i = 0; i < SIGMA.length; i++) {
            if (SIGMA[i] == ch) return i;
        }
        return -1;
    }

    public static void main(String[] args) throws IOException {
        buildTable();
        boolean[] current = new boolean[Q.length];
        current[Q0] = true; // начальное состояние r0
        closure(current); // учитываем ε-переход ещё до первого символа

        System.out.println("НКА: введите цепочку из a и b, затем Enter.");
        System.out.print("Начальное множество: ");
        printStates(current);
        System.out.println();

        int ch;
        while ((ch = System.in.read()) != -1) {
            if (ch == '\n' || ch == '\r') break;

            System.out.print("По " + (char) ch + ": ");
            printStates(current);

            int symbol = symbolIndex(ch);
            if (symbol == -1) {
                // Для символа вне алфавита в таблице нет переходов
                current = new boolean[Q.length];
            } else {
                current = step(current, symbol);
            }

            System.out.print(" -> ");
            printStates(current);
            System.out.println();
        }

        boolean result = false;
        for (int state = 0; state < Q.length; state++) {
            if (current[state] && F[state]) result = true;
        }
        System.out.println(result ? "Accept" : "Reject");
        printTable();
    }
}
