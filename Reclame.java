import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Reclame extends Task{
    static Reclame reclama = new Reclame();
    int[][] matrix;
    /* Matricea de adiacenta a grafului */
    int N, M, K;
    boolean foundTrue = false;
    static String decision;
    static ArrayList<Integer> result = new ArrayList<>();
    /* Array in care se vor stoca nodurile ce nu trebuie afisate */

    public static void main(String[] args) {
        try {
            reclama.solve();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        try {
            reclama.readProblemData();
            while (!foundTrue) {
                reclama.formulateOracleQuestion();
                reclama.askOracle();
                reclama.decipherOracleAnswer();
                if (!foundTrue) {
                    K--;
                }
            }
            reclama.writeAnswer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Se formuleaza mai multe cereri spre oracol si ne oprim cu un pas inainte
        de a obtine false. Initial gasim o solutie putin restrictiva, apoi incercam
        sa gasim un numar de noduri ce pot fi scoase cat mai mic. */
    }

    @Override
    public void readProblemData() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] splitted = line.split(" ");
        N = Integer.parseInt(splitted[0]);
        M = Integer.parseInt(splitted[1]);
        K = N - 1;
        matrix = new int[N][N];
        for (int i = 0; i < M; i++) {
            line = reader.readLine();
            splitted = line.split(" ");
            int u = Integer.parseInt(splitted[0]);
            int v = Integer.parseInt(splitted[1]);
            matrix[u - 1][v - 1] = 1;
            matrix[v - 1][u - 1] = 1;
        }
        /* Se citesc numarul de noduri si de muchii. Apoi se completeaza matricea de
        adiacenta. Marimea grupului cautat este initial numarul de noduri minus 1. */
    }

    @Override
    public void formulateOracleQuestion() throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("sat.cnf"));
        StringBuilder output = new StringBuilder();

        int numberOfClauses = K + (K * (K - 1)) / 2 * N + K * K * M + K * N * (N - 1) / 2;
        /* Formula determinata pe baza for-urilor de mai jos. */
        output.append("p cnf ").append(N * K).append(" ");
        output.append(numberOfClauses).append("\n");

        for (int i = 1; i <= K; i++) {
            for (int j = 0; j < N; ++j) {
                output.append(K * j + i).append(" ");
            }
            output.append("0\n");
        }
        /* Fiecare clauza exprima necesitatea ca pe fiecare pozitie posibila cel 
        putin un nod sa aiba valoarea 1. Pe o linie se afla toate nodurile codificate
        corespunzator pozitiei verificate. */

        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                if (matrix[i][j] == 1) {
                    for (int x = 1; x <= K; x++) {
                        for (int y = 1; y <= K; y++) {
                            output.append((-1) * (K * i + x)).append(" ");
                            output.append((-1) * (K * j + y)).append(" 0\n");
                        }
                    }
                }
            }
        }
        /* Fiecare clauza exprima necesitatea ca pentru fiecare pereche de noduri intre
        care exista o muchie sa nu aiba valoarea 1 ambele noduri vecine cu muchia in
        mod concomitent.
        Fiecare nod pus primul (parcurse prin for-ul cu i) este verificat cu fiecare
        nod pus al doilea (parcurse prin for-ul cu j) sa nu fie ambele puse in acelasi
        timp cu valoarea 1. Cele doua for-uri prin care se trece daca o muchie exista
        pozitioneaza cele doua noduri adiacente muchiei pe cele K pozitii. */

        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                for (int l = 1; l <= K; l++) {
                    output.append((-1) * (K * i + l)).append(" ");
                    output.append((-1) * (K * j + l)).append(" 0\n");
                }
            }
        }
        /* Fiecare clauza exprima necesitatea ca doua noduri sa nu aiba valoarea 1
        pe aceeasi pozitie in mod concomitent.
        Fiecare nod pus primul (parcurse prin for-ul cu i) este verificat cu fiecare
        nod pus al doilea (parcurse prin for-ul cu j) sa nu fie ambele puse in acelasi
        timp cu valoarea 1 pe aceeasi pozitie, pana se parcurg toate pozitiile. */

        for (int i = 0; i < N; i++) {
            for (int j = 1; j < K; j++) {
                for (int l = j + 1; l <= K; l++) {
                    output.append((-1) * (K * i + j)).append(" ");
                    output.append((-1) * (K * i + l)).append(" 0\n");
                }
            }
        }
        /* Fiecare clauza exprima necesitatea ca fiecare nod sa nu aiba valoarea 1
        pe mai multe pozitii in mod concomitent.
        Fiecare nod (parcurs for-ul cu i) este pus pe 2 pozitii diferite in cele doua
        for-uri cu j si l pana se parcurg toate combinatiile posibile. */

        writer.write(output.toString());
        writer.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("sat.sol"));
        decision = reader.readLine();
        if (decision.equals("True")) {
            foundTrue = true;
            reader.readLine();
            String line = reader.readLine();
            String[] splitted = line.split(" ");
            for (String number : splitted) {
                int value = Integer.parseInt(number);
                if (value > 0) {
                    if (value % K == 0) {
                        result.add(value / K);
                    } else {
                        result.add(value / K + 1);
                    }
                }
            }
            /* Daca oracolul returneaza true se retin toate valorile pozitive. */
        }
        reader.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        if (decision.equals("True")) {
            for (int i = 1; i <= N; i++) {
                if (!result.contains(i)) {
                    System.out.print(i + " ");
                }
            }
            System.out.println();
        }
        /* Se printeaza toate nodurile care nu au fost gasite deoarece am rezolvat
        problema intr-o logica negativa. */
    }
}