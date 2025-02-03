package br.com.forum_hub.domain.autenticacao.github;

public record DadosEmail(String email,
                         Boolean primary,
                         Boolean verified,
                         String visibility) {
}
