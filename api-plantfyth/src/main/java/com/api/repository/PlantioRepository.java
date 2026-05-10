package com.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.api.api_plantfyth.model.Plantio;

public interface PlantioRepository extends JpaRepository<Plantio, Integer>{

      public Plantio findByNome(String nome);

    public List<Plantio> findByNomeStartsWith(String nome);

    public List<Plantio> findByNomeEndsWith(String nome);

    public List<Plantio> findByNomeContains(String nome);

     List<Plantio> findByUsuarioId(Integer usuario_id);
     List<Plantio> findByEspecimeId(Integer especime_id);

    List<Plantio> findByNomeContaining(String nome);

}
