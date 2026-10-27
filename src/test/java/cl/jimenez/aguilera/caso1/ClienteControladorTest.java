package cl.jimenez.aguilera.caso1;

import cl.jimenez.aguilera.caso1.model.Cliente;
import cl.jimenez.aguilera.caso1.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ClienteControladorTest {

    @Autowired
    private WebApplicationContext contextoWeb;

    @Autowired
    private ClienteRepository clienteRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void inicializar() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(contextoWeb).build();
    }

    @Test
    void debeListarClientes() throws Exception {
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rut").exists());
    }

    @Test
    void debeObtenerClientePorId() throws Exception {
        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("76.543.210-K"));
    }

    @Test
    void debeActualizarEstadoCliente() throws Exception {
        String cuerpoJson = """
                {
                    "estado": "ACTIVO"
                }
                """;

        mockMvc.perform(patch("/api/clientes/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        Cliente cliente = clienteRepository.findById(1L).orElseThrow();
        assertEquals("ACTIVO", cliente.getEstado());
    }
}
