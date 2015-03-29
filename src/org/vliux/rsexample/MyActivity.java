package org.vliux.rsexample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MyActivity extends Activity {
    private RenderScript mRenderScript;
    private ImageView mImageView;
    private SeekBar mSeekBar;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mImageView = (ImageView)findViewById(R.id.iv);
        mSeekBar = (SeekBar)findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                doRender(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        doRender(mSeekBar.getProgress());
    }


    private void doRender(float radius){
        if(null == mRenderScript){
            mRenderScript = RenderScript.create(this);
        }

        //int width = mImageView.getWidth();
        //int height = mImageView.getHeight();
        Bitmap bmpIn = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
        Bitmap outBmp = Bitmap.createBitmap(bmpIn.getWidth(), bmpIn.getHeight(), Bitmap.Config.ARGB_8888);
        Allocation allocIn = Allocation.createFromBitmap(mRenderScript, bmpIn);
        Allocation allocOut = Allocation.createTyped(mRenderScript, allocIn.getType());
        ScriptIntrinsicBlur scriptIntrinsic = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
        scriptIntrinsic.setInput(allocIn);

        if(radius <= 0f){ radius = 0.1f;}
        if(radius > 25f){ radius = 25f;}
        scriptIntrinsic.setRadius(radius);
        scriptIntrinsic.forEach(allocOut);
        allocOut.copyTo(outBmp);
        mImageView.setImageBitmap(outBmp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mRenderScript){
            mRenderScript.destroy();
        }
    }
}
