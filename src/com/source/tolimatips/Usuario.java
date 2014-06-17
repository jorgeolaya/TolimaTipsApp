package com.source.tolimatips;

import java.io.Serializable;

public class Usuario implements Serializable{
	
	private int id;
	private String nombre;
	private String correo;
	private char estado;
	
	public Usuario(int id, String nombre, String correo, char estado) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.correo = correo;
		this.estado = estado;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public char getEstado() {
		return estado;
	}

	public void setEstado(char estado) {
		this.estado = estado;
	}	

}
