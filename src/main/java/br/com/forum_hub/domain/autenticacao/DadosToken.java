package br.com.forum_hub.domain.autenticacao;

public record DadosToken(String tokenAcesso, String refreshToken, Boolean a2f) {
}
