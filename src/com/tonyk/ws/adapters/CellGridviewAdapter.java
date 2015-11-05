package com.tonyk.ws.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tonyk.ws.Cell;
import com.tonyk.ws.R;

public class CellGridviewAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Cell> cells;
	private boolean isTwoPlayer = false;

	public CellGridviewAdapter(Context context, ArrayList<Cell> cells) {
		this.context = context;
		this.cells = cells;
	}
	
	public void setIsTwoPlayer(boolean isTwoPlayer) {
		this.isTwoPlayer = isTwoPlayer;
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
		if (isTwoPlayer) {
			tvLetter.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.cell_text_size_two_player));
		}
		return convertView;
	}

}
