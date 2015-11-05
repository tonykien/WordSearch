package com.tonyk.ws.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.ads.AdRequest;
import com.tonyk.ws.Cell;
import com.tonyk.ws.R;
import com.tonyk.ws.R.id;
import com.tonyk.ws.R.layout;
import com.tonyk.ws.adapters.CellGridviewAdapter;
import com.tonyk.ws.custom.StrokeView;
import com.tonyk.ws.utils.Define;
import com.tonyk.ws.utils.WSUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class TwoPlayerActivity extends Activity implements OnTouchListener{

	private ArrayList<Cell> mListCells = new ArrayList<Cell>();
	private ArrayList<Integer> mAvaiableDirection = new ArrayList<Integer>();

	public int SIZE_X = Define.SIZE_X;
	public int SIZE_Y = Define.SIZE_Y - 2;
	
	private GridView mGridLetter1;
	private StrokeView mStrokeView1;
	private GridView mGridLetter2;
	private StrokeView mStrokeView2;
	private CellGridviewAdapter mAdapter;
	
	private Cell mStartCell, mEndCell;
	private HashMap<String, TextView> mTvWordMap = new HashMap<String, TextView>();
	private String[] mListWords;
	private boolean mIsCreating;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_two_player);
		
		mGridLetter1 = (GridView) findViewById(R.id.gvLetter1);
		mStrokeView1 = (StrokeView) findViewById(R.id.strokeview1);
		
		mGridLetter2 = (GridView) findViewById(R.id.gvLetter2);
		mStrokeView2 = (StrokeView) findViewById(R.id.strokeview2);
		
		mAdapter = new CellGridviewAdapter(this, mListCells);
		mGridLetter1.setNumColumns(SIZE_X);
		mGridLetter1.setAdapter(mAdapter);
		mStrokeView1.setOnTouchListener(this);
		
		mGridLetter2.setNumColumns(SIZE_X);
		mGridLetter2.setAdapter(mAdapter);
		mStrokeView2.setOnTouchListener(this);
		
		WSUtil.sSizeX = SIZE_X;
		WSUtil.sSizeY = SIZE_Y;
		initListWord(MainActivity.LEVEL_WORD[0]);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		onTouch((StrokeView) v, event);
		return true;
	}
	
	private void onTouch(StrokeView strokeView, MotionEvent event) {
		final float CELL_SIZE = (float) strokeView.getWidth() / SIZE_X;
		strokeView.setStrokeWidth(CELL_SIZE * 2 / 3);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			PointF startPoint = new PointF(event.getX(), event.getY());
			mStartCell = WSUtil.getCellByRowColumn((int) (event.getY() / CELL_SIZE),
					(int) (event.getX() / CELL_SIZE), mListCells);
			strokeView.setColorRandom();

			startPoint = WSUtil.adjustStartPoint(startPoint, (mStartCell.getColumn() + 0.5f)
					* CELL_SIZE, (mStartCell.getRow() + 0.5f) * CELL_SIZE, CELL_SIZE / 6);
			strokeView.initStartPoint(startPoint.x, startPoint.y);
			strokeView.initEndPoint();
			strokeView.setIsDrawing(true);
			break;
		case MotionEvent.ACTION_MOVE:
			if (strokeView.isDrawing()) {
				strokeView.setEndPoint(event.getX(), event.getY());
				strokeView.invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (strokeView.isDrawing()) {
				PointF endPoint = new PointF(event.getX(), event.getY());
				strokeView.setEndPoint(event.getX(), event.getY());
				strokeView.setIsDrawing(false);

				mEndCell = WSUtil.getCellByRowColumn((int) (event.getY() / CELL_SIZE),
						(int) (event.getX() / CELL_SIZE), mListCells);
				if (mEndCell == null) {
					strokeView.invalidate();
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
				strokeView.setEndPoint(endPoint.x, endPoint.y);

				if (word.length() > 0) {
					if (mTvWordMap.containsKey(word.toString())) {
						mStrokeView1.addFixLine();
						mStrokeView2.addFixLine();
						mTvWordMap.get(word.toString()).setTextColor(Color
								.parseColor("#66ffffff"));
						
						mTvWordMap.remove(word.toString());
						if (mTvWordMap.isEmpty()) {
							// actionWhenFindAllWords(); TODO
						}
					}
				}

				strokeView.invalidate();
			}
			break;
		default:
			break;
		}
	}
	
	public void initListWord(String listWord) {
		mListCells.clear();
		mStrokeView1.reset();
		mStrokeView2.reset();
		mTvWordMap.clear();
		
		// listWord = "LEVEL6,BATHROOM";
		mListWords = listWord.split(",");

		// ArrayList<TextView> listTvWord = new ArrayList<TextView>();
		float density = getResources().getDisplayMetrics().density;
		for (int i = 0; i < mListWords.length; i+=2) {
			TextView tvWord = new TextView(this);
//			tvWord.setText(mListWords[i]);
//			tvWord.setTextColor(Color.WHITE);
//			tvWord.setGravity(Gravity.CENTER_HORIZONTAL);
//			tvWord.setPadding((int) (8 * density), (int) (4 * density), (int) (8 * density),
//					(int) (4 * density));
//			listTvWord.add(tvWord);
			// mFlowLayout.addView(tvWord);
			mTvWordMap.put(mListWords[i], tvWord);
		}
//		Collections.shuffle(listTvWord);
//		for (TextView tvWord : listTvWord) {
//			// mFlowLayout.addView(tvWord);
//		}

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
				dialog = new ProgressDialog(TwoPlayerActivity.this);
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
			}
		}.execute();

	}

	public boolean autoFillCell() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				mIsCreating = false;
			}
		}, 600);
		mIsCreating = true;
		for (int i = 0; i < mListWords.length; i+=2) {
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


}
