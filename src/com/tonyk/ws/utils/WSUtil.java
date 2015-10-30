package com.tonyk.ws.utils;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Button;

import com.tonyk.ws.Cell;
import com.tonyk.ws.MainActivity.Direction;

public class WSUtil {

	public static ArrayList<Integer> getAvaiableDirections(String word, int startPos, ArrayList<Cell> listCells, ArrayList<Integer> avaiableDirections) {
		for (int i = avaiableDirections.size() - 1; i >= 0; i--) {
			switch (avaiableDirections.get(i).intValue()) {
			case Direction.E:
				if (!checkDirectionEast(word, startPos, listCells)) {
					avaiableDirections.remove(i);
				}
				break;
			case Direction.N:
				if (!checkDirectionNorth(word, startPos, listCells)) {
					avaiableDirections.remove(i);
				}
				break;
			case Direction.NE:
				if (!checkDirectionNorthEast(word, startPos, listCells)) {
					avaiableDirections.remove(i);
				}
				break;
			case Direction.NW:
				if (!checkDirectionNorthWest(word, startPos, listCells)) {
					avaiableDirections.remove(i);
				}
				break;
			case Direction.S:
				if (!checkDirectionSouth(word, startPos, listCells)) {
					avaiableDirections.remove(i);
				}
				break;
			case Direction.SE:
				if (!checkDirectionSouthEast(word, startPos, listCells)) {
					avaiableDirections.remove(i);
				}
				break;
			case Direction.SW:
				if (!checkDirectionSouthWest(word, startPos, listCells)) {
					avaiableDirections.remove(i);
				}
				break;
			case Direction.W:
				if (!checkDirectionWest(word, startPos, listCells)) {
					avaiableDirections.remove(i);
				}
				break;

			default:
				break;
			}
		}
		
		return avaiableDirections;
	}

