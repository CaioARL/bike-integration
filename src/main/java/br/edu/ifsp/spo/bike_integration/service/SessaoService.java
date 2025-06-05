package br.edu.ifsp.spo.bike_integration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.model.Sessao;
import br.edu.ifsp.spo.bike_integration.model.Usuario;
import br.edu.ifsp.spo.bike_integration.model.dto.JwtUserDTO;
import br.edu.ifsp.spo.bike_integration.repository.SessaoRepository;
import br.edu.ifsp.spo.bike_integration.service.jwt.JwtService;

@Service
public class SessaoService {

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private JwtService jwtService;

    public Boolean isValid(String token) {
        return sessaoRepository.findByToken(token).isPresent();
    }

    public Usuario getUsuarioByToken(String token) {
        return sessaoRepository.findByToken(token).get().getUsuario();
    }

    public void create(Usuario usuario) {
        RoleType role = usuario.getRole() != null ? usuario.getRole()
                : usuario.getCpf() != null ? RoleType.PF : usuario.getCnpj() != null ? RoleType.PJ : RoleType.ADMIN;
        String token = jwtService.create(JwtUserDTO.builder()
                .nickname(usuario.getNomeUsuario())
                .email(usuario.getEmail())
                .role(role.getValue())
                .build());

        // busca sessao pelo usuario
        Sessao sessao = sessaoRepository.findByUsuario(usuario).orElse(new Sessao());
        sessao.setToken(token);
        sessao.setUsuario(usuario);
        sessaoRepository.saveAndFlush(sessao);
    }

    public void create(Usuario usuario, String token) {
        Sessao sessao = sessaoRepository.findByUsuario(usuario).orElse(new Sessao());
        sessao.setToken(token);
        sessao.setUsuario(usuario);
        sessaoRepository.save(sessao);
    }

}
