package cl.jimenez.aguilera.caso1.service;

import cl.jimenez.aguilera.caso1.dto.CrearItemPedidoDTO;
import cl.jimenez.aguilera.caso1.dto.CrearPedidoDTO;
import cl.jimenez.aguilera.caso1.exception.OperacionInvalidaException;
import cl.jimenez.aguilera.caso1.exception.RecursoNoEncontradoException;
import cl.jimenez.aguilera.caso1.exception.StockInsuficienteException;
import cl.jimenez.aguilera.caso1.model.Cliente;
import cl.jimenez.aguilera.caso1.model.EstadoPedido;
import cl.jimenez.aguilera.caso1.model.ItemPedido;
import cl.jimenez.aguilera.caso1.model.Pedido;
import cl.jimenez.aguilera.caso1.model.Producto;
import cl.jimenez.aguilera.caso1.repository.ClienteRepository;
import cl.jimenez.aguilera.caso1.repository.PedidoRepository;
import cl.jimenez.aguilera.caso1.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProductoRepository productoRepository,
                         ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findByIdConDetalle(id);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPorNumeroPedido(String numeroPedido) {
        return pedidoRepository.findByNumeroPedidoConDetalle(numeroPedido);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaDesc(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorClienteRut(String rut) {
        return pedidoRepository.findByClienteRutOrderByFechaDesc(rut);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorClienteRutYEstado(String rut, EstadoPedido estado) {
        if (estado == null) {
            return pedidoRepository.findByClienteRutOrderByFechaDesc(rut);
        }
        return pedidoRepository.findByClienteRutAndEstadoOrderByFechaDesc(rut, estado);
    }

    public Pedido crearPedido(CrearPedidoDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new OperacionInvalidaException("El pedido debe contener al menos un item");
        }

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + dto.getClienteId()));

        if (Boolean.FALSE.equals(cliente.getActivo()) || "INACTIVO".equalsIgnoreCase(cliente.getEstado())) {
            throw new OperacionInvalidaException("No es posible generar pedidos para un cliente inactivo");
        }

        for (CrearItemPedidoDTO itemDto : dto.getItems()) {
            Producto producto = productoRepository.findById(itemDto.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + itemDto.getProductoId()));

            if (!producto.tieneStockSuficiente(itemDto.getCantidad())) {
                throw new StockInsuficienteException(
                        producto.getNombre(),
                        itemDto.getCantidad(),
                        producto.getStockDisponible() != null ? producto.getStockDisponible() : 0
                );
            }
        }

        String numeroGenerado = generarNumeroPedido();
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(numeroGenerado);
        pedido.setCliente(cliente);
        pedido.setFecha(OffsetDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setObservacion(dto.getObservacion());
        pedido.setTotal(BigDecimal.ZERO);

        for (CrearItemPedidoDTO itemDto : dto.getItems()) {
            Producto producto = productoRepository.findById(itemDto.getProductoId()).get();
            producto.descontarStock(itemDto.getCantidad());
            productoRepository.save(producto);

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProducto(producto);
            item.setCantidad(itemDto.getCantidad());
            item.setPrecioUnitario(producto.getPrecio());
            item.calcularSubtotal();

            pedido.agregarItem(item);
        }

        pedido.recalcularTotal();
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findByIdConDetalle(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + id));

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new OperacionInvalidaException("No se puede cambiar el estado de un pedido que ya se encuentra CANCELADO");
        }

        if (pedido.getEstado() == EstadoPedido.COMPLETADO && nuevoEstado == EstadoPedido.CANCELADO) {
            throw new OperacionInvalidaException("No se puede cancelar un pedido COMPLETADO");
        }

        if (nuevoEstado == EstadoPedido.CANCELADO && pedido.getEstado() != EstadoPedido.CANCELADO) {
            for (ItemPedido item : pedido.getItems()) {
                Producto producto = item.getProducto();
                producto.restaurarStock(item.getCantidad());
                productoRepository.save(producto);
            }
        }

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    private String generarNumeroPedido() {
        long conteo = pedidoRepository.count() + 1025;
        String propuesta = "#" + conteo;
        while (pedidoRepository.findByNumeroPedido(propuesta).isPresent()) {
            conteo++;
            propuesta = "#" + conteo;
        }
        return propuesta;
    }
}
