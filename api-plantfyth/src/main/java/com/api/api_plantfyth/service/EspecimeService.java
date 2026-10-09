package com.api.api_plantfyth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.api_plantfyth.model.Especime;
import com.api.api_plantfyth.model.Plantio;
import com.api.api_plantfyth.repository.EspecimeRepository;

@Service
public class EspecimeService {
     @Autowired
    private EspecimeRepository especimeRepository;


    public Especime saveEspecime(Especime especime) {
		return especimeRepository.save(especime);
	}

    public void deleteEspecime(int id) {
		especimeRepository.deleteById(id);
	}
    
    public List<Especime> findAll() {
        return especimeRepository.findAll();
    }

        public Especime buscarPorId(int id) {
            return especimeRepository.findById(id).orElse(null);
        }

    public void deletar(Integer id) {
        especimeRepository.deleteById(id);
    }
}
