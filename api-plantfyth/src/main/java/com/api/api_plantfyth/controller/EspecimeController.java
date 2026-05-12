package com.api.api_plantfyth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.api_plantfyth.model.Especime;
import com.api.api_plantfyth.model.Plantio;
import com.api.api_plantfyth.repository.EspecimeRepository;
import com.api.api_plantfyth.service.EspecimeService;
import com.api.api_plantfyth.service.PlantioService;

@RestController
@RequestMapping("/especimes")
public class EspecimeController {

	@Autowired
	private EspecimeService especimeService;
    private PlantioService plantioService;
	private EspecimeRepository especimeRepository;
	
	@GetMapping
	public List<Especime> listarTodos(){
		return especimeService.listarTodos();
	}

	@GetMapping("/nome_especime/{nome_especime}")
	public Especime buscarPorNome_Especime(@PathVariable String nome_especime){
		Especime especime = especimeService.buscarPorNome(nome_especime);
		return especime;
	}

	@GetMapping("/primeiro_nome/{nome_especime}")
	public List<Especime> buscarPorPrimeiroNome(@PathVariable String nome_especime){
		return especimeService.buscarPorPrimeiroNome(nome_especime);
	}

	@GetMapping("/ultimo_nome/{nome_especime}")
	public List<Especime> buscarPorUltimoNome(@PathVariable String nome_especime) {
		return especimeService.buscarPorUltimoNome(nome_especime);
	}

	@GetMapping("/letra-do-nome/{nome_especime}")
	public List<Especime> buscarPorLetraDoNome(@PathVariable String nome_especime) {
		return especimeService.buscarPorLetraNoNome(nome_especime);
	}

	
	@DeleteMapping("/{id}")
	public String deletar(@PathVariable int id){
		especimeService.deletar(id);
		return "Especime Deletado com Sucesso!!!";
	}

	@PostMapping
	public Especime inserir(@RequestBody Especime especime){
		return especimeService.salvar(especime);
	}

     @GetMapping("/especimes/{id}")
 public List<Plantio> buscarPorEspecime(
        @PathVariable Integer id
) {

    return plantioService.buscarPorEspecimeId(id);
}
    
}
