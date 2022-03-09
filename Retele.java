import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Retele extends Task{
    static Retele retea = new Retele();
    int[][] matrix;
    /* Matricea de adiacenta a grafului */
    int N, M, K;
    static String decision;
    static ArrayList<Integer> result = new ArrayList<>();
    /* Array in care se vor stoca nodurile ce trebuie afisate */

    public static void main(String[] args) {
        try {
            retea.solve();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        try {
            retea.readProblemData();
            retea.formulateOracleQuestion();
            retea.askOracle();
            retea.decipherOracleAnswer();
            retea.writeAnswer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readProblemData() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] splitted = line.split(" ");
        N = Integer.parseInt(splitted[0]);
        M = Integer.parseInt(splitted[1]);
        K = Integer.parseInt(splitted[2]);
        matrix = new int[N][N];
        for (int i = 0; i < M; i++) {
            line = reader.readLine();
            splitted = line.split(" ");
            int u = Integer.parseInt(splitted[0]);
            int v = Integer.parseInt(splitted[1]);
            matrix[u - 1][v - 1] = 1;
            matrix[v - 1][u - 1] = 1;
        }
        /* Se citesc numarul de noduri, de muchii si marimea grupului de noduri cautat.
        Apoi se completeaza matricea de adiacenta. */
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        FileWriter myWriter = new FileWriter("sat.cnf");
        StringBuilder output = new StringBuilder();

        int numberOfClauses = K + K * K * (N * (N - 1) / 2 - M) + (K * (K - 1)) / 2 * N;
        /* Formula determinata pe baza for-urilor de mai jos. */
        output.append("p cnf ").append(N * K).append(" ");
        output.append(numberOfClauses).append("\n");

        for (int i = 1; i <= K; i++) {
            for (int j = 0; j < N; j++) {
                output.append(K * j + i).append(" ");
            }
            output.append("0\n");
        }
        /* Fiecare clauza exprima necesitatea ca pe fiecare pozitie posibila cel 
        putin un nod sa aiba valoarea 1. Pe o linie se afla toate nodurile codificate
        corespunzator pozitiei verificate. */
        

        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                if (matrix[i][j] == 0) {
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
        care nu exista o muchie sa nu aiba valoarea 1 ambele noduri vecine cu muchia in
        mod concomitent.
        Fiecare nod pus primul (parcurse prin for-ul cu i) este verificat cu fiecare
        nod pus al doilea (parcurse prin for-ul cu j) sa nu fie ambele puse in acelasi
        timp cu valoarea 1. Cele doua for-uri prin care se trece daca o muchie nu exista
        pozitioneaza cele doua noduri adiacente muchiei pe cele K pozitii. */
        

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

        myWriter.write(output.toString());
        myWriter.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("sat.sol"));
        decision = reader.readLine();
        if (decision.equals("True")) {
            reader.readLine();
            String line = reader.readLine();
            String[] splitted = line.split(" ");
            for (String number : splitted) {
                int value = Integer.parseInt(number);
                if (value > 0) {
                    result.add(value);
                }
            }
        }
        /* Daca oracolul returneaza true se retin toate valorile pozitive. */
        reader.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        System.out.println(decision);
        if (decision.equals("True")) {
            for (Integer number : result) {
                if (number % K == 0) {
                    System.out.print(number / K + " ");
                } else {
                    System.out.print(number / K + 1 + " ");
                }
            }
            System.out.println();
        }
        /* Se printeaza valorile pozitive tinandu-se cont de faptul ca oracolul returneaza
        un numar de valori egal cu numarul de noduri inmultit cu numarul de pozitii,
        astfel numarul retinut trebuie impartit la marimea grupului cautat. */
    }
}