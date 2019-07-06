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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yys.cs446.es.R;
import com.yys.cs446.es.castle_model.grid;
import com.yys.cs446.es.castle_model.player;
import com.yys.cs446.es.castle_model.tile;
import com.yys.cs446.es.castle_model.tile.RESOURCES;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class tileView extends View {

    // class variables
    private Paint xPaintSquare;

    private boolean toggleSelect = false;

    private float originX = 0;
    private float originY = 0;
    private float lastOffsetX;
    private float lastOffsetY;
    private float lastPinchDistance;
    private float zoomFactor = (float)4.5;
    
    // maybe view only needs to know about myGrid.getgrid()
    // if object is copied it will need strict updates (but avoids race conditions?)
    private grid myGrid = null;

    private player myPlayer = null;

    // bitmap dictionaries for tiles
    private HashMap<String, Bitmap> originalBitmaps;
    private HashMap<String, Bitmap> scaledBitmaps;

    private Bitmap worker1Bitmap;
    private Bitmap worker2Bitmap;

    private float tileWidth = 170;
    private float tileHeight = 300;


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

        //load original Tile Bitmaps
        originalBitmaps = new HashMap<String, Bitmap>();
        originalBitmaps.put("grassTile", BitmapFactory.decodeResource(getResources(), R.drawable.grass));
        originalBitmaps.put("grainTile", BitmapFactory.decodeResource(getResources(), R.drawable.grain));
        originalBitmaps.put("woodsLightTile", BitmapFactory.decodeResource(getResources(), R.drawable.woods_light));
        originalBitmaps.put("waterTile", BitmapFactory.decodeResource(getResources(), R.drawable.water));
        originalBitmaps.put("townTile", BitmapFactory.decodeResource(getResources(), R.drawable.town));
        originalBitmaps.put("ownedTile", BitmapFactory.decodeResource(getResources(), R.drawable.tile_owned));
        originalBitmaps.put("selectedTile", BitmapFactory.decodeResource(getResources(), R.drawable.tile_selected));
        originalBitmaps.put("fogTile", BitmapFactory.decodeResource(getResources(), R.drawable.fog));
        originalBitmaps.put("mountainTile", BitmapFactory.decodeResource(getResources(), R.drawable.mountain));

        scaledBitmaps = new HashMap<String, Bitmap>(originalBitmaps);

        worker1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.worker1);
        worker2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.worker2);


        // when ui is built get dimensions
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    updateBitmapScale();
             }
        });

    }

    public void update(grid g, player p) {
        myGrid = g;
        myPlayer = p;
        // generate tooltip text?
        postInvalidate();
    }

    public int convertIndiciesToPixels(int xIndex, int yIndex) {

        // TODO: must return tuple(?) with x and y ??
        return 0;
    }

    // uses private zoomfactor and rescales bitmaps
    // must rescale ORIGINAL bitmaps to avoid accumulating compression
    private void updateBitmapScale() {
        Iterator it = originalBitmaps.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            // use pair.getKey(), pair.getValue()
            scaledBitmaps.put(pair.getKey().toString(), getResizedBitmap(originalBitmaps.get(pair.getKey()), (int)(getWidth() / zoomFactor), (int)((getWidth()  / zoomFactor) * 1.5)));
            it.remove();
        }


        // build unit dimensions off adjusted tile dimensions
        tileWidth = scaledBitmaps.get("grassTile").getWidth();
        tileHeight = scaledBitmaps.get("grassTile").getHeight();

        worker1Bitmap = getResizedBitmap(worker1Bitmap, (int)tileWidth / 2, (int)tileHeight / 3);
        worker2Bitmap = getResizedBitmap(worker2Bitmap, (int)tileWidth / 2, (int)tileHeight / 3);

    }

    public void setCamera(int xIndex, int yIndex) {
        originX = ((float)yIndex * tileWidth * (float)0.7143) + (tileWidth / 2);
        originY = ((float)yIndex * tileHeight * (float)0.2976 + xIndex * tileHeight * (float)0.5813) + (tileHeight / 2);
        //Log.d("CAMERA DEBUG***:", "R: " + tileWidth);
        //Log.d("CAMERA DEBUG***:", "X: " + originX);
        //Log.d("CAMERA DEBUG***:", "Y: " + originY);
    }

    public void interact() {
        toggleSelect = !toggleSelect;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //canvas.drawRect(xRectSquare, xPaintSquare);

        // catches baf/ uninitialized states
        if (myGrid == null) {
            //canvas.drawBitmap(originalBitmaps.get("townTile"), 0, 0, null);
            return;
        }
        
        tile[][] gridArray = myGrid.getGrid();

        // draw land tiles themselves
        for (int i = 0; i < gridArray.length; i++) {
            for (int j = 0; j < gridArray[i].length; j++) {
                //Log.d("SIZE DEBUG***:", "width: " + grassTileBitmap.getWidth());
                //Log.d("SIZE DEBUG***:", "height: " + grassTileBitmap.getHeight());


                int x = (int) (j * tileWidth * 0.74) - Math.round(originX) ;
                int y = (int) (j * tileHeight * 0.29 + i * tileHeight * 0.577) - Math.round(originY);

                // out of "draw distance"
                if (x < -tileWidth || y < -tileHeight || x > getWidth() || y > getHeight()) {
                    continue;
                }

                if (gridArray[i][j].getType() == RESOURCES.NONE) {
                    // let space be "empty"
                    continue;
                }

                if (myPlayer.isTileVisible(i, j) == false) {
                    //** no fog of war for debugging
                    //canvas.drawBitmap(scaledBitmaps.get("fogTile"), x, y, null);
                    //continue;
                }

                if (gridArray[i][j].getType() == RESOURCES.GRASS) {
                    canvas.drawBitmap(scaledBitmaps.get("grassTile"), x, y, null);
                } else if (gridArray[i][j].getType() == RESOURCES.GRAIN) {
                    canvas.drawBitmap(scaledBitmaps.get("grainTile"), x, y, null);
                } else if (gridArray[i][j].getType() == RESOURCES.WOOD) {
                    canvas.drawBitmap(scaledBitmaps.get("woodsLightTile"), x, y, null);
                } else if (gridArray[i][j].getType() == RESOURCES.WATER) {
                    canvas.drawBitmap(scaledBitmaps.get("waterTile"), x, y, null);
                } else if (gridArray[i][j].getType() == RESOURCES.TOWN) {
                    canvas.drawBitmap(scaledBitmaps.get("townTile"), x, y, null);
                } else if (gridArray[i][j].getType() == RESOURCES.MOUNTAIN) {
                    canvas.drawBitmap(scaledBitmaps.get("mountainTile"), x, y, null);
                }

                // draw player ile overlay info
                // may need testing for Zbuffer (draw order)
                // but should be good as long ad elements stay in the 2:3 width:length ratio
                if (myPlayer.isTileOwned(i,j)) {
                    canvas.drawBitmap(scaledBitmaps.get("ownedTile"), x, y, null);
                }
            }
        }

        // draw player overlay information
        // **This is not UI which is outside of tileView
        /*
        {
            int x = (int) (myPlayer.getSelectY() * tileWidth * 0.7143) - Math.round(originX);
            int y = (int) (myPlayer.getSelectY() * tileHeight * 0.2976 + myPlayer.getSelectX() * tileHeight * 0.5813) - Math.round(originY);
            canvas.drawBitmap(scaledBitmaps.get("selectedTile"), x, y, null);
        }
        */
        canvas.drawText("Grain: " + Integer.toString(5) + " Wood: " + Integer.toString(6), 100, 100, xPaintSquare);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // set init value for scrolling
                lastOffsetX = event.getX();
                lastOffsetY = event.getY();

                // set init value for pinching
                if (event.getPointerCount() == 2){
                    float xDist = event.getX(0) - event.getX(1);
                    float yDist = event.getY(0) - event.getY(1);
                    double squareDist = Math.pow(xDist, 2) + Math.pow(yDist, 2);
                    lastPinchDistance = (float)Math.sqrt(squareDist);
                }

                if (toggleSelect) {
                    toggleSelect = !toggleSelect;
                    int selectY = (int) Math.round((event.getX() - (tileWidth/2) + originX) / 0.7143 / tileWidth);
                    int selectX = (int) Math.round( ((event.getY() - (tileHeight/2) + originY) -  (selectY * tileHeight * 0.2976)) / tileHeight / 0.5813);
                    //myPlayer.selectTile(selectX, selectY);
                }
                return true;
            }
            case MotionEvent.ACTION_MOVE: {

                if (event.getPointerCount() == 2) {
                    float xDist = event.getX(0) - event.getX(1);
                    float yDist = event.getY(0) - event.getY(1);
                    double squareDist = Math.pow(xDist, 2) + Math.pow(yDist, 2);
                    float trueDist = (float)Math.sqrt(squareDist);
                    // Adjust zoomfactor and origin relatively based on trueDist
                    //zoomFactor += (trueDist - lastPinchDistance) / 1000;
                    //originX +=
                    //originY +=
                    lastPinchDistance = trueDist;
                    //updateBitmapScale();
                    // dont do other touch events
                    return true;
                }
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
