package com.api.api_plantfyth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.api_plantfyth.model.Especime;
import com.api.api_plantfyth.model.Plantio;
import com.api.api_plantfyth.repository.EspecimeRepository;
import com.api.api_plantfyth.repository.PlantioRepository;
import com.api.api_plantfyth.service.PerenualService;
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
	@Autowired
	private PlantioRepository plantioRepository;
	@Autowired
	private EspecimeRepository especimeRepository;
	@Autowired
	private PerenualService perenualService;
	
	@GetMapping
	public List<Plantio> listarTodos(){
		return plantioService.findAll();
	}

	@GetMapping("/id/{id}")
	public Plantio buscarPorId(@PathVariable int id){
		Plantio plantio = plantioService.buscarPorId(id);
		return plantio;
	}

	@GetMapping("/nome_plantio/{nome_planta}")
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


/* */
@PostMapping("/inserir")
public ResponseEntity<?> inserir(@RequestBody Plantio plantio) {
    if (plantio.getEspecimeId() == null) {
        return ResponseEntity.badRequest().body("Especime ID é nulo");
    }

    Especime especime = especimeRepository.findByPerenualId(plantio.getEspecimeId())
        .orElseGet(() -> {
            Especime nova = perenualService.buscarDetalhe(plantio.getEspecimeId());
            if (nova != null) {
                return especimeRepository.save(nova);
            }
            return null;
        });

    if (especime == null) {
        return ResponseEntity.notFound().build();
    }

    // achou no banco mas está incompleto
    if (especime.getDescricao() == null) {
        Especime completo = perenualService.buscarDetalhe(especime.getPerenualId());
        if (completo != null) {
            completo.setId(especime.getId());
            especime = especimeRepository.save(completo);
        }
    }

    plantio.setEspecime(especime);
    plantio.setEspecimeId(especime.getId().intValue());
    return ResponseEntity.ok(plantioService.savePlantio(plantio));
}
	@PutMapping("/id/{id}")
	public Plantio atualizar(@RequestBody Plantio plantio, @PathVariable Integer id){
		Plantio plantioAtualizar = plantioService.buscarPorId(id);
		plantioAtualizar.setId(id);	
		plantioAtualizar.setNome(plantio.getNome());
        plantioAtualizar.setData_que_foi_Plantado(plantio.getData_que_foi_Plantado());
        plantioAtualizar.setFoiRegadoHoje(plantio.getFoiRegadoHoje());
        plantioAtualizar.setHorarioAteIrrigar(plantio.getHorarioAteIrrigar());
        plantioAtualizar.setPrevisaoProximaIrrigacao(plantio.getPrevisaoProximaIrrigacao());
        plantioAtualizar.setPrevisaoProximaPoda(plantio.getPrevisaoProximaPoda());
        plantioAtualizar.setTamanhoAtualCM(plantio.getTamanhoAtualCM());
        plantioAtualizar.setPrevisaoTamanhoCM(plantio.getPrevisaoTamanhoCM());
        plantioAtualizar.setPlantadaComo(plantio.getPlantadaComo());
        plantioAtualizar.setImagemPersonalizada(plantio.getImagemPersonalizada());
		plantioAtualizar.setEspecime(plantio.getEspecime());
		return plantioService.savePlantio(plantioAtualizar);
	}
	

 
}