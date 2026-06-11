package com.api.api_plantfyth.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatBotService {

    @Value("${openai.api.key}")
    private String openAiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private PerenualService perenualService;
    @Autowired
    private GptService gptService;

    public String chat(String pergunta) {
        String intencao = detectarIntencao(pergunta);
        String dadosApi = buscarNaApi(intencao);
        return gerarResposta(pergunta, dadosApi);
    }

    private String detectarIntencao(String pergunta) {
        String prompt = """
        Você é um assistente de plantas. O usuário perguntou: "%s"
        Responda APENAS com uma das opções abaixo, sem explicação:
        - LISTAR
        - BUSCAR_NOME:[nome Popular em inglês]
        - BUSCAR_NOME_CIENTIFICO:[nome científico latino preferencial, ou nome cientifico em inglês se não souber]
        - BUSCAR_ID:[número]
        - DIAGNOSTICO:[descrição do problema]
        """.formatted(pergunta);
        return gptService.chamarGpt(prompt).trim();
    }

    private String buscarNaApi(String intencao) {
        if (intencao.startsWith("BUSCAR_ID:")) {
            int id = Integer.parseInt(intencao.replace("BUSCAR_ID:", "").trim().replaceAll("[^0-9]", ""));
            return perenualService.buscarPorId(id);

        } else if (intencao.startsWith("BUSCAR_NOME:")) {
            String nome = intencao.replace("BUSCAR_NOME:", "").trim();
            return perenualService.buscarPorNomeCientifico(nome);

        } else if (intencao.startsWith("DIAGNOSTICO:")) {
            String problema = intencao.replace("DIAGNOSTICO:", "").trim();
            return perenualService.buscarPorNome(problema);

        } else {
            return perenualService.buscarPorNome("");
        }
    }

    private String gerarResposta(String pergunta, String dadosApi) {
        String prompt = """
                Você é um assistente especialista em plantas chamado PlantFyth.
                Responda em português brasileiro de forma clara e amigável.
                
                Se o usuário estiver descrevendo um problema com a planta (folhas amarelas, manchas, etc),
                faça um diagnóstico com base nos dados da API e sugira soluções.
                
                Se o usuário estiver buscando informações sobre uma planta,
                traduza e apresente os dados de forma organizada e clara.
             
                Caso o usuário perguntar sobre uma planta,
                apresente informações sobre os cuidados de maneira breve que se deve ter com a luz, 
                regas, solo e temperatura.
                
                Não digite link de imagem. Não use * ou # na resposta

                
                Pergunta do usuário: "%s"
                Dados da API: %s
                """.formatted(pergunta, dadosApi);
        return gptService.chamarGpt(prompt);
    }

}
