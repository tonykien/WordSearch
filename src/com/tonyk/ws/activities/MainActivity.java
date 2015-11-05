package com.tonyk.ws.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tonyk.ws.Cell;
import com.tonyk.ws.R;
import com.tonyk.ws.adapters.CellGridviewAdapter;
import com.tonyk.ws.custom.FlowLayout;
import com.tonyk.ws.custom.StrokeView;
import com.tonyk.ws.utils.Define;
import com.tonyk.ws.utils.WSUtil;

public class MainActivity extends BaseActivity implements OnTouchListener, OnClickListener {

	private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
	private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
	private static final int ANIM_DURATION = 500;
	private static final int ANIM_SCALE = 1;
	public static final String PACKAGE = "com.tonyk.ws.activities";
	private static final int LOADING_WAIT_TIME = 500;

	public static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVXYZW";

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
			"CENTIPEDE,COCKROACH,PARASITE,MOSQUITO,LADYBUG,SCORPION,CRICKET,SPIDER,BEETLE,SNAIL,SWARM,FLEA,WORM,MOTH,WASP,ANT,FLY,BEE" };

//	private ArrayList<String> mFindoutWords = new ArrayList<String>();

	private ArrayList<Cell> mListCells = new ArrayList<Cell>();
	private ArrayList<Integer> mAvaiableDirection = new ArrayList<Integer>();

	public int SIZE_X = Define.SIZE_X;
	public int SIZE_Y = Define.SIZE_Y;

	public int TIME_COUNT = 120;
	private CountDownTimer mCountDownTimer;
	private ProgressBar mProgressBar;
	private long mRunningTime;

	private GridView mGridLetter;
	private CellGridviewAdapter mAdapter;

	private StrokeView mStrokeView;
	private FlowLayout mFlowLayout;

	private AdView mAdView;

	private int mLevel = 0;

	private Dialog mCompleteDialog;
	private boolean mIsCreating = false;
	private Cell mStartCell, mEndCell;
	
	private HashMap<String, TextView> mTvWordMap = new HashMap<String, TextView>();

	private int mOriginalOrientation;
	private ImageView mIvLevelIcon;
	private RelativeLayout mTopLayout, mContentLayout;
	private BitmapDrawable mBitmapDrawable;
	private ColorDrawable mBackground;
	int mLeftDelta;
	int mTopDelta;
	float mWidthScale;
	float mHeightScale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("LETTER", "oncreate");

		mAdView = (AdView) findViewById(R.id.adView);

		mGridLetter = (GridView) findViewById(R.id.gvLetter);
		mStrokeView = (StrokeView) findViewById(R.id.strokeview);
		mFlowLayout = (FlowLayout) findViewById(R.id.flowLayout);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

		mGridLetter.setNumColumns(SIZE_X);
		mAdapter = new CellGridviewAdapter(this, mListCells);
		mGridLetter.setAdapter(mAdapter);
		mStrokeView.setOnTouchListener(this);
		mLevel = getIntent().getIntExtra(ChooseLevelActivity.KEY_LEVEL, 0);
		// initListWord(LEVEL_WORD[mLevel]);

		/************/
		mIvLevelIcon = (ImageView) findViewById(R.id.iv_level_icon);
		mTopLayout = (RelativeLayout) findViewById(R.id.topLevelLayout);
		mContentLayout = (RelativeLayout) findViewById(R.id.contentLayout);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
			setupAnimation();
		} else {
			mIvLevelIcon.setVisibility(View.GONE);
			initListWord(LEVEL_WORD[mLevel]);
		}
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
					showDialogTimeOut();
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
//		mFindoutWords.clear();
		mTvWordMap.clear();
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}

		// listWord = "LEVEL6,BATHROOM";
		mListWords = listWord.split(",");
		TIME_COUNT = mListWords.length * 10;

		ArrayList<TextView> listTvWord = new ArrayList<TextView>();
		float density = getResources().getDisplayMetrics().density;
		for (int i = 0; i < mListWords.length; i++) {
			TextView tvWord = new TextView(this);
			tvWord.setText(mListWords[i]);
			tvWord.setTextColor(Color.WHITE);
			tvWord.setGravity(Gravity.CENTER_HORIZONTAL);
			tvWord.setPadding((int) (8 * density), (int) (4 * density), (int) (8 * density),
					(int) (4 * density));
			listTvWord.add(tvWord);
			// mFlowLayout.addView(tvWord);
			mTvWordMap.put(mListWords[i], tvWord);
		}
		Collections.shuffle(listTvWord);
		for (TextView tvWord : listTvWord) {
			mFlowLayout.addView(tvWord);
		}

		// init cells
		for (int i = 0; i < SIZE_X * SIZE_Y; i++) {
			Cell cell = new Cell(i % SIZE_X, i / SIZE_X);
			mListCells.add(cell);
		}

		fillCellAsyncTask(true);
	}

	public void fillCellAsyncTask(final boolean isFirstCreate) {
		new AsyncTask<Void, Void, Void>() {
			private ProgressDialog dialog;
			private long startTime;

			protected void onPreExecute() {
				startTime = System.currentTimeMillis();
				dialog = new ProgressDialog(MainActivity.this);
				dialog.setMessage("Creating");
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				// dialog.show();
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
				// dialog.dismiss();
				mAdapter.notifyDataSetChanged();

				// Check API >= 12
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
					if (isFirstCreate) {
						long executeTime = System.currentTimeMillis() - startTime;
						Log.i("fillCell", "executeTime:" + executeTime);
						if (executeTime < LOADING_WAIT_TIME) {
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									// enterAnimContentLayout();
									exitAnimLevelIcon();
								}
							}, LOADING_WAIT_TIME - executeTime);
						} else {
							// enterAnimContentLayout();
							exitAnimLevelIcon();
						}
					} else {
						initCountDownTime();
					}
				} else {
					/** start count down time */
					initCountDownTime();

					// show adview
					AdRequest adRequest = new AdRequest.Builder().build();
					mAdView.loadAd(adRequest);
				}
			};
		}.execute();
	}

	private void initCountDownTime() {
		mProgressBar.setMax(TIME_COUNT);
		mProgressBar.setProgress(mProgressBar.getMax());
		mCountDownTimer = new CountDownTimer(TIME_COUNT * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				mRunningTime = millisUntilFinished;
				mProgressBar.setProgress((int) (millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
				mRunningTime = 0;
				mProgressBar.setProgress(0);
				showDialogTimeOut();
			}
		};
		mCountDownTimer.start();
	}

	private void exitAnimLevelIcon() {
		// hide imageview level icon
		mIvLevelIcon.animate().setDuration(ANIM_DURATION * 2 / 3).alpha(0).scaleX(0).scaleY(0)
				.translationX(mIvLevelIcon.getWidth() / 2)
				.translationY(mIvLevelIcon.getHeight() / 2).setInterpolator(sAccelerator);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				enterAnimContentLayout();
			}
		}, ANIM_DURATION * 2 / 3 + 20);
	}

	private void enterAnimContentLayout() {
		mContentLayout.setVisibility(View.VISIBLE);
		mContentLayout.setScaleX(0.2f);
		mContentLayout.setScaleY(0.2f);

		mContentLayout.animate().setDuration(ANIM_DURATION).alpha(1).scaleX(1).scaleY(1)
				.setInterpolator(new BounceInterpolator());

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mFlowLayout.setPivotY(0);
				mFlowLayout.setScaleY(0);
				mFlowLayout.setVisibility(View.VISIBLE);
				mFlowLayout.animate().setDuration(ANIM_DURATION / 2).scaleY(1f);
				/** start count down time */
				initCountDownTime();

				// show adview
				AdRequest adRequest = new AdRequest.Builder().build();
				mAdView.loadAd(adRequest);

			}
		}, ANIM_DURATION + 100);
	}

	public void onBtnRandomClick(View v) {
		recreateWordSearch();
	}

	private void recreateWordSearch() {
		for (int i = 0; i < mListWords.length; i++) {
			((TextView) mFlowLayout.getChildAt(i)).setTextColor(Color.WHITE);
		}

		mCountDownTimer.cancel();
		mStrokeView.reset();
		for (Cell cell : mListCells) {
			cell.setLetter('\u0000');
			cell.setFilled(false);
		}

		fillCellAsyncTask(false);
	}

	public boolean autoFillCell() {
		WSUtil.sSizeX = SIZE_X;
		WSUtil.sSizeY = SIZE_Y;
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				mIsCreating = false;
			}
		}, 600);
		mIsCreating = true;
		for (int i = 0; i < mListWords.length; i++) {
			String word = mListWords[i];
			// int startPos = getStartPosition(word);
			// checkDirection(word, startPos);
			int startPos = WSUtil.getStartPosition(word, mListCells, mAvaiableDirection);
			mAvaiableDirection = WSUtil.getAvaiableDirections(word, startPos, mListCells,
					mAvaiableDirection);
			while (mAvaiableDirection.isEmpty()) {
				// startPos = getStartPosition(word);
				// checkDirection(word, startPos);
				startPos = WSUtil.getStartPosition(word, mListCells, mAvaiableDirection);
				mAvaiableDirection = WSUtil.getAvaiableDirections(word, startPos, mListCells,
						mAvaiableDirection);
				// Log.e("isCreating", isCreating + "");
				if (!mIsCreating) {
					Log.i("not", "not success");
					for (Cell cell : mListCells) {
						cell.setLetter('\u0000');
						cell.setFilled(false);
					}
					return false;
				}
			}

			int direct = WSUtil.getRandomDirection(mAvaiableDirection);
			WSUtil.setCellByDirect(direct, word, startPos, mListCells);
		}
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final float CELL_SIZE = (float) mStrokeView.getWidth() / SIZE_X;
		mStrokeView.setStrokeWidth(CELL_SIZE * 2 / 3);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			PointF startPoint = new PointF(event.getX(), event.getY());
			mStartCell = WSUtil.getCellByRowColumn((int) (event.getY() / CELL_SIZE),
					(int) (event.getX() / CELL_SIZE), mListCells);
			mStrokeView.setColorRandom();

			startPoint = WSUtil.adjustStartPoint(startPoint, (mStartCell.getColumn() + 0.5f)
					* CELL_SIZE, (mStartCell.getRow() + 0.5f) * CELL_SIZE, CELL_SIZE / 6);
			mStrokeView.initStartPoint(startPoint.x, startPoint.y);
			mStrokeView.initEndPoint();
			mStrokeView.setIsDrawing(true);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mStrokeView.isDrawing()) {
				mStrokeView.setEndPoint(event.getX(), event.getY());
				mStrokeView.invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mStrokeView.isDrawing()) {
				PointF endPoint = new PointF(event.getX(), event.getY());
				mStrokeView.setEndPoint(event.getX(), event.getY());
				mStrokeView.setIsDrawing(false);

				mEndCell = WSUtil.getCellByRowColumn((int) (event.getY() / CELL_SIZE),
						(int) (event.getX() / CELL_SIZE), mListCells);
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
					if (mTvWordMap.containsKey(word.toString())) {
						mStrokeView.addFixLine();
						mTvWordMap.get(word.toString()).setTextColor(Color
								.parseColor("#66ffffff"));
						
						mTvWordMap.remove(word.toString());

//						if (!mFindoutWords.contains(word.toString())) {
//							mFindoutWords.add(word.toString());
//						}

						if (mTvWordMap.isEmpty()) {
							actionWhenFindAllWords();
						}
					}
//					for (int i = 0; i < mListWords.length; i++) {
//						if (word.toString().equals(mListWords[i])) {
//							mStrokeView.addFixLine();
//							((TextView) mFlowLayout.getChildAt(i)).setTextColor(Color
//									.parseColor("#66ffffff"));
//
//							if (!mFindoutWords.contains(mListWords[i])) {
//								mFindoutWords.add(mListWords[i]);
//							}
//
//							if (mFindoutWords.size() == mListWords.length) {
//								actionWhenFindAllWords();
//							}
//							// showDialogWhenComplete();
//							break;
//						}
//					}
				}

				mStrokeView.invalidate();
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDialogReplay:
			dismisCompleteDialog();
			recreateWordSearch();
			break;
		case R.id.btnDialogMenu:
			dismisCompleteDialog();
			finish();
			break;
		case R.id.btnDialogNext:
			dismisCompleteDialog();
			mLevel++;

			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mContentLayout.animate().setDuration(ANIM_DURATION).alpha(0);
						mFlowLayout.setVisibility(View.INVISIBLE);
						mIvLevelIcon.setImageResource(ChooseLevelActivity.sLevelIconRes[mLevel]);
						runEnterAnimation();
					}
				}, 200);
			} else {
				initListWord(LEVEL_WORD[mLevel]);
			}
			break;
		}
	};

	private void showDialogWhenComplete() {
		mCompleteDialog = new Dialog(MainActivity.this, R.style.DialogCompleteAnim);
		// dialog.setTitle("Animation Dialog");
		mCompleteDialog.setContentView(R.layout.dialog_complete_level);
		// dialog.getWindow().getAttributes().windowAnimations =
		// R.style.dialog_animation_left_right;
		mCompleteDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		mCompleteDialog.show();

		Button btnReplay = (Button) mCompleteDialog.findViewById(R.id.btnDialogReplay);
		Button btnMenu = (Button) mCompleteDialog.findViewById(R.id.btnDialogMenu);
		Button btnNext = (Button) mCompleteDialog.findViewById(R.id.btnDialogNext);
		btnReplay.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
		btnNext.setOnClickListener(this);
	}

	private void dismisCompleteDialog() {
		if (mCompleteDialog != null && mCompleteDialog.isShowing()) {
			mCompleteDialog.dismiss();
		}
	}
	
	private void showDialogTimeOut() {
		mCompleteDialog = new Dialog(MainActivity.this, R.style.DialogTimeOutAnim);
		// dialog.setTitle("Animation Dialog");
		mCompleteDialog.setContentView(R.layout.dialog_time_out);
		// dialog.getWindow().getAttributes().windowAnimations =
		// R.style.dialog_animation_left_right;
		mCompleteDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		mCompleteDialog.show();

		Button btnReplay = (Button) mCompleteDialog.findViewById(R.id.btnDialogReplay);
		Button btnMenu = (Button) mCompleteDialog.findViewById(R.id.btnDialogMenu);
		btnReplay.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
	}

	private void actionWhenFindAllWords() {
		// TODO
		mCountDownTimer.cancel();

		// save level
		SharedPreferences pref = getSharedPreferences(ChooseLevelActivity.PREF_NAME, MODE_PRIVATE);
		if (mLevel == pref.getInt(ChooseLevelActivity.KEY_MAX_LEVEL, 0)) {
			pref.edit().putInt(ChooseLevelActivity.KEY_MAX_LEVEL, mLevel + 1).commit();
		}

		showDialogWhenComplete();
		
	}

	private void showCompleteDialogButtonIcon() {
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
	
	private void setLeftBounds(Button button, Drawable drawable) {
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

	@SuppressWarnings("deprecation")
	private void setupAnimation() {
		mContentLayout.setVisibility(View.GONE);
		mFlowLayout.setVisibility(View.INVISIBLE);
		// Retrieve the data we need for the picture/description to display and
		// the thumbnail to animate it from
		Bundle bundle = getIntent().getExtras();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				bundle.getInt(PACKAGE + ".resourceId"));
		final int thumbnailTop = bundle.getInt(PACKAGE + ".top");
		final int thumbnailLeft = bundle.getInt(PACKAGE + ".left");
		final int thumbnailWidth = bundle.getInt(PACKAGE + ".width");
		final int thumbnailHeight = bundle.getInt(PACKAGE + ".height");
		mOriginalOrientation = bundle.getInt(PACKAGE + ".orientation");

		mBitmapDrawable = new BitmapDrawable(getResources(), bitmap);
		mIvLevelIcon.setImageDrawable(mBitmapDrawable);

		mBackground = new ColorDrawable(getResources().getColor(R.color.app_color));
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			mTopLayout.setBackground(mBackground);
		} else {
			mTopLayout.setBackgroundDrawable(mBackground);
		}

		// Only run the animation if we're coming from the parent activity, not
		// if
		// we're recreated automatically by the window manager (e.g., device
		// rotation)
		ViewTreeObserver observer = mIvLevelIcon.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				mIvLevelIcon.getViewTreeObserver().removeOnPreDrawListener(this);

				// Figure out where the thumbnail and full size versions
				// are, relative
				// to the screen and each other
				int[] screenLocation = new int[2];
				mIvLevelIcon.getLocationOnScreen(screenLocation);
				mLeftDelta = thumbnailLeft - screenLocation[0];
				mTopDelta = thumbnailTop - screenLocation[1];

				// Scale factors to make the large version the same size as
				// the thumbnail
				mWidthScale = (float) thumbnailWidth / mIvLevelIcon.getWidth();
				mHeightScale = (float) thumbnailHeight / mIvLevelIcon.getHeight();

				Log.i("enter anim", "leftDelta:" + mLeftDelta + " topDelta:" + mTopDelta
						+ " scale:" + mWidthScale + " - " + mHeightScale);

				// Set starting values for properties we're going to animate.
				// These
				// values scale and position the full size version down to the
				// thumbnail
				// size/location, from which we'll animate it back up
				mIvLevelIcon.setPivotX(0);
				mIvLevelIcon.setPivotY(0);
				mIvLevelIcon.setScaleX(mWidthScale);
				mIvLevelIcon.setScaleY(mHeightScale);
				mIvLevelIcon.setTranslationX(mLeftDelta);
				mIvLevelIcon.setTranslationY(mTopDelta);
				runEnterAnimation();

				return true;
			}
		});
	}

	/**
	 * The enter animation scales the picture in from its previous thumbnail
	 * size/location, colorizing it in parallel. In parallel, the background of
	 * the activity is fading in. When the pictue is in place, the text
	 * description drops down.
	 */
	public void runEnterAnimation() {
		final long duration = (long) (ANIM_DURATION * ANIM_SCALE);

		// We'll fade the text in later
		// mTextView.setAlpha(0);

		// mContentLayout.setPivotX(0);
		// mContentLayout.setPivotY(0);
		// mContentLayout.setAlpha(0);

		// Animate scale and translation to go from thumbnail to full size
//		int transX = (int) ((mIvLevelIcon.getWidth() * 0.5) / 2);
//		int transY = (int) ((mIvLevelIcon.getHeight() * 0.5) / 2);
		mIvLevelIcon.animate().setDuration(duration).scaleX(1f).scaleY(1f).alpha(1).translationX(0)
				.translationY(0).setInterpolator(sDecelerator);
		// .withEndAction(new Runnable() {
		// public void run() {
		// initListWord(LEVEL_WORD[mLevel]);
		// }
		// });

		// Fade in the black background
		ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255); // 0-255
		bgAnim.setDuration(duration);
		bgAnim.start();

		new Handler().postDelayed(new Runnable() {
			public void run() {
				initListWord(LEVEL_WORD[mLevel]);
			}
		}, duration);

		// Animate a color filter to take the image from grayscale to full
		// color.
		// This happens in parallel with the image scaling and moving into
		// place.
		// ObjectAnimator colorizer = ObjectAnimator.ofFloat(this,
		// "saturation", 0, 1);
		// colorizer.setDuration(duration);
		// colorizer.start();

		// Animate a drop-shadow of the image
		// ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mShadowLayout,
		// "shadowDepth", 0, 1);
		// shadowAnim.setDuration(duration);
		// shadowAnim.start();
	}

}
