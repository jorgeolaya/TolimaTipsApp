package com.source.tolimatips;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class NewTipActivity extends Activity {

	private Usuario user;

	private LatLng tippos;

	private Spinner municipio;
	private Spinner categoria;

	private LinkedList<Municipio> mun;
	private LinkedList<Categoria> cat;
	
	private Tip ntip;
	
	private float calificacion;
	
	private RatingBar puntuacion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_new_tip);

		Bundle bundle = getIntent().getExtras();

		user = (Usuario) bundle.get("Data_Us");

		tippos = (LatLng) bundle.get("pos");

		mun = new LinkedList<Municipio>();

		cat = new LinkedList<Categoria>();

		municipio = (Spinner) findViewById(R.id.municipio);

		categoria = (Spinner) findViewById(R.id.categoria);
		
		puntuacion = (RatingBar) findViewById(R.id.puntuacion);

		puntuacion.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			public void onRatingChanged(RatingBar ratingBar,
					float rating, boolean fromUser) {

				calificacion = rating;

			}
		});

		try {
			new HttpTask().execute("http://181.51.33.41/WS/public/municipio/").get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error  " + e.getMessage());
		}

		try {
			new HttpTask2().execute("http://181.51.33.41/WS/public/categoria/").get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error  " + e.getMessage());
		}

		// Creamos el adaptador
		ArrayAdapter<Municipio> spinner_adapter = new ArrayAdapter<Municipio>(
				this, android.R.layout.simple_spinner_item, mun);
		// Añadimos el layout para el menú y se lo damos al spinner
		spinner_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		municipio.setAdapter(spinner_adapter);

		// Creamos el adaptador
		ArrayAdapter<Categoria> spinner_adapter2 = new ArrayAdapter<Categoria>(
				this, android.R.layout.simple_spinner_item, cat);
		// Añadimos el layout para el menú y se lo damos al spinner
		spinner_adapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		categoria.setAdapter(spinner_adapter2);

	}
	
	public void crearTip(View view){
		
		TextView titulo = (TextView) findViewById(R.id.titulo);
		
		TextView descripcion = (TextView) findViewById(R.id.descripcion);
		
		TextView url = (TextView) findViewById(R.id.URL);
		
		
		
		if(!(url.getText().toString().contains("http://www.youtube.com/"))){
			url.setText("http://www.youtube.com/embed/s3RlRhBFgsY");
		}
		
		String munici = municipio.getSelectedItem().toString();
		
		String cate = categoria.getSelectedItem().toString();
		
		int id_municipio = buscarmunicipio(munici).getId();
		
		int id_categoria = buscarcategoria(cate).getId();		
		
		ntip = new Tip(0,id_categoria , 1, id_municipio, titulo.getText() + "", descripcion.getText() + "", url.getText() + "", calificacion, tippos.latitude, tippos.longitude);
		
		try {
			new HttpTask3()
					.execute("http://181.51.33.41/WS/public/tip").get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error  " + e.getMessage());
		}
		Toast.makeText(	getApplicationContext(),"Tip Creador Satisfactoriamente",	Toast.LENGTH_LONG).show();
		Intent act = new Intent(this, MapaActivity.class);
		act.putExtra("Data_Us", user);
		startActivity(act);
		finish();
	}

	public void jsonToArraymunicipio(JSONArray jsonArray) throws JSONException {
		// Recorremos el array y convertimos en objetos JSON
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = null;
			try {
				// Se crea un Objeto JSON por cada iteración
				jsonObj = new JSONObject(jsonArray.getString(i));

				// Crear objeto tipo Parqueadero con los datos
				Municipio municipio = new Municipio(Integer.parseInt(jsonObj
						.getString("ID_MUNICIPIO")),
						jsonObj.getString("DESCRIPCION"));
				// Agregar objeto al Array de Parqueaderos
				mun.add(municipio);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class HttpTask extends AsyncTask<String, Integer, Long> {

		protected void onPreExecute() {
		}

		protected Long doInBackground(String... urls) {

			int count = urls.length;
			long totalSize = 0;
			for (int i = 0; i < count; i++) {

				HttpClient httpclient = new DefaultHttpClient();

				httpclient.getParams().setParameter(
						ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

				HttpGet requestGet = new HttpGet(urls[i]);
				try {
					HttpResponse response = httpclient.execute(requestGet);
					String responseText = EntityUtils.toString(response
							.getEntity());
					// System.out.println("Respuesta GET: " + responseText);
					JSONArray jsonArray = null;

					try {
						// Se serializa el String de la respuesta en un JSON
						jsonArray = new JSONArray(responseText);
						// Este metodo permite recorrer el JSON y hacer lo
						// correspondiente
						jsonToArraymunicipio(jsonArray);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						System.out.println("Error " + e.getMessage());
					}
				} catch (ClientProtocolException e) {
					System.out.println("Error " + e.getMessage());
				} catch (IOException e) {
					System.out.println("Error " + e.getMessage());
				}

				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Error " + e.getMessage());
				}
				// Escape early if cancel() is called
				if (isCancelled())
					break;
			}
			return totalSize;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected void onPostExecute(Long result) {
		}

	}

	public void jsonToArraycategoria(JSONArray jsonArray) throws JSONException {
		// Recorremos el array y convertimos en objetos JSON
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = null;
			try {
				// Se crea un Objeto JSON por cada iteración
				jsonObj = new JSONObject(jsonArray.getString(i));

				// Crear objeto tipo Parqueadero con los datos
				Categoria categoria = new Categoria(Integer.parseInt(jsonObj
						.getString("ID_CATEGORIA")),
						jsonObj.getString("DESCRIPCION"));
				// Agregar objeto al Array de Parqueaderos
				cat.add(categoria);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class HttpTask2 extends AsyncTask<String, Integer, Long> {

		protected void onPreExecute() {
		}

		protected Long doInBackground(String... urls) {

			int count = urls.length;
			long totalSize = 0;
			for (int i = 0; i < count; i++) {

				HttpClient httpclient = new DefaultHttpClient();

				httpclient.getParams().setParameter(
						ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

				HttpGet requestGet = new HttpGet(urls[i]);
				try {
					HttpResponse response = httpclient.execute(requestGet);
					String responseText = EntityUtils.toString(response
							.getEntity());
					// System.out.println("Respuesta GET: " + responseText);
					JSONArray jsonArray = null;

					try {
						// Se serializa el String de la respuesta en un JSON
						jsonArray = new JSONArray(responseText);
						// Este metodo permite recorrer el JSON y hacer lo
						// correspondiente
						jsonToArraycategoria(jsonArray);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						System.out.println("Error " + e.getMessage());
					}
				} catch (ClientProtocolException e) {
					System.out.println("Error " + e.getMessage());
				} catch (IOException e) {
					System.out.println("Error " + e.getMessage());
				}

				catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Error " + e.getMessage());
				}
				// Escape early if cancel() is called
				if (isCancelled())
					break;
			}
			return totalSize;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected void onPostExecute(Long result) {
		}

	}
	
	private class HttpTask3 extends AsyncTask<String, Integer, Long> {

		protected void onPreExecute() {

		}

		protected Long doInBackground(String... urls) {

			int count = urls.length;
			long totalSize = 0;
			for (int i = 0; i < count; i++) {

				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(
						ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
				HttpPost requestPost = new HttpPost(urls[i]);
				requestPost.setHeader("content-type", "application/json");
				
				try {
					// Construimos el objeto cliente en formato JSON
					JSONObject dato = new JSONObject();

					dato.put("ID_CATEGORIA", ntip.getId_categoria() + "");

					dato.put("ID_ESTADO", "1");
					
					dato.put("ID_USUARIO", user.getId() + "");

					dato.put("ID_MUNICIPIO", ntip.getId_municipio() + "");
					
					dato.put("TITULO", ntip.getTitulo() + "");
					
					dato.put("DESCRIPCION", ntip.getDescripcion() + "");
					
					dato.put("URL_VIDEO", ntip.getUrl() + "");
					
					dato.put("PUNTUACION", ntip.getPuntaje() + "");
					
					dato.put("LATITUD", ntip.getLatitud() + "");
					
					dato.put("LONGITUD", ntip.getLongitud() + "");
					
					System.out.println(dato.toString());
					StringEntity entity = new StringEntity(dato.toString());
					
					requestPost.setHeader("Accept", "application/json");
		            requestPost.setHeader("Content-type", "application/json");
		 
					
					
					requestPost.setEntity(entity);

					HttpResponse response = httpclient.execute(requestPost);
					String responseText = EntityUtils.toString(response
							.getEntity());
					
					System.out.println("Respuesta POST: " + responseText);

					

				} catch (ClientProtocolException e) {

				} catch (IOException e) {

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Escape early if cancel() is called
				if (isCancelled())
					break;
			}
			return totalSize;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected void onPostExecute(Long result) {

		}
	}
	
	public Municipio buscarmunicipio(String nombre) {
		for (int i = 0; i < mun.size(); i++) {
			if (mun.get(i).getDescripcion().equals(nombre)) {
				return mun.get(i);
			}
		}
		return null;
	}
	
	public Categoria buscarcategoria(String nombre) {
		for (int i = 0; i < cat.size(); i++) {
			if (cat.get(i).getDescripcion().equals(nombre)) {
				return cat.get(i);
			}
		}
		return null;
	}

}
