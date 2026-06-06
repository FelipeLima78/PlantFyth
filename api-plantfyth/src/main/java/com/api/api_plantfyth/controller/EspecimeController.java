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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.api_plantfyth.model.Especime;
import com.api.api_plantfyth.model.Plantio;
import com.api.api_plantfyth.repository.EspecimeRepository;
import com.api.api_plantfyth.service.EspecimeService;
import com.api.api_plantfyth.service.PerenualService;
import com.api.api_plantfyth.service.PlantioService;

@RestController
@RequestMapping("/especimes")
public class EspecimeController {

	@Autowired
	private EspecimeService especimeService;
	@Autowired
    private PlantioService plantioService;
	@Autowired
	private EspecimeRepository especimeRepository;
	@Autowired
	private PerenualService perenualService;
	
	@GetMapping
	public List<Especime> listarTodos(){
		return especimeService.listarTodos();
	}

	@GetMapping("/importar-automatico")
public String importarAutomatico() {
   perenualService.importarPlantasIndoor(1);
    return "Importação automática da página 1 concluída!";
}


	@GetMapping("/indoor")
	public List<Especime> listarIndoor(@RequestParam(defaultValue = "1") int page) {
    return perenualService.buscarIndoor(page);
	}

	@GetMapping("/indoor/{perenualId}")
	public Especime buscarDetalhe(@PathVariable int perenualId) {
    return perenualService.buscarDetalhe(perenualId);
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
