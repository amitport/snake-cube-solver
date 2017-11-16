/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.skeletonapp;

import java.util.Stack;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This class provides a basic demonstration of how to write an Android
 * activity. Inside of its window, it places a single view: an EditText that
 * displays and edits some internal text.
 */
public class SkeletonActivity extends Activity {
    
    static final private int SEND_ID = Menu.FIRST;
    static final private int CLEAR_ID = Menu.FIRST + 1;

    private EditText mEditor;
    private TextView mText;
    private Handler mHandler;
    private ProgressBar mProgressBar;
    
    public SkeletonActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.skeleton_activity);

        // Find the text editor view inside the layout, because we
        // want to do various programmatic things with it.
        mEditor = (EditText) findViewById(R.id.editor);
    	mText = ((TextView) findViewById(R.id.text));
    	mProgressBar = ((ProgressBar) findViewById(R.id.progressBar));
        // Hook up button presses to the appropriate event handler.
        ((Button) findViewById(R.id.clear)).setOnClickListener(mClearListener);
        ((Button) findViewById(R.id.send)).setOnClickListener(mSendListener);
        
        mHandler = new Handler();
    }

    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Called when your activity's options menu needs to be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // We are going to create two menus. Note that we assign them
        // unique integer IDs, labels from our string resources, and
        // given them shortcuts.
        menu.add(0, SEND_ID, 0, R.string.send).setShortcut('0', 's');
        menu.add(0, CLEAR_ID, 0, R.string.clear).setShortcut('1', 'c');

        return true;
    }

    /**
     * Called right before your activity's option menu is displayed.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Before showing the menu, we need to decide whether the clear
        // item is enabled depending on whether there is text to clear.
        menu.findItem(CLEAR_ID).setVisible(mEditor.getText().length() > 0);

        return true;
    }

    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case SEND_ID:
        	sendAndStuff();
            return true;
        case CLEAR_ID:
            clear();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A call-back for when the user presses the back button.
     */
    OnClickListener mSendListener = new OnClickListener() {
        public void onClick(View v) {
        	sendAndStuff();
        }
    };
    
    public void sendAndStuff(){
    	new Thread(new CubeSolverRunnable(mProgressBar,mText,mHandler,mEditor.getText().toString())).start();
//    	Log.e("tag", "bhere");
//    	char[] ca = mEditor.getText().toString().toCharArray();
//    	segments = new int[ca.length];
//    	for (int i = 0; i < ca.length; i++) {
//    		segments[i] = Integer.parseInt(""+ca[i]);
//		}
//    	Log.e("tag", ""+segments.length);
//    	new Thread(new Runnable(){
//			@Override
//			public void run() {
//				solveCube();
//			}
//    	}).start();
//    	
//    	  	Log.e("tag", "here");
 //   	mText.setText("the sqrt of " +mEditor.getText() + " is " 
//    	+ Double.valueOf(Math.sqrt(Integer.parseInt(mEditor.getText().toString()))).intValue());
    }

    public void clear(){
    	mEditor.setText("");
    	mText.setText("");
    }
    /**
     * A call-back for when the user presses the clear button.
     */
    OnClickListener mClearListener = new OnClickListener() {
        public void onClick(View v) {
            clear();
        }
    };
    
    
    /**********************************************************/
    
	static enum Dir {
		UP,DOWN,RIGHT,LEFT,IN,OUT
	}
	boolean[][][] cube = new boolean[4][4][4];
	Stack<String> trace = new Stack<String>();
    int[] segments = {2,3,1};
	
	/* 
	 * x   z
	 * |  /
	 * | /
	 * |/
	 * -----y
	 */
	
	public boolean tryMove(boolean changedDir,Dir dir, int x, int y, int z, int steps, int segmentsLeft){

		if (steps==0) {
			if (segmentsLeft > 0){
				int newSteps = segments[segments.length-segmentsLeft];
				switch (dir){
				case UP:
				case DOWN:
					return 
						(tryMove(true,Dir.RIGHT,x,y,z,newSteps,segmentsLeft-1)
						||
						tryMove(true,Dir.LEFT,x,y,z,newSteps,segmentsLeft-1)
						||
						tryMove(true,Dir.IN,x,y,z,newSteps,segmentsLeft-1)
						||
						tryMove(true,Dir.OUT,x,y,z,newSteps,segmentsLeft-1));
				case RIGHT:
				case LEFT:
					return 
						(tryMove(true,Dir.UP,x,y,z,newSteps,segmentsLeft-1)
						||
						tryMove(true,Dir.DOWN,x,y,z,newSteps,segmentsLeft-1)
						||
						tryMove(true,Dir.IN,x,y,z,newSteps,segmentsLeft-1)
						||
						tryMove(true,Dir.OUT,x,y,z,newSteps,segmentsLeft-1));
				case IN:
				case OUT:
					return 
						(tryMove(true,Dir.UP,x,y,z,newSteps,segmentsLeft-1)
						||
						tryMove(true,Dir.DOWN,x,y,z,newSteps,segmentsLeft-1)
						||
						tryMove(true,Dir.RIGHT,x,y,z,newSteps,segmentsLeft-1)
						||
						tryMove(true,Dir.LEFT,x,y,z,newSteps,segmentsLeft-1));
				}
				
			} else return true;
		} else {
			if (changedDir) {
				trace.push(dir.toString() + " " + steps);
			}
		}
		switch (dir) {
			case UP: 
				if (x<3 && cube[x+1][y][z] == false) {
					cube[x+1][y][z] = true;
					boolean res = tryMove(false,dir, x+1, y, z, steps-1, segmentsLeft);
					if (res == false) cube[x+1][y][z] = false;
					return res;
				}
				break;
			case DOWN: 
				if (x>0 && cube[x-1][y][z] == false){
					cube[x-1][y][z]  = true;
					boolean res = tryMove(false,dir, x-1, y, z, steps-1, segmentsLeft);
					if (res == false) cube[x-1][y][z] = false;
					return res;
				}
				break;
			case RIGHT: 
				if (y<3 && cube[x][y+1][z] == false){
					cube[x][y+1][z] = true;
					boolean res = tryMove(false,dir, x, y+1, z, steps-1, segmentsLeft);
					if (res == false) cube[x][y+1][z] = false;
					return res;
				}
				break;
			case LEFT: 
				if (y>0 && cube[x][y-1][z] == false){
					cube[x][y-1][z] = true;
					boolean res = tryMove(false,dir, x, y-1, z, steps-1, segmentsLeft);
					if (res == false) cube[x][y-1][z] = false;
					return res;
				}
				break;
			case IN: 
				if (z<3 && cube[x][y][z+1] == false){
					cube[x][y][z+1] = true;
					boolean res = tryMove(false,dir, x, y, z+1, steps-1, segmentsLeft);
					if (res == false) cube[x][y][z+1] = false;
					return res;
				}
				break;
			case OUT: 
				if (z>0 && cube[x][y][z-1] == false){
					cube[x][y][z-1] = true;
					boolean res = tryMove(false,dir, x, y, z-1, steps-1, segmentsLeft);
					if (res == false) cube[x][y][z-1] = false;
					return res;
				}
				break;
		}
		trace.pop();
		return false;
	}
	
	public synchronized void solveCube() {
		mHandler.post(new Runnable(){

			@Override
			public void run() {
				mText.setText("solving...");	
			}
			
		});
		//mText.setText("solving...");
		for (int x=0; x<4; x++)
			for (int y=0; y<4; y++)
				for (int z=-1; z<3; z++){
						if (tryMove(true,Dir.IN, x, y, z, segments[0], segments.length-1) == true){
							final String str = "start at ("+x+","+y+","+ z + ") \n" + trace;
							mHandler.post(new Runnable(){

								@Override
								public void run() {
									mText.setText(str);	
								}
								
							});
							trace.clear();
							cube = new boolean[4][4][4];
							return;
						}
						
				}
	}
	//static int[] segments = {3,1,2,1,1,3,1,2,1,2};
}
