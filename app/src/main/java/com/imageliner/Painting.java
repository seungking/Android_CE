package com.imageliner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import top.defaults.colorpicker.ColorPickerPopup;

import static com.imageliner.ShopActivity.BitmapToString;

public class Painting extends AppCompatActivity implements View.OnClickListener
{
    static DrawingView mDrawingView;
	static ImageView mFillBackgroundImageView;
	static ImageView mColorImageView;
	static ImageView mStrokeImageView;
	static ImageView mUndoImageView;
	static ImageView mRedoImageView;
	static ImageView back;
	static ImageView next;
	static int weight =1;
	static int height =1;

    private static int mCurrentBackgroundColor;
    private static int mCurrentColor;
    private static int mCurrentStroke;
    private static final int MAX_STROKE_WIDTH = 50;
	private boolean extractingColor = false;

	Bitmap image_out;
	ImageView BigImage;

	public static float h=0;
	public static float w=0;
	public static int x=0;
	public static int y=0;

	private InterstitialAd mInterstitialAd;

	public static void startWithBitmap(@NonNull Context context, @NonNull Bitmap bitmap) {
		Intent intent = new Intent(context, Painting.class);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);




//        intent.putExtra("bitmap",BitmapToString(bitmap));
		intent.setData(Uri.parse(path));
		context.startActivity(intent);
		Log.d("LOG1", "startwithbitmap");
	}
//    public static void startWithBitmap(@NonNull Context context, @NonNull String bitmap) {
//        Intent intent = new Intent(context, Painting.class);
////        intent.setData(Uri.parse(bitmap));
//        intent.putExtra("bitmap",bitmap);
//        context.startActivity(intent);
//        Log.d("LOG1", "startwithbitmap");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting);

        ArrayList<String> noad  = getStringArrayPref(this,"noad");
        if (noad.size()==0) {
            Log.d("LOG1", "Painting1");
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-1992325656759505/3524605711");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }

//        if(getIntent().hasExtra("bitmap")) {
//            byte[] getByte = getIntent().getByteArrayExtra("bitmap");
//            image_out = BitmapFactory.decodeByteArray( getByte, 0, getByte.length ) ;
//            }

		Uri uri = getIntent().getData();


		try {
			image_out = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d("LOG1", "path : " + uri.getPath());
		Log.d("LOG1", "path : " + getFilePath(uri));
//		File file = new File(getFilePath(uri));
		File file = new File(uri.getPath());
		if(file.exists()) {
			boolean isDelete = file.delete();
			Log.d("LOG1", "path1");
			if(isDelete) Log.e("file delete ?", String.valueOf(isDelete));
		}
		else{
			Log.d("LOG1", "path2");
		}


		Log.d("LOG1", "Painting2");
        mDrawingView = (DrawingView) findViewById(R.id.main_drawing_view);
        mFillBackgroundImageView = (ImageView)findViewById(R.id.main_fill_iv);
        mColorImageView = (ImageView)findViewById(R.id.main_color_iv);
        mStrokeImageView = (ImageView)findViewById(R.id.main_stroke_iv);
        mUndoImageView = (ImageView)findViewById(R.id.main_undo_iv);
        mRedoImageView = (ImageView)findViewById(R.id.main_redo_iv);
		back = (ImageView)findViewById(R.id.painting_back);
		next = (ImageView)findViewById(R.id.painting_next);

        mDrawingView.setOnClickListener(this);
        mFillBackgroundImageView.setOnClickListener(this);
        mColorImageView.setOnClickListener(this);
        mStrokeImageView.setOnClickListener(this);
        mUndoImageView.setOnClickListener(this);
        mRedoImageView.setOnClickListener(this);
        back.setOnClickListener(this);
        next.setOnClickListener(this);

		ButterKnife.bind(this);

		initDrawingView();
    }

	@Override
	protected void onStart() {
		super.onStart();
        ArrayList<String> noad  = getStringArrayPref(this,"noad");
        if (noad.size()==0) {
		if(!mInterstitialAd.isLoaded()) mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
		showUnsupportedSnackBar();
	}

	private void showUnsupportedSnackBar() {
		Snackbar.make(mDrawingView,"Tap anywhere to start", Snackbar.LENGTH_SHORT).show();
	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
	    weight =  mDrawingView.getWidth();
	    height = mDrawingView.getHeight();
        Log.d("LOG1", String.valueOf(mDrawingView.getWidth()));
        Log.d("LOG1", String.valueOf(mDrawingView.getHeight()));
        mDrawingView.setwh(weight,height);
        mDrawingView.initPaintwithbitmap(image_out);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_share:
//				requestPermissionsAndSaveBitmap();
				break;
			case R.id.action_clear:
				new AlertDialog.Builder(this)
						.setTitle("Clear canvas")
						.setMessage("Are you sure you want to clear the canvas?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mDrawingView.clearCanvas();
							}
						})
						.setNegativeButton("Cancel", null)
						.show();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void initDrawingView()
	{
        Log.d("LOG1", "Painting initdrawingview");
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int wwidth = size.x;
		int wheight = size.y;

		mDrawingView.setwh(wwidth,(wheight*77)/100);
		mDrawingView.initPaintwithbitmap(image_out);
		mCurrentBackgroundColor = ContextCompat.getColor(this, android.R.color.white);
		mCurrentColor = ContextCompat.getColor(this, android.R.color.black);
		mCurrentStroke = 10;
	}

	private void startStrokeSelectorDialog()
	{
		StrokeSelectorDialog dialog = StrokeSelectorDialog.newInstance(mCurrentStroke, MAX_STROKE_WIDTH);

		dialog.setOnStrokeSelectedListener(new StrokeSelectorDialog.OnStrokeSelectedListener()
		{
			@Override
			public void onStrokeSelected(int stroke)
			{
				mCurrentStroke = stroke;
				mDrawingView.setPaintStrokeWidth(mCurrentStroke);
			}
		});

		dialog.show(getSupportFragmentManager(), "StrokeSelectorDialog");
	}

    private void startShareDialog(Uri uri)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }

