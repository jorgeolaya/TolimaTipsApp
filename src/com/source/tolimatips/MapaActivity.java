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
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaActivity extends Activity implements OnMapClickListener {

	private GoogleMap mapa;

	private ArrayList<Tip> tips;
	private ArrayList<Municipio> municipios;
	private ArrayList<Categoria> categorias;
	private ArrayList<Tip> tipsFiltrados;

	private static final String URL = "http://181.51.33.41/WS/public/";
	private static final String TIPS = "tip/";
	private static final String MUNICIPIOS = "municipio/";
	private static final String CATEGORIAS = "categoria/";

	private Spinner spinMunicipio;
	private Spinner spinCategoria;
	private EditText txtPalabraClave;

	private Usuario user;

	private LocationManager locationManager;

	private Location loc;

	private String provider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_mapa);

		Bundle bundle = getIntent().getExtras();

		user = (Usuario) bundle.get("Data_Us");

		tips = new ArrayList<Tip>();
		municipios = new ArrayList<Municipio>();
		categorias = new ArrayList<Categoria>();

		spinMunicipio = (Spinner) findViewById(R.id.spinMunicipio);
		spinCategoria = (Spinner) findViewById(R.id.spinCategoria);
		txtPalabraClave = (EditText) findViewById(R.id.editPalabraClave);

		mapa = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		mapa.setMyLocationEnabled(true);

		mapa.getUiSettings().setZoomControlsEnabled(false);

		mapa.getUiSettings().setCompassEnabled(true);

		mapa.getUiSettings().setRotateGesturesEnabled(true);

		mapa.setOnMapClickListener(this);

		OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent act = new Intent(MapaActivity.this, TipActivity.class);
				Tip tips = buscarTip(marker.getTitle());
				act.putExtra("datos", tips);
				startActivity(act);
			}
		};

		mapa.setOnInfoWindowClickListener(listener);

		try {
			new TipsTask().execute(URL + TIPS).get();
			new MunicipiosTask().execute(URL + MUNICIPIOS).get();
			new CategoriasTask().execute(URL + CATEGORIAS).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error  " + e.getMessage());
		}

		llenarMarcas();

		// Obtenemos una referencia al LocationManager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Choosing the best criteria depending on what is available.
		Criteria criteria = new Criteria();

		provider = locationManager.getBestProvider(criteria, false);

		loc = locationManager.getLastKnownLocation(provider);

		if (loc == null) {

			Toast.makeText(getApplicationContext(),
					"No se Encuentra tu ubicacion", Toast.LENGTH_LONG).show();
		}

		else {
			mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(loc.getLatitude(), loc.getLongitude()), 16));
		}

	}

	public Tip buscarTip(String nombre) {
		for (int i = 0; i < tips.size(); i++) {
			if (tips.get(i).getTitulo().equals(nombre)) {
				return tips.get(i);
			}
		}
		return null;
	}

	public void onClickBusqueda(View view) {

		
		llenarMarcas(filtrar());

	}

	public void llenarMarcas() {

		for (int i = 0; i < tips.size(); i++) {
			Tip par = tips.get(i);
			MarkerOptions marca = new MarkerOptions();
			marca.position(new LatLng(par.getLatitud(), par.getLongitud()));
			marca.title(par.getTitulo());
			if (par.getId_categoria() == 1) {
				marca.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_cultura));
			}
			if (par.getId_categoria() == 2) {
				marca.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_gastronomia));
			}
			if (par.getId_categoria() == 3) {
				marca.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_turismo));
			}
			if (par.getId_categoria() == 4) {
				marca.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_religion));
			}
			mapa.addMarker(marca);

		}
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

	public void llenarMarcas(ArrayList<Tip> tips) {

		mapa.clear();

		for (int i = 0; i < tips.size(); i++) {
			Tip par = tips.get(i);
			MarkerOptions marca = new MarkerOptions();
			marca.position(new LatLng(par.getLatitud(), par.getLongitud()));
			marca.title(par.getTitulo());
			if (par.getId_categoria() == 1) {
				marca.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_cultura));
			}
			if (par.getId_categoria() == 2) {
				marca.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_gastronomia));
			}
			if (par.getId_categoria() == 3) {
				marca.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_turismo));
			}
			if (par.getId_categoria() == 4) {
				marca.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_marker_religion));
			}
			mapa.addMarker(marca);

		}
	}

	public void jsonToArraytips(JSONArray jsonArray) throws JSONException {
		// Recorremos el array y convertimos en objetos JSON
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = null;
			try {
				// Se crea un Objeto JSON por cada iteraci�n
				jsonObj = new JSONObject(jsonArray.getString(i));

				// Crear objeto tipo Parqueadero con los datos
				Tip tip = new Tip(
						Integer.parseInt(jsonObj.getString("ID_TIP")),
						Integer.parseInt(jsonObj.getString("ID_CATEGORIA")),
						Integer.parseInt(jsonObj.getString("ID_ESTADO")),
						Integer.parseInt(jsonObj.getString("ID_MUNICIPIO")),
						jsonObj.getString("TITULO"),
						jsonObj.getString("DESCRIPCION"),
						jsonObj.getString("URL_VIDEO"),
						Float.parseFloat(jsonObj.getString("PUNTUACION")),
						Double.parseDouble(jsonObj.getString("LATITUD")),
						Double.parseDouble(jsonObj.getString("LONGITUD")));
				// Agregar objeto al Array de Parqueaderos
				tips.add(tip);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class TipsTask extends AsyncTask<String, Integer, Long> {

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

	public void crearTip(View view) {
		LatLng nuevo = new LatLng(mapa.getCameraPosition().target.latitude,
				mapa.getCameraPosition().target.longitude);
		Intent act = new Intent(MapaActivity.this, NewTipActivity.class);
		act.putExtra("Data_Us", user);
		act.putExtra("pos", nuevo);
		startActivity(act);
		finish();
	}

	public void listTip(View view) {
		Intent act = new Intent(MapaActivity.this, ListarTipActivity.class);
		act.putExtra("Data_Us", user);
		startActivity(act);
	}

	@Override
	public void onBackPressed() {
		// Do Here what ever you want do on back press;
		Intent mainIntent;

		mainIntent = new Intent().setClass(this, MainActivity.class);
		mainIntent.putExtra("Data_Us", user);
		startActivity(mainIntent);
		finish();
	}

	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	private class MunicipiosTask extends AsyncTask<String, Integer, Long> {

		public void jsonToArraymunicipio(JSONArray jsonArray)
				throws JSONException {
			// Recorremos el array y convertimos en objetos JSON
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = null;
				try {
					// Se crea un Objeto JSON por cada iteraci�n
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
					MapaActivity.this, android.R.layout.simple_spinner_item,
					municipios);
			// A�adimos el layout para el men� y se lo damos al spinner
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
					// Se crea un Objeto JSON por cada iteraci�n
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
					MapaActivity.this, android.R.layout.simple_spinner_item,
					categorias);
			// A�adimos el layout para el men� y se lo damos al spinner
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
