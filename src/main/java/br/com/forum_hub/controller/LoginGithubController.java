package br.com.forum_hub.controller;

import br.com.forum_hub.domain.autenticacao.github.LoginGithubService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/login/github")
public class LoginGithubController {

    private final LoginGithubService loginGithubService;

    public LoginGithubController(LoginGithubService loginGithubService) {
        this.loginGithubService = loginGithubService;
    }

    @GetMapping
    public ResponseEntity<Void> redirecionarGithub(){
        var url = loginGithubService.gerarUrl();

        var headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/autorizado")
    public ResponseEntity<String> obterToken(@RequestParam String code){
        var token = loginGithubService.obterToken(code);
        return ResponseEntity.ok(token);
    }
}
