package com.api.api_plantfyth.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.api.api_plantfyth.model.Especime;
import com.api.api_plantfyth.repository.EspecimeRepository;
import com.api.api_plantfyth.service.ChatBotService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PerenualService {

    
    @Value("${perenual.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    GptService gptService;
    @Autowired
    private EspecimeRepository especimeRepository;


    private static final Map<String, String> TRADUCOES = Map.ofEntries(
    Map.entry("Annual", "Anual"),
    Map.entry("Perennial", "Perene"),
    Map.entry("Herbaceous Perennial", "Herbácea Perene"),
    Map.entry("Biennial", "Bienal"),
    Map.entry("Biannual", "Bianual"),

    Map.entry("Low", "Lento"),
    Map.entry("Moderate", "Moderado"),
    Map.entry("High", "Rápido"),

    Map.entry("full sun", "Sol pleno"),
    Map.entry("part shade", "Meia sombra"),
    Map.entry("full shade", "Sombra total"),
    Map.entry("part sun/part shade", "Sol parcial e meia sombra"),
    Map.entry("sun-part shade", "Sol e meia sombra"),
    Map.entry("filtered shade", "Sombra filtrada"),

    Map.entry("days", "dias"),
    Map.entry("weeks", "semanas"),
    Map.entry("months", "meses"),

    Map.entry("January", "Janeiro"),
    Map.entry("February", "Fevereiro"),
    Map.entry("March", "Março"),
    Map.entry("April", "Abril"),
    Map.entry("May", "Maio"),
    Map.entry("June", "Junho"),
    Map.entry("July", "Julho"),
    Map.entry("August", "Agosto"),
    Map.entry("September", "Setembro"),
    Map.entry("October", "Outubro"),
    Map.entry("November", "Novembro"),
    Map.entry("December", "Dezembro")
);

private String traduzir(String valor) {
    if (valor == null) return null;
    return TRADUCOES.getOrDefault(valor, valor);
}

  public Especime parseDetalhe(String json) {
    try {
        JsonNode node = mapper.readTree(json);
        Especime e = new Especime();

        e.setPerenualId(node.path("id").asInt());
        e.setNomePopular(node.path("common_name").asText(null));
        e.setFamilia(node.path("family").asText(null));
        e.setCiclo(traduzir(node.path("cycle").asText(null)));
        e.setCrescimento(traduzir(node.path("growth_rate").asText(null)));
        e.setDescricao(node.path("description").asText(null));

        JsonNode sciName = node.path("scientific_name");
        if (sciName.isArray() && !sciName.isEmpty())
            e.setNomeCientifico(sciName.get(0).asText(null));

        JsonNode sunlight = node.path("sunlight");
        if (sunlight.isArray() && !sunlight.isEmpty())
            e.setExposicaoALuz(traduzir(sunlight.get(0).asText(null)));

      JsonNode watering = node.path("watering_general_benchmark");
if (!watering.isMissingNode()) {
    String valor = watering.path("value").asText("0");
    valor = valor.replace("\"", ""); 
    e.setPeriodoIrrigacao(valor); 
    e.setUnidadeIrrigacao(traduzir(watering.path("unit").asText("days")));
    
}
        JsonNode pruning = node.path("pruning_month");
        if (pruning.isArray()) {
            List<String> meses = new ArrayList<>();
            for (JsonNode mes : pruning)
                meses.add(traduzir(mes.asText()));
            e.setPeriodoPoda(String.join(", ", meses));
        }

        JsonNode dimensions = node.path("dimensions");
        if (dimensions.isArray()) {
            for (JsonNode dim : dimensions) {
               if (dim.path("type").isNull() || "Height".equalsIgnoreCase(dim.path("type").asText())) {
    e.setTamanhoAdultoCM(dim.path("max_value").floatValue() * 30.48f);}
                }
            }

        return e;
    } catch (Exception ex) {
        throw new RuntimeException("Erro ao parsear detalhe da Perenual", ex);
    }
}
//busca a url, e converte todos dados do json daquela especie para o model.
    public Especime buscarDetalhe(int id) {
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/v2/species/details/" + id)
            .queryParam("key", apiKey)
            .toUriString();

    String json = restTemplate.getForObject(url, String.class);
    return parseDetalhe(json);
}
//lista especies que acha na api, confere se não há duplicatas quando puxar, preenche com poucos dados para não pesar o spinner
public List<Especime> ListarEspecies(String json) {
    List<Especime> lista = new ArrayList<>();
    Set<String> nomesVistos = new HashSet<>();
    
    try {
        JsonNode root = mapper.readTree(json);
        JsonNode data = root.get("data");
        for (JsonNode node : data) {
            String nome = node.path("common_name").asText(null);
            
            if (nome == null || !nomesVistos.add(nome.toLowerCase())) continue;
            
            Especime e = new Especime();
            e.setNomePopular(nome);
            e.setFamilia(node.path("family").asText(null));
            e.setPerenualId(node.path("id").asInt());
            
            JsonNode sciName = node.path("scientific_name");
            if (sciName.isArray() && !sciName.isEmpty())
                e.setNomeCientifico(sciName.get(0).asText(null));

            lista.add(e);
        }
    } catch (Exception e) {
        throw new RuntimeException("Erro ao parsear lista da Perenual", e);
    }
    return lista;
}


// busca a requisitação
public List<Especime> buscarIndoor(int page) {
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/species-list")
            .queryParam("key", apiKey)
            .queryParam("indoor", 1)
            .queryParam("page", page)
            .toUriString();

    String json = restTemplate.getForObject(url, String.class);
    //puxa a lista com poucos dados
    List<Especime> listaExterna = ListarEspecies(json);
    List<Especime> resultado = new ArrayList<>();

    for (Especime e : listaExterna) {
        //percorre todo o banco procurando pelo id da api (que tbm é salvo no banco)
        Especime doBanco = especimeRepository.findByPerenualId(e.getPerenualId()).orElse(null);
        // se exxistir e ter descricao, puxar item do banco
        if (doBanco != null && doBanco.getDescricao() != null) {
            resultado.add(doBanco);
        } else {
            //senao, salva o item que ta puxando da api
            if (doBanco == null) especimeRepository.save(e);
            resultado.add(e);
        }
    }

    return resultado;
}
//busca nome cientifico pro chat, pra nao precisar traduzir pra procurar
 public String buscarPorNomeCientifico(String nome) {
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/species-list")
            .queryParam("key", apiKey)
            .queryParam("q", nome)
            .toUriString();

    return restTemplate.getForObject(url, String.class);
}

    // busca nome pro chat buscar diagnostico
    public String buscarPorNome(String nome) {
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/species-list")
            .queryParam("key", apiKey)
            .queryParam("q", nome)
            .toUriString();

    return restTemplate.getForObject(url, String.class);
}
// busca id pro chat
public String buscarPorId(int id) {
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/v2/species/details/" + id)
            .queryParam("key", apiKey)
            .toUriString();

    return restTemplate.getForObject(url, String.class);
}

//traduzir os valores do json  quando cadastrar a planta, para por os atributos nela que precisam de calculo e para já traduzir
//informações do especime
public void traduzirEspecime(Especime e) {
    String prompt = """
            Traduza os valores do JSON abaixo do inglês para o português brasileiro.
            Retorne APENAS o JSON traduzido, sem explicação, sem markdown, sem código.
            {
              "nomePopular": "%s",
              "descricao": "%s"
            }
            """.formatted(e.getNomePopular(), e.getDescricao());

    try {
        String resposta = gptService.chamarGpt(prompt).trim();
        JsonNode node = mapper.readTree(resposta);
        //ele traduz aq pq nome popular e descrição é mt variavel, não é pre-setado qnem os outros
        e.setNomePopular(node.path("nomePopular").asText(e.getNomePopular()));
        e.setDescricao(node.path("descricao").asText(e.getDescricao()));
    } catch (Exception ex) {
         throw new RuntimeException("Erro ao parsear lista da Perenual", ex);
    }
}  

}