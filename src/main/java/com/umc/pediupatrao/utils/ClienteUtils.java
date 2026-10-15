package com.umc.pediupatrao.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.umc.pediupatrao.entity.Cliente;

/**
 * Classe com métodos auxiliares envolvendo e entidade {@link Cliente}
 * 
 * @author Enrico Bertozzi
 * @version 1.0
 */
@Component
public class ClienteUtils {
    
    /**
     * Formata o endereço completo do cliente baseado em sua fragmentação
     * 
     * @param cliente Cliente com os dados do endereço
     * @return Endereco no formato (Logradouro, Número, Complemento, Bairro, Cidade, Estado)
     */
    public String formatarEndereco(Cliente cliente) {
        List<String> partes = List.of(
                cliente.getLogradouro(),
                cliente.getNumero(),
                cliente.getComplemento(),
                cliente.getBairro(),
                cliente.getCidade(),
                cliente.getEstado());

        List<String> partesValidas = partes.stream()
                .filter((p) -> p != null && !p.trim().isEmpty())
                .toList();
        
        if (!partesValidas.isEmpty()) {
            return String.join(", ", partesValidas);
        }

        return (cliente.getEndereco() != null && !cliente.getEndereco().isEmpty()) ? cliente.getEndereco() : "-"; 
    }
}
