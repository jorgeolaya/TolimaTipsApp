package com.source.tolimatips;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayerFragment;

public class TipActivity extends Activity {

	private Tip tip;

	private YouTubePlayerFragment youTubePlayerFragment;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tip);
		
		Bundle bundle = getIntent().getExtras();
		
		tip = (Tip) bundle.get("datos");
		
		TextView nombre = (TextView) findViewById(R.id.titulo);
		
		nombre.setText(tip.getTitulo());
		
		TextView descripcion = (TextView) findViewById(R.id.descripcion);
		
		descripcion.setText(tip.getDescripcion());
		
		RatingBar puntuacion = (RatingBar) findViewById(R.id.puntuacion);
		
		puntuacion.setRating(tip.getPuntaje());
		
		if(tip.getUrl().contains("http://www.youtube.com/")){
			String url =  tip.getUrl();
			
			String[] s;
			
			if(url.contains("embed")){
				s= url.split("embed/");
			}else{
				s= url.split("v=");
			}	
			
			String resultado=s[1].substring(0,11);

			youTubePlayerFragment = ((YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.map));

			VideoFragment f = VideoFragment.newInstance(resultado);
			
			getFragmentManager().beginTransaction().replace(R.id.YouTubePlayer, f).commit();
		}		

	}
	
	public void volver(View view) {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}

}
