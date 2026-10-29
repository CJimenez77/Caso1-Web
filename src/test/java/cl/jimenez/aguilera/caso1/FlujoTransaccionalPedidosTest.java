package cl.jimenez.aguilera.caso1;

import cl.jimenez.aguilera.caso1.model.EstadoPedido;
import cl.jimenez.aguilera.caso1.model.Pedido;
import cl.jimenez.aguilera.caso1.model.Producto;
import cl.jimenez.aguilera.caso1.repository.PedidoRepository;
import cl.jimenez.aguilera.caso1.repository.ProductoRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FlujoTransaccionalPedidosTest {

    @Autowired
    private WebApplicationContext contextoWeb;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void inicializar() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(contextoWeb).build();
    }

    @Test
    @DisplayName("POST /api/pedidos -> Debe crear pedido y descontar stock cuando hay disponibilidad")
    void debeCrearPedidoYDescontarStockCuandoHayDisponibilidad() throws Exception {
        Producto producto = productoRepository.findById(1L).orElseThrow();
        int stockInicial = producto.getStockDisponible();
        int cantidadSolicitada = 2;

        String cuerpoJson = """
                {
                    "clienteId": 1,
                    "observacion": "Prueba de transaccionalidad exitosa",
                    "items": [
                        {
                            "productoId": 1,
                            "cantidad": 2
                        }
                    ]
                }
                """;

        MvcResult resultado = mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.numeroPedido").exists())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.total").value(9980.00))
                .andReturn();

        Number idExtraido = JsonPath.read(resultado.getResponse().getContentAsString(), "$.id");
        Long pedidoId = idExtraido.longValue();

        Pedido pedidoPersistido = pedidoRepository.findById(pedidoId).orElseThrow();
        assertEquals(EstadoPedido.PENDIENTE, pedidoPersistido.getEstado());

        Producto productoActualizado = productoRepository.findById(1L).orElseThrow();
        assertEquals(stockInicial - cantidadSolicitada, productoActualizado.getStockDisponible());
    }

    @Test
    @DisplayName("POST /api/pedidos -> Debe rechazar pedido y revertir stock cuando no hay disponibilidad (409 Conflict)")
    void debeRechazarPedidoYRevertirStockCuandoNoHayDisponibilidad() throws Exception {
        Producto producto = productoRepository.findById(6L).orElseThrow();
        int stockInicial = producto.getStockDisponible();
        int cantidadExcessiva = stockInicial + 50;

        String cuerpoJson = String.format("""
                {
                    "clienteId": 1,
                    "observacion": "Prueba de quiebre de stock",
                    "items": [
                        {
                            "productoId": 6,
                            "cantidad": %d
                        }
                    ]
                }
                """, cantidadExcessiva);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigoEstado").value(409))
                .andExpect(jsonPath("$.detalles.nombreProducto").value(producto.getNombre()))
                .andExpect(jsonPath("$.detalles.cantidadSolicitada").value(cantidadExcessiva))
                .andExpect(jsonPath("$.detalles.stockDisponible").value(stockInicial));

        Producto productoSinModificar = productoRepository.findById(6L).orElseThrow();
        assertEquals(stockInicial, productoSinModificar.getStockDisponible());
    }

    @Test
    @DisplayName("PATCH /api/pedidos/{id}/estado -> Debe restaurar stock al cancelar pedido")
    void debeRestaurarStockAlCancelarPedido() throws Exception {
        Producto producto = productoRepository.findById(3L).orElseThrow();
        int stockInicial = producto.getStockDisponible();

        String cuerpoCreacion = """
                {
                    "clienteId": 1,
                    "observacion": "Pedido para cancelacion",
                    "items": [
                        {
                            "productoId": 3,
                            "cantidad": 1
                        }
                    ]
                }
                """;

        MvcResult resultadoCreacion = mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoCreacion))
                .andExpect(status().isCreated())
                .andReturn();

        Number idExtraido = JsonPath.read(resultadoCreacion.getResponse().getContentAsString(), "$.id");
        Long pedidoId = idExtraido.longValue();

        Producto productoDescontado = productoRepository.findById(3L).orElseThrow();
        assertEquals(stockInicial - 1, productoDescontado.getStockDisponible());

        String cuerpoCancelacion = """
                {
                    "estado": "CANCELADO"
                }
                """;

        mockMvc.perform(patch("/api/pedidos/" + pedidoId + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoCancelacion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));

        Producto productoRestaurado = productoRepository.findById(3L).orElseThrow();
        assertEquals(stockInicial, productoRestaurado.getStockDisponible());
    }

    @Test
    @DisplayName("GET /api/clientes/{id}/pedidos -> Debe listar pedidos por cliente")
    void debeListarPedidosPorCliente() throws Exception {
        mockMvc.perform(get("/api/clientes/1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
