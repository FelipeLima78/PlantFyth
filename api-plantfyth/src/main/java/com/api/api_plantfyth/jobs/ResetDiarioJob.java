
package com.api.api_plantfyth.jobs;

import com.api.api_plantfyth.repository.PlantioRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class ResetDiarioJob {

    private final PlantioRepository repo;

    private static final ZoneId FUSO =
            ZoneId.of("America/Sao_Paulo");

    private static final Path ARQUIVO_RESET =
            Path.of("ultimo-reset.txt");

    public ResetDiarioJob(PlantioRepository repo) {
        this.repo = repo;
    }

    // Verifica se houve algum dia sem reset quando a API inicia
    @EventListener(ApplicationReadyEvent.class)
    public void verificarResetAoIniciar() {
        resetarSeNecessario();
    }

    // Continua executando diariamente à meia-noite
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void resetarMeiaNoite() {
        resetarSeNecessario();
    }

    @Transactional
    public void resetarSeNecessario() {
        LocalDate hoje = LocalDate.now(FUSO);

        try {
            if (Files.exists(ARQUIVO_RESET)) {
                String conteudo =
                        Files.readString(ARQUIVO_RESET).trim();

                if (!conteudo.isEmpty()) {
                    LocalDate ultimoReset =
                            LocalDate.parse(conteudo);

                    if (ultimoReset.equals(hoje)) {
                        System.out.println(
                                "Reset diário já realizado hoje: " + hoje);
                        return;
                    }
                }
            }

            // Reseta as flags diárias no banco
            repo.resetarFlagsDiarias();

            // Registra a data do reset
            Files.writeString(ARQUIVO_RESET, hoje.toString());

            System.out.println(
                    "Reset diário realizado em: " + hoje);

        } catch (IOException |
                 java.time.format.DateTimeParseException e) {
            throw new RuntimeException(
                    "Erro ao executar o reset diário", e);
        }
    }
}
