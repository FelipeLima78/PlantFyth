package com.api.api_plantfyth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.api_plantfyth.model.Especime;
import com.api.api_plantfyth.repository.EspecimeRepository;
import com.api.api_plantfyth.service.EspecimeService;

@RestController
@RequestMapping("/especimes")
public class EspecimeController {

	@Autowired
	private EspecimeService especimeService;
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
	
	@PostMapping
public Especime criar(@RequestBody Especime especime) {
    return especimeRepository.save(especime);
}

@PutMapping("/id/{id}")
public Especime atualizar(@PathVariable Integer id, @RequestBody Especime especime) {
    especimeService.buscarPorId(id);
    especime.setId(id);
    return especimeRepository.save(especime);
}
}
