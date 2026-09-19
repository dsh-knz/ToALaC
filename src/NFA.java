/** Вариант 6б: НКА */
import java.io.IOException;

public class NFA {
    private static final int STATES = 2;

    // table[откуда][символ: a=0, b=1][куда] = есть ли переход
    private static final boolean[][][] table = new boolean[STATES][2][STATES];
    private static final boolean[][] epsilon = new boolean[STATES][STATES];
    private static final boolean[] accepting = {true, true};

    private static void buildTable() {
        table[0][1][0] = true; // b: остаёмся в r0
        table[1][0][1] = true; // a: остаёмся в r1
        epsilon[0][1] = true; // ε: начинаем блок из a без чтения символа
    }

    /** Добавляем к множеству все состояния, достижимые без чтения символа */
    private static void closure(boolean[] states) {
        boolean changed;
        do {
            changed = false;
            for (int from = 0; from < STATES; from++) {
                for (int to = 0; to < STATES; to++) {
                    if (states[from] && epsilon[from][to] && !states[to]) {
                        states[to] = true;
                        changed = true;
                    }
                }
            }
        } while (changed);
    }

    /** Выполняем все возможные переходы по одному символу */
    private static boolean[] step(boolean[] current, int symbol) {
        boolean[] next = new boolean[STATES];
        for (int from = 0; from < STATES; from++) {
            for (int to = 0; to < STATES; to++) {
                if (current[from] && table[from][symbol][to]) {
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
        for (int state = 0; state < STATES; state++) {
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
        for (int from = 0; from < STATES; from++) {
            System.out.print("r" + from + " | ");
            for (int symbol = 0; symbol < 2; symbol++) {
                boolean[] targets = new boolean[STATES];
                for (int to = 0; to < STATES; to++) {
                    targets[to] = table[from][symbol][to];
                }
                printStates(targets);
                System.out.print(" | ");
            }
            printStates(epsilon[from]);
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        buildTable();
        boolean[] current = {true, false}; // начальное состояние r0
        closure(current); // учитываем ε-переход ещё до первого символа

        System.out.println("НКА: введите цепочку из a и b, затем Enter.");
        System.out.print("Начальное множество: ");
        printStates(current);
        System.out.println();

        int ch;
        boolean valid = true;
        while ((ch = System.in.read()) != -1) {
            if (ch == '\n' || ch == '\r') break;
            if (ch != 'a' && ch != 'b') {
                valid = false;
                break;
            }
            System.out.print("По " + (char) ch + ": ");
            printStates(current);
            current = step(current, ch == 'a' ? 0 : 1);
            System.out.print(" -> ");
            printStates(current);
            System.out.println();
        }

        boolean result = false;
        for (int state = 0; state < STATES; state++) {
            if (current[state] && accepting[state]) result = true;
        }
        if (!valid) {
            System.out.println("Ошибка: допустимы только a и b.");
        } else {
            System.out.println(result ? "Accept" : "Reject");
        }
        printTable();
    }
}
