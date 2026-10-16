package com.umc.pediupatrao.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private final AuditoriaService auditoriaService;

    ClienteService(ClienteRepository clienteRepository, ClienteUtils clienteUtils, AuditoriaService auditoriaService) {
        this.clienteRepository = clienteRepository;
        this.clienteUtils = clienteUtils;
        this.auditoriaService = auditoriaService;
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

        Cliente salvo = clienteRepository.save(cliente);

        Map<String, Object[]> campos = new HashMap<>();
        campos.put("nome", new Object[] { null, salvo.getNome() });
        campos.put("telefone", new Object[] { null, salvo.getTelefone() });
        campos.put("endereco", new Object[] { null, salvo.getEndereco() });

        auditoriaService.registrar("INCLUSAO", "CLIENTE", salvo.getId(), campos, null);

        return salvo;
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

        Map<String, Object[]> campos = new HashMap<>();

        auditoriaService.registrar("EXCLUSAO", "CLIENTE", id, campos, null);
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

        Cliente clienteAntigo = clienteRepository.findById(cliente.getId()).get();

        String enderecoFormatado = clienteUtils.formatarEndereco(cliente);
        cliente.setEndereco(enderecoFormatado);
        Cliente atualizado = clienteRepository.save(cliente);

        Map<String, Object[]> camposModificados = new HashMap<>();

        if (!Objects.equals(clienteAntigo.getNome(), atualizado.getNome())) {
            camposModificados.put("nome", new Object[] { clienteAntigo.getNome(), atualizado.getNome() });
        }
        if (!Objects.equals(clienteAntigo.getTelefone(), atualizado.getTelefone())) {
            camposModificados.put("telefone", new Object[] { clienteAntigo.getTelefone(), atualizado.getTelefone() });
        }
        if (!Objects.equals(clienteAntigo.getEndereco(), atualizado.getEndereco())) {
            camposModificados.put("endereco", new Object[] { clienteAntigo.getEndereco(), atualizado.getEndereco() });
        }

        if (!camposModificados.isEmpty()) {
            auditoriaService.registrar("ALTERACAO", "CLIENTE", atualizado.getId(), camposModificados, null);
        }

        return atualizado;
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
