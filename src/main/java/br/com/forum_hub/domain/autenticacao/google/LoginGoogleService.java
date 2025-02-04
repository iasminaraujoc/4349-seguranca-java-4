package br.com.forum_hub.domain.autenticacao.google;

import br.com.forum_hub.domain.autenticacao.github.DadosEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class LoginGoogleService {

    @Value("${google.oauth.client.id}")
    private String clientId;
    @Value("${google.oauth.client.secret}")
    private String clientSecret;
    private final String redirectUri = "http://localhost:8080/login/google/autorizado";
    private final RestClient restClient;

    public LoginGoogleService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public String gerarUrl(){
        return "https://accounts.google.com/o/oauth2/v2/auth"+
                "?client_id="+clientId +
                "&redirect_uri="+redirectUri +
                "&scope=https://www.googleapis.com/auth/userinfo.email" +
                "&response_type=code";
    }

    private String obterToken(String code) {
        var resposta = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("code", code, "client_id", clientId,
                        "client_secret", clientSecret, "redirect_uri", redirectUri,
                        "grant_type", "authorization_code"))
                .retrieve()
                .body(String.class);
        return resposta;
    }

    public String obterEmail(String code){
        var token = obterToken(code);
        System.out.println(token);

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        var resposta = restClient.get()
                .uri("https://api.github.com/user/emails")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(DadosEmail[].class);

        var repositorios = restClient.get()
                .uri("https://api.github.com/user/repos")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
        System.out.println(repositorios);

        for(DadosEmail d: resposta){
            if(d.primary() && d.verified())
                return d.email();
        }

        return null;
    }
}
