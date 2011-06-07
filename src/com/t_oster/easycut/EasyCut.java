package com.t_oster.easycut;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.t_oster.liblasercut.BlackWhiteRaster;
import com.t_oster.liblasercut.EngravingProperty;
import com.t_oster.liblasercut.IllegalJobException;
import com.t_oster.liblasercut.LaserJob;
import com.t_oster.liblasercut.RasterPart;
import com.t_oster.liblasercut.epilog.EpilogCutter;
import com.t_oster.util.BitmapAdapter;
import com.t_oster.util.Util;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EasyCut extends Activity implements OnClickListener {

    public static final int MSG_EXCEPTION = 0;
    public static final int MSG_PROGRESSTEXT = 1;
    
    private static final int PICTURE_RESULT = 10;
    private File tempFile;
    private SelectRectView selView;
    private Button bOk;
    private Bitmap fullImage;
    private ProgressDialog pd;
    private Handler handler;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         this.handler = new Handler(){
            @Override
            public void handleMessage(Message m)
            {
                switch(m.what)
                {
                    case MSG_EXCEPTION:
                        Toast.makeText(EasyCut.this, "Exception: "+m.obj, Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_PROGRESSTEXT:
                        pd.setMessage(m.obj.toString());
                        break;
                }
            }
         };
        setContentView(R.layout.main);
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempFile = new File(Environment.getExternalStorageDirectory() + "/make_machine_example.jpg");
        camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        this.startActivityForResult(camera, PICTURE_RESULT);
       
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICTURE_RESULT) //
        {
            if (resultCode == Activity.RESULT_OK) {
                // Display image received on the view
                fullImage = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                selView = new SelectRectView(this);
                selView.setImageBitmap(fullImage);
                bOk = new Button(this);
                bOk.setText("OK");
                bOk.setOnClickListener(this);
                LinearLayout ll = new LinearLayout(this);
                ll.addView(bOk);
                ll.addView(selView);

                setContentView(ll);
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    public void onClick(View view) {
        if (view.equals(bOk)) {
            pd = ProgressDialog.show(this, "Please wait", "processing", true, false);
            new Thread() {

                @Override
                public void run() {
                    int x = (int) (selView.getSelectedX() * fullImage.getWidth() / 100);
                    int y = (int) (selView.getSelectedY() * fullImage.getHeight() / 100);
                    int w = (int) (selView.getSelectedWidth() * fullImage.getWidth() / 100);
                    int h = (int) (selView.getSelectedHeight() * fullImage.getHeight() / 100);
                    try
                    {
                        int dpi = 500;
                        int ewidth = (int) Util.mm2px(50, dpi);
                        int eheight = (int) (h * ewidth / w);
                        
                        Matrix m = new Matrix();
                        RectF src = new RectF(x,y,x+w,y+h);
                        RectF dst = new RectF(0,0,ewidth,eheight);
                        if (!m.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER))
                        {
                            throw new Exception("Passt nicht");
                        }
                        EasyCut.this.handler.sendMessage(Message.obtain(EasyCut.this.handler, MSG_PROGRESSTEXT,"Scaling..."));
                        //Bitmap testScale = Bitmap.createScaledBitmap(fullImage, endwidth, endheight, true);
                        Bitmap selection = Bitmap.createBitmap(fullImage, x, y, w, h, m, false);
                        EasyCut.this.handler.sendMessage(Message.obtain(EasyCut.this.handler, MSG_PROGRESSTEXT,"Dithering..."));
                        
                        //Bitmap scaled = Bitmap.createScaledBitmap(selection, endwidth, endheight, true);
                        BlackWhiteRaster bwr = new BlackWhiteRaster(new BitmapAdapter(selection), BlackWhiteRaster.DITHER_FLOYD_STEINBERG);
                        RasterPart rp = new RasterPart(new EngravingProperty(40, 100));
                        EpilogCutter instance = new EpilogCutter("137.226.56.228");
                        EasyCut.this.handler.sendMessage(Message.obtain(EasyCut.this.handler, MSG_PROGRESSTEXT,"Printing..."));
                        
                        instance.sendJob(new LaserJob("android", "bla", "bla", dpi, null, null, rp));
                        pd.dismiss();
                    }
                    catch (Exception e)
                    {
                        pd.dismiss();
                        EasyCut.this.handler.sendMessage(Message.obtain(EasyCut.this.handler, MSG_EXCEPTION,e));
                    }
                        
                }
            }.start();
            
        }
    }
}
