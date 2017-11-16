package com.example.android.skeletonapp;

import java.util.Stack;

import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CubeSolverRunnable implements Runnable {
	
	/*
	 * x    z
	 * |   / 
	 * |  / 
	 * | /
	 *    -----y
	 */
	static enum Dir {
		UP(1, 0, 0), DOWN(-1, 0, 0), RIGHT(0, 1, 0), LEFT(0, -1, 0), IN(0, 0, 1), OUT(
				0, 0, -1);

		int dx, dy, dz;

		Dir(int dx, int dy, int dz) {
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
		}
	}

	final private TextView mText;
	final private ProgressBar mProgressBar;
	final private Handler mHandler;
	final private int[] segments;
	boolean[][][] cube = new boolean[4][4][4];
	final Stack<String> trace = new Stack<String>();

	public CubeSolverRunnable(ProgressBar mProgressBar, TextView mText, Handler mHandler, String s) {
		super();
		this.mText = mText;
		this.mHandler = mHandler;
		this.mProgressBar = mProgressBar;
		segments = new int[s.length()];
		for (int i = 0; i < s.length(); i++) {
			segments[i] = Integer.parseInt("" + s.charAt(i));
		}
	}

	public boolean tryWalkForward(int x, int y, int z, Dir dir, int steps) {
		if (steps == 0)
			return true;
		x += dir.dx;
		y += dir.dy;
		z += dir.dz;// make the step
		if (x >= 0 && x < 4 && y >= 0 && y < 4 && z >= 0 && z < 4 // check
																	// bounds
				&& !cube[x][y][z] // check free
		) {
			boolean res = tryWalkForward(x, y, z, dir, steps - 1);
			if (res == true)
				cube[x][y][z] = true;
			return res;
		} else
			return false;
	}

	public void walkBackwards(int x, int y, int z, Dir dir, int steps) {
		if (steps == 0)
			return;
		cube[x][y][z] = false;
		walkBackwards(x + dir.dx, y + dir.dy, z + dir.dz, dir, steps - 1);
	}

	public boolean walkRec(int x, int y, int z, Dir dir, int segmentIdx) {
		if (segmentIdx == segments.length)
			return true;
		if (tryWalkForward(x, y, z, dir, segments[segmentIdx])) {
			trace.push(dir.toString().toLowerCase() +" "+ segments[segmentIdx]);
			postProgress();
			if (segmentIdx + 1 == segments.length)
				return true;
			switch (dir) {
			case UP:
				x += segments[segmentIdx];
				for (Dir nextDir : new Dir[] { Dir.RIGHT, Dir.LEFT, Dir.IN,
						Dir.OUT }) {
					if (walkRec(x, y, z, nextDir, segmentIdx + 1))
						return true;
				}
				walkBackwards(x, y, z, Dir.DOWN, segments[segmentIdx]);
				break;
			case DOWN:
				x -= segments[segmentIdx];
				for (Dir nextDir : new Dir[] { Dir.RIGHT, Dir.LEFT, Dir.IN,
						Dir.OUT }) {
					if (walkRec(x, y, z, nextDir, segmentIdx + 1))
						return true;
				}
				walkBackwards(x, y, z, Dir.UP, segments[segmentIdx]);
				break;
			case RIGHT:
				y += segments[segmentIdx];
				for (Dir nextDir : new Dir[] { Dir.UP, Dir.DOWN, Dir.IN,
						Dir.OUT }) {
					if (walkRec(x, y, z, nextDir, segmentIdx + 1))
						return true;
				}
				walkBackwards(x, y, z, Dir.LEFT, segments[segmentIdx]);
				break;
			case LEFT:
				y -= segments[segmentIdx];
				for (Dir nextDir : new Dir[] { Dir.UP, Dir.DOWN, Dir.IN,
						Dir.OUT }) {
					if (walkRec(x, y, z, nextDir, segmentIdx + 1))
						return true;
				}
				walkBackwards(x, y, z, Dir.RIGHT, segments[segmentIdx]);
				break;
			case IN:
				z += segments[segmentIdx];
				for (Dir nextDir : new Dir[] { Dir.RIGHT, Dir.LEFT, Dir.UP,
						Dir.DOWN }) {
					if (walkRec(x, y, z, nextDir, segmentIdx + 1))
						return true;
				}
				walkBackwards(x, y, z, Dir.OUT, segments[segmentIdx]);
				break;
			case OUT:
				z -= segments[segmentIdx];
				for (Dir nextDir : new Dir[] { Dir.RIGHT, Dir.LEFT, Dir.UP,
						Dir.DOWN }) {
					if (walkRec(x, y, z, nextDir, segmentIdx + 1))
						return true;
				}
				walkBackwards(x, y, z, Dir.IN, segments[segmentIdx]);
				break;
			}
			trace.pop();
			postProgress();
		}
		return false;
	}

	public void postProgress(){
	mHandler.post(new Runnable() {
		@Override
		public void run() {
			mProgressBar.setProgress(trace.size());
			//mText.setText(""+trace);
		}
	});
	}
	
	public void out(final String s) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mText.setText(s);
			}
		});
	}

