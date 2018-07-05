package co.com.ceiba.estacionamiento.unitarias;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.com.ceiba.estacionamiento.EstacionamientoApplication;
import co.com.ceiba.estacionamiento.api.ParqueaderoController;
import co.com.ceiba.estacionamiento.dominio.TicketParqueadero;
import co.com.ceiba.estacionamiento.dominio.Vehiculo;
import co.com.ceiba.estacionamiento.dominio.dto.TicketParqueaderoDTO;
import co.com.ceiba.estacionamiento.dominio.dto.VehiculoDTO;
import co.com.ceiba.estacionamiento.dominio.servicios.ParqueaderoServicio;
import co.com.ceiba.estacionamiento.dominio.servicios.TarifaServicio;
import co.com.ceiba.estacionamiento.dominio.servicios.VehiculoServicio;
import co.com.ceiba.estacionamiento.enumeraciones.EnumTipoVehiculo;
import co.com.ceiba.estacionamiento.persistencia.builders.TicketParqueaderoDTOBuilder;
import co.com.ceiba.estacionamiento.persistencia.entidades.TicketParqueaderoEntity;
import co.com.ceiba.estacionamiento.persistencia.entidades.VehiculoEntity;
import co.com.ceiba.estacionamiento.testdatabuilder.CarroTestDataBuilder;
import co.com.ceiba.estacionamiento.testdatabuilder.TicketParqueaderoEntityTestDataBuilder;
import co.com.ceiba.estacionamiento.testdatabuilder.TicketParqueaderoTestDataBuilder;
import co.com.ceiba.estacionamiento.testdatabuilder.VehiculoDTOTestDataBuilder;
import co.com.ceiba.estacionamiento.testdatabuilder.VehiculoEntityTestDataBuilder;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { EstacionamientoApplication.class })
@WebMvcTest(ParqueaderoController.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParqueaderoRestApiTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ParqueaderoServicio parqueaderoSevicio;

	@MockBean
	private VehiculoServicio vehiculoServicio;

	@MockBean
	private TarifaServicio tarifaServicio;

	@Test
	public void consultarVehiculosParqueadero() throws Exception {

		VehiculoEntity vehiculoEntity = new VehiculoEntityTestDataBuilder().withPlaca("CXX-000").withCilindraje(10)
				.withTipoVehiculo(EnumTipoVehiculo.CARRO.name()).build();
		TicketParqueaderoEntity ticketParqueaderoEntity = new TicketParqueaderoEntityTestDataBuilder().withId(1l)
				.withValor(1000).withFechaIngreso(new Date()).withFechaSalida(new Date()).withVehiculo(vehiculoEntity)
				.build();

		TicketParqueaderoDTO ticketParqueaderoDTO = TicketParqueaderoDTOBuilder.convertirADTO(ticketParqueaderoEntity);

		List<TicketParqueaderoDTO> ticketParqueaderos = Arrays.asList(ticketParqueaderoDTO);

		given(parqueaderoSevicio.listarVehiculosParqueadero(0, 10, "ASC", "fechaIngreso")).willReturn(ticketParqueaderos);

		mvc.perform(
				get("/parqueadero/listadovehiculos?pagina=0&tamano=10&dirOrdenamiento=ASC&campoOrdenamiento=fechaIngreso")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void ingresarVehiculoParqueadero() throws Exception {

		VehiculoDTO vehiculoDTO = new VehiculoDTOTestDataBuilder().withPlaca("CXX-001").withCilindraje(10)
				.withTipoVehiculo(EnumTipoVehiculo.MOTO.name()).build();

		String vehiculoDTOJson = json(vehiculoDTO);

		given(parqueaderoSevicio.registraringreso(vehiculoDTO, vehiculoServicio, tarifaServicio)).willReturn(true);

		mvc.perform(
				post("/parqueadero/ingresarvehiculo").contentType(MediaType.APPLICATION_JSON).content(vehiculoDTOJson))
				.andExpect(status().isOk());
	}

	@Test
	public void retirarVehiculoParqueadero() throws Exception {
		Vehiculo carro = new CarroTestDataBuilder().withPlaca("CXX-000").build();

		TicketParqueadero ticketParqueadero = new TicketParqueaderoTestDataBuilder().withVehiculo(carro)
				.withFechaIngreso(new Date()).build();

		given(parqueaderoSevicio.retirarVehiculo(carro.getPlaca(), vehiculoServicio, tarifaServicio))
				.willReturn(ticketParqueadero);

		mvc.perform(
				post("/parqueadero/retirarvehiculo").contentType(MediaType.APPLICATION_JSON).content(carro.getPlaca()))
				.andExpect(status().isOk());
	}

	public static String json(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
