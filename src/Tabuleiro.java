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

    // Método para carregar o tabuleiro do JSON
    public void carregarTabuleiroDeJSON(String nomeArquivo) {
        JSONArray navios = carregarJSON(nomeArquivo);
        for (int i = 0; i < navios.length(); i++) {
            JSONObject navio = navios.getJSONObject(i);
            JSONArray posicoes = navio.getJSONArray("posicoes");
            for (int j = 0; j < posicoes.length(); j++) {
                JSONArray posicao = posicoes.getJSONArray(j);
                int linha = posicao.getInt(0);
                int coluna = posicao.getInt(1);
                grade[linha][coluna] = '*'; // Marca as posições dos navios
            }
        }
    }

    // Exibe o tabuleiro no terminal
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
                if (grade[i][j] == '*' && !mostrarNavios) {
                    System.out.print("[ ]"); // Exibe posições do adversário como vazias
                } else {
                    System.out.print("[" + grade[i][j] + "]");
                }
            }
            System.out.println();
        }
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

    public void atualizacaoStausTabela (String posicao, String simbolo){
        if (posicao.length() != 2) {
            System.out.println("Erro: Posição inválida.");
            return;
        }

        int linha = Character.getNumericValue(posicao.charAt(0));
        int coluna = Character.getNumericValue(posicao.charAt(1));

        if (linha < 0 || linha >= 10 || coluna < 0 || coluna >= 10) {
            System.out.println("Erro: Posição fora dos limites do tabuleiro.");
            return;
        }

        grade[linha][coluna] = simbolo.charAt(0);
    }

}
