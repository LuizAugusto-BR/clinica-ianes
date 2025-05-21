package com.sistema.clinica.services;

import com.sistema.clinica.models.Funcionario;
import com.sistema.clinica.models.dtos.FuncionarioDTO;
import com.sistema.clinica.models.dtos.mappers.FuncionarioMapper;
import com.sistema.clinica.repositories.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FuncionarioServiceTest {

    @InjectMocks
    private FuncionarioService funcionarioService;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Funcionario funcionario;

    @BeforeEach
    public void setUp() {
        funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNome("Carlos Silva");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setPassword("senha123");
    }

    @Test
    public void deveRetornarTodosFuncionarios() {
        when(funcionarioRepository.findAll()).thenReturn(List.of(funcionario));

        List<FuncionarioDTO> result = funcionarioService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Carlos Silva", result.get(0).nome());
    }

    @Test
    public void deveRetornarFuncionarioPorId() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));

        FuncionarioDTO result = funcionarioService.findById(1L);

        assertNotNull(result);
        assertEquals("Carlos Silva", result.nome());
    }

    @Test
    public void deveLancarExcecaoQuandoFuncionarioNaoEncontrado() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceAccessException.class, () -> funcionarioService.findById(1L));
    }

    @Test
    public void deveInserirFuncionario() {
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(funcionario);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");

        FuncionarioDTO funcionarioDTO = FuncionarioMapper.toDTO(funcionario);

        FuncionarioDTO result = funcionarioService.insert(funcionarioDTO);

        assertNotNull(result);
        assertEquals("senhaCriptografada", result.password());
    }

    @Test
    public void deveExcluirFuncionario() {
        doNothing().when(funcionarioRepository).deleteById(1L);

        funcionarioService.delete(1L);

        verify(funcionarioRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deveAtualizarFuncionario() {
        Funcionario updatedFuncionario = new Funcionario();
        updatedFuncionario.setNome("Carlos Silva Atualizado");

        FuncionarioDTO updatedFuncionarioDTO = FuncionarioMapper.toDTO(updatedFuncionario);

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(updatedFuncionario);

        FuncionarioDTO result = funcionarioService.update(1L, updatedFuncionarioDTO);

        assertNotNull(result);
        assertEquals("Carlos Silva Atualizado", result.nome());
    }
}
