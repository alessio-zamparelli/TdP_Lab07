package it.polito.tdp.poweroutages.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PowerOutagesEvent implements Comparable<PowerOutagesEvent> {

	private LocalDateTime eventBegan;
	private LocalDateTime eventFinished;
	private int costumersAffected;
	private int id;
	private long outageDuration;
	private int year;
	private int nerc_id;

	public PowerOutagesEvent(LocalDateTime eventBegan, LocalDateTime eventFinished, int costumersAffected, int id) {
		
		this.eventBegan = eventBegan;
		this.eventFinished = eventFinished;
		this.costumersAffected = costumersAffected;
		this.id = id;
		
		LocalDateTime tempDateTime = LocalDateTime.from(eventBegan);
		this.outageDuration = tempDateTime.until(eventFinished, ChronoUnit.HOURS);
		
		this.year = eventBegan.getYear();
	}
	
public PowerOutagesEvent(LocalDateTime eventBegan, LocalDateTime eventFinished, int costumersAffected, int id, int nerc_id) {
		
		this.eventBegan = eventBegan;
		this.eventFinished = eventFinished;
		this.costumersAffected = costumersAffected;
		this.id = id;
		this.nerc_id = nerc_id;
		
		LocalDateTime tempDateTime = LocalDateTime.from(eventBegan);
		this.outageDuration = tempDateTime.until(eventFinished, ChronoUnit.HOURS);
		
		this.year = eventBegan.getYear();
	}

	public LocalDateTime getEventBegan() {
		return eventBegan;
	}

	public LocalDateTime getEventFinished() {
		return eventFinished;
	}

	public long getCostumersAffected() {
		return costumersAffected;
	}

	public Integer getId() {
		return this.id;
	}
	
//	@Override
//	public String toString() {
//		return String.format("PowerOutagesEvent [eventBegan=%s, eventFinished=%s, costumersAffected=%s, id=%d]", eventBegan,
//				eventFinished, costumersAffected, id);
//	}
	

	@Override
	public int compareTo(PowerOutagesEvent o) {
		return this.eventBegan.compareTo(o.eventBegan);
	}

	public long getOutageDuration() {
		return outageDuration;
	}

	public int getYear() {
		return year;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PowerOutagesEvent other = (PowerOutagesEvent) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PowerOutagesEvent [id=");
		builder.append(id);
		builder.append(", costumersAffected=");
		builder.append(costumersAffected);
		builder.append(", eventBegan=");
		builder.append(eventBegan);
		builder.append(", eventFinished=");
		builder.append(eventFinished);
		builder.append(", nerc_id=");
		builder.append(nerc_id);
		builder.append(", outageDuration=");
		builder.append(outageDuration);
		builder.append(", year=");
		builder.append(year);
		builder.append("]");
		return builder.toString();
	}

	

}
