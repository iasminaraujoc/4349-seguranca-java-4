package br.com.forum_hub.domain.autenticacao;

import jakarta.validation.constraints.NotBlank;

public record DadosA2F(@NotBlank String email,
                       @NotBlank String codigo) {
}
