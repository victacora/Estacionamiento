package co.com.ceiba.estacionamiento.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import co.com.ceiba.estacionamiento.dominio.TicketParqueadero;
import co.com.ceiba.estacionamiento.dominio.dto.TicketParqueaderoDTO;
import co.com.ceiba.estacionamiento.dominio.dto.VehiculoDTO;
import co.com.ceiba.estacionamiento.dominio.excepciones.AccesoRestringidoException;
import co.com.ceiba.estacionamiento.dominio.excepciones.CupoExcedidoException;
import co.com.ceiba.estacionamiento.dominio.excepciones.VehiculoNoEncontradoException;
import co.com.ceiba.estacionamiento.dominio.excepciones.VehiculoNoRegistradoException;
import co.com.ceiba.estacionamiento.dominio.excepciones.VehiculoRegistradoException;
import co.com.ceiba.estacionamiento.dominio.servicios.ParqueaderoServicio;
import co.com.ceiba.estacionamiento.dominio.servicios.TarifaServicio;
import co.com.ceiba.estacionamiento.dominio.servicios.VehiculoServicio;

@Controller
@RequestMapping("/parqueadero")
public class ParqueaderoController {

	private static final long COD_OPERACION_EXITOSA = 1001l;
	private static final long COD_OPERACION_ERRONEA = 1002l;
	@Autowired
	private ParqueaderoServicio parqueaderoSevicio;

	@Autowired
	private VehiculoServicio vehiculoServicio;

	@Autowired
	private TarifaServicio tarifaServicio;

	@GetMapping(value = "/listadovehiculos")
	@ResponseBody
	public List<TicketParqueaderoDTO> listarTodosLosTicketsParqueadero(@RequestParam int pagina,
			@RequestParam int tamano, @RequestParam String dirOrdenamiento, @RequestParam String campoOrdenamiento) {
		return parqueaderoSevicio.listarVehiculosParqueadero(pagina, tamano, dirOrdenamiento, campoOrdenamiento);
	}

	@PostMapping(value = "/ingresarvehiculo")
	public @ResponseBody Map<Long, String> registrarIngreso(@RequestBody VehiculoDTO vehiculoDTO) {
		Map<Long, String> resultado = new HashMap<>();
		try {
			boolean vehiculoRegistradoCorrectamente = parqueaderoSevicio.registraringreso(vehiculoDTO, vehiculoServicio,
					tarifaServicio);
			if (vehiculoRegistradoCorrectamente) {
				resultado.put(COD_OPERACION_EXITOSA, "Vehiculo registrado correctamente.");
			} else {

				resultado.put(COD_OPERACION_ERRONEA, "No se pudo registrar el vehiculo.");
			}
		} catch (CupoExcedidoException e) {
			resultado.put(e.getCodigoError(), e.getMessage());
		} catch (AccesoRestringidoException e) {
			resultado.put(e.getCodigoError(), e.getMessage());
		} catch (VehiculoRegistradoException e) {
			resultado.put(e.getCodigoError(), e.getMessage());
		}
		return resultado;
	}

	@PostMapping(value = "/retirarvehiculo")
	public @ResponseBody Map<Long, String> retirarvehiculo(@RequestBody String placa) {
		Map<Long, String> resultado = new HashMap<>();
		try {
			TicketParqueadero ticketParqueadero = parqueaderoSevicio.retirarVehiculo(placa, vehiculoServicio,
					tarifaServicio);
			if (ticketParqueadero != null) {
				resultado.put(COD_OPERACION_EXITOSA, "El vehiculo fue retirado correctamente.");
			} else {
				resultado.put(COD_OPERACION_ERRONEA, "No se pudo retirar el vehiculo.");
			}
		} catch (VehiculoNoEncontradoException e) {
			resultado.put(e.getCodigoError(), e.getMessage());
		} catch (VehiculoNoRegistradoException e) {
			resultado.put(e.getCodigoError(), e.getMessage());
		}
		return resultado;
	}
}
