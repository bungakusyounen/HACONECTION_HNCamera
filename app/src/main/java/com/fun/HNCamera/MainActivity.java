package com.fun.HNCamera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fun.HNCamera.R;

import static android.R.attr.tag;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends Activity implements View.OnClickListener {

    private Uri m_uri;
    private Uri fileUri;
    private static final int REQUEST_CHOOSER = 1000;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int EMO = 999;
    private final int PHY = 998;
    private final int CUL = 997;
    private final Handler handler = new Handler();
    private int currentColor;
    private ImageButton Physical;
    private ImageButton Culture;
    private ImageButton Emotional;
    private int physicalStatus = 0;
    private int cultureStatus = 0;
    private int emotionalStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setbuttonListener();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelected(false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setColor(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setHSV(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setHSV(float selected) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.liner1);
        float[] hsv = new float[3];
        Color.colorToHSV(currentColor, hsv);
        hsv[2] = (float) 0.5 + selected / 200;
        layout.setBackgroundColor(Color.HSVToColor(hsv));
    }

    private void setColor(int selected) {
        //背景(layoutView1)の状態を取得(インタンスを生成)
        LinearLayout layout = (LinearLayout) findViewById(R.id.liner1);
        //ボタンの状態(id)を取得
        switch (selected) {
            //0(1つ目)
            case 0:
                Toast.makeText(getApplicationContext(), "Nothing", Toast.LENGTH_SHORT).show();
                //背景色を白に変更
                currentColor = Color.rgb(255, 255, 255);
                break;
            //(12つ目)
            case 1:
                Toast.makeText(getApplicationContext(), "Positive", Toast.LENGTH_SHORT).show();
                //背景色を青に変更

                currentColor = Color.rgb(255, 200, 0);
                break;
            //2(3つ目)
            case 2:
                Toast.makeText(getApplicationContext(), "Negative", Toast.LENGTH_SHORT).show();
                //背景色を赤に変更
                currentColor = Color.rgb(190, 10, 120);
                break;
        }
        layout.setBackgroundColor(currentColor);
    }

    private void setbuttonListener() {
        Button button1 = (Button) findViewById(R.id.buttonPanel);
        Button button2 = (Button) findViewById(R.id.camera_button);
        Button save = (Button) findViewById(R.id.Savebutton);
//        ImageButton stampbutton = (ImageButton)findViewById(R.id.imageButton);
        Culture = (ImageButton) findViewById(R.id.Culture);
        Culture.setOnClickListener(this);
        Physical = (ImageButton) findViewById(R.id.Physical);
        Physical.setOnClickListener(this);
        Emotional = (ImageButton) findViewById(R.id.Emotional);
        Emotional.setOnClickListener(this);
        button1.setOnClickListener(button1_onClick);
        button2.setOnClickListener(button2_onClick);
        save.setOnClickListener(save_click);
        //Culture.setOnClickListener(_onClick);
        //stampbutton.setOnClickListener(stampbutton_onclick);
    }



/*
    public  void setButton1_onClick(){
        System.out.println("クリック");
    }*/
    /*
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button1:
                //処理
                break;
        }
    }
*/

    /*private View.OnClickListener Culture_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(i == 1){
                i = 2;
                ImageButton.setImageResource(R.drawable.culture未選択);
            }else if(i == 2){
                i = 0;
                ImageButton.setImageResource(R.drawable.eatenapple);
            }else{
                ImageButton.setImageResource(R.drawable.eatenapple);
            }
        }
    };*/

    private View.OnClickListener button1_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showGallery();
        }
    };

    private View.OnClickListener button2_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playCamera();
        }
    };

    private View.OnClickListener stampbutton_onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
            int requestcode = 666;
            startActivityForResult(intent, requestcode);
        }
    };

    private View.OnClickListener save_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            run();
        }
    };

    private void playCamera() {

        //カメラの起動Intentの用意
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, 0);

    }

    private void showGallery() {
        // ギャラリー用のIntent作成
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {

            if (resultCode != RESULT_OK) {
                // キャル時
                return;
            }

            Uri resultUri = (data != null ? data.getData() : m_uri);

            if (resultUri == null) {
                // 取得失敗
                return;
            }

            MediaScannerConnection.scanFile(
                    this,
                    new String[]{resultUri.getPath()},
                    new String[]{"image/jpeg"},

                    null
            );
            // 画像を設定
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            int orientation = ImageUtil.getOrientation(resultUri);
            Log.d("tag", "orientation=" + orientation);
            Bitmap bmp = ImageUtil.createBitmapFromUri(this, resultUri, orientation);
            imageView.setImageBitmap(bmp);
            //imageView.setImageURI(resultUri);
        }
        if (requestCode == 666) {
            if (resultCode == Activity.RESULT_OK) {
                int flag = data.getIntExtra("stamp_number", -10);
                FrameLayout frame = (FrameLayout) findViewById(R.id.framelayout);
                FrameLayout.LayoutParams prams = new FrameLayout.LayoutParams(WC, WC);
                DragViewListener dvListener;
                switch (flag) {
                    case 1:
                        int emo_id = View.generateViewId();
                        ImageView emotional = new ImageView(this);
                        emotional.setImageResource(R.drawable.stampemotionnone);
                        emotional.setId(emo_id);
                        frame.addView(emotional, prams);
                        dvListener = new DragViewListener(emotional);
                        emotional.setOnTouchListener(dvListener);

                        break;
                    case 2:
                        int phy_id = View.generateViewId();
                        ImageView physical = new ImageView(this);
                        physical.setImageResource(R.drawable.stampphysicalnone);
                        physical.setId(phy_id);
                        frame.addView(physical, prams);
                        dvListener = new DragViewListener(physical);
                        physical.setOnTouchListener(dvListener);
                        break;
                    case 3:
                        int cul_id = View.generateViewId();
                        ImageView culture = new ImageView(this);
                        culture.setImageResource(R.drawable.stampcluturenone);
                        culture.setId(cul_id);
                        frame.addView(culture, prams);
                        dvListener = new DragViewListener(culture);
                        culture.setOnTouchListener(dvListener);
                        break;
                }
            }
        }
    }

    public void run() {
        handler.post(new Runnable() {
            public void run() {
                final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                final Date date = new Date(System.currentTimeMillis());
                String filename = Environment.getExternalStorageDirectory() + "/HN Camera/" + df.format(date) + ".png";

                final File file = new File(filename);
                final File dir = new File(Environment.getExternalStorageDirectory() + "/HN Camera/");

                if (!dir.exists()) {
                    boolean result = dir.mkdirs();
                    System.out.println(result);
                    dir.mkdirs();
                }
                file.getParentFile().mkdir();
                saveCapture(findViewById(android.R.id.content), file);
                Toast.makeText(MainActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                String[] filePath = {filename};
                String[] mimeType = {"image/*"};
                MediaScannerConnection.scanFile(getApplicationContext(), filePath, mimeType, null);
            }
        });
    }

    private void createFolderSaveImage(Bitmap imageToSave, String fileName) {
        String folderPath = Environment.getExternalStorageDirectory() + "/NewFolder/";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public void saveCapture(View view, File file) {
        Bitmap capture = getViewCapture(view);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            capture.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            final Date date = new Date(System.currentTimeMillis());
            registAndroidDB("/strage/emulated/O/Pictures/" + df.format(date) + ".png");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos == null) return;
            try {
                fos.close();
            } catch (Exception ie) {
                fos = null;
            }
        }
    }

    private void registAndroidDB(String path) {
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = this.getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "ge/jpeg");
        values.put("_date", path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    public Bitmap getViewCapture(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        if (cache == null) return null;
        Bitmap screen_shot = Bitmap.createBitmap(cache);
        view.setDrawingCacheEnabled(false);
//        ImageView imageview = (ImageView) findViewById(R.id.imageView1);
//        imageview.setImageBitmap(screen_shot);
        return screen_shot;
    }


    public void onRadioButtonClicked(View view) {
        // ラジオボタンの選択状態を取得
        RadioButton radioButton = (RadioButton) view;
        // getId()でラジオボタンを識別し、ラジオボタンごとの処理を行う
        //押してあるか否か
        boolean checked = radioButton.isChecked();

    }

    @Override
    public void onClick(View v) {
        if (v == Physical) {
            if (physicalStatus % 3 == 0) {
                Physical.setImageResource(R.drawable.stampphysical);
                physicalStatus++;
            } else if (physicalStatus % 3 == 1) {
                Physical.setImageResource(R.drawable.stampphysicalbad);
                physicalStatus++;
            } else if (physicalStatus % 3 == 2) {
                Physical.setImageResource(R.drawable.stampphysicalnone);
                physicalStatus++;
            }

        } else if (v == Culture) {
            if (cultureStatus % 3 == 0) {
                Culture.setImageResource(R.drawable.stampculture);
                cultureStatus++;
            } else if (cultureStatus % 3 == 1) {
                Culture.setImageResource(R.drawable.stampculturebad);
                cultureStatus++;
            } else if (cultureStatus % 3 == 2) {
                Culture.setImageResource(R.drawable.stampcluturenone);
                cultureStatus++;
            }

        }else if(v == Emotional){
            if(emotionalStatus%3==0) {
                Emotional.setImageResource(R.drawable.stampemotion);
                emotionalStatus++;
            }else if(emotionalStatus%3==1){
                Emotional.setImageResource(R.drawable.stampemotionbad);
                emotionalStatus++;
            }else if(emotionalStatus%3==2){
                Emotional.setImageResource(R.drawable.stampemotionnone);
                emotionalStatus++;
            }
        }
        }
    }

