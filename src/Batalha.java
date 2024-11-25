import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Batalha {

    public List<String> extrairPosicoes(String nomeArquivo) {
        List<String> posicoesCompactadas = new ArrayList<>();

        try {
            // Ler o conteúdo do arquivo JSON como uma string
            String conteudo = new String(Files.readAllBytes(Paths.get(nomeArquivo)), StandardCharsets.UTF_8);


            // Converter a string em um array JSON
            JSONArray navios = new JSONArray(conteudo);

            // Iterar sobre cada navio
            for (int i = 0; i < navios.length(); i++) {
                JSONObject navio = navios.getJSONObject(i);

                // Obter a lista de posições do navio
                JSONArray posicoes = navio.getJSONArray("posicoes");

                // Iterar pelas posições e compactar no formato "linha + coluna"
                for (int j = 0; j < posicoes.length(); j++) {
                    JSONArray posicao = posicoes.getJSONArray(j);
                    int linha = posicao.getInt(0);
                    int coluna = posicao.getInt(1);

                    // Adicionar ao ArrayList como "linha + coluna"
                    posicoesCompactadas.add(linha + "" + coluna);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao processar o JSON: " + e.getMessage());
        }

        return posicoesCompactadas;
    }

    public boolean tiroComparaPosicao (List<String> lista1,List<String> lista2){
        HashSet<String> elementos2 = new HashSet<>(lista2);

        for (String elemento : lista1){
            if(!elementos2.contains(elemento)){
                return false;
            }
        }
        return  true;
    }

    public String respostaTiro (List<String> lista1,String posicao){
       String linhaColuna = linhaColuna(posicao);
        for (String elemento : lista1){
            if(posicao.contains(elemento)){
                System.out.println("Tiro acertado com êxito o navio: " + linhaColuna);
                return "X";
            }
        }
        System.out.println("Tiro Errado: " + linhaColuna);
        return "A";
    }
    private String linhaColuna (String posicao){

        int linha = Character.getNumericValue(posicao.charAt(0));
        int coluna = Character.getNumericValue(posicao.charAt(1));

        return "Linha: " + linha + " Coluna: " + coluna;
    }
}
