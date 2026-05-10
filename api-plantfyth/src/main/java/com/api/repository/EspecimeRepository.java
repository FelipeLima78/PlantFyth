package com.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.api_plantfyth.model.Especime;

public interface EspecimeRepository extends JpaRepository<Especime, Integer> {

      public Especime findByNome(String nome);

    public List<Especime> findByNomeStartsWith(String nome);

    public List<Especime> findByNomeEndsWith(String nome);

    public List<Especime> findByNomeContains(String nome);
}
