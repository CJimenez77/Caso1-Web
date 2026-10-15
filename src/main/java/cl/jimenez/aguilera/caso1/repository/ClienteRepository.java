package cl.jimenez.aguilera.caso1.repository;

import cl.jimenez.aguilera.caso1.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByRut(String rut);

    Optional<Cliente> findByCorreo(String correo);

    boolean existsByRut(String rut);

    boolean existsByCorreo(String correo);

    List<Cliente> findAllByActivoTrue();
}
