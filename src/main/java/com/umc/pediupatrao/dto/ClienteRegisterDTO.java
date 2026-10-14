package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.Cliente;

/**
 * DTO para registro de novos clientes
 * 
 * @author Enrico Bertozzi
 * @version 1.0
 */
public record ClienteRegisterDTO(
        String id,
        String nome,
        String telefone,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String endereco) {

    /**
     * Converte os dados do DTO para um objeto da classe do domínio da aplicação
     * 
     * @return {@link Cliente} com dados preenchidos do DTO
     */
    public Cliente toModel() {
        Cliente cli = new Cliente();
        cli.setId(id);
        cli.setNome(nome);
        cli.setTelefone(telefone);
        cli.setCep(cep);
        cli.setLogradouro(logradouro);
        cli.setNumero(numero);
        cli.setComplemento(complemento);
        cli.setBairro(bairro);
        cli.setCidade(cidade);
        cli.setEstado(estado);
        cli.setEndereco(endereco);
        return cli;
    }

}
