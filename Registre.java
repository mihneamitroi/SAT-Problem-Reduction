import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Registre extends Task{
    static Registre registru = new Registre();
    int[][] matrix;
    /* Matricea de adiacenta a grafului */
    int N, M, K;
    static String decision;
    static ArrayList<Integer> result = new ArrayList<>();
    /* Array in care se vor stoca nodurile ce trebuie afisate */

    public static void main(String[] args) {
        try {
            registru.solve();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        try {
            registru.readProblemData();
            registru.formulateOracleQuestion();
            registru.askOracle();
            registru.decipherOracleAnswer();
            registru.writeAnswer();
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
        /* Se citesc numarul de variabile, de relatii si numarul de registre disponibile.
        Apoi se completeaza matricea de adiacenta. */
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        FileWriter myWriter = new FileWriter("sat.cnf");
        StringBuilder output = new StringBuilder();

        int numberOfClauses = N + K * (K - 1) / 2 * N + K * M;
        /* Formula determinata pe baza for-urilor de mai jos. */
        output.append("p cnf ").append(N * K).append(" ");
        output.append(numberOfClauses).append("\n");

        for (int j = 0; j < N; j++) {
            for (int i = 1; i <= K; i++) {
                output.append(K * j + i).append(" ");
            }
            output.append("0\n");
        }
        /* Fiecare clauza exprima necesitatea ca pentru fiecare variabila macar un
        registru sa aiba valoarea 1. Pe o linie se afla toti registrii. */


        for (int i = 0; i < N; i++) {
            for (int j = 1; j < K; j++) {
                for (int l = j + 1; l <= K; l++) {
                    output.append((-1) * (K * i + l)).append(" ");
                    output.append((-1) * (K * i + j)).append(" 0\n");
                }
            }
        }
        /* Fiecare clauza exprima necesitatea ca fiecare variabila sa nu aiba valoarea 1
        in mai multi registri in mod concomitent.
        Fiecare variabila (parcursa for-ul cu i) este pus in 2 registri diferiti in 
        cele doua for-uri cu j si l pana se parcurg toate combinatiile posibile. */

        
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                if (matrix[i][j] == 1) {
                    for (int x = 1; x <= K; x++) {
                        output.append((-1) * (K * j + x)).append(" ");
                        output.append((-1) * (K * i + x)).append(" 0\n");
                    }
                }
            }
        }
        /* Fiecare clauza exprima necesitatea ca pentru fiecare pereche de variabile
        intre care exista o relatie sa nu aiba valoarea 1 ambele variabile din relatie
        in mod concomitent in acelasi registru.
        Fiecare variabila pusa prima (parcurse prin for-ul cu i) este verificata cu 
        fiecare variabila pusa a doua (parcurse prin for-ul cu j) sa nu fie ambele puse
        in acelasi timp cu valoarea 1. For-ul prin care se trece daca o relatie exista
        pozitioneaza cele doua variabile in cele K registre. */
    
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
                    System.out.print(K + " ");
                } else {
                    System.out.print(number % K + " ");
                }
            }
            System.out.println();
        }
        /* Se printeaza registrul atribuit pentru a retine fiecare variabila. */
    }
}