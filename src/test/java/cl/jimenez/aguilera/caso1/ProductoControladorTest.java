package cl.jimenez.aguilera.caso1;

import cl.jimenez.aguilera.caso1.model.Producto;
import cl.jimenez.aguilera.caso1.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class ProductoControladorTest {

    @Autowired
    private WebApplicationContext contextoWeb;

    @Autowired
    private ProductoRepository productoRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void inicializar() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(contextoWeb).build();
    }

    @Test
    @DisplayName("GET /api/productos -> Debe listar productos activos")
    void debeListarProductosActivos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].codigo").exists());
    }

    @Test
    @DisplayName("GET /api/productos/1 -> Debe obtener producto por ID")
    void debeObtenerProductoPorId() throws Exception {
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("P-001"));
    }

    @Test
    @DisplayName("GET /api/productos/bajo-stock -> Debe listar productos bajo stock mínimo")
    void debeListarProductosBajoStockMinimo() throws Exception {
        mockMvc.perform(get("/api/productos/bajo-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PATCH /api/productos/1/stock -> Debe ajustar stock de producto")
    void debeAjustarStockDeProducto() throws Exception {
        String cuerpoJson = """
                {
                    "nuevoStock": 75
                }
                """;

        mockMvc.perform(patch("/api/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockDisponible").value(75));

        Producto producto = productoRepository.findById(1L).orElseThrow();
        assertEquals(75, producto.getStockDisponible());
    }
}
