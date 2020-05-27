package com.imageliner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;
import top.defaults.colorpicker.ColorPickerPopup;

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

    private static int mCurrentBackgroundColor;
    private static int mCurrentColor;
    private static int mCurrentStroke;
    private static final int MAX_STROKE_WIDTH = 50;
	private boolean extractingColor = false;

	Bitmap image_out;
	ImageView BigImage;

	private InterstitialAd mInterstitialAd;

	public static void startWithBitmap(@NonNull Context context, @NonNull Bitmap bitmap) {
		Intent intent = new Intent(context, Painting.class);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
		intent.setData(Uri.parse(path));
		context.startActivity(intent);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting);

		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
		mInterstitialAd.loadAd(new AdRequest.Builder().build());

		Uri uri = getIntent().getData();
		if (uri != null) {
			try {
				Log.d("LOG1", "type1 1");
				image_out = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			} catch (Exception e) {

			}
		}

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
//		mDrawingView.setBackground(getResources().getDrawable(R.drawable.welcom));
//		mDrawingView.setForeground(getResources().getDrawable(R.drawable.welcom));

//        mDrawingView.setBackground(getResources().getDrawable(R.drawable.welcom));
//        mDrawingView.setForeground(getResources().getDrawable(R.drawable.welcom));

		ButterKnife.bind(this);

		initDrawingView();
    }

	@Override
	protected void onStart() {
		super.onStart();
		if (!mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
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
		mCurrentBackgroundColor = ContextCompat.getColor(this, android.R.color.white);
		mCurrentColor = ContextCompat.getColor(this, android.R.color.black);
		mCurrentStroke = 10;

		mDrawingView.initPaintwithbitmap(image_out);
		if (!mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
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
//					Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
//				}
//			}
//		}
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.painting_back:
				finish();
				break;
			case R.id.painting_next:

				if (mInterstitialAd.isLoaded()) {
					mInterstitialAd.show();
				} else {
					Log.d("TAG", "The interstitial wasn't loaded yet.");
				}
				finish();
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
				Toast.makeText(getApplicationContext(),
						R.string.tap_to_extract_color,
						Toast.LENGTH_LONG).show();
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

	private Uri getImageUri(Context context, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
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
}
