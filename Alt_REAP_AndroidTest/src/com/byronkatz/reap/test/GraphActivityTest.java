package com.byronkatz.reap.test;

import junit.framework.Assert;
import android.app.Activity;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.SeekBar;

import com.byronkatz.reap.activity.GraphActivity;

public class GraphActivityTest extends ActivityInstrumentationTestCase2<GraphActivity> {

	private Activity mActivity;
	private SeekBar mSeekBar;
	private com.byronkatz.reap.general.WrappingSlidingDrawer mDrawer;
	
	@SuppressWarnings("deprecation")
	public GraphActivityTest() {
		super("com.byronkatz.reap.activity.GraphActivity", GraphActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		mSeekBar = (SeekBar) mActivity.findViewById(com.byronkatz.reap.R.id.valueSlider);
		mDrawer = (com.byronkatz.reap.general.WrappingSlidingDrawer) mActivity.findViewById(com.byronkatz.reap.R.id.drawer);
	}
	
	public void testSeekBarValueStoredAfterPause() {
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mSeekBar.setProgress(33);
			}
		});
		getInstrumentation().callActivityOnPause(mActivity);
		SharedPreferences sp = mActivity.getSharedPreferences("TheName", 0);
		assertEquals(33, sp.getInt("valuebar", 0));
		
	}
	
	public void testSeekBarValueStoredAfterStop() {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mSeekBar.setProgress(33);
			}
		});
		getInstrumentation().callActivityOnStop(mActivity);
	}
	
	public void testSeekBarProperlyPlacedAfterResume() {	
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mSeekBar.setProgress(33);
			}
		});
		getInstrumentation().callActivityOnPause(mActivity);
		getInstrumentation().callActivityOnResume(mActivity);
		Assert.assertEquals(33, mSeekBar.getProgress());
	}
	
	public void testSeekBarProperlyPlacedAfterStart() {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mSeekBar.setProgress(33);
			}
		});
		
		mActivity.finish();
		Activity activity = getActivity();
		activity = getActivity();
		mSeekBar = (SeekBar) activity.findViewById(com.byronkatz.reap.R.id.valueSlider);
		Assert.assertEquals(33, mSeekBar.getProgress());
	}
	
	public void testSeekBarUI() {
		mActivity.runOnUiThread(new Runnable() {
		public void run() {
			mDrawer.open();
			mSeekBar.setProgress(20);
			mSeekBar.setProgress(40);
			mSeekBar.setProgress(60);
		}
		});
		
	}
	
	
}
