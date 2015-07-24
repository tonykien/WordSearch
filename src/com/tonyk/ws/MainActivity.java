package com.tonyk.ws;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity {

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

	private String[] mListWords = new String[] { "MOBIPHONE", "PANASONIC", "TOSHIBA", "SAMSUNG", "ANDROID",
			"SUZUKI", "TOYOTA", "DAIKIN", "HONDA", "APPLE", "SHARP", "SONY"};

	private ArrayList<Cell> mListCells = new ArrayList<Cell>();
	private ArrayList<Integer> mAvaiableDirection = new ArrayList<Integer>();

	public int SIZE_X = 12;
	public int SIZE_Y = 15;
	
	private GridView mGridLetter;
	GvAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("LETTER", "oncreate");
		mGridLetter = (GridView) findViewById(R.id.gvLetter);
		mGridLetter.setNumColumns(SIZE_X);

		ArrayList<Character> letters = new ArrayList<Character>();
		Random r = new Random();
		for (int i = 0; i < SIZE_X * SIZE_Y; i++) {
			char c = (char) (r.nextInt(26) + 'A');
			letters.add(Character.valueOf(c));
			Cell cell = new Cell(i % SIZE_X, i / SIZE_X);
			mListCells.add(cell);
		}

		for (int i = 0; i < mListWords.length; i++) {
			String word = mListWords[i];
			int startPos = getStartPosition(word);
			checkDirection(word, startPos);
			while (mAvaiableDirection.isEmpty()) {
				startPos = getStartPosition(word);
				checkDirection(word, startPos);
			}

			int direct = getDirection();
			setCellByDirect(direct, word, startPos);
		}

		adapter = new GvAdapter(this, mListCells);
		mGridLetter.setAdapter(adapter);
	

	}
	
	public void onBtnRandomClick(View v) {
		mListCells.clear();
		for (int i = 0; i < SIZE_X * SIZE_Y; i++) {
			Cell cell = new Cell(i % SIZE_X, i / SIZE_X);
			mListCells.add(cell);
		}
		
		for (int i = 0; i < mListWords.length; i++) {
			String word = mListWords[i];
			int startPos = getStartPosition(word);
			checkDirection(word, startPos);
			while (mAvaiableDirection.isEmpty()) {
				startPos = getStartPosition(word);
				checkDirection(word, startPos);
			}

			int direct = getDirection();
			setCellByDirect(direct, word, startPos);
		}
		
		adapter.notifyDataSetChanged();
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
		} while ((offsetX < x && x < SIZE_X - 1 - offsetX && offsetY < y && y < SIZE_Y - 1 - offsetY)
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

	class GvAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<Cell> cells;

		public GvAdapter(Context context, ArrayList<Cell> cells) {
			this.context = context;
			this.cells = cells;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cells.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

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
}
