package br.com.forum_hub.controller;

import br.com.forum_hub.domain.autenticacao.DadosA2F;
import br.com.forum_hub.domain.autenticacao.DadosLogin;
import br.com.forum_hub.domain.autenticacao.DadosRefreshToken;
import br.com.forum_hub.domain.autenticacao.DadosToken;
import br.com.forum_hub.domain.autenticacao.TokenService;
import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.domain.usuario.UsuarioRepository;
import br.com.forum_hub.infra.seguranca.totp.TotpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final TotpService totpService;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository usuarioRepository, TotpService totpService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.totpService = totpService;
    }

    @PostMapping("/login")
    public ResponseEntity<DadosToken> efetuarLogin(@Valid @RequestBody DadosLogin dados){
        var autenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = authenticationManager.authenticate(autenticationToken);

        var usuario = (Usuario) authentication.getPrincipal();
        if(usuario.isA2fAtiva()){
            return ResponseEntity.ok(new DadosToken(null, null, true));
        }

        String tokenAcesso = tokenService.gerarToken(usuario);
        String refreshToken = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new DadosToken(tokenAcesso, refreshToken, false));
    }

    @PostMapping("/verificar-a2f")
    public ResponseEntity<DadosToken> verificarSegundoFator(@Valid @RequestBody DadosA2F dadosA2F){
        var usuario = usuarioRepository.findByEmailIgnoreCaseAndVerificadoTrueAndAtivoTrue(dadosA2F.email())
                .orElseThrow();
        var codigoValido = totpService.verificarCodigo(dadosA2F.codigo(), usuario);
        if(!codigoValido){
            throw new BadCredentialsException("Código inválido");
        }
        String tokenAcesso = tokenService.gerarToken(usuario);
        String refreshToken = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new DadosToken(tokenAcesso, refreshToken, false));
    }

    @PostMapping("/atualizar-token")
    public ResponseEntity<DadosToken> atualizarToken(@Valid @RequestBody DadosRefreshToken dados){
        var refreshToken = dados.refreshToken();
        Long idUsuario = Long.valueOf(tokenService.verificarToken(refreshToken));
        var usuario = usuarioRepository.findById(idUsuario).orElseThrow();

        String tokenAcesso = tokenService.gerarToken(usuario);
        String tokenAtualizacao = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new DadosToken(tokenAcesso, tokenAtualizacao, false));
    }
}
