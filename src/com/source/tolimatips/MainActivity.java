package com.source.tolimatips;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	private Usuario user;

	private ArrayList<Tip> tips;
	private ArrayList<Tip> tipsFiltrados;
	private ArrayList<Municipio> municipios;
	private ArrayList<Categoria> categorias;

	private static final String URL = "http://181.51.33.41/WS/public/";
	private static final String TIPS = "tip/";
	private static final String MUNICIPIOS = "municipio/";
	private static final String CATEGORIAS = "categoria/";

	private ListView lista;

	private Spinner spinMunicipio;
	private Spinner spinCategoria;
	private EditText txtPalabraClave;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		tips = new ArrayList<Tip>();
		municipios = new ArrayList<Municipio>();
		categorias = new ArrayList<Categoria>();

		super.onCreate(savedInstanceState);

		
		
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);

		
		
		Bundle bundle = getIntent().getExtras();
		spinMunicipio = (Spinner) findViewById(R.id.spinMunicipio);
		spinCategoria = (Spinner) findViewById(R.id.spinCategoria);
		txtPalabraClave = (EditText) findViewById(R.id.editPalabraClave);

		user = (Usuario) bundle.get("Data_Us");

		try {

			new TipsTask().execute(URL + TIPS).get();
			new MunicipiosTask().execute(URL + MUNICIPIOS).get();
			new CategoriasTask().execute(URL + CATEGORIAS).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error  " + e.getMessage());
		}
		
		lista = (ListView) findViewById(R.id.LisaTipsUsuario);

		Lista_Tips_Adaptador adapter = new Lista_Tips_Adaptador(
				MainActivity.this,R.layout.activity_listatips ,tips);

		lista.setAdapter(adapter);
		
		
		lista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				Intent act = new Intent(MainActivity.this,TipActivity.class);
				act.putExtra("datos", tips.get(position));
				startActivity(act);
			}
			
		});
	}

	public ArrayList<Tip> filtrar() {

		tipsFiltrados = new ArrayList<Tip>();

		Municipio municipio = (Municipio) spinMunicipio.getSelectedItem();
		Categoria categoria = (Categoria) spinCategoria.getSelectedItem();
		String palabraClave = txtPalabraClave.getText().toString();

		for (Tip t : tips) {

			if (t.getId_municipio() == municipio.getId()
					|| t.getId_categoria() == categoria.getId()
					|| t.getTitulo().contains(palabraClave)) {

				tipsFiltrados.add(t);

			}

		}
		return tipsFiltrados;

	}

	public void onClickBusqueda(View view) {
		
		filtrar();
		Lista_Tips_Adaptador adapter = new Lista_Tips_Adaptador(
				MainActivity.this,R.layout.activity_listatips,tipsFiltrados);
		lista.setAdapter(adapter);
		lista.refreshDrawableState();
		lista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				Intent act = new Intent(MainActivity.this,TipActivity.class);
				act.putExtra("datos", tipsFiltrados.get(position));
				startActivity(act);
			}
			
		});

	}

	public void onClickMapa(View view) {
		Intent act = new Intent(this, MapaActivity.class);
		act.putExtra("Data_Us", user);
		startActivity(act);
		finish();
	}

	public void listTip(View view){
		Intent act = new Intent(this,ListarMisTipActivity.class);
		act.putExtra("Data_Us", user);
		startActivity(act);
	}

	private class TipsTask extends AsyncTask<String, Integer, Long> {

		public void jsonToArraytips(JSONArray jsonArray) throws JSONException {
			// Recorremos el array y convertimos en objetos JSON
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = null;
				try {
					// Se crea un Objeto JSON por cada iteración
					jsonObj = new JSONObject(jsonArray.getString(i));

					// Crear objeto tipo Parqueadero con los datos
					Tip tip = new Tip(
							Integer.parseInt(jsonObj.getString("ID_TIP")),
							Integer.parseInt(jsonObj.getString("ID_CATEGORIA")),
							Integer.parseInt(jsonObj.getString("ID_ESTADO")),
							Integer.parseInt(jsonObj.getString("ID_MUNICIPIO")),
							jsonObj.getString("TITULO"), jsonObj
									.getString("DESCRIPCION"), jsonObj
									.getString("URL_VIDEO"),
							Float.parseFloat(jsonObj.getString("PUNTUACION")),
							Double.parseDouble(jsonObj.getString("LATITUD")),
							Double.parseDouble(jsonObj.getString("LONGITUD")));
					tips.add(tip);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
						jsonToArraytips(jsonArray);
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

	private class MunicipiosTask extends AsyncTask<String, Integer, Long> {

		public void jsonToArraymunicipio(JSONArray jsonArray)
				throws JSONException {
			// Recorremos el array y convertimos en objetos JSON
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = null;
				try {
					// Se crea un Objeto JSON por cada iteración
					jsonObj = new JSONObject(jsonArray.getString(i));

					// Crear objeto tipo Parqueadero con los datos
					Municipio municipio = new Municipio(
							Integer.parseInt(jsonObj.getString("ID_MUNICIPIO")),
							jsonObj.getString("DESCRIPCION"));
					// Agregar objeto al Array de Parqueaderos
					municipios.add(municipio);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		protected void onPostExecute(Long result) {
			ArrayAdapter<Municipio> spinAdapMunicipio = new ArrayAdapter<Municipio>(
					MainActivity.this, android.R.layout.simple_spinner_item,
					municipios);
			// Añadimos el layout para el menú y se lo damos al spinner
			spinAdapMunicipio
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spinMunicipio.setAdapter(spinAdapMunicipio);
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

	}

	private class CategoriasTask extends AsyncTask<String, Integer, Long> {

		public void jsonToArraycategoria(JSONArray jsonArray)
				throws JSONException {
			// Recorremos el array y convertimos en objetos JSON
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = null;
				try {
					// Se crea un Objeto JSON por cada iteración
					jsonObj = new JSONObject(jsonArray.getString(i));

					// Crear objeto tipo Parqueadero con los datos
					Categoria categoria = new Categoria(
							Integer.parseInt(jsonObj.getString("ID_CATEGORIA")),
							jsonObj.getString("DESCRIPCION"));
					// Agregar objeto al Array de Parqueaderos
					categorias.add(categoria);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		protected void onPostExecute(Long result) {
			ArrayAdapter<Categoria> spinAdapCategoria = new ArrayAdapter<Categoria>(
					MainActivity.this, android.R.layout.simple_spinner_item,
					categorias);
			// Añadimos el layout para el menú y se lo damos al spinner
			spinAdapCategoria
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spinCategoria.setAdapter(spinAdapCategoria);
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

	}
}