package com.umc.pediupatrao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.repository.ClienteRepository;
import com.umc.pediupatrao.utils.ClienteUtils;

/**
 * Service para cadastro e gerencimaneto de {@link Cliente} na plataforma.
 * 
 * @author Enrico Bertozzi
 * @author Alenssandro Aparecido da Silva Horas
 * 
 * @version 2.0
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private final ClienteUtils clienteUtils;

    ClienteService(ClienteRepository clienteRepository, ClienteUtils clienteUtils) {
        this.clienteRepository = clienteRepository;
        this.clienteUtils = clienteUtils;
    }

    /** 
     * Salva um novo cliente no banco de dados do sistema
     * 
     * @param cliente Cliente a ser registrado no sistema
     * 
     * @return Cliente registrado no banco de dados.
     */
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    public Cliente novoCliente(Cliente cliente) {
        String enderecoFormatado = clienteUtils.formatarEndereco(cliente);
        cliente.setEndereco(enderecoFormatado);
        return clienteRepository.save(cliente);
    }

    /**
     * Busca todos os clientes regitrados no sistema
     * 
     * @return Lista com todos os clientes registrados
     */
    @PreAuthorize("hasAuthority('CLIENT_READ_ALL')")
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    /**
     * Exclui um cliente registrado na plataforma
     * 
     * @param id Identificador único do cliente
     * 
     * @throw IllegalArgumentException Quanndo o cliente não está cadastrado na
     *        plataforma
     */
    @PreAuthorize("hasAuthority('CLIENT_DELETE')")
    public void excluir(String id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado para remoção.");
        }

        clienteRepository.deleteById(id);
    }

    /**
     * Busca por um cliente pelo seu identificador único
     * 
     * @param id Identificador único do cliente
     * 
     * @return Optional contendo ou não o cliente
     */
    @PreAuthorize("hasAuthority('CLIENT_READ')")
    public Optional<Cliente> buscarPorId(String id) {
        return clienteRepository.findById(id);
    }

    /**
     * Atualiza um cliente existente na plataforma
     * 
     * @param cliente Objeto com os dados atualizados do cliente.
     * 
     * @throws IllegalArgumentException Quando o cliente não está cadastrado na
     *                                  plataforma
     * 
     * @return Cliente com os dados atualizados
     */
    @PreAuthorize("hasAuthority('CLIENT_UPDATE')")
    public Cliente atualizar(Cliente cliente) {
        // Guard Clause caso o cliente não exista
        if (!clienteRepository.existsById(cliente.getId())) {
            throw new IllegalArgumentException("Cliente não encontrado para atualização.");
        }

        String enderecoFormatado = clienteUtils.formatarEndereco(cliente);
        cliente.setEndereco(enderecoFormatado);
        return clienteRepository.save(cliente);
    }

    /**
     * Avalia se cliente é novo na plataforma ou um cliente existente para
     * selecionar a operação de registro ou atualização
     * 
     * @param cliente Cliente a ser registrado ou atualizado
     * 
     * @return Cliente após ser registrado ou atualizado
     */
    @PreAuthorize("hasAnyAuthority('CLIENT_CREATE', 'CLIENT_UPDATE')")
    public Cliente salvar(Cliente cliente) {
        // Novo Cliente
        if (cliente.getId() == null)
            return this.novoCliente(cliente);
        else
            return this.atualizar(cliente);
    }

}
