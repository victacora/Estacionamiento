package co.com.ceiba.estacionamiento.persistencia.builders;

import co.com.ceiba.estacionamiento.dominio.TicketParqueadero;
import co.com.ceiba.estacionamiento.dominio.Vehiculo;
import co.com.ceiba.estacionamiento.persistencia.entidades.TicketParqueaderoEntity;
import co.com.ceiba.estacionamiento.persistencia.entidades.VehiculoEntity;

public final class TicketParqueaderoBuilder {

	private TicketParqueaderoBuilder() {
		throw new IllegalStateException("Clase utilidad");
	}

	public static TicketParqueadero convertirADominio(TicketParqueaderoEntity ticketParqueaderoEntity) {
		TicketParqueadero ticketParqueadero = null;
		if (ticketParqueaderoEntity != null) {
			Vehiculo vehiculo = null;
			VehiculoEntity vehiculoEntity = ticketParqueaderoEntity.getVehiculo();
			vehiculo = VehiculoBuilder.convertirADominio(vehiculoEntity);
			ticketParqueadero = new TicketParqueadero(ticketParqueaderoEntity.getFechaIngreso(),
					ticketParqueaderoEntity.getFechaSalida(), ticketParqueaderoEntity.getValor(), vehiculo);
		}
		return ticketParqueadero;
	}

	public static TicketParqueaderoEntity convertirAEntity(TicketParqueadero ticketParqueadero) {
		TicketParqueaderoEntity ticketParqueaderoEntity = new TicketParqueaderoEntity();
		if (ticketParqueadero != null) {
			ticketParqueaderoEntity.setFechaIngreso(ticketParqueadero.getFechaIngreso());
			ticketParqueaderoEntity.setFechaSalida(ticketParqueadero.getFechaSalida());
			ticketParqueaderoEntity.setValor(ticketParqueadero.getValor());
			VehiculoEntity vehiculoEntity = VehiculoBuilder.convertirAEntity(ticketParqueadero.getVehiculo());
			vehiculoEntity.agregarTicket(ticketParqueaderoEntity);
			ticketParqueaderoEntity.setVehiculo(vehiculoEntity);
		}
		return ticketParqueaderoEntity;
	}
}
