package com.tonyk.ws;

public class Cell {

	private char letter;
	private int column;
	private int row;
	private boolean isFilled = false;
	
	public Cell(int column, int row) {
		this.column = column;
		this.row = row;
	}

	public char getLetter() {
		return letter;
	}

	public void setLetter(char letter) {
		this.letter = letter;
		isFilled = true;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public void setFilled(boolean isFilled) {
		this.isFilled = isFilled;
	}
	
}
