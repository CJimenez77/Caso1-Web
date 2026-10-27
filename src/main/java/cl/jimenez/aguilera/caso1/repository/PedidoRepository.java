package cl.jimenez.aguilera.caso1.repository;

import cl.jimenez.aguilera.caso1.model.EstadoPedido;
import cl.jimenez.aguilera.caso1.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    List<Pedido> findByClienteIdOrderByFechaDesc(Long clienteId);

    List<Pedido> findByClienteRutOrderByFechaDesc(String rut);

    List<Pedido> findByClienteIdAndEstadoOrderByFechaDesc(Long clienteId, EstadoPedido estado);

    List<Pedido> findByClienteRutAndEstadoOrderByFechaDesc(String rut, EstadoPedido estado);

    List<Pedido> findAllByOrderByFechaDesc();

    long countByClienteId(Long clienteId);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.cliente LEFT JOIN FETCH p.items i LEFT JOIN FETCH i.producto WHERE p.id = :id")
    Optional<Pedido> findByIdConDetalle(@Param("id") Long id);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.cliente LEFT JOIN FETCH p.items i LEFT JOIN FETCH i.producto WHERE p.numeroPedido = :numeroPedido")
    Optional<Pedido> findByNumeroPedidoConDetalle(@Param("numeroPedido") String numeroPedido);
}
