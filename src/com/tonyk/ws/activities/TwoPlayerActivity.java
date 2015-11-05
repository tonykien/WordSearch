package com.tonyk.ws.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.widget.GridView;
import android.widget.TextView;

import com.tonyk.ws.Cell;
import com.tonyk.ws.R;
import com.tonyk.ws.adapters.CellGridviewAdapter;
import com.tonyk.ws.custom.StrokeView;
import com.tonyk.ws.utils.Define;
import com.tonyk.ws.utils.WSUtil;

public class TwoPlayerActivity extends Activity implements OnTouchListener {

	private ArrayList<Cell> mListCells = new ArrayList<Cell>();
	private ArrayList<Integer> mAvaiableDirection = new ArrayList<Integer>();

	public int SIZE_X = Define.SIZE_X;
	public int SIZE_Y = Define.SIZE_Y - 2;

	private TextView mTvWord1, mTvWord2;
	private GridView mGridLetter1;
	private StrokeView mStrokeView1;
	private GridView mGridLetter2;
	private StrokeView mStrokeView2;
	private CellGridviewAdapter mAdapter;

	private Cell mStartCell1, mEndCell1;
	private Cell mStartCell2, mEndCell2;
	private ArrayList<String> mListWord = new ArrayList<String>();
	private String[] mWordArray;
	private boolean mIsCreating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_two_player);

		mGridLetter1 = (GridView) findViewById(R.id.gvLetter1);
		mStrokeView1 = (StrokeView) findViewById(R.id.strokeview1);

		mGridLetter2 = (GridView) findViewById(R.id.gvLetter2);
		mStrokeView2 = (StrokeView) findViewById(R.id.strokeview2);
		
		mTvWord1 = (TextView) findViewById(R.id.tvWordsPlayer1);
		mTvWord2 = (TextView) findViewById(R.id.tvWordsPlayer2);

		mTvWord1.setSelected(true);
		mTvWord2.setSelected(true);
		
		mStrokeView1.setColorOfPlayer1();
		mStrokeView2.setColorOfPlayer2();

		mAdapter = new CellGridviewAdapter(this, mListCells);
		mGridLetter1.setNumColumns(SIZE_X);
		mGridLetter1.setAdapter(mAdapter);
		mStrokeView1.setOnTouchListener(this);

		mGridLetter2.setNumColumns(SIZE_X);
		mGridLetter2.setAdapter(mAdapter);
		mStrokeView2.setOnTouchListener(this);

		if (Build.VERSION.SDK_INT < 11) {
			RotateAnimation animation = new RotateAnimation(0, 180,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			animation.setDuration(0);
			animation.setFillAfter(true);
			findViewById(R.id.layoutPlayer1).startAnimation(animation);
		} else {
			findViewById(R.id.layoutPlayer1).setRotation(180);
		}

		WSUtil.sSizeX = SIZE_X;
		WSUtil.sSizeY = SIZE_Y;
		initListWord(MainActivity.LEVEL_WORD[0]);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == mStrokeView1.getId()) {
			onTouch((StrokeView) v, event, mStartCell1, mEndCell1);
		} else {
			onTouch((StrokeView) v, event, mStartCell2, mEndCell2);
		}

		return true;
	}

	private void onTouch(StrokeView strokeView, MotionEvent event,
			Cell startCell, Cell endCell) {
		final float CELL_SIZE = (float) strokeView.getWidth() / SIZE_X;
		strokeView.setStrokeWidth(CELL_SIZE * 2 / 3);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			PointF startPoint = new PointF(event.getX(), event.getY());
			startCell = WSUtil.getCellByRowColumn(
					(int) (event.getY() / CELL_SIZE),
					(int) (event.getX() / CELL_SIZE), mListCells);

			if (strokeView.getId() == mStrokeView1.getId()) {
				mStartCell1 = startCell;
			} else {
				mStartCell2 = startCell;
			}

			startPoint = WSUtil.adjustStartPoint(startPoint,
					(startCell.getColumn() + 0.5f) * CELL_SIZE,
					(startCell.getRow() + 0.5f) * CELL_SIZE, CELL_SIZE / 6);
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

				endCell = WSUtil.getCellByRowColumn(
						(int) (event.getY() / CELL_SIZE),
						(int) (event.getX() / CELL_SIZE), mListCells);
				if (strokeView.getId() == mStrokeView1.getId()) {
					mEndCell1 = endCell;
				} else {
					mEndCell2 = endCell;
				}
				if (endCell == null) {
					strokeView.invalidate();
					break;
				}

				StringBuilder word = new StringBuilder();
				if (startCell.getColumn() == endCell.getColumn()) {
					// direction S
					if (startCell.getRow() <= endCell.getRow()) {
						for (int i = startCell.getRow(); i <= endCell.getRow(); i++) {
							word.append(mListCells.get(
									startCell.getColumn() + i * SIZE_X)
									.getLetter());
						}
					} else { // direction N
						for (int i = startCell.getRow(); i >= endCell.getRow(); i--) {
							word.append(mListCells.get(
									startCell.getColumn() + i * SIZE_X)
									.getLetter());
						}
					}

				} else if (startCell.getRow() == endCell.getRow()) {
					/** Direction E & W */
					if (startCell.getColumn() <= endCell.getColumn()) {
						for (int i = startCell.getColumn(); i <= endCell
								.getColumn(); i++) {
							word.append(mListCells.get(
									i + startCell.getRow() * SIZE_X)
									.getLetter());
						}
					} else {
						for (int i = startCell.getColumn(); i >= endCell
								.getColumn(); i--) {
							word.append(mListCells.get(
									i + startCell.getRow() * SIZE_X)
									.getLetter());
						}
					}

				} else if (startCell.getRow() - endCell.getRow() == startCell
						.getColumn() - endCell.getColumn()) {
					if (startCell.getColumn() <= endCell.getColumn()) {
						for (int i = startCell.getColumn(); i <= endCell
								.getColumn(); i++) {
							word.append(mListCells
									.get(i
											+ (startCell.getRow() + i - startCell
													.getColumn()) * SIZE_X)
									.getLetter());
						}
					} else {
						for (int i = startCell.getColumn(); i >= endCell
								.getColumn(); i--) {
							word.append(mListCells
									.get(i
											+ (startCell.getRow() + i - startCell
													.getColumn()) * SIZE_X)
									.getLetter());
						}
					}

				} else if (startCell.getRow() - endCell.getRow() == endCell
						.getColumn() - startCell.getColumn()) {
					if (startCell.getColumn() <= endCell.getColumn()) {
						for (int i = startCell.getColumn(); i <= endCell
								.getColumn(); i++) {
							word.append(mListCells
									.get(i
											+ (startCell.getRow() - i + startCell
													.getColumn()) * SIZE_X)
									.getLetter());
						}
					} else {
						for (int i = startCell.getColumn(); i >= endCell
								.getColumn(); i--) {
							word.append(mListCells
									.get(i
											+ (startCell.getRow() - i + startCell
													.getColumn()) * SIZE_X)
									.getLetter());
						}
					}

				}

				// adjust end point
				if (endPoint.y > endCell.getRow() * CELL_SIZE + CELL_SIZE * 2
						/ 3) {
					endPoint.y = endCell.getRow() * CELL_SIZE + CELL_SIZE * 2
							/ 3;
				} else if (endPoint.y < endCell.getRow() * CELL_SIZE
						+ CELL_SIZE * 1 / 3) {
					endPoint.y = endCell.getRow() * CELL_SIZE + CELL_SIZE * 1
							/ 3;
				}

				if (endPoint.x > endCell.getColumn() * CELL_SIZE + CELL_SIZE
						* 2 / 3) {
					endPoint.x = endCell.getColumn() * CELL_SIZE + CELL_SIZE
							* 2 / 3;
				} else if (endPoint.x < endCell.getColumn() * CELL_SIZE
						+ CELL_SIZE * 1 / 3) {
					endPoint.x = endCell.getColumn() * CELL_SIZE + CELL_SIZE
							* 1 / 3;
				}
				strokeView.setEndPoint(endPoint.x, endPoint.y);

				if (word.length() > 0) {
					if (mListWord.contains(word.toString())) {
						Log.i("word", "" + word.toString());
						if (strokeView.getId() == mStrokeView1.getId()) {
							mStrokeView1.addFixLine();
							mStrokeView2.addFixLine(
									mStrokeView1.getCurrentStartPoint(),
									endPoint, mStrokeView1.getPaint());
							mStrokeView2.invalidate();
						} else {
							mStrokeView2.addFixLine();
							mStrokeView1.addFixLine(
									mStrokeView2.getCurrentStartPoint(),
									endPoint, mStrokeView2.getPaint());
							mStrokeView1.invalidate();
						}

						mListWord.remove(word.toString());
						if (mListWord.isEmpty()) {
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
		mListWord.clear();

		// listWord = "LEVEL6,BATHROOM";
		mWordArray = listWord.split(",");

		// ArrayList<TextView> listTvWord = new ArrayList<TextView>();
		String textWords = "";
		for (int i = 2; i < mWordArray.length; i ++) {
			mListWord.add(mWordArray[i]);
			textWords += mWordArray[i] + "   ";
		}
		mTvWord1.setText(textWords);
		mTvWord2.setText(textWords);

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
		for (int i = 0; i < mWordArray.length; i += 2) {
			String word = mWordArray[i];
			// int startPos = getStartPosition(word);
			// checkDirection(word, startPos);
			int startPos = WSUtil.getStartPosition(word, mListCells,
					mAvaiableDirection);
			mAvaiableDirection = WSUtil.getAvaiableDirections(word, startPos,
					mListCells, mAvaiableDirection);
			while (mAvaiableDirection.isEmpty()) {
				// startPos = getStartPosition(word);
				// checkDirection(word, startPos);
				startPos = WSUtil.getStartPosition(word, mListCells,
						mAvaiableDirection);
				mAvaiableDirection = WSUtil.getAvaiableDirections(word,
						startPos, mListCells, mAvaiableDirection);
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
