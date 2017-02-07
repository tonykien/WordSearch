package com.tonyk.ws.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tonyk.ws.Constants;
import com.tonyk.ws.R;
import com.tonyk.ws.custom.RoundedImageView;

public class ChooseLevelActivity extends BaseActivity {

	public static final String[] LEVEL_TITLE = new String[] { "FRUIT", "BODYPART", "ANIMAL",
			"BIRD", "INSECT", "BATHROOM" };

	public static final String PREF_NAME = "word_search_pref";
	public static final String KEY_LEVEL = "level";
	public static final String KEY_MAX_LEVEL = "max_level";

	public static final int[] sLevelIconRes = new int[] { R.drawable.level1_icon, R.drawable.level2_icon,
			R.drawable.level3_icon, R.drawable.level4_icon, R.drawable.level5_icon, R.drawable.level6_icon,
			R.drawable.level7_icon, R.drawable.level8_icon, R.drawable.level9_icon, R.drawable.level10_icon,
			R.drawable.level11_icon, R.drawable.level12_icon, R.drawable.level13_icon };

	private int mMaxLevel;
	private int mPosition;
	
	private GridView mGvLevel;
	private GvLevelAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_level);
		setupHeader();
		
		final Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
		scaleAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Intent i = new Intent(ChooseLevelActivity.this, MainActivity.class);
				i.putExtra(KEY_LEVEL, mPosition);
				startActivity(i);

			}
		});

		mGvLevel = (GridView) findViewById(R.id.gvLevel);
		mAdapter = new GvLevelAdapter(this);
		mGvLevel.setAdapter(mAdapter);
		mGvLevel.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				// if (position <= mMaxLevel) {
				// mPosition = position;
				// view.startAnimation(scaleAnim);
				// }

				// TODO Auto-generated method stub
				if (position <= mMaxLevel) {
					Intent intent = new Intent(ChooseLevelActivity.this, MainActivity.class);
					intent.putExtra(KEY_LEVEL, position);
					// startActivity(intent);

					int[] screenLocation = new int[2];
					view.getLocationOnScreen(screenLocation);
					// PictureData info = mPicturesData.get(v);
					// Intent subActivity = new Intent(ActivityAnimations.this,
					// PictureDetailsActivity.class);
					int orientation = getResources().getConfiguration().orientation;
					intent.putExtra(MainActivity.PACKAGE + ".orientation", orientation)
							.putExtra(MainActivity.PACKAGE + ".resourceId", sLevelIconRes[position])
							.putExtra(MainActivity.PACKAGE + ".left", screenLocation[0])
							.putExtra(MainActivity.PACKAGE + ".top", screenLocation[1])
							.putExtra(MainActivity.PACKAGE + ".width", view.getWidth())
							.putExtra(MainActivity.PACKAGE + ".height", view.getHeight());
					// putExtra(PACKAGE + ".description", info.description);
					startActivity(intent);

					Log.i("startActivity", "resourceId:" + sLevelIconRes[position] + " left:" + screenLocation[0] 
							+ " Top:" + screenLocation[1] + " width:" + view.getWidth() + " height:" + view.getHeight());
					
					// Override transitions: we don't want the normal window
					// animation in addition
					// to our custom one
					overridePendingTransition(0, 0);
				}

			}
		});

	}

	@Override
	protected void onResume() {
		SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		mMaxLevel = pref.getInt(KEY_MAX_LEVEL, 0);
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	
	private void setupHeader() {
		ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.choose_level);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	        if (mGvLevel != null) {
				mGvLevel.setNumColumns(4);
			}
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	        if (mGvLevel != null) {
				mGvLevel.setNumColumns(3);
			}
	    }
		
		super.onConfigurationChanged(newConfig);
	}

	private class GvLevelAdapter extends BaseAdapter {

		private Context context;

		public GvLevelAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return sLevelIconRes.length;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_level_gridview, parent, false);

			final RoundedImageView ivLevelIcon = (RoundedImageView) convertView
					.findViewById(R.id.ivLevelIcon);
			ivLevelIcon.setImageResource(sLevelIconRes[position]);
			RoundedImageView ivLocker = (RoundedImageView) convertView.findViewById(R.id.ivLocker);
			if (position <= mMaxLevel) {
				ivLocker.setVisibility(View.GONE);
			}
			
			return convertView;
		}

	}
}
