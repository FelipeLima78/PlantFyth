package com.api.api_plantfyth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.api_plantfyth.model.Plantio;
import com.api.api_plantfyth.repository.PlantioRepository;
import com.api.api_plantfyth.service.PlantioService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/plantios")
public class PlantioController{

	@Autowired
	private PlantioService plantioService;
	private PlantioRepository plantioRepository;
	
	@GetMapping
	public List<Plantio> listarTodos(){
		return plantioService.findAll();
	}

	@GetMapping("/id/{id}")
	public Plantio buscarPorId(@PathVariable int id){
		Plantio plantio = plantioService.buscarPorId(id);
		return plantio;
	}

	@GetMapping("/nome_plantio/{nome_plantio}")
	public Plantio buscarPorNome_Planta(@PathVariable String nome_planta){
		Plantio planta = plantioService.buscarPorNome(nome_planta);
		return planta;
	}

	@GetMapping("/primeiro_nome/{nome_planta}")
	public List<Plantio> buscarPorPrimeiroNome(@PathVariable String nome_planta){
		return plantioService.buscarPorPrimeiroNome(nome_planta);
	}

	@GetMapping("/ultimo_nome/{nome_planta}")
	public List<Plantio> buscarPorUltimoNome(@PathVariable String nome_planta) {
		return plantioService.buscarPorUltimoNome(nome_planta);
	}

	@GetMapping("/letra-do-nome/{nome_planta}")
	public List<Plantio> buscarPorLetraDoNome(@PathVariable String nome_planta) {
		return plantioService.buscarPorLetraNoNome(nome_planta);
	}

	
	@DeleteMapping("/{id}")
	public String deletar(@PathVariable int id){
		plantioService.deletePlantio(id);
		return "Plantio Deletado com Sucesso!!!";
	}

	@PostMapping("/inserir")
	public Plantio inserir(@RequestBody Plantio plantio){
		return plantioService.savePlantio(plantio);
	}

	@PutMapping("/id/{id}")
	public Plantio atualizar(@RequestBody Plantio plantio, @PathVariable Integer id){
		Plantio plantioAtualizar = plantioService.buscarPorId(id);
		plantioAtualizar.setId(plantio.getId());
		plantioAtualizar.setNome(plantio.getNome());
        plantioAtualizar.setDataQueFoiPlantado(plantio.getDataQueFoiPlantado());
        plantioAtualizar.setFoiRegado(plantio.isFoiRegado());
        plantioAtualizar.setDataAdubo(plantio.getDataAdubo());
        plantioAtualizar.setHorarioAreIrrigar(plantio.getHorarioAreIrrigar());
        plantioAtualizar.setPrevisaoProximaIrrigacao(plantio.getPrevisaoProximaIrrigacao());
        plantioAtualizar.setPrevisaoProximaAdubacao(plantio.getPrevisaoProximaAdubacao());
        plantioAtualizar.setPrevisaoProximaPoda(plantio.getPrevisaoProximaPoda());
        plantioAtualizar.setNivelUmidade(plantio.getNivelUmidade());
        plantioAtualizar.setTamanhoAtualCM(plantio.getTamanhoAtualCM());
        plantioAtualizar.setPrevisaoTamanho(plantio.getPrevisaoTamanho());
        plantioAtualizar.setPlantadaComo(plantio.getPlantadaComo());
        plantioAtualizar.setImagemPersonalizada(plantio.getImagemPersonalizada());
		return plantioService.savePlantio(plantioAtualizar);
	}


    @GetMapping("/plantios/usuario/{id}")
    public ResponseEntity<List<Plantio>>
    buscarPlantiosUsuario(
            @PathVariable Integer id){

        return ResponseEntity.ok(plantioService
                .buscarPorUsuarioId(id)
        );
    }

 
}