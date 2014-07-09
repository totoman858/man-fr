package com.elcubano.man_fr;

import com.elcubano.man_fr.ManualActivity;
import com.elcubano.man_fr.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Class that loads the html content of a manpage
 * @author chmod0
 *
 */ 

public class BrowserActivity extends Activity {

	// A view to display web pages
	private WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Ask rights  to display the progress bar
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		
		setContentView(R.layout.pages_browser);
		// Get the manpage to be open from the Intent extras
		final Page manpage = (Page)((Bundle)getIntent().getExtras().get("manpage")).get("manpage");
		// Url pointing to the right manpage
		String manPageUrl ="/htmlman/htmlman" + manpage.getSection() + "/" + manpage.getName() + "." + manpage.getSection() + ".html";
		String baseUrl;

		webview = (WebView)findViewById(R.id.webview);
		// Set a new webview client to manage the opening of links
		webview.setWebViewClient(new MyWebViewClient());
		// Enable javascript on manpages
		webview.getSettings().setJavaScriptEnabled(true);		
		
		final Activity activity = this;
		
		// Customize the webview to display a progress bar on top of the activity
		webview.setWebChromeClient(new WebChromeClient(){
			public void onProgressChanged(WebView view, int progress){
				activity.setTitle("Chargement...");
				activity.setProgress(progress * 100);
				if(progress == 100)
					activity.setTitle("man fr");
			}
		});
		
		// Check if the page file exists on the external storage
		if( ! ManualActivity.checkPageFilesOnExternalStorage(manPageUrl)) {
					
					// man pages are store in assets folder
					baseUrl = "file:///android_asset";			
					Log.d("MANPAGES", "Man page found in external storage");
						
					// Popup debug
					// new AlertDialog.Builder(this)
				    // .setTitle("Info warning")
				    // .setMessage(Environment.getExternalStorageDirectory().getAbsolutePath())
				    // .show();
					
		} else {
					baseUrl = "file:///android_asset";
					Log.d("MANPAGES", "Man page not found in external storage");
		}
		
		webview.loadUrl(baseUrl + manPageUrl);

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // Check if the key event was the BACK key and if there's history
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
	        webview.goBack();
	        return true;
	    }
	    // If it wasn't the BACK key or there's no web page history, bubble up to the default
	    // system behavior (probably exit the activity)
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Create the options menu of the activity
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.options_browser, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Actions to do when the options menu is clicked
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Instantiates a layout XML file into its corresponding View objects
		LayoutInflater factory = getLayoutInflater();
		
		switch(item.getItemId()){
		case R.id.about_menu:
			AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
			aboutDialog.setTitle("A propos");
			aboutDialog.setIcon(R.drawable.ic_launcher);
			aboutDialog.setView(factory.inflate(R.layout.about_popup, null,
					false));
			aboutDialog.setPositiveButton("Ok", null);
			aboutDialog.show();
			break;
		case R.id.section_menu:
			AlertDialog.Builder sectionsDialog = new AlertDialog.Builder(this);
			sectionsDialog.setTitle("Sections");
			sectionsDialog.setView(factory.inflate(R.layout.sections_popup, null, false));
			sectionsDialog.setPositiveButton("Ok", null);
			sectionsDialog.show();
			break;
		case R.id.quit_menu:
			AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
			quitDialog.setTitle("Quit");
			//break;
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * This is a class that handle the notifications of the webview
	 * I want the manpages url to open within the webview, and external urls to open in the stock android browser.
	 * @author chmod0
	 *
	 */
	private class MyWebViewClient extends WebViewClient{
		
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			try{				
					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri request = Uri.parse(url);
			        intent.setData(request);
			        // Let Android start its stock browser
			        startActivity(intent);
					return true;
					
			} catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
	}
}
