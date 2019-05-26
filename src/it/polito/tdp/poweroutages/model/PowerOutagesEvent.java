package it.polito.tdp.poweroutages.model;

import java.time.LocalDateTime;

public class PowerOutagesEvent implements Comparable<PowerOutagesEvent> {

	private LocalDateTime eventBegan;
	private LocalDateTime eventFinished;
	private double costumersAffected;
	private int id;

	public PowerOutagesEvent(LocalDateTime eventBegan, LocalDateTime eventFinished, double costumersAffected, int id) {
		super();
		this.eventBegan = eventBegan;
		this.eventFinished = eventFinished;
		this.costumersAffected = costumersAffected;
		this.id = id;
	}

	public LocalDateTime getEventBegan() {
		return eventBegan;
	}

	public LocalDateTime getEventFinished() {
		return eventFinished;
	}

	public double getCostumersAffected() {
		return costumersAffected;
	}

	public Integer getId() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return String.format("PowerOutagesEvent [eventBegan=%s, eventFinished=%s, costumersAffected=%s, id=%d]", eventBegan,
				eventFinished, costumersAffected, id);
	}

	@Override
	public int compareTo(PowerOutagesEvent o) {
		return this.eventBegan.compareTo(o.eventBegan);
	}

	

}
