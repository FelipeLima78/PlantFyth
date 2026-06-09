package com.api.api_plantfyth.controller;

import java.util.Date;
import java.time.LocalDateTime;	
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.api_plantfyth.model.Plantio;
import com.api.api_plantfyth.model.Usuario;
import com.api.api_plantfyth.repository.UsuarioRepository;
import com.api.api_plantfyth.service.PlantioService;
import com.api.api_plantfyth.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")

public class UsuarioController{

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
    private PlantioService plantioService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@GetMapping
	public List<Usuario> listarTodos(){
		return usuarioService.findAll();
	}
    @GetMapping("/id/{id}")
	public Usuario buscarPorId(@PathVariable int id){
		Usuario usuario = usuarioService.buscarPorId(id);
		return usuario;
	}

    	@GetMapping("/email/{email}")
	public Usuario buscarPorEmail(@PathVariable String email){
		Usuario usuario = usuarioService.buscarPorEmail(email);
		return usuario;
	}

	
	@DeleteMapping("/deletar/{id}")
	public String deletar(@PathVariable int id){
		usuarioService.deleteUsuario(id);
		return "Usuario Deletado com Sucesso!!!";
	}

	@PostMapping("/inserir")
	public Usuario inserir(@RequestBody Usuario usuario){

		return usuarioService.saveUsuario(usuario);
	}

	@PutMapping("/id/{id}")
	public Usuario atualizar(@RequestBody Usuario usuario, @PathVariable Integer id){
		Usuario usuarioAtualizar = usuarioService.buscarPorId(id);
        usuarioAtualizar.setId(id);
        usuarioAtualizar.setNome(usuario.getNome());
        usuarioAtualizar.setEmail(usuario.getEmail());
        usuarioAtualizar.setHash_senha(usuario.getHash_senha());
		return usuarioService.saveUsuario(usuarioAtualizar);
	}

    @GetMapping("/usuario/{id}")
 public List<Plantio> buscarPorUsuario(
        @PathVariable Integer id
) {

    return plantioService.buscarPorUsuarioId(id);
}
    @PostMapping("/login") 
    public ResponseEntity<String> login(@RequestBody Usuario usuario) {

    Usuario dbUser= usuarioRepository.findByEmail(usuario.getEmail())
        .orElse(null);

    if (dbUser == null) {
        return ResponseEntity.status(401).body("Usuário não encontrado");
    }

    boolean senhaOk = passwordEncoder.matches(
        usuario.getHash_senha(),
        dbUser.getHash_senha()
    );

    if (!senhaOk) {
        return ResponseEntity.status(401).body("Senha inválida");
    }
	dbUser.setUltimo_login(LocalDateTime.now());
    usuarioRepository.save(dbUser);

    return ResponseEntity.ok("Login OK");
} 


}