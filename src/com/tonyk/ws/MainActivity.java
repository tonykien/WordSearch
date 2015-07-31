package com.tonyk.ws;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tonyk.ws.custom.FlowLayout;
import com.tonyk.ws.custom.StrokeView;

public class MainActivity extends Activity implements OnTouchListener {

	public static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVXYZW";

	public static final class Direction {
		public static final int N = 0;
		public static final int NE = 1;
		public static final int E = 2;
		public static final int SE = 3;
		public static final int S = 4;
		public static final int SW = 5;
		public static final int W = 6;
		public static final int NW = 7;
	}

	private String[] mListWords = new String[] { "HOANGVUNAM", "TRUNGKIEN", "MOBIPHONE",
			"PANASONIC", "MOTOROLA", "TOSHIBA", "SAMSUNG", "ANDROID", "SUZUKI", "TOYOTA", "DAIKIN",
			"HAIYEN", "BPHONE", "GOOGLE", "HONDA", "APPLE", "SHARP", "NOKIA", "DREAM", "SONY" };
	String ANIMAL = "BUTTERFLY,ABALONE,PEACOCK,RABBIT,CANARY,DONKEY,PIGEON,SPIDER,GIBBON,EAGLE,PANDA,SHARK,SNAKE,TIGER,WOLF,DUCK,SWAN,CRAB,LION,PUMA,ANT,BEE,FOX,PIG,DOG,CAT";
	String BODY = "SHOULDER,STOMACH,FINGER,TONGUE,ANKLE,CHEST,WAIST,MOUTH,CHEEK,ELBOW,THIGH,THUMB,BRAIN,HAND,NECK,BACK,CHIN,HAIR,FOOT,KNEE,FACE,NOSE,HIP,ARM,LEG,EAR,LIP,EYE";
	String FRUIT = "STRAWBERRY,WATERMELON,AVOCADO,COCONUT,PEANUT,PAPAYA,ORANGE,BANANA,DURIAN,CHERRY,LONGAN,GRAPE,APPLE,MANGO,LEMON,PEACH,GUAVA,PEAR,PLUM,LIME,KIWI,FIG";

	public static final String[] LEVEL_WORD = new String[] {
			"STRAWBERRY,WATERMELON,AVOCADO,COCONUT,PEANUT,PAPAYA,ORANGE,BANANA,DURIAN,CHERRY,LONGAN,GRAPE,APPLE,MANGO,LEMON,PEACH,GUAVA,PEAR,PLUM,LIME,KIWI,FIG",
			"SHOULDER,STOMACH,FINGER,TONGUE,ANKLE,CHEST,WAIST,MOUTH,CHEEK,ELBOW,THIGH,THUMB,BRAIN,HAND,NECK,BACK,CHIN,HAIR,FOOT,KNEE,FACE,NOSE,HIP,ARM,LEG,EAR,LIP,EYE",
			"BUTTERFLY,ABALONE,PEACOCK,RABBIT,CANARY,DONKEY,PIGEON,SPIDER,GIBBON,EAGLE,PANDA,SHARK,SNAKE,TIGER,WOLF,DUCK,SWAN,CRAB,LION,PUMA,ANT,BEE,FOX,PIG,DOG,CAT",
			"SPARROW,VULTURE,FEATHER,OSTRICH,PENGUIN,TURKEY,PIGEON,FALCON,PARROT,TALON,CRANE,HERON,STORK,EAGLE,GOOSE,CROW,DOVE,NEST,DUCK,SWAN,OWL",
			"CENTIPEDE,COCKROACH,PARASITE,MOSQUITO,LADYBUG,SCORPION,CRICKET,SPIDER,BEETLE,SNAIL,SWARM,FLEA,WORM,MOTH,WASP,ANT,FLY,BEE"};

	private ArrayList<String> mFindoutWords = new ArrayList<String>();

	private ArrayList<Cell> mListCells = new ArrayList<Cell>();
	private ArrayList<Integer> mAvaiableDirection = new ArrayList<Integer>();

	public int SIZE_X = 8;
	public int SIZE_Y = 8;

	public int TIME_COUNT = 120;
	private CountDownTimer mCountDownTimer;
	private ProgressBar mProgressBar;
	private long mRunningTime;

	private GridView mGridLetter;
	private GvAdapter mAdapter;

	private StrokeView mStrokeView;
	private FlowLayout mFlowLayout;

	private int mLevel = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("LETTER", "oncreate");

		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		mGridLetter = (GridView) findViewById(R.id.gvLetter);
		mGridLetter.setNumColumns(SIZE_X);

		mAdapter = new GvAdapter(this, mListCells);
		mGridLetter.setAdapter(mAdapter);

		mStrokeView = (StrokeView) findViewById(R.id.strokeview);
		mStrokeView.setOnTouchListener(this);

		mFlowLayout = (FlowLayout) findViewById(R.id.flowLayout);

		mLevel = getIntent().getIntExtra(ChooseLevelActivity.KEY_LEVEL, 0);
		initListWord(LEVEL_WORD[mLevel]);

	}

	@Override
	protected void onResume() {
		if (mCountDownTimer != null && mRunningTime > 0) {
			mCountDownTimer = new CountDownTimer(mRunningTime, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					Log.i("Log_tag", "Tick of Progress" + millisUntilFinished);
					mRunningTime = millisUntilFinished;
					mProgressBar.setProgress((int) (millisUntilFinished / 1000));
				}

				@Override
				public void onFinish() {
					mRunningTime = 0;
					mProgressBar.setProgress(0);
				}
			};
			mCountDownTimer.start();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		super.onPause();
	}

	public void initListWord(String listWord) {
		mListCells.clear();
		mFlowLayout.removeAllViews();
		mStrokeView.reset();
		mFindoutWords.clear();

		// listWord = "LEVEL6,BATHROOM";
		mListWords = listWord.split(",");
		TIME_COUNT = mListWords.length * 10;

		float density = getResources().getDisplayMetrics().density;
		for (int i = 0; i < mListWords.length; i++) {
			TextView tvWord = new TextView(this);
			tvWord.setText(mListWords[i]);
			tvWord.setTextColor(Color.WHITE);
			tvWord.setGravity(Gravity.CENTER_HORIZONTAL);
			tvWord.setPadding((int) (8 * density), (int) (4 * density), (int) (8 * density),
					(int) (4 * density));
			mFlowLayout.addView(tvWord);
		}

		// init cells
		for (int i = 0; i < SIZE_X * SIZE_Y; i++) {
			Cell cell = new Cell(i % SIZE_X, i / SIZE_X);
			mListCells.add(cell);
		}

		fillCellAsyncTask();

		// fill all empty cell
//		Random r = new Random();
//		for (Cell cell : mListCells) {
//			if (!cell.isFilled()) {
//				char letter = (char) (r.nextInt(26) + 'A');
//				cell.setLetter(letter);
//			}
//		}
	}

	public void fillCellAsyncTask() {
		new AsyncTask<Void, Void, Void>() {
			private ProgressDialog dialog;

			protected void onPreExecute() {
				dialog = new ProgressDialog(MainActivity.this);
				dialog.setMessage("Creating");
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			};

			@Override
			protected Void doInBackground(Void... params) {
				boolean fillSuccess = autoFillCell();
				while (!fillSuccess) {
					fillSuccess = autoFillCell();
				}
				// fill all empty cell
				Random r = new Random();
				for (Cell cell : mListCells) {
					if (!cell.isFilled()) {
						char letter = (char) (r.nextInt(26) + 'A');
						cell.setLetter(letter);
					}
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				dialog.dismiss();
				mAdapter.notifyDataSetChanged();

				mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
				mProgressBar.setMax(TIME_COUNT);
				mProgressBar.setProgress(mProgressBar.getMax());
				mCountDownTimer = new CountDownTimer(TIME_COUNT * 1000, 1000) {

					@Override
					public void onTick(long millisUntilFinished) {
						Log.i("Log_tag", "Tick of Progress" + millisUntilFinished);
						mRunningTime = millisUntilFinished;
						mProgressBar.setProgress((int) (millisUntilFinished / 1000));
					}

					@Override
					public void onFinish() {
						mRunningTime = 0;
						mProgressBar.setProgress(0);
						// TODO timeout
					}
				};
				mCountDownTimer.start();

			};

		}.execute();
	}

	public void onBtnRandomClick(View v) {
		for (int i = 0; i < mListWords.length; i++) {
			((TextView) mFlowLayout.getChildAt(i)).setTextColor(Color.BLACK);
		}

		mCountDownTimer.cancel();
		mStrokeView.reset();
		// mListCells.clear();
		// for (int i = 0; i < SIZE_X * SIZE_Y; i++) {
		// Cell cell = new Cell(i % SIZE_X, i / SIZE_X);
		// mListCells.add(cell);
		// }
		for (Cell cell : mListCells) {
			cell.setLetter('\u0000');
			cell.setFilled(false);
		}

		fillCellAsyncTask();

		// adapter.notifyDataSetChanged();
	}

	boolean isCreating = false;

	public boolean autoFillCell() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				isCreating = false;
			}
		}, 600);
		isCreating = true;
		for (int i = 0; i < mListWords.length; i++) {
			String word = mListWords[i];
			int startPos = getStartPosition(word);
			checkDirection(word, startPos);
			while (mAvaiableDirection.isEmpty()) {
				startPos = getStartPosition(word);
				checkDirection(word, startPos);
				// Log.e("isCreating", isCreating + "");
				if (!isCreating) {
					Log.i("not", "not success");
					for (Cell cell : mListCells) {
						cell.setLetter('\u0000');
						cell.setFilled(false);
					}
					return false;
				}
			}

			int direct = getDirection();
			setCellByDirect(direct, word, startPos);
		}
		return true;
	}

	public int getStartPosition(String word) {
		mAvaiableDirection.clear();
		Random r = new Random();
		int x, y;
		Cell cell;
		int offsetX = SIZE_X - word.length();
		int offsetY = SIZE_Y - word.length();
		do {
			x = r.nextInt(SIZE_X);
			y = r.nextInt(SIZE_Y);
			cell = mListCells.get(x + y * SIZE_X);
		} while ((offsetX < x && x < SIZE_X - 1 - offsetX && offsetY < y && y < SIZE_Y - 1
				- offsetY)
				|| (cell.isFilled() && cell.getLetter() != word.charAt(0)));

		if (x <= SIZE_X - word.length() && y <= SIZE_Y - word.length()) {
			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.S))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.S));
			}

			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.SE))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.SE));
			}

			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.E))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.E));
			}
		}

		if (x <= SIZE_X - word.length() && y >= word.length()) {
			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.N))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.N));
			}

			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.NE))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.NE));
			}

			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.E))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.E));
			}
		}

		if (x >= word.length() && y >= word.length()) {
			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.N))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.N));
			}

			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.NW))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.NW));
			}

			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.W))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.W));
			}
		}

		if (x >= word.length() && y <= SIZE_Y - word.length()) {
			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.S))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.S));
			}

			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.SW))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.SW));
			}

			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.W))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.W));
			}
		}

		if (x <= SIZE_X - word.length()) {
			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.E))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.E));
			}
		}

		if (x >= word.length()) {
			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.W))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.W));
			}
		}

		if (y <= SIZE_Y - word.length()) {
			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.S))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.S));
			}
		}

		if (y >= word.length()) {
			if (!mAvaiableDirection.contains(Integer.valueOf(Direction.N))) {
				mAvaiableDirection.add(Integer.valueOf(Direction.N));
			}
		}

		return x + y * SIZE_X;
	}

	public int getDirection() {
		Random r = new Random();
		return mAvaiableDirection.get(r.nextInt(mAvaiableDirection.size())).intValue();
	}

	public void checkDirection(String word, int startPos) {
		for (int i = mAvaiableDirection.size() - 1; i >= 0; i--) {
			switch (mAvaiableDirection.get(i).intValue()) {
			case Direction.E:
				if (!checkDirectionEast(word, startPos)) {
					mAvaiableDirection.remove(i);
				}
				break;
			case Direction.N:
				if (!checkDirectionNorth(word, startPos)) {
					mAvaiableDirection.remove(i);
				}
				break;
			case Direction.NE:
				if (!checkDirectionNorthEast(word, startPos)) {
					mAvaiableDirection.remove(i);
				}
				break;
			case Direction.NW:
				if (!checkDirectionNorthWest(word, startPos)) {
					mAvaiableDirection.remove(i);
				}
				break;
			case Direction.S:
				if (!checkDirectionSouth(word, startPos)) {
					mAvaiableDirection.remove(i);
				}
				break;
			case Direction.SE:
				if (!checkDirectionSouthEast(word, startPos)) {
					mAvaiableDirection.remove(i);
				}
				break;
			case Direction.SW:
				if (!checkDirectionSouthWest(word, startPos)) {
					mAvaiableDirection.remove(i);
				}
				break;
			case Direction.W:
				if (!checkDirectionWest(word, startPos)) {
					mAvaiableDirection.remove(i);
				}
				break;

			default:
				break;
			}
		}
	}

	public boolean checkDirectionNorth(String word, int startPos) {
		int pos;
		Cell cell;
		for (int i = 0; i < word.length(); i++) {
			pos = startPos - SIZE_X * i;
			cell = mListCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkDirectionSouth(String word, int startPos) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos + SIZE_X * i;
			Cell cell = mListCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkDirectionEast(String word, int startPos) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos + i;
			Cell cell = mListCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkDirectionWest(String word, int startPos) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos - i;
			Cell cell = mListCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkDirectionNorthEast(String word, int startPos) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos - SIZE_X * i + i;
			Cell cell = mListCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkDirectionSouthEast(String word, int startPos) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos + SIZE_X * i + i;
			Cell cell = mListCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkDirectionNorthWest(String word, int startPos) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos - SIZE_X * i - i;
			Cell cell = mListCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean checkDirectionSouthWest(String word, int startPos) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos + SIZE_X * i - i;
			Cell cell = mListCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private void setCellByDirect(int direct, String word, int startPos) {
		int pos;
		Cell cell;
		switch (direct) {
		case Direction.E:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos + i;
				cell = mListCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.N:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos - SIZE_X * i;
				cell = mListCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.NE:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos - SIZE_X * i + i;
				cell = mListCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.NW:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos - SIZE_X * i - i;
				cell = mListCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.S:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos + SIZE_X * i;
				cell = mListCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.SE:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos + SIZE_X * i + i;
				cell = mListCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.SW:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos + SIZE_X * i - i;
				cell = mListCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.W:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos - i;
				cell = mListCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;

		default:
			break;
		}
	}

	public Cell getCellByRowColumn(int row, int column) {
		if (column + row * SIZE_X < mListCells.size()) {
			return mListCells.get(column + row * SIZE_X);
		}
		return null;
	}

	private class GvAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<Cell> cells;

		public GvAdapter(Context context, ArrayList<Cell> cells) {
			this.context = context;
			this.cells = cells;
		}

		@Override
		public int getCount() {
			return cells.size();
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
			convertView = inflater.inflate(R.layout.cell_of_grid, parent, false);

			TextView tvLetter = (TextView) convertView.findViewById(R.id.tvLetter);
			tvLetter.setText(Character.valueOf(cells.get(position).getLetter()).toString());
			return convertView;
		}

	}

	private Cell mStartCell, mEndCell;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final float CELL_SIZE = (float) mStrokeView.getWidth() / SIZE_X;
		mStrokeView.setStrokeWidth(CELL_SIZE * 2 / 3);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			PointF startPoint = new PointF(event.getX(), event.getY());
			mStartCell = getCellByRowColumn((int) (event.getY() / CELL_SIZE),
					(int) (event.getX() / CELL_SIZE));
			mStrokeView.setColorRandom();

			startPoint = adjustStartPoint(startPoint, (mStartCell.getColumn() + 0.5f) * CELL_SIZE,
					(mStartCell.getRow() + 0.5f) * CELL_SIZE, CELL_SIZE / 6);
			mStrokeView.initStartPoint(startPoint.x, startPoint.y);
			mStrokeView.initEndPoint();
			StrokeView.isDrawing = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (StrokeView.isDrawing) {
				mStrokeView.setEndPoint(event.getX(), event.getY());
				mStrokeView.invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (StrokeView.isDrawing) {
				PointF endPoint = new PointF(event.getX(), event.getY());
				mStrokeView.setEndPoint(event.getX(), event.getY());
				StrokeView.isDrawing = false;

				mEndCell = getCellByRowColumn((int) (event.getY() / CELL_SIZE),
						(int) (event.getX() / CELL_SIZE));
				if (mEndCell == null) {
					mStrokeView.invalidate();
					break;
				}

				StringBuilder word = new StringBuilder();
				if (mStartCell.getColumn() == mEndCell.getColumn()) {
					// direction S
					if (mStartCell.getRow() <= mEndCell.getRow()) {
						for (int i = mStartCell.getRow(); i <= mEndCell.getRow(); i++) {
							word.append(mListCells.get(mStartCell.getColumn() + i * SIZE_X)
									.getLetter());
						}
					} else { // direction N
						for (int i = mStartCell.getRow(); i >= mEndCell.getRow(); i--) {
							word.append(mListCells.get(mStartCell.getColumn() + i * SIZE_X)
									.getLetter());
						}
					}

				} else if (mStartCell.getRow() == mEndCell.getRow()) {
					/** Direction E & W */
					if (mStartCell.getColumn() <= mEndCell.getColumn()) {
						for (int i = mStartCell.getColumn(); i <= mEndCell.getColumn(); i++) {
							word.append(mListCells.get(i + mStartCell.getRow() * SIZE_X)
									.getLetter());
						}
					} else {
						for (int i = mStartCell.getColumn(); i >= mEndCell.getColumn(); i--) {
							word.append(mListCells.get(i + mStartCell.getRow() * SIZE_X)
									.getLetter());
						}
					}

				} else if (mStartCell.getRow() - mEndCell.getRow() == mStartCell.getColumn()
						- mEndCell.getColumn()) {
					if (mStartCell.getColumn() <= mEndCell.getColumn()) {
						for (int i = mStartCell.getColumn(); i <= mEndCell.getColumn(); i++) {
							word.append(mListCells
									.get(i + (mStartCell.getRow() + i - mStartCell.getColumn())
											* SIZE_X).getLetter());
						}
					} else {
						for (int i = mStartCell.getColumn(); i >= mEndCell.getColumn(); i--) {
							word.append(mListCells
									.get(i + (mStartCell.getRow() + i - mStartCell.getColumn())
											* SIZE_X).getLetter());
						}
					}

				} else if (mStartCell.getRow() - mEndCell.getRow() == mEndCell.getColumn()
						- mStartCell.getColumn()) {
					if (mStartCell.getColumn() <= mEndCell.getColumn()) {
						for (int i = mStartCell.getColumn(); i <= mEndCell.getColumn(); i++) {
							word.append(mListCells
									.get(i + (mStartCell.getRow() - i + mStartCell.getColumn())
											* SIZE_X).getLetter());
						}
					} else {
						for (int i = mStartCell.getColumn(); i >= mEndCell.getColumn(); i--) {
							word.append(mListCells
									.get(i + (mStartCell.getRow() - i + mStartCell.getColumn())
											* SIZE_X).getLetter());
						}
					}

				}

				// adjust end point
				if (endPoint.y > mEndCell.getRow() * CELL_SIZE + CELL_SIZE * 2 / 3) {
					endPoint.y = mEndCell.getRow() * CELL_SIZE + CELL_SIZE * 2 / 3;
				} else if (endPoint.y < mEndCell.getRow() * CELL_SIZE + CELL_SIZE * 1 / 3) {
					endPoint.y = mEndCell.getRow() * CELL_SIZE + CELL_SIZE * 1 / 3;
				}

				if (endPoint.x > mEndCell.getColumn() * CELL_SIZE + CELL_SIZE * 2 / 3) {
					endPoint.x = mEndCell.getColumn() * CELL_SIZE + CELL_SIZE * 2 / 3;
				} else if (endPoint.x < mEndCell.getColumn() * CELL_SIZE + CELL_SIZE * 1 / 3) {
					endPoint.x = mEndCell.getColumn() * CELL_SIZE + CELL_SIZE * 1 / 3;
				}
				mStrokeView.setEndPoint(endPoint.x, endPoint.y);

				if (word.length() > 0) {
					Toast.makeText(MainActivity.this, word.toString(), Toast.LENGTH_SHORT).show();
					for (int i = 0; i < mListWords.length; i++) {
						if (word.toString().equals(mListWords[i])) {
							mStrokeView.addFixLine();
							((TextView) mFlowLayout.getChildAt(i)).setTextColor(Color
									.parseColor("#66ffffff"));

							if (!mFindoutWords.contains(mListWords[i])) {
								mFindoutWords.add(mListWords[i]);
							}

							if (mFindoutWords.size() == mListWords.length) {
								findAllWords();
							}
							break;
						}
					}
				}

				mStrokeView.invalidate();
			}
			break;
		default:
			break;
		}
		return true;
	}

	private void findAllWords() {
		// TODO
		mCountDownTimer.cancel();

		// save level
		SharedPreferences pref = getSharedPreferences(ChooseLevelActivity.PREF_NAME, MODE_PRIVATE);
		if (mLevel > pref.getInt(ChooseLevelActivity.KEY_MAX_LEVEL, 0)) {
			pref.edit().putInt(ChooseLevelActivity.KEY_MAX_LEVEL, mLevel).commit();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You won!");
		builder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();

				mLevel++;
				initListWord(LEVEL_WORD[mLevel]);
			}
		});

		builder.setNeutralButton(R.string.menu, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				finish();
			}
		});

		builder.setNegativeButton(R.string.replay, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				initListWord(LEVEL_WORD[mLevel]);
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		dialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialogInterface) {
				Button btnNext = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
				Button btnMenu = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
				Button btnReplay = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

				// if you do the following it will be left aligned, doesn't look
				// correct
				// button.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_play,
				// 0, 0, 0);

				Drawable drawableNext = getResources().getDrawable(R.drawable.icon_next);
				Drawable drawableMenu = getResources().getDrawable(R.drawable.icon_menu);
				Drawable drawableReplay = getResources().getDrawable(R.drawable.icon_restart);

				// set the bounds to place the drawable a bit right
				drawableNext.setBounds((int) (drawableNext.getIntrinsicWidth() * 0.3), 0,
						(int) (drawableNext.getIntrinsicWidth() * 1.3),
						drawableNext.getIntrinsicHeight());
				setLeftBounds(btnNext, drawableNext);

				drawableMenu.setBounds((int) (drawableMenu.getIntrinsicWidth() * 1), 0,
						(int) (drawableMenu.getIntrinsicWidth() * 2),
						drawableMenu.getIntrinsicHeight());
				setLeftBounds(btnMenu, drawableMenu);

				drawableReplay.setBounds((int) (drawableReplay.getIntrinsicWidth() * 0.3), 0,
						(int) (drawableReplay.getIntrinsicWidth() * 1.3),
						drawableReplay.getIntrinsicHeight());
				setLeftBounds(btnReplay, drawableReplay);

				// btnNext.setCompoundDrawables(drawableNext, null, null, null);
				// btnReplay.setCompoundDrawables(drawableReplay, null, null,
				// null);
				// btnMenu.setCompoundDrawables(drawableMenu, null, null, null);

				// btnReplay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_restart,
				// 0, 0, 0);
				// centerImageAndTextInButton(btnReplay);

				// btnNext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_next,
				// 0, 0, 0);
				// centerImageAndTextInButton(btnNext);

				// btnMenu.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_menu,
				// 0, 0, 0);
				// centerImageAndTextInButton(btnMenu);

				// Spannable buttonLabel = new SpannableString("");
				// buttonLabel.setSpan(new ImageSpan(getApplicationContext(),
				// R.drawable.icon_menu,
				// ImageSpan.ALIGN_BOTTOM), 1, 1,
				// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// btnMenu.setText(buttonLabel);
				// could modify the placement more here if desired
				// button.setCompoundDrawablePadding();
			}
		});

		dialog.show();
	}

	public void setLeftBounds(Button button, Drawable drawable) {
		Rect textBounds = new Rect();
		// Get text bounds
		CharSequence text = button.getText();
		if (text != null && text.length() > 0) {
			TextPaint textPaint = button.getPaint();
			textPaint.getTextBounds(text.toString(), 0, text.length(), textBounds);
		}

		int boundLeft = (button.getWidth() - drawable.getIntrinsicWidth() - textBounds.width() - (button
				.getPaddingLeft() + button.getPaddingRight())) / 2;
		int boundRight = boundLeft + drawable.getIntrinsicWidth();

		drawable.setBounds(boundLeft, 0, boundRight, drawable.getIntrinsicHeight());
		// button.setText("");
		button.setCompoundDrawables(drawable, null, null, null);
	}

	public static void centerImageAndTextInButton(Button button) {
		final int IMAGE2TEXT = 4; // distance between image and text
		Rect textBounds = new Rect();
		// Get text bounds
		CharSequence text = button.getText();
		if (text != null && text.length() > 0) {
			TextPaint textPaint = button.getPaint();
			textPaint.getTextBounds(text.toString(), 0, text.length(), textBounds);
		}
		Log.i("centerImageAndTextInButton", "" + text.toString() + " width:" + textBounds.width());

		// Set left drawable bounds
		Drawable leftDrawable = button.getCompoundDrawables()[0];
		if (leftDrawable != null) {
			Rect leftBounds = leftDrawable.copyBounds();
			int width = button.getWidth() - (button.getPaddingLeft() + button.getPaddingRight());
			int leftOffset = (width - (textBounds.width() + leftBounds.width()) - button
					.getCompoundDrawablePadding()) / 2 - button.getCompoundDrawablePadding();
			leftBounds.offset(leftOffset, 0);
			leftDrawable.setBounds(leftBounds);
		}
	}

	public PointF adjustStartPoint(PointF startPoint, float centerX, float centerY, float radius) {
		float distance = (float) Math.sqrt((centerX - startPoint.x) * (centerX - startPoint.x)
				+ (centerY - startPoint.y) * (centerY - startPoint.y));
		if (distance > radius) {
			float k = 1 - distance / radius;
			startPoint.x = (startPoint.x - k * centerX) / (1 - k);
			startPoint.y = (startPoint.y - k * centerY) / (1 - k);
		}
		return startPoint;
	}
}