//	private void requestPermissionsAndSaveBitmap()
//	{
//		if (PermissionManager.checkWriteStoragePermissions(this))
//		{
//			Uri uri = FileManager.saveBitmap(this, mDrawingView.getBitmap());
//			startShareDialog(uri);
//		}
//	}
//
//	@Override
//	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
//	{
//		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//		switch (requestCode)
//		{
//			case PermissionManager.REQUEST_WRITE_STORAGE:
//			{
//				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//				{
//					Uri uri = FileManager.saveBitmap(this, mDrawingView.getBitmap());
//					startShareDialog(uri);
//				} else
//				{
//				}
//			}
//		}
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.painting_back:
				finish();
                ArrayList<String> noad  = getStringArrayPref(this,"noad");
                if (noad.size()==0) {
                    if (mInterstitialAd.isLoaded()) { //광고가 로드 되었을 시
                        mInterstitialAd.show(); //보여준다
                    } else mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
				break;
			case R.id.painting_next:
//				requestPermissionsAndSaveBitmap();
                Log.d("LOG1","W : " +String.valueOf(w));
                Log.d("LOG1","H : " +String.valueOf(h));
                Log.d("LOG1","X : " +String.valueOf(x));
                Log.d("LOG1","Y : " +String.valueOf(y));
				mDrawingView.setDrawingCacheEnabled(true);
				mDrawingView.buildDrawingCache();
				Bitmap drawingCache = mDrawingView.getDrawingCache();
				drawingCache = Bitmap.createBitmap(drawingCache,x,y,(int)w,(int)h);

				ArrayList<Item> data =  new ArrayList<Item>();
				ArrayList<String> titles = getStringArrayPref(Painting.this,"titles");
				ArrayList<String> dates = getStringArrayPref(Painting.this,"dates");
				ArrayList<String> images = getStringArrayPref(Painting.this,"images");
				ArrayList<String> simages = getStringArrayPref(Painting.this,"simages");

                String imagetitle = "Untitled";
                int index=1;
                while(true){
                    Log.d("LOG1",imagetitle+String.valueOf(index));
                    if(titles.indexOf(imagetitle+String.valueOf(index))==-1) {
                        imagetitle=imagetitle+String.valueOf(index);
                        Log.d("LOG1","titles1");
                        break;
                    }
                    else  index++;
                    Log.d("LOG1","titles2");
                }
                titles.add(imagetitle);
				dates.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
				images.add(BitmapToString(drawingCache));
				simages.add(BitmapToString(Bitmap.createScaledBitmap(drawingCache, (int) drawingCache.getWidth()/2, (int) drawingCache.getHeight()/2, true)));

				setStringArrayPref(Painting.this,"titles",titles);
				setStringArrayPref(Painting.this,"dates",dates);
				setStringArrayPref(Painting.this,"images",images);
				setStringArrayPref(Painting.this,"simages",simages);
				finish();

                noad  = getStringArrayPref(this,"noad");
                if (noad.size()==0) {
                    if (mInterstitialAd.isLoaded()) { //광고가 로드 되었을 시
                        mInterstitialAd.show(); //보여준다
                    } else mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
//				SampleActivity.startWithBitmap(Painting.this,drawingCache);
				break;
			case R.id.main_fill_iv:
				Log.d("LOG1", "fill");
				new ColorPickerPopup.Builder(this)
						.initialColor(Color.RED) // Set initial color
						.enableBrightness(true) // Enable brightness slider or not
						.enableAlpha(false) // Enable alpha slider or not
						.okTitle("Choose")
						.cancelTitle("Cancel")
						.showIndicator(true)
						.showIndicator(true)
						.showValue(false)
						.build()
						.show(v, new ColorPickerPopup.ColorPickerObserver() {
							@Override
							public void onColorPicked(int color) {
								mCurrentColor = color;
								mDrawingView.setPaintColor(mCurrentColor);
								mFillBackgroundImageView.setBackgroundColor(color);
							}
						});
				break;
			case R.id.main_color_iv:
				Snackbar.make(mDrawingView,"Tap anywhere to extract a color", Snackbar.LENGTH_SHORT).show();
				mDrawingView.extractingColor();
				break;
			case R.id.main_stroke_iv:
				Log.d("LOG1", "stroke");
				startStrokeSelectorDialog();
				break;
			case R.id.main_undo_iv:
				Log.d("LOG1", "undo");
				mDrawingView.undo();
				break;
			case R.id.main_redo_iv:
				Log.d("LOG1", "redo");
				mDrawingView.redo();
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		LinePath linePath;
		Log.d("LOG1", "extractingcolor1");
		int index;
		int id;
		int eventMasked = event.getActionMasked();
		switch (eventMasked) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN: {
				index = event.getActionIndex();
				id = event.getPointerId(index);

				if (extractingColor) { //If the user chose the 'extract color' menu option, the touch event indicates where they want to extract the color from.
					extractingColor = false;

					Log.d("LOG1", "extractingcolor");

					View v = findViewById(R.id.main_drawing_view);
					v.setDrawingCacheEnabled(true);
					Bitmap cachedBitmap = v.getDrawingCache();

					int newColor = cachedBitmap.getPixel(Math.round(event.getX(index)), Math.round(event.getY(index)));

					v.destroyDrawingCache();
//					colorChanged( newColor );
					mCurrentColor = newColor;
					mDrawingView.setPaintColor(mCurrentColor);
					mFillBackgroundImageView.setBackgroundColor(newColor);

					Toast.makeText(getApplicationContext(),
							R.string.color_extracted,
							Toast.LENGTH_SHORT).show();

				} else {

//					linePath = multiLinePathManager.addLinePathWithPointer( id );
//					if( linePath != null ) {
//						linePath.touchStart( event.getX( index ), event.getY( index ) );
//					} else {
//						Log.e( "anupam", "Too many fingers!" );
//					}
				}

				break;
			}
		}
		return true;
	}

	public static void aaa(float x, float y){
			Log.d("LOG1", "extractingcolor2");

			View v = mDrawingView.findViewById(R.id.main_drawing_view);
			v.setDrawingCacheEnabled(true);
			Bitmap cachedBitmap = v.getDrawingCache();

			int newColor = cachedBitmap.getPixel(Math.round(x), Math.round(y));

			v.destroyDrawingCache();
			mCurrentColor = newColor;
			mDrawingView.setPaintColor(mCurrentColor);
			mFillBackgroundImageView.setBackgroundColor(newColor);
	}

	public static void setbitmapsize(float a, float b, int c, int d){
        Log.d("LOG1", "setbitmapsize");
		w = a;
		h = b;
		x = c;
		y = d;
        Log.d("LOG1",String.valueOf(w));
        Log.d("LOG1",String.valueOf(h));
        Log.d("LOG1",String.valueOf(x));
        Log.d("LOG1",String.valueOf(y));
	}

	private ArrayList<String> getStringArrayPref(Context context, String str) {
		String string = PreferenceManager.getDefaultSharedPreferences(context).getString(str, null);
		ArrayList arrayList = new ArrayList();
		if (string != null) {
			try {
				JSONArray jSONArray = new JSONArray(string);
				for (int i = 0; i < jSONArray.length(); i++) {
					arrayList.add(jSONArray.optString(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return arrayList;
	}

	public void setStringArrayPref(Context context, String str, ArrayList<String> arrayList) {
		SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		JSONArray jSONArray = new JSONArray();
		for (int i = 0; i < arrayList.size(); i++) {
			jSONArray.put(arrayList.get(i));
		}
		if (arrayList.isEmpty()) {
			edit.putString(str, null);
		} else {
			edit.putString(str, jSONArray.toString());
		}
		edit.apply();
	}

    public static Bitmap StringToBitmap(String str) {
        try {
            byte[] decode = Base64.decode(str, 0);
            return BitmapFactory.decodeByteArray(decode, 0, decode.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

	private String getFilePath(Uri uri) {
		String[] projection = {MediaStore.Images.Media.DATA};

		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(projection[0]);
			String picturePath = cursor.getString(columnIndex); // returns null
			cursor.close();
			return picturePath;
		}
		return null;
	}

	private Uri getImageUri(Context context, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

}
