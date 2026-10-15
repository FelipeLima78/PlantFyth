package com.api.api_plantfyth.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.api.api_plantfyth.repository.PlantioRepository;

@Component
public class ResetDiarioJob {

    private final PlantioRepository repo;  

    public ResetDiarioJob(PlantioRepository repo) {
        this.repo = repo;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void resetar() {
        repo.resetarFlagsDiarias();
    }
}