package com.api.api_plantfyth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.api_plantfyth.model.Usuario;
import com.api.api_plantfyth.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
	private UsuarioRepository usuarioRepository;
	
	public List<Usuario> findAll(){
		return usuarioRepository.findAll();
	}
	
	//buscar por id 
	public Usuario buscarPorId(int id) {
		return usuarioRepository.findById(id).get();
	}
	//busca por email
	public Usuario buscarPorEmail(String email) {
		return usuarioRepository.findByEmail(email).get();
	}
	
	//Create e update
	public Usuario saveUsuario(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}
	
	//Delete
	public void deleteUsuario(int id) {
		usuarioRepository.deleteById(id);
	}
	
	
}
