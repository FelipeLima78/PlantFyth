package com.api.api_plantfyth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.api_plantfyth.model.Especime;
import com.api.api_plantfyth.repository.EspecimeRepository;

@Service
public class EspecimeService {
     @Autowired
    private EspecimeRepository especimeRepository;

    // LISTAR TODOS
    public List<Especime> listarTodos() {

        return especimeRepository.findAll();
    }

    // BUSCAR POR NOME EXATO
    public Especime buscarPorNome(String nome) {

        return especimeRepository.findByNome(nome);
    }

    // BUSCAR NOMES QUE COMEÇAM COM
    public List<Especime> buscarPorPrimeiroNome(String nome) {

        return especimeRepository.findByNomeStartsWith(nome);
    }

    // BUSCAR NOMES QUE TERMINAM COM
    public List<Especime> buscarPorUltimoNome(String nome) {

        return especimeRepository.findByNomeEndsWith(nome);
    }

    // BUSCAR NOMES QUE CONTÉM
    public List<Especime> buscarPorLetraNoNome(String nome) {

        return especimeRepository.findByNomeContains(nome);
    }

    // SALVAR
    public Especime salvar(Especime especime) {

        return especimeRepository.save(especime);
    }

    // DELETAR
    public void deletar(Integer id) {

        especimeRepository.deleteById(id);
    }
}
