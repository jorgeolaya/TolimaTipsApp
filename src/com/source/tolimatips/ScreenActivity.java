package com.source.tolimatips;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class ScreenActivity extends Activity {

	private long splashDelay = 3000; // 3 segundos
	private Usuario user;
	private AccountManager accountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		accountManager = AccountManager.get(this);

		Account a = accountManager.getAccountsByType("com.google")[0];

		String correo = a.name;

		try {
			Object response = new HttpTask().execute(
					"http://181.51.33.41/WS/public/usuario/" + correo).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error  " + e.getMessage());
		}

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Intent i = new Intent(ScreenActivity.this, MainActivity.class);
				i.putExtra("Data_Us", user);
				startActivity(i);
				finish();
			}
		};

		Timer timer = new Timer();

		timer.schedule(task, splashDelay);// Pasado los 3 segundos dispara la
											// tarea
	}

	public void jsonUsuario(JSONObject jsonObj) throws JSONException {
		// Recorremos el array y convertimos en objetos JSON
		try {
			// Crear objeto tipo Usuario con los datos
			user = new Usuario(
					Integer.parseInt(jsonObj.getString("ID_USUARIO")),
					jsonObj.getString("NOMBRE"), jsonObj.getString("CORREO"),
					jsonObj.getString("ESTADO").charAt(0));
			// Agregar objeto al Array de Parqueaderos
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Error" + e.getMessage());
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
					try {
						// Se serializa el String de la respuesta en un JSON
						JSONObject jsonObj = new JSONObject(responseText);

						// Este metodo permite recorrer el JSON y hacer lo
						// correspondiente
						jsonUsuario(jsonObj);
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
}
