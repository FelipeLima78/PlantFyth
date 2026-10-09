package com.api.api_plantfyth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


	@GetMapping
	public List<Especime> listarTodos(){
		return especimeService.findAll();
	}
	@GetMapping("/id/{id}")
	public Especime buscarPorId(@PathVariable int id){
		Especime especime = especimeService.buscarPorId(id);
		return especime;
	}
	
	@DeleteMapping("/{id}")
	public String deletar(@PathVariable int id){
		especimeService.deletar(id);
		return "Especime Deletado com Sucesso!!!";
	}
}
