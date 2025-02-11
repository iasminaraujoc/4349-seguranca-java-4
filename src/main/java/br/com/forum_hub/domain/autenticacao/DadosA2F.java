package br.com.forum_hub.domain.autenticacao;

import br.com.forum_hub.domain.usuario.a2f.TipoA2f;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosA2F(@NotBlank String email,
                       @NotBlank String codigo) {
}