	private static boolean checkDirectionNorth(String word, int startPos, ArrayList<Cell> listCells) {
		int pos;
		Cell cell;
		for (int i = 0; i < word.length(); i++) {
			pos = startPos - Define.SIZE_X * i;
			cell = listCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkDirectionSouth(String word, int startPos, ArrayList<Cell> listCells) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos + Define.SIZE_X * i;
			Cell cell = listCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkDirectionEast(String word, int startPos, ArrayList<Cell> listCells) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos + i;
			Cell cell = listCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkDirectionWest(String word, int startPos, ArrayList<Cell> listCells) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos - i;
			Cell cell = listCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkDirectionNorthEast(String word, int startPos, ArrayList<Cell> listCells) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos - Define.SIZE_X * i + i;
			Cell cell = listCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkDirectionSouthEast(String word, int startPos, ArrayList<Cell> listCells) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos + Define.SIZE_X * i + i;
			Cell cell = listCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkDirectionNorthWest(String word, int startPos, ArrayList<Cell> listCells) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos - Define.SIZE_X * i - i;
			Cell cell = listCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkDirectionSouthWest(String word, int startPos, ArrayList<Cell> listCells) {
		for (int i = 0; i < word.length(); i++) {
			int pos = startPos + Define.SIZE_X * i - i;
			Cell cell = listCells.get(pos);
			if (cell.isFilled() && cell.getLetter() != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public static void setCellByDirect(int direct, String word, int startPos, ArrayList<Cell> listCells) {
		int pos;
		Cell cell;
		switch (direct) {
		case Direction.E:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos + i;
				cell = listCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.N:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos - Define.SIZE_X * i;
				cell = listCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.NE:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos - Define.SIZE_X * i + i;
				cell = listCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.NW:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos - Define.SIZE_X * i - i;
				cell = listCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.S:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos + Define.SIZE_X * i;
				cell = listCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.SE:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos + Define.SIZE_X * i + i;
				cell = listCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.SW:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos + Define.SIZE_X * i - i;
				cell = listCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;
		case Direction.W:
			for (int i = 0; i < word.length(); i++) {
				pos = startPos - i;
				cell = listCells.get(pos);
				cell.setLetter(word.charAt(i));
			}
			break;

		default:
			break;
		}
	}

	public static int getStartPosition(String word, ArrayList<Cell> listCells, ArrayList<Integer> avaiableDirections) {
		avaiableDirections.clear();
		Random r = new Random();
		int x, y;
		Cell cell;
		int offsetX = Define.SIZE_X - word.length();
		int offsetY = Define.SIZE_Y - word.length();
		do {
			x = r.nextInt(Define.SIZE_X);
			y = r.nextInt(Define.SIZE_Y);
			cell = listCells.get(x + y * Define.SIZE_X);
		} while ((offsetX < x && x < Define.SIZE_X - 1 - offsetX && offsetY < y && y < Define.SIZE_Y - 1
				- offsetY)
				|| (cell.isFilled() && cell.getLetter() != word.charAt(0)));

		if (x <= Define.SIZE_X - word.length() && y <= Define.SIZE_Y - word.length()) {
			if (!avaiableDirections.contains(Integer.valueOf(Direction.S))) {
				avaiableDirections.add(Integer.valueOf(Direction.S));
			}

			if (!avaiableDirections.contains(Integer.valueOf(Direction.SE))) {
				avaiableDirections.add(Integer.valueOf(Direction.SE));
			}

			if (!avaiableDirections.contains(Integer.valueOf(Direction.E))) {
				avaiableDirections.add(Integer.valueOf(Direction.E));
			}
		}

		if (x <= Define.SIZE_X - word.length() && y >= word.length()) {
			if (!avaiableDirections.contains(Integer.valueOf(Direction.N))) {
				avaiableDirections.add(Integer.valueOf(Direction.N));
			}

			if (!avaiableDirections.contains(Integer.valueOf(Direction.NE))) {
				avaiableDirections.add(Integer.valueOf(Direction.NE));
			}

			if (!avaiableDirections.contains(Integer.valueOf(Direction.E))) {
				avaiableDirections.add(Integer.valueOf(Direction.E));
			}
		}

		if (x >= word.length() && y >= word.length()) {
			if (!avaiableDirections.contains(Integer.valueOf(Direction.N))) {
				avaiableDirections.add(Integer.valueOf(Direction.N));
			}

			if (!avaiableDirections.contains(Integer.valueOf(Direction.NW))) {
				avaiableDirections.add(Integer.valueOf(Direction.NW));
			}

			if (!avaiableDirections.contains(Integer.valueOf(Direction.W))) {
				avaiableDirections.add(Integer.valueOf(Direction.W));
			}
		}

		if (x >= word.length() && y <= Define.SIZE_Y - word.length()) {
			if (!avaiableDirections.contains(Integer.valueOf(Direction.S))) {
				avaiableDirections.add(Integer.valueOf(Direction.S));
			}

			if (!avaiableDirections.contains(Integer.valueOf(Direction.SW))) {
				avaiableDirections.add(Integer.valueOf(Direction.SW));
			}

			if (!avaiableDirections.contains(Integer.valueOf(Direction.W))) {
				avaiableDirections.add(Integer.valueOf(Direction.W));
			}
		}

		if (x <= Define.SIZE_X - word.length()) {
			if (!avaiableDirections.contains(Integer.valueOf(Direction.E))) {
				avaiableDirections.add(Integer.valueOf(Direction.E));
			}
		}

		if (x >= word.length()) {
			if (!avaiableDirections.contains(Integer.valueOf(Direction.W))) {
				avaiableDirections.add(Integer.valueOf(Direction.W));
			}
		}

		if (y <= Define.SIZE_Y - word.length()) {
			if (!avaiableDirections.contains(Integer.valueOf(Direction.S))) {
				avaiableDirections.add(Integer.valueOf(Direction.S));
			}
		}

		if (y >= word.length()) {
			if (!avaiableDirections.contains(Integer.valueOf(Direction.N))) {
				avaiableDirections.add(Integer.valueOf(Direction.N));
			}
		}

		return x + y * Define.SIZE_X;
	}

	public static int getRandomDirection(ArrayList<Integer> avaiableDirections) {
		Random r = new Random();
		return avaiableDirections.get(r.nextInt(avaiableDirections.size())).intValue();
	}
	
	public static Cell getCellByRowColumn(int row, int column, ArrayList<Cell> listCells) {
		if (column + row * Define.SIZE_X < listCells.size()) {
			return listCells.get(column + row * Define.SIZE_X);
		}
		return null;
	}
	
	public static PointF adjustStartPoint(PointF startPoint, float centerX, float centerY, float radius) {
		float distance = (float) Math.sqrt((centerX - startPoint.x) * (centerX - startPoint.x)
				+ (centerY - startPoint.y) * (centerY - startPoint.y));
		if (distance > radius) {
			float k = 1 - distance / radius;
			startPoint.x = (startPoint.x - k * centerX) / (1 - k);
			startPoint.y = (startPoint.y - k * centerY) / (1 - k);
		}
		return startPoint;
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
}
