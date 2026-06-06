package com.api.api_plantfyth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.api_plantfyth.model.Especime;
import com.api.api_plantfyth.model.Plantio;

@Repository
public interface EspecimeRepository extends JpaRepository<Especime, Integer> {

      public Especime findByNomePopular(String nome);

    Optional<Especime> findByPerenualId(Integer perenualId);
    
    boolean existsByPerenualId(Integer perenualId);
    
}
