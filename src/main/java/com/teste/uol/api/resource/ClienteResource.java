package com.teste.uol.api.resource;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.teste.uol.api.model.Cliente;
import com.teste.uol.api.model.Historico;
import com.teste.uol.api.repository.ClienteRepository;
import com.teste.uol.api.repository.HistoricoRepository;
import com.teste.uol.api.service.ClienteService;
import com.teste.uol.api.service.ConfigService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping ("/clientes")
@Api (value = "Cliente", description = "Operações referentes ao Cliente")
public class ClienteResource {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private HistoricoRepository historicoRepository;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private ClienteService clienteService;
	
	@ApiOperation(value="Lista todos os clientes cadastrados")
	@ApiResponses(value= {
			 @ApiResponse(code = 200, message = "Lista com todos os clientes"),
		     @ApiResponse(code = 204, message = "Não a clientes cadastrados"),		
	}
	)
	@GetMapping
	public ResponseEntity<?> listar() {
		List<Cliente> clientes = clienteRepository.findAll();
		
		return !clientes.isEmpty() ? ResponseEntity.ok(clientes) : ResponseEntity.noContent().build();	
	
	}
	
	@ApiOperation(value="Busca cliente por Id")
	@ApiResponses(value= {
			 @ApiResponse(code = 200, message = "Cliente do {codigo} encotrado"),
		     @ApiResponse(code = 404, message = "Cliente não encontrado"),		
	}
	)
	@GetMapping("/{codigo}")
	public ResponseEntity<?> buscarPorId(@PathVariable long codigo) {
		Cliente cliente = clienteRepository.findOne(codigo);
		
		return cliente != null ? ResponseEntity.ok(cliente) : ResponseEntity.notFound().build();	
	}
	
	@ApiOperation(value="Cadastrada um novo cliente no sistema")
	@ApiResponses(value= {
			 @ApiResponse(code = 201, message = "Cliente cadastrado com sucesso"),	
	}
	)
	@PostMapping
	public ResponseEntity<Cliente> criar(@RequestBody @Valid Cliente cliente, HttpServletResponse response){		
		
		try {
			Historico historico = configService.preencherHistorico();
			
			Cliente clienteSalvo = clienteRepository.save(cliente);
			
			historico.setCliente(clienteSalvo);
			
			historicoRepository.save(historico);
			
			URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
					.buildAndExpand(clienteSalvo.getIdCliente()).toUri();
			
			return ResponseEntity.created(uri).body(clienteSalvo);
			
		}catch(Exception e){
			throw new RuntimeException();
		}
	}
	
	
	
	@ApiOperation(value="Altera um Cliente")
	@ApiResponses(value= {
			 @ApiResponse(code = 204, message = "Cliente do {codigo} alterado com sucesso"),
		     @ApiResponse(code = 404, message = "Cliente não encontrado"),		
	}
	)
	@PutMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Cliente> atualizar(@PathVariable long codigo, @RequestBody Cliente cliente){
		Cliente clienteSalvo = clienteService.atualizar(codigo, cliente);
		return ResponseEntity.ok().body(clienteSalvo);
		
	}
	
	@ApiOperation(value="Altera a propriedade Idade")
	@ApiResponses(value= {
			 @ApiResponse(code = 204, message = "Cliente do {codigo} alterado com sucesso"),
		     @ApiResponse(code = 404, message = "Cliente não encontrado"),		
	}
	)
	@PutMapping("/{codigo}/idade")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarPropriedadeIdade(@PathVariable long codigo, @RequestBody int idade) {
		clienteService.atualizarPropriedadeIdade(codigo, idade);
	}
	
	@ApiOperation(value="Altera a propriedade Nome")
	@ApiResponses(value= {
			 @ApiResponse(code = 204, message = "Cliente do {codigo} alterado com sucesso"),
		     @ApiResponse(code = 404, message = "Cliente não encontrado"),		
	}
	)
	@PutMapping("/{codigo}/nome")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarPropriedadeNome(@PathVariable long codigo, @RequestBody String nome) {
		clienteService.atualizarPropriedadeNome(codigo, nome);
	}
	
	@ApiOperation(value="Remove um cliente por Id")
	@ApiResponses(value= {
			 @ApiResponse(code = 204, message = "Cliente do {codigo} removido com sucesso"),
		     @ApiResponse(code = 404, message = "Cliente não encontrado"),		
	}
	)
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable long codigo) {
		clienteRepository.delete(codigo);
	}
}
