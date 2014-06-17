package com.source.tolimatips;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ListarMisTipActivity extends Activity {

	private ArrayList<Tip> tips;

	private ListView lista;

	private Usuario user;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		tips = new ArrayList<Tip>();

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_listar);

		Bundle bundle = getIntent().getExtras();

		user = (Usuario) bundle.get("Data_Us");

		try {
			new HttpTask().execute("http://181.51.33.41/WS/public/tip/"+user.getId())
					.get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error  " + e.getMessage());
		}

		lista = (ListView) findViewById(R.id.listaTips);

		lista.setAdapter(new Lista_Tips_Adaptador(ListarMisTipActivity.this,R.layout.activity_listatips,tips));
		
		lista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				Intent act = new Intent(ListarMisTipActivity.this,TipActivity.class);
				act.putExtra("datos", tips.get(position));
				startActivity(act);
			}
			
		});

	}

	public void jsonToArraytips(JSONArray jsonArray)
			throws JSONException {
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
	
	public void home(View view) {
		Intent act = new Intent(ListarMisTipActivity.this, MainActivity.class);
		act.putExtra("datosUsuario", user);
		startActivity(act);
		finish();
	}

	public void back(View view) {
		Intent intent = new Intent();
		intent.putExtra("resultado", "valor");
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		// Do Here what ever you want do on back press;
		finish();
	}

}
