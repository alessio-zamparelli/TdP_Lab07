package it.polito.tdp.poweroutages.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
//import java.time.temporal.TemporalUnit;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
//import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.polito.tdp.poweroutages.db.PowerOutageDAO;

public class Model {

	private PowerOutageDAO podao;

	// per la funzione ricorsiva
	private Case solution;
	

	public Model() {
		podao = new PowerOutageDAO();

	}

	public List<Nerc> getNercList() {
		return podao.getNercList();
	}

	public List<PowerOutagesEvent> worstCaseAnalysis(Nerc nerc, int maxYear, int maxHours) {

//		Map<Integer, PowerOutagesEvent> idMapEvents = new HashMap<>();

//		for (PowerOutagesEvent poe : podao.getPowerOutagesEvents(nerc)) {
//			idMapEvents.put(poe.getId(), poe);
//		}
		
		Map<LocalDateTime, PowerOutagesEvent> idMapEvents = podao.getPowerOutagesEvents(nerc).parallelStream().collect(Collectors.toMap(PowerOutagesEvent::getEventBegan, Function.identity()));
//		System.out.println(idMapEvents.values());
		solution = new Case(false, LocalDateTime.MAX, LocalDateTime.MIN, new HashMap<Integer, PowerOutagesEvent>(), Long.MIN_VALUE);
		
		magicFunction(new TreeMap<Integer, PowerOutagesEvent>(), maxYear, maxHours, idMapEvents);
		
		return solution.getEventsList();
	}

	private void magicFunction(Map<Integer, PowerOutagesEvent> partial, int maxYear, int maxHours, Map<LocalDateTime, PowerOutagesEvent> idMapEvents) {

		if(idMapEvents.size()==0)
			// inizio dei giochi
			return;
		System.out.format("[DEBUG] partial: %s\n", partial.keySet());
		Case partialCase = partialValid(partial, maxYear, maxHours);
		if(partial.size()!=0)
			System.out.format("[DEBUG] case   : %s\n", partialCase.getEventsList().parallelStream().map(a->a.getId()).collect(Collectors.toList()));
		if(!partialCase.isValid)
			// controllo sulla validità della soluzione parziale
			return;
		
		if(partialCase.maxPeopleAffected > solution.maxPeopleAffected) {
			// questo è il caso peggiore (quindi quello che cerco)
			solution = partialCase;
		}
		
		for (PowerOutagesEvent e : idMapEvents.values()) {

			Map<Integer, PowerOutagesEvent> newPartial = new TreeMap<>(partial);
			newPartial.put(e.getId(), e);
			Map<LocalDateTime, PowerOutagesEvent> newIdMapEvents = new TreeMap<>(idMapEvents);
			newIdMapEvents.remove(e.getEventBegan());
			magicFunction(newPartial, maxYear, maxHours, newIdMapEvents);

		}

	}



	private Case partialValid(Set<PowerOutagesEvent> partial, int maxYear, int maxHours) {
		
		LocalDateTime yearBegan = LocalDateTime.MAX;
		LocalDateTime yearEnd = LocalDateTime.MIN;
		long hours = 0;
		long maxPeopleAffected = 0;
		boolean isValid = true;
		
		if(partial.size()==0)
			return new Case(true);
		
		
		
		for(PowerOutagesEvent poe: partial) {
			// se inizia prima
			if(poe.getEventBegan().isBefore(yearBegan))
				yearBegan = poe.getEventBegan();
			// se finisce dopo
			if(poe.getEventFinished().isAfter(yearEnd))
				yearEnd = poe.getEventFinished();
			
			hours += Duration.between(poe.getEventBegan(), poe.getEventFinished()).toHours();
			maxPeopleAffected+=poe.getCostumersAffected();
		}		
		// controllo di non aver sforato gli anni
		if(ChronoUnit.YEARS.between(yearBegan, yearEnd)>maxYear)
			isValid=false;
		// controllo di non aver sforato le ore
		if(hours>maxHours)
			isValid=false;
		
		return new Case(isValid, yearBegan, yearEnd, partial, maxPeopleAffected);
	}
	
	private class Case {
		// mi serviva un wrapper! (bruttino pero cosi...)
		private boolean isValid;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private long maxPeopleAffected;
		private Map<Integer, PowerOutagesEvent> events;
		
		public Case(boolean isValid, LocalDateTime startDate, LocalDateTime endDate, Map<Integer, PowerOutagesEvent> events, long maxPeopleAffected) {
			this.isValid = isValid;
			this.startDate = startDate;
			this.endDate = endDate;
			this.events = events;
			this.maxPeopleAffected = maxPeopleAffected;
		}

		public Case(boolean isValid) {
			this.isValid = isValid;
			
		}

		public List<PowerOutagesEvent> getEventsList() {
			return events.values().parallelStream().collect(Collectors.toList());
		}

		public boolean isValid() {
			return isValid;
		}

		public LocalDateTime getStartDate() {
			return startDate;
		}

		public LocalDateTime getEndDate() {
			return endDate;
		}

		public Map<Integer, PowerOutagesEvent> getEvents() {
			return events;
		}
		
		public double getMaxPeopleAffected() {
			return maxPeopleAffected;
		}

		public void setValid(boolean isValid) {
			this.isValid = isValid;
		}

		public void setStartDate(LocalDateTime startDate) {
			this.startDate = startDate;
		}

		public void setEndDate(LocalDateTime endDate) {
			this.endDate = endDate;
		}

		public void setEvents(Map<Integer, PowerOutagesEvent> events) {
			this.events = events;
		}
		
		public void setMaxPeopleAffected(long maxPeopleAffected) {
			this.maxPeopleAffected = maxPeopleAffected;
		}

		@Override
		public String toString() {
			return String.format("Case [isValid=%s, startDate=%s, endDate=%s, maxPeopleAffected=%s, events=%s]",
					isValid, startDate, endDate, maxPeopleAffected, events);
		}
		
		
		
		
		
	}

}
