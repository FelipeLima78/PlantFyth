package com.api.api_plantfyth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;

import com.api.api_plantfyth.model.Especime;
import com.api.api_plantfyth.model.Plantio;
import com.api.api_plantfyth.model.Usuario;
import com.api.api_plantfyth.repository.EspecimeRepository;
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
	@Autowired
	private PlantioRepository plantioRepository;
	@Autowired
	private EspecimeRepository especimeRepository;

	
	@GetMapping
	public List<Plantio> listarTodos(){
		return plantioService.findAll();
	}

	@PostMapping("/inserir")
	public Plantio inserir(@RequestBody Plantio plantio){

		return plantioService.savePlantio(plantio);
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


	@PutMapping("/id/{id}")
	public Plantio atualizar(@RequestBody Plantio plantio, @PathVariable Integer id){
		Plantio plantioAtualizar = plantioService.buscarPorId(id);
		plantioAtualizar.setId(id);	
		plantioAtualizar.setNome(plantio.getNome());
		plantioAtualizar.setFoiRegadoHoje(plantio.getFoiRegadoHoje());
        plantioAtualizar.setPrevisaoProximaIrrigacao(plantio.getPrevisaoProximaIrrigacao());
        plantioAtualizar.setPrevisaoProximaPoda(plantio.getPrevisaoProximaPoda());
		plantioAtualizar.setData_que_foi_Plantado(plantio.getData_que_foi_Plantado());
        plantioAtualizar.setPegouSolHoje(plantio.getPegouSolHoje());
		plantioAtualizar.setEspecime(plantio.getEspecime());
		plantioAtualizar.setPrevisaoProximaAdubacao(plantio.getPrevisaoProximaAdubacao());
		return plantioService.savePlantio(plantioAtualizar);
	}
	@GetMapping("/usuario/{id}")
public List<Plantio> buscarPlantiosUsuario(@PathVariable Integer id) {
    return plantioService.buscarPorUsuarioId(id);
}		

@PostMapping("/resetar-flags-diarias/{usuarioId}")
public ResponseEntity<String> resetarFlagsDiarias(
        @PathVariable Integer usuarioId) {

    plantioRepository.resetarFlagsDiariasPorUsuario(usuarioId);

    return ResponseEntity.ok(
            "Flags diárias resetadas com sucesso."
    );
}

}