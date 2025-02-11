package br.com.forum_hub.domain.autenticacao;

import br.com.forum_hub.domain.usuario.a2f.TipoA2f;

public record DadosToken(String tokenAcesso, String refreshToken, TipoA2f a2f) {
}
