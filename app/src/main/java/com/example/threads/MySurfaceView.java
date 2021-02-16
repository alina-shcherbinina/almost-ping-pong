package com.example.threads;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import java.util.Random;


public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    DrawThread thread;
    SurfaceHolder surfaceHolder;


    Rect myRect = new Rect();
    Paint p3 = new Paint();
    class DrawThread extends Thread {
        float x = 300, y = 300; //for first ball
        float x1 = 300, y1 = 300; //for sec ball

        int rad = 30;
        Random r = new Random();
        Paint p = new Paint(); // for first ball
        Paint p2 = new Paint(); // for sec ball
         // rect

        boolean runFlag = true;
        // в конструкторе нужно передать holder для дальнейшего доступа к канве
        public DrawThread(SurfaceHolder holder) {
            this.holder = holder;
        }


        SurfaceHolder holder;


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            super.run();
            p.setColor(Color.rgb(79,144,242)); // blue
            p2.setColor(Color.rgb(32,162,28)); // green



            // выполняем цикл (рисуем кадры) пока флаг включен
            while (runFlag) {
                Canvas c = holder.lockCanvas();
                myRect.set(400, 400,  600, 600);
                // если успешно захватили канву
                if (c != null) {

                    c.drawColor(Color.rgb(202,145,145)); // display color

                    // случайные блуждания - сдвигаем координаты шарика в случайную сторону
                    //r.nextFloat() * 10 - 5;

                    if (x >= c.getWidth() - rad  || x1 >= c.getWidth() - rad ) {
                        x -= r.nextFloat() * 100 - 5;
                        x1 -= r.nextFloat() * 100 - 5;

                    } else if (y >= c.getHeight() - rad || y1 >= c.getHeight() - rad ) {
                        y -= r.nextFloat() * 100 - 5;
                        y1 -= r.nextFloat() * 100 - 5;

                    } else if (x < rad || x1 < rad){
                        x += r.nextFloat() * 100 - 5;
                        x1 += r.nextFloat() * 100 - 5;

                    } else if (y < rad || y1 < rad) {
                        y += r.nextFloat() * 100 - 5;
                        y1 += r.nextFloat() * 100 - 5;

                    }
                    else if (x1 == x || y1 == y) {
                        x -= r.nextFloat() * 100 - 5;
                        x1 += r.nextFloat() * 100 - 5;
                        y += r.nextFloat() * 100 - 5;
                        y1 -= r.nextFloat() * 100 - 5;
                    }
                    else if ((x >= myRect.left && x <= myRect.right) && (y >= myRect.top && y <= myRect.bottom) ) {
                        x -= r.nextFloat() * 100 - 5;
                        y += r.nextFloat() * 100 - 5;

                        float rand = r.nextFloat() * 100;
                        float rand1 = r.nextFloat() * 100;
                        float rand2 = r.nextFloat() * 100;

                        p.setColor(Color.rgb( (int)rand , (int)rand1 , (int)rand2 ));
                    }
                    else if ((x1 >= myRect.left && x1 <= myRect.right) && (y1 >= myRect.top && y1 <= myRect.bottom) ) {
                        x1 -= r.nextFloat() * 100 - 5;
                        y1 += r.nextFloat() * 100 - 5;

                        float rand = r.nextFloat() * 100;
                        float rand1 = r.nextFloat() * 100;
                        float rand2 = r.nextFloat() * 100;

                        p2.setColor(Color.rgb( (int)rand , (int)rand1 , (int)rand2 ));
                    }
                    else {
                        x+= r.nextFloat() * 100 - 50;
                        y+= r.nextFloat() * 100 - 50;

                        x1 += r.nextFloat() * 100 - 50;
                        y1 += r.nextFloat() * 100 - 50;
                    }

                    c.drawCircle(x,y,rad,p);
                    c.drawCircle(x1,y1,rad,p2);
                    c.drawRect(myRect, p3);
                    Log.i("TAG",  "BLUE x " + x + " y " + y);
                    Log.i("TAG",  "GREEN  x1 " + x1 + " y1 " + y1);
                    holder.unlockCanvasAndPost(c);

                    // нужна пауза на каждом кадре
                    try {
                        Thread.sleep(100); }
                    catch (InterruptedException e) {}
                }
            }

        }


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        p3.setColor(Color.BLACK);
        float xCoor = event.getX();
        float yCoor = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                myRect.set((int) xCoor, (int)xCoor, (int) yCoor,(int) yCoor);

                break;
        }
        return true;
    }
    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new DrawThread(holder);
        thread.start();
        // убеждаемся, что поток запускается
        Log.d("mytag", "DrawThread is running");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // при изменении конфигурации поверхности поток нужно перезапустить
        thread.runFlag = false;
        thread = new DrawThread(holder);
        thread.start();
    }

    // поверхность уничтожается - поток останавливаем
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.runFlag = false;
    }
}
