package com.source.tolimatips;

public class Municipio {
	
	private int id;
	
	private String descripcion;

	public Municipio(int id, String descripcion) {
		super();
		this.id = id;
		this.descripcion = descripcion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}	
	
	
	public String toString(){
		return descripcion;
	}

}
