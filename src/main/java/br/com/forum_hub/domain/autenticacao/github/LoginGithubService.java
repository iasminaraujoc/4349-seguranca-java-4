package br.com.forum_hub.domain.autenticacao.github;

import org.springframework.stereotype.Service;

@Service
public class LoginGithubService {

    public String gerarUrl(){
        return "https://github.com/login/oauth/authorize"+
                "?client_id=Ov23linqPz0cwrTV1Rau"+
                "&redirect_uri=http://localhost:8080/login/github/autorizado"+
                "&scope=read:user,user:email";
    }
}
