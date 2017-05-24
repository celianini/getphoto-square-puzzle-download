package com.project.celianini.getphoto;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {


    private Button mButton, mbtnpuzzle;
    private ImageView mImageView;
    private Bitmap bitmap;
    private Bitmap[] puzzlePiecesArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.myButton);
        mImageView = (ImageView) findViewById(R.id.myImage);
        mbtnpuzzle=(Button)findViewById(R.id.btnpuzzle);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");      //開啟Pictures畫面Type設定為image
                intent.setAction(Intent.ACTION_GET_CONTENT);    //使用Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, 1);      //取得相片後, 返回
            }
        });
        mbtnpuzzle.setOnClickListener(mbtnpuzzleListener);
    }

    /* 取得相片後返回的監聽 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* 當使用者按下確定後 */
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();                       //取得圖檔的路徑
            Log.e("uri", uri.toString());                   //寫log
            ContentResolver cr = this.getContentResolver(); //抽象資料的接口

            try {
                /* 由抽象資料接口轉換圖檔路徑為Bitmap */
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                mImageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Button.OnClickListener mbtnpuzzleListener = new Button.OnClickListener() {
        public void onClick(View v) {
            FileOutputStream fos = null;

                //Provide number of rows and column
                int row = 4;
                int col = 3;

                //total width and total height of an image
                int tWidth = bitmap.getWidth();
                int tHeight = bitmap.getHeight();

                //   System.out.println("Image Dimension: " + tWidth + "x" + tHeight);

                //width and height of each piece
                int eWidth = tWidth / col;
                int eHeight = tHeight / row;
                puzzlePiecesArray = new Bitmap[col * row];

                int counter = 0;
                for (int i = 0; i < row; i++) {

                    for (int j = 0; j < col; j++) {
                        try {
                           // System.out.println("creating piece: " + i + " " + j);
                            File f = new File(MainActivity.this.getCacheDir(), "images" + counter + ".png");
                            f.createNewFile();
                            puzzlePiecesArray[counter] = Bitmap.createBitmap(bitmap,
                                   i * eWidth, j * eHeight, eWidth, eHeight);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            puzzlePiecesArray[counter].compress(Bitmap.CompressFormat.PNG, 30 /*ignored for PNG*/, bos);
                            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                            fos = new FileOutputStream(f);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                            // BufferedImage SubImgage = originalImgage.getSubimage(y, x, eWidth, eHeight);
                            //ImageIO.write(SubImgage, "jpg", outputfile);

                            counter++;


                            if (fos != null) {
                                Toast.makeText(MainActivity.this, "fileoutputstream success", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "fileoutputstream fail", Toast.LENGTH_LONG).show();
                        }
                    }


                }


        }

    };
}


