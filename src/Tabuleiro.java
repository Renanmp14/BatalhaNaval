import org.json.JSONArray;
import org.json.JSONObject;

public class Tabuleiro {
    private final char[][] grade;
    private boolean mostrarNavios;

    public Tabuleiro(boolean mostrarNavios) {
        this.mostrarNavios = mostrarNavios;
        grade = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grade[i][j] = ' ';
            }
        }
    }

    // Método para carregar o tabuleiro do arquivo JSON
    public void carregarTabuleiroDeJSON(String nomeArquivo) {
        JSONArray navios = carregarJSON(nomeArquivo);
        for (int i = 0; i < navios.length(); i++) {
            JSONObject navio = navios.getJSONObject(i);
            JSONArray posicoes = navio.getJSONArray("posicoes");
            for (int j = 0; j < posicoes.length(); j++) {
                JSONArray posicao = posicoes.getJSONArray(j);
                int linha = posicao.getInt(0);
                int coluna = posicao.getInt(1);
                grade[linha][coluna] = 'N'; // Marca as posições dos navios com 'N'
            }
        }
    }

    // Método para exibir o tabuleiro no terminal
    public void exibirTabuleiro(String titulo) {
        System.out.println("\n" + titulo);
        System.out.print("   ");
        for (int i = 0; i < 10; i++) {
            System.out.print(" " + i + " ");
        }
        System.out.println();

        for (int i = 0; i < 10; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 10; j++) {
                if (grade[i][j] == 'N' && !mostrarNavios) {
                    System.out.print("[ ]"); // Oculta os navios no tabuleiro adversário
                } else {
                    System.out.print("[" + grade[i][j] + "]");
                }
            }
            System.out.println();
        }
    }

    // Método para processar o ataque, indicando se acertou ou errou
    public char processarAtaque(int linha, int coluna) {
        if (grade[linha][coluna] == 'N') {
            grade[linha][coluna] = 'X'; // Acertou um navio
            return 'X';
        } else if (grade[linha][coluna] == ' ') {
            grade[linha][coluna] = 'Y'; // Acertou água
            return 'Y';
        }
        return grade[linha][coluna]; // Se já foi atacado
    }

    // Método para verificar se todos os navios foram afundados
    public boolean todosNaviosAfundados() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (grade[i][j] == 'N') {
                    return false; // Ainda há partes de navio
                }
            }
        }
        return true;
    }

    // Método para carregar o arquivo JSON de posições de navios
    private JSONArray carregarJSON(String nomeArquivo) {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(nomeArquivo)));
            return new JSONArray(content);
        } catch (Exception e) {
            System.out.println("Erro ao carregar o arquivo JSON: " + e.getMessage());
            return new JSONArray();
        }
    }
}
