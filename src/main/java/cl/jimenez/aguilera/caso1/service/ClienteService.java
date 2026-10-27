package cl.jimenez.aguilera.caso1.service;

import cl.jimenez.aguilera.caso1.model.Cliente;
import cl.jimenez.aguilera.caso1.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarActivos() {
        return clienteRepository.findAllByActivoTrueOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorRut(String rut) {
        return clienteRepository.findByRut(rut);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorRutOId(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return Optional.empty();
        }
        String limpio = termino.trim();
        Long idNumerico = null;
        try {
            idNumerico = Long.parseLong(limpio.replace("C-", "").replace("c-", "").trim());
        } catch (NumberFormatException ignored) {
        }
        return clienteRepository.buscarPorRutOId(limpio, idNumerico);
    }

    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> actualizar(Long id, Cliente datosActualizados) {
        return clienteRepository.findById(id).map(clienteExistente -> {
            clienteExistente.setRut(datosActualizados.getRut());
            clienteExistente.setNombre(datosActualizados.getNombre());
            clienteExistente.setCorreo(datosActualizados.getCorreo());
            clienteExistente.setTelefono(datosActualizados.getTelefono());
            if (datosActualizados.getEstado() != null) {
                clienteExistente.setEstado(datosActualizados.getEstado());
            }
            if (datosActualizados.getActivo() != null) {
                clienteExistente.setActivo(datosActualizados.getActivo());
            }
            return clienteRepository.save(clienteExistente);
        });
    }

    public boolean eliminar(Long id) {
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setActivo(false);
            cliente.setEstado("INACTIVO");
            clienteRepository.save(cliente);
            return true;
        }).orElse(false);
    }
}
