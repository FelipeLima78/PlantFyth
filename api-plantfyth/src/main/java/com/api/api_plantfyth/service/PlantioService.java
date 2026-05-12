package com.api.api_plantfyth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.api_plantfyth.model.Plantio;
import com.api.api_plantfyth.repository.PlantioRepository;

@Service
public class PlantioService {
    @Autowired
	private PlantioRepository plantioRepository;
	
	public List<Plantio> findAll(){
		return plantioRepository.findAll();
	}
	
	//buscar por id 
	public Plantio buscarPorId(int id) {
		return plantioRepository.findById(id).get();
	}
	
	//Create e update
	public Plantio savePlantio(Plantio plantio) {
		return plantioRepository.save(plantio);
	}
	
	//Delete
	public void deletePlantio(int id) {
		plantioRepository.deleteById(id);
	}

     public Plantio buscarPorNome(String nome) {

        return plantioRepository.findByNome(nome);
    }

    // BUSCAR NOMES QUE COMEÇAM COM
    public List<Plantio> buscarPorPrimeiroNome(String nome) {

        return plantioRepository.findByNomeStartsWith(nome);
    }

    // BUSCAR NOMES QUE TERMINAM COM
    public List<Plantio> buscarPorUltimoNome(String nome) {

        return plantioRepository.findByNomeEndsWith(nome);
    }

    // BUSCAR NOMES QUE CONTÉM
    public List<Plantio> buscarPorLetraNoNome(String nome) {

        return plantioRepository.findByNomeContains(nome);
    }

    // BUSCAR POR USUÁRIO
    public List<Plantio> buscarPorUsuarioId(Integer usuarioId) {

        return plantioRepository.findByUsuarioId(usuarioId);
    }

    // BUSCAR POR ESPÉCIME
    public List<Plantio> buscarPorEspecimeId(Integer especimeId) {

        return plantioRepository.findByEspecimeId(especimeId);
    }

    // BUSCA FLEXÍVEL
    public List<Plantio> buscarPorNomeContem(String nome) {

        return plantioRepository.findByNomeContaining(nome);
    }
}
