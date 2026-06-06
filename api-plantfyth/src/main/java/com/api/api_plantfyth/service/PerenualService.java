package com.api.api_plantfyth.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.api.api_plantfyth.model.Especime;
import com.api.api_plantfyth.repository.EspecimeRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PerenualService {

    @Value("${perenual.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private EspecimeRepository especimeRepository;


    public void importarPlantasIndoor(int pagina) {
        List<Especime> listaExterna = buscarIndoor(pagina);

        for (Especime e : listaExterna) {
            if (!especimeRepository.existsByPerenualId(e.getPerenualId())) {
                especimeRepository.save(e);
            }
        }
    }
   public Especime parseDetalhe(String json) {
    try {
        JsonNode node = mapper.readTree(json);
        Especime e = new Especime();

        e.setPerenualId(node.path("id").asInt());
        e.setNomePopular(node.path("common_name").asText(null));
        e.setFamilia(node.path("family").asText(null));
        e.setCiclo(node.path("cycle").asText(null));
        e.setCrescimento(node.path("growth_rate").asText(null));
        e.setDescricao(node.path("description").asText(null));

        JsonNode sciName = node.path("scientific_name");
        if (sciName.isArray() && !sciName.isEmpty())
            e.setNomeCientifico(sciName.get(0).asText(null));

        JsonNode sunlight = node.path("sunlight");
        if (sunlight.isArray() && !sunlight.isEmpty())
            e.setExposicaoALuz(sunlight.get(0).asText(null));

        JsonNode watering = node.path("watering_general_benchmark");
        e.setPeriodoIrrigacao(watering.path("value").asText(null));
        e.setUnidadeIrrigacao(watering.path("unit").asText(null));

        JsonNode pruning = node.path("pruning_month");
        if (pruning.isArray()) {
            List<String> meses = new ArrayList<>();
            for (JsonNode mes : pruning)
                meses.add(mes.asText());
            e.setPeriodoPoda(String.join(", ", meses));
        }

        JsonNode dimensions = node.path("dimensions");
        if (dimensions.isArray()) {
            for (JsonNode dim : dimensions) {
                if ("Height".equalsIgnoreCase(dim.path("type").asText())) {
                    e.setTamanhoAdultoCM(dim.path("max_value").floatValue());
                }
            }
        }

        return e;
    } catch (Exception e) {
        throw new RuntimeException("Erro ao parsear detalhe da Perenual", e);
    }
}
    public Especime buscarDetalhe(int id) {
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/v2/species/details/" + id)
            .queryParam("key", apiKey)
            .toUriString();

    String json = restTemplate.getForObject(url, String.class);
    return parseDetalhe(json);
}

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





public List<Especime> buscarIndoor(int page) {
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/species-list")
            .queryParam("key", apiKey)
            .queryParam("indoor", 1)
            .queryParam("page", page)
            .toUriString();

    String json = restTemplate.getForObject(url, String.class);
    return ListarEspecies(json);
}

 /*   // GET /api/plants/indoor
     public List<Especime> getDetalhes() {
    // 1. Chamada real à API Perenual
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/species-list")
            .queryParam("key", apiKey)
            .queryParam("indoor", 1)
            .toUriString();

    String json = restTemplate.getForObject(url, String.class);

    // 2. Converte o JSON para List<Especime>
    List<Especime> lista = new ArrayList<>();
    try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode data = root.get("data");

        for (JsonNode p : data) {
            Especime e = new Especime();

            e.setPerenualId(p.get("id").asInt());
            e.setNome(p.path("common_name").asText(null));
            
            // 🛠️ CORREÇÃO AQUI: Trata o scientific_name como Array
            JsonNode sciNameNode = p.path("scientific_name");
            if (sciNameNode.isArray() && !sciNameNode.isEmpty()) {
                // Pega o primeiro item do array: ["Nome Cientifico"] -> "Nome Cientifico"
                e.setNome_cientifico(sciNameNode.get(0).asText(null));
            } else {
                e.setNome_cientifico(null);
            }
            
            lista.add(e);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    return lista;
}

    // GET /api/plants/species/{id}
    public String buscarPorId(int id) {
        String url = UriComponentsBuilder
                .fromUriString("https://perenual.com/api/species/details/" + id)
                .queryParam("key", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
    // Buscar doença por ID (1-100)
public String buscarDoencaPorId(int id) {
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/pest-disease-list")
            .queryParam("key", apiKey)
            .queryParam("id", id)
            .toUriString();
    return restTemplate.getForObject(url, String.class);
}

// Listar doenças (pode filtrar por nome)
public String listarDoencas(String query) {
    UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/pest-disease-list")
            .queryParam("key", apiKey);
    
    if (query != null && !query.isEmpty()) {
        builder.queryParam("q", query);
    }

    return restTemplate.getForObject(builder.toUriString(), String.class);
  }
  public String buscarGuiaCuidados(int especieId) {
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/species-care-guide-list")
            .queryParam("key", apiKey)
            .queryParam("species_id", especieId)
            .toUriString();
    return restTemplate.getForObject(url, String.class);
}
public String buscarEspeciePorNome(String nome) {
    String url = UriComponentsBuilder
            .fromUriString("https://perenual.com/api/species-list")
            .queryParam("key", apiKey)
            .queryParam("q", nome)
            .toUriString();
    return restTemplate.getForObject(url, String.class);
}*/
}