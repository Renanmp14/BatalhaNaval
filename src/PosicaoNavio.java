import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class PosicaoNavio {
    private static final int TAMANHO_TABULEIRO = 10;
    private static final char[][] tabuleiro = new char [TAMANHO_TABULEIRO][TAMANHO_TABULEIRO];

    static {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++){
            for (int j =0; j < TAMANHO_TABULEIRO;j++){
                tabuleiro[i][j] = '-';
            }
        }
    }
    private static final Navio[] NAVIOS = {
            new Navio(5, "porta-avioes"),
            new Navio(4, "encouracado"),
            new Navio(3, "cruzador"),
            new Navio(3, "cruzador"),
            new Navio(2, "destroier"),
            new Navio(2, "destroier")
    };
    public static JSONArray gerarPosicoesNavios() {
        JSONArray naviosArray = new JSONArray();
        Random random = new Random();

        for (Navio navio : NAVIOS) {
            int tamanho = navio.getTamanho();
            String tipo = navio.getTipo();

            boolean posicionado = false;
            while (!posicionado) {
                int linha = random.nextInt(TAMANHO_TABULEIRO);
                int coluna = random.nextInt(TAMANHO_TABULEIRO);

                // Tentativa de posicionar na horizontal
                if (podePosicionarNavio(linha, coluna, tamanho, true)) {
                    posicionado = posicionarNavio(linha, coluna, tamanho, true, navio, naviosArray);
                }
                // Se não foi possível na horizontal, tenta na vertical
                if (!posicionado && podePosicionarNavio(linha, coluna, tamanho, false)) {
                    posicionado = posicionarNavio(linha, coluna, tamanho, false, navio, naviosArray);
                }
            }
        }
        return naviosArray;
    }

    private static boolean podePosicionarNavio(int linha, int coluna, int tamanho, boolean orientacaoHorizontal) {
        if (orientacaoHorizontal) {
            if (coluna + tamanho > TAMANHO_TABULEIRO) return false;
            for (int i = 0; i < tamanho; i++) {
                if (tabuleiro[linha][coluna + i] != '-') return false;
            }
        } else {
            if (linha + tamanho > TAMANHO_TABULEIRO) return false;
            for (int i = 0; i < tamanho; i++) {
                if (tabuleiro[linha + i][coluna] != '-') return false;
            }
        }
        return true;
    }

    private static boolean posicionarNavio(int linha, int coluna, int tamanho, boolean orientacaoHorizontal, Navio navio, JSONArray naviosArray) {
        List<int[]> posicoes = new ArrayList<>();
        for (int i = 0; i < tamanho; i++) {
            if (orientacaoHorizontal) {
                tabuleiro[linha][coluna + i] = 'N';
                posicoes.add(new int[]{linha, coluna + i});
            } else {
                tabuleiro[linha + i][coluna] = 'N';
                posicoes.add(new int[]{linha + i, coluna});
            }
        }

        // Criação do JSON para o navio
        JSONObject navioJSON = new JSONObject();
        navioJSON.put("tipo", navio.getTipo());

        // Ajustar a forma como as posições são adicionadas para evitar a formatação com quebras de linha
        JSONArray posicoesArray = new JSONArray();
        for (int[] posicao : posicoes) {
            posicoesArray.put(new JSONArray(posicao));
        }
        navioJSON.put("posicoes", posicoesArray);

        naviosArray.put(navioJSON);
        return true;
    }

    public static void salvarNaviosEmJSON(JSONArray naviosArray, String caminhoArquivo) {
        try (FileWriter file = new FileWriter(caminhoArquivo)) {
            file.write(naviosArray.toString());
            System.out.println("Arquivo JSON salvo como: " + caminhoArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
