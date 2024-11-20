import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.BufferedReader;

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

    // Atualiza com base no arquivo JSON do adversário
    public char atualizarComBaseNoArquivo(int linha, int coluna, String arquivoAdversario) {
        // Lê o arquivo JSON do adversário (exemplo: "navios_adversario.json")
        JSONArray naviosAdversario = carregarJSON("ArquivoAdversario.json");

        for (int i = 0; i < naviosAdversario.length(); i++) {
            JSONObject navio = naviosAdversario.getJSONObject(i);
            JSONArray posicoes = navio.getJSONArray("posicoes");

            for (int j = 0; j < posicoes.length(); j++) {
                JSONArray posicao = posicoes.getJSONArray(j);
                if (posicao.getInt(0) == linha && posicao.getInt(1) == coluna) {
                    grade[linha][coluna] = 'X'; // Acertou navio
                    return 'X';
                }
            }
        }

        grade[linha][coluna] = 'Y'; // Acertou água
        return 'Y';
    }

    // Verifica se todos os navios foram afundados
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

    // Exibe o tabuleiro no console
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

    // Processa o ataque no tabuleiro
    public char processarAtaque(int linha, int coluna) {
        if (grade[linha][coluna] == 'N') {
            grade[linha][coluna] = 'X'; // Acertou navio
            return 'X';
        } else if (grade[linha][coluna] == ' ') {
            grade[linha][coluna] = 'Y'; // Acertou água
            return 'Y';
        }
        return grade[linha][coluna]; // Caso já tenha sido atacado
    }

    public static JSONArray carregarJSON(String nomeArquivo) {
        StringBuilder conteudo = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                conteudo.append(linha);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar arquivo JSON: " + nomeArquivo);
        }

        // Retorna o JSONArray a partir do conteúdo do arquivo
        return new JSONArray(conteudo.toString());
    }
}
