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


    public void salvarListaEspecimes(List<Especime> lista) {
        for (Especime e : lista) {
            if (!especimeRepository.existsByPerenualId(e.getPerenualId())) {
                especimeRepository.save(e);
            }
        }
    }
    
    public List<Especime> listarTodos() {
        return especimeRepository.findAll();
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
