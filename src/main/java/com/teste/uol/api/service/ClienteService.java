package com.teste.uol.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.teste.uol.api.model.Cliente;
import com.teste.uol.api.repository.ClienteRepository;

@Service
public class ClienteService {

	
	@Autowired
	private ClienteRepository clienteRepository;
	
	public Cliente atualizar(long codigo, Cliente cliente) {
		Cliente clienteSalvo = buscaClientePeloCodigo(codigo);
		BeanUtils.copyProperties(cliente, clienteSalvo, "idCliente");
		return clienteRepository.save(clienteSalvo);
	}
	
	private Cliente buscaClientePeloCodigo(long codigo) {
		Cliente clienteSalvo = clienteRepository.findOne(codigo);
		if (clienteSalvo == null) {
			throw new  EmptyResultDataAccessException(1);
		}
		return clienteSalvo;
	}

	public void atualizarPropriedadeIdade(long codigo, int idade) {
		Cliente clienteSalvo = buscaClientePeloCodigo(codigo);
		clienteSalvo.setIdade(idade);
		clienteRepository.save(clienteSalvo);
		
	}

	public void atualizarPropriedadeNome(long codigo, String nome) {
		Cliente clienteSalvo = buscaClientePeloCodigo(codigo);
		clienteSalvo.setNome(nome);
		clienteRepository.save(clienteSalvo);
	}
}