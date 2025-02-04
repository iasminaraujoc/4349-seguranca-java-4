package br.com.forum_hub.domain.autenticacao.github;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class LoginGithubService {

    private final String clientId = "Ov23linqPz0cwrTV1Rau";
    private final String clientSecret = "b32f75184e1e0584bb1894085d59d44990d3257d";
    private final String redirectUri = "http://localhost:8080/login/github/autorizado";
    private final RestClient restClient;

    public LoginGithubService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public String gerarUrl(){
        return "https://github.com/login/oauth/authorize"+
                "?client_id="+clientId +
                "&redirect_uri="+redirectUri +
                "&scope=read:user,user:email,public_repo";
    }

    private String obterToken(String code) {
        var resposta = restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("code", code, "client_id", clientId,
                        "client_secret", clientSecret, "redirect_uri", redirectUri))
                .retrieve()
                .body(Map.class);
        return resposta.get("access_token").toString();
    }

    public String obterEmail(String code){
        var token = obterToken(code);

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
