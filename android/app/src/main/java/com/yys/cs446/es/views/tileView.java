package com.yys.cs446.es.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yys.cs446.es.R;
import com.yys.cs446.es.castle_model.grid;
import com.yys.cs446.es.castle_model.player;

public class tileView extends View {

    // class variables
    private Paint xPaintSquare;

    private boolean toggleSelect = false;

    private float originX = 0;
    private float originY = 0;
    private float lastOffsetX;
    private float lastOffsetY;
    private grid myGrid = null;
    private player myPlayer = null;

    private Bitmap grassTileBitmap;
    private Bitmap grainTileBitmap;
    private Bitmap woodsTileBitmap;
    private Bitmap waterTileBitmap;
    private Bitmap townTileBitmap;
    private Bitmap playerOwnedTileBitmap;
    private Bitmap playerSelectTileBitmap;
    private Bitmap fogTileBitmap;

    private float tileWidth = 0;
    private float tileHeight = 0;


    public tileView(Context context) {
        super(context);

        init(null);
    }

    public tileView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public tileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public tileView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        xPaintSquare = new Paint(Paint.ANTI_ALIAS_FLAG);
        xPaintSquare.setColor(Color.BLACK);
        xPaintSquare.setTextSize(60);

        grassTileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grass);
        grainTileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grain);
        woodsTileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.woods_light);
        waterTileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water);
        townTileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.town);
        playerOwnedTileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile_owned);
        playerSelectTileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tile_selected);
        fogTileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fog);


        // when ui is built get dimensions
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int sizefactor = 4;

                grassTileBitmap = getResizedBitmap(grassTileBitmap, getWidth() / sizefactor, (int)((float)(getWidth()  / sizefactor) * 1.5));
                grainTileBitmap = getResizedBitmap(grainTileBitmap, getWidth() / sizefactor, (int)((float)(getWidth()  / sizefactor) * 1.5));
                woodsTileBitmap = getResizedBitmap(woodsTileBitmap, getWidth() / sizefactor, (int)((float)(getWidth()  / sizefactor) * 1.5));
                waterTileBitmap = getResizedBitmap(waterTileBitmap, getWidth() / sizefactor, (int)((float)(getWidth()  / sizefactor) * 1.5));
                townTileBitmap = getResizedBitmap(townTileBitmap, getWidth() / sizefactor, (int)((float)(getWidth()  / sizefactor) * 1.5));
                playerOwnedTileBitmap = getResizedBitmap(playerOwnedTileBitmap, getWidth() / sizefactor, (int)((float)(getWidth()  / sizefactor) * 1.5));
                playerSelectTileBitmap = getResizedBitmap(playerSelectTileBitmap, getWidth() / sizefactor, (int)((float)(getWidth()  / sizefactor) * 1.5));
                fogTileBitmap = getResizedBitmap(fogTileBitmap, getWidth() / sizefactor, (int)((float)(getWidth()  / sizefactor) * 1.5));

                tileWidth = grassTileBitmap.getWidth();
                tileHeight = grassTileBitmap.getHeight();

             }
        });

    }

    public void update(castle_model.grid g, castle_model.player p) {
        myGrid = g;
        myPlayer = p;
        // generate tooltip text?
        postInvalidate();
    }

    public int convertIndiciesToPixels(int xIndex, int yIndex) {

        // TODO: must return tuple(?) with x and y ??
        return 0;
    }

    public void setCamera(int xIndex, int yIndex) {
        originX = ((float)yIndex * tileWidth * (float)0.7143) + (tileWidth / 2);
        originY = ((float)yIndex * tileHeight * (float)0.2976 + xIndex * tileHeight * (float)0.5813) + (tileHeight / 2);
        //Log.d("CAMERA DEBUG***:", "R: " + tileWidth);
        //Log.d("CAMERA DEBUG***:", "X: " + originX);
        //Log.d("CAMERA DEBUG***:", "Y: " + originY);
    }

    public void interact() {
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (myGrid == null) {
            return;
        }
        canvas.drawBitmap(grassTileBitmap, 0, 0, null);
        /*
        base_tile[][] gridArray = myGrid.getGrid();

        for (int i = 0; i < gridArray.length; i++) {
            for (int j = 0; j < gridArray[i].length; j++) {
                //Log.d("SIZE DEBUG***:", "width: " + grassTileBitmap.getWidth());
                //Log.d("SIZE DEBUG***:", "height: " + grassTileBitmap.getHeight());


                int x = (int) (j * tileWidth * 0.7143) - Math.round(originX) ;
                int y = (int) (j * tileHeight * 0.2976 + i * tileHeight * 0.5813) - Math.round(originY);

                if (myPlayer.isTileVisible(i, j) == false) {
                    canvas.drawBitmap(fogTileBitmap, x, y, null);
                    continue;
                }

                if (gridArray[i][j].getType() == grid.RESOURCES.NONE) {
                    canvas.drawBitmap(grassTileBitmap, x, y, null);
                } else if (gridArray[i][j].getType() == grid.RESOURCES.GRAIN) {
                    canvas.drawBitmap(grainTileBitmap, x, y, null);
                } else if (gridArray[i][j].getType() == grid.RESOURCES.WOOD) {
                    canvas.drawBitmap(woodsTileBitmap, x, y, null);
                } else if (gridArray[i][j].getType() == grid.RESOURCES.WATER) {
                    canvas.drawBitmap(waterTileBitmap, x, y, null);
                } else if (gridArray[i][j].getType() == grid.RESOURCES.TOWN) {
                    canvas.drawBitmap(townTileBitmap, x, y, null);
                }
            }
        }

        for (base_tile t : myPlayer.getOwnedTiles()) {
            int x = (int) (t.get_y() * tileWidth * 0.7143) - Math.round(originX);
            int y = (int) (t.get_y() * tileHeight * 0.2976 + t.get_x() * tileHeight * 0.5813) - Math.round(originY);
            canvas.drawBitmap(playerOwnedTileBitmap, x, y, null);
        }
        {
            int x = (int) (myPlayer.getSelectY() * tileWidth * 0.7143) - Math.round(originX);
            int y = (int) (myPlayer.getSelectY() * tileHeight * 0.2976 + myPlayer.getSelectX() * tileHeight * 0.5813) - Math.round(originY);
            canvas.drawBitmap(playerSelectTileBitmap, x, y, null);
        }
        */

        //canvas.drawText("Grain: " + Integer.toString((int)myPlayer.getOwnedResources()[1]) + " Wood: " + Integer.toString((int)myPlayer.getOwnedResources()[2]), 100, 100, xPaintSquare);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastOffsetX = event.getX();
                lastOffsetY = event.getY();

                int selectY = (int) Math.round((event.getX() - (tileWidth/2) + originX) / 0.7143 / tileWidth);
                int selectX = (int) Math.round( ((event.getY() - (tileHeight/2) + originY) -  (selectY * tileHeight * 0.2976)) / tileHeight / 0.5813);
                // Use selected indicies somehow
                //myPlayer.selectTile(selectX, selectY);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                float x = event.getX();
                float y = event.getY();

                //alter class values
                originX += lastOffsetX - x;
                originY += lastOffsetY - y;

                lastOffsetX = x;
                lastOffsetY = y;
                postInvalidate();
                return true;
            }
            default: {
                return true;
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int reqW, int reqH) {
        Matrix matrix = new Matrix();

        RectF src = new RectF(0,0, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(0,0, reqW, reqH);

        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
