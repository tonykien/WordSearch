package com.tonyk.ws;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.tonyk.ws.custom.RoundedImageView;

public class ChooseLevelActivity extends Activity {

	public static final String[] LEVEL_TITLE = new String[] { "FRUIT", "BODYPART", "ANIMAL", "BIRD",
		"INSECT", "BATHROOM"};

	public static final String PREF_NAME = "word_search_pref";
	public static final String KEY_LEVEL = "level";
	public static final String KEY_MAX_LEVEL = "max_level";

	private int[] mLevelIconRes = new int[] { R.drawable.level1_icon, R.drawable.level2_icon,
			R.drawable.level3_icon, R.drawable.level4_icon, R.drawable.level5_icon, R.drawable.level6_icon };

	private int mMaxLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_level);

		GridView gvLevel = (GridView) findViewById(R.id.gvLevel);
		gvLevel.setAdapter(new GvLevelAdapter(this));
		gvLevel.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (position <= mMaxLevel) {
					Intent i = new Intent(ChooseLevelActivity.this, MainActivity.class);
					i.putExtra(KEY_LEVEL, position);
					startActivity(i);
				}

			}
		});

	}

	@Override
	protected void onResume() {
		SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		mMaxLevel = pref.getInt(KEY_MAX_LEVEL, 0);
		super.onResume();
	}

	private class GvLevelAdapter extends BaseAdapter {

		private Context context;

		public GvLevelAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return mLevelIconRes.length;
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

			RoundedImageView ivLevelIcon = (RoundedImageView) convertView
					.findViewById(R.id.ivLevelIcon);
			ivLevelIcon.setImageResource(mLevelIconRes[position]);
			RoundedImageView ivLocker = (RoundedImageView) convertView.findViewById(R.id.ivLocker);
			if (position <= mMaxLevel) {
				ivLocker.setVisibility(View.GONE);
			}

			return convertView;
		}

	}
}
