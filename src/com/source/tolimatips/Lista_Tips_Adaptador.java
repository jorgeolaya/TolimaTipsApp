package com.source.tolimatips;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class Lista_Tips_Adaptador extends BaseAdapter {

	private ArrayList<Tip> entrada;
	private int R_layout_IdView;
	private Context contexto;
	
	public Lista_Tips_Adaptador(Context contexto, int R_layout_IdView,ArrayList<Tip> entrada) {
		super();
		this.contexto = contexto;
		this.entrada = entrada;
		this.R_layout_IdView = R_layout_IdView;
	}

	@Override
	public View getView(int posicion, View view, ViewGroup pariente) {

		LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		view = vi.inflate(R_layout_IdView, null,true);
		
		if (entrada != null) {
			
			TextView titulo = (TextView) view.findViewById(R.id.titulo);
			
			if (titulo != null){
			
				titulo.setText(entrada.get(posicion).getTitulo());
				
				
			
			}			

			RatingBar puntuacion = (RatingBar) view.findViewById(R.id.puntuacion);
			
			if (puntuacion != null){
				
				puntuacion.setRating((float) entrada.get(posicion).getPuntaje());
			
			}	
			
			TextView descripcion = (TextView) view.findViewById(R.id.detalle);
			
			if (descripcion != null){
				
				descripcion.setText(entrada.get(posicion).getDescripcion());
			}
		}
		
		return view;
	}

	@Override
	public int getCount() {
		return entrada.size();
	}

	@Override
	public Object getItem(int posicion) {
		return entrada.get(posicion);
	}

	@Override
	public long getItemId(int posicion) {
		return posicion;
	}

	
}
