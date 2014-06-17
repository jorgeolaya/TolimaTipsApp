package com.source.tolimatips;

import java.io.Serializable;

public class Tip implements Serializable{
	
	private int id;
	private int id_categoria;
	private int id_estado;
	private int id_municipio;
	private String titulo;
	private String descripcion;
	private String url;
	private float puntaje;
	private double latitud;
	private double longitud;
	
	public Tip(int id, int id_categoria, int id_estado, int id_municipio,String titulo, String descripcion, String url, float puntaje,double latitud, double longitud) {
		this.id = id;
		this.id_categoria = id_categoria;
		this.id_estado = id_estado;
		this.id_municipio = id_municipio;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.url = url;
		this.puntaje = puntaje;
		this.latitud = latitud;
		this.longitud = longitud;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_categoria() {
		return id_categoria;
	}

	public void setId_categoria(int id_categoria) {
		this.id_categoria = id_categoria;
	}

	public int getId_estado() {
		return id_estado;
	}

	public void setId_estado(int id_estado) {
		this.id_estado = id_estado;
	}

	public int getId_municipio() {
		return id_municipio;
	}

	public void setId_municipio(int id_municipio) {
		this.id_municipio = id_municipio;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public float getPuntaje() {
		return puntaje;
	}

	public void setPuntaje(float puntaje) {
		this.puntaje = puntaje;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}	

}