//	public String t(Stack s){
//		String str = "";
//		for (Object object : s) {
//			
//		}
//	}
	public boolean startAt(int x, int y){
		for (int z = -1; z < 4 - segments[0]; z++) {
			out("trying to start at ("+y+","+x+","+(z+1)+")");
			if (walkRec(x, y, z, Dir.IN, 0) == true) {
				out("solved!\nstarted at ("+y+","+x+","+(z+1)+")"+
						" and then went "+trace);
				Log.e("solved", "solved!\nstarted at ("+y+","+x+","+(z+1)+")"+
						" and then went "+trace);
				return true;
			}
			//else {
//				progress += ".";
//			}
		}
		return false;
	}
	@Override
	public void run() {
		if (segments.length == 0){
			out("INPUT ERROR: no segments entered");
			return;
		}
		if (segments[0]<2||segments[0]>4){
			out("INPUT ERROR: can't have a first segment of length "+segments[0]);
			return;
		}
		int sum = 0;
		for (int i = 0; i < segments.length; i++) {
			if (segments[i]<1||segments[i]>3){
				out("INPUT ERROR: can't have an inner segment of length "+segments[i]);
				return;
			}
			sum += segments[i];
		}
		if (sum > 64){
			out("INPUT ERROR: total sum of segments is "+sum);
		}
	//	out(""+sum);
	//	final int s = sum;
		
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressBar.setMax(segments.length);
				//mText.setText(""+trace);
			}
		});
		//out(""+sum);
	//	String progress = "solving";

		if (! (startAt(0,0)||startAt(1,0)||startAt(1,1))){
			out("failed");
		}
	}

	public boolean tryMove(boolean changedDir, Dir dir, int x, int y, int z,
			int steps, int segmentsLeft) {

		if (steps == 0) {
			if (segmentsLeft > 0) {
				int newSteps = segments[segments.length - segmentsLeft];
				switch (dir) {
				case UP:
				case DOWN:
					return (tryMove(true, Dir.RIGHT, x, y, z, newSteps,
							segmentsLeft - 1)
							|| tryMove(true, Dir.LEFT, x, y, z, newSteps,
									segmentsLeft - 1)
							|| tryMove(true, Dir.IN, x, y, z, newSteps,
									segmentsLeft - 1) || tryMove(true, Dir.OUT,
								x, y, z, newSteps, segmentsLeft - 1));
				case RIGHT:
				case LEFT:
					return (tryMove(true, Dir.UP, x, y, z, newSteps,
							segmentsLeft - 1)
							|| tryMove(true, Dir.DOWN, x, y, z, newSteps,
									segmentsLeft - 1)
							|| tryMove(true, Dir.IN, x, y, z, newSteps,
									segmentsLeft - 1) || tryMove(true, Dir.OUT,
								x, y, z, newSteps, segmentsLeft - 1));
				case IN:
				case OUT:
					return (tryMove(true, Dir.UP, x, y, z, newSteps,
							segmentsLeft - 1)
							|| tryMove(true, Dir.DOWN, x, y, z, newSteps,
									segmentsLeft - 1)
							|| tryMove(true, Dir.RIGHT, x, y, z, newSteps,
									segmentsLeft - 1) || tryMove(true,
								Dir.LEFT, x, y, z, newSteps, segmentsLeft - 1));
				}

			} else
				return true;
		} else {
			if (changedDir) {
				trace.push(dir.toString() + " " + steps);
			}
		}
		switch (dir) {
		case UP:
			if (x < 3 && cube[x + 1][y][z] == false) {
				cube[x + 1][y][z] = true;
				boolean res = tryMove(false, dir, x + 1, y, z, steps - 1,
						segmentsLeft);
				if (res == false)
					cube[x + 1][y][z] = false;
				return res;
			}
			break;
		case DOWN:
			if (x > 0 && cube[x - 1][y][z] == false) {
				cube[x - 1][y][z] = true;
				boolean res = tryMove(false, dir, x - 1, y, z, steps - 1,
						segmentsLeft);
				if (res == false)
					cube[x - 1][y][z] = false;
				return res;
			}
			break;
		case RIGHT:
			if (y < 3 && cube[x][y + 1][z] == false) {
				cube[x][y + 1][z] = true;
				boolean res = tryMove(false, dir, x, y + 1, z, steps - 1,
						segmentsLeft);
				if (res == false)
					cube[x][y + 1][z] = false;
				return res;
			}
			break;
		case LEFT:
			if (y > 0 && cube[x][y - 1][z] == false) {
				cube[x][y - 1][z] = true;
				boolean res = tryMove(false, dir, x, y - 1, z, steps - 1,
						segmentsLeft);
				if (res == false)
					cube[x][y - 1][z] = false;
				return res;
			}
			break;
		case IN:
			if (z < 3 && cube[x][y][z + 1] == false) {
				cube[x][y][z + 1] = true;
				boolean res = tryMove(false, dir, x, y, z + 1, steps - 1,
						segmentsLeft);
				if (res == false)
					cube[x][y][z + 1] = false;
				return res;
			}
			break;
		case OUT:
			if (z > 0 && cube[x][y][z - 1] == false) {
				cube[x][y][z - 1] = true;
				boolean res = tryMove(false, dir, x, y, z - 1, steps - 1,
						segmentsLeft);
				if (res == false)
					cube[x][y][z - 1] = false;
				return res;
			}
			break;
		}
		trace.pop();
		return false;
	}
}
