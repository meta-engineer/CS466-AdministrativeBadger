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
import com.yys.cs446.es.castle_model.player.RESOURCES;
import com.yys.cs446.es.castle_model.tile;
import com.yys.cs446.es.castle_model.tile.TILETYPE;
import com.yys.cs446.es.castle_model.unit;
import com.yys.cs446.es.castle_model.worker;
import com.yys.cs446.es.castle_model.troop;
import com.yys.cs446.es.castle_model.settler;

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
    
    // maybe view only needs to know about myGrid.getTiles()
    // if object is copied it will need strict updates (but avoids race conditions?)
    private grid myGrid = null;

    private player myPlayer = null;

    // bitmap dictionaries for tiles
    private HashMap<String, Bitmap> originalBitmaps;
    private HashMap<String, Bitmap> scaledBitmaps;

    // unit bitmaps need own original/scaled dicts
    private Bitmap worker1Bitmap;
    private Bitmap worker2Bitmap;
    private Bitmap troopLeft1Bitmap;

    private Bitmap tileProgressFrame;
    private Bitmap tileProgressFill;
    private Bitmap tileHpFrame;
    private Bitmap tileHpFill;
    private Bitmap unitHpFrame;
    private Bitmap unitHpFill;

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
        xPaintSquare.setColor(Color.WHITE);
        xPaintSquare.setTextSize(30);

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

        // MAKE SURE ADDED BITMAPS ARE ALSO RESIZED IN updateBitmapScale
        worker1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.worker1);
        worker2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.worker2);
        troopLeft1Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.troop_right1);

        tileProgressFrame = BitmapFactory.decodeResource(getResources(), R.drawable.progress_bar_frame);
        tileProgressFill = BitmapFactory.decodeResource(getResources(), R.drawable.progress_bar_fill);
        tileHpFrame = BitmapFactory.decodeResource(getResources(), R.drawable.hp_bar_frame);
        tileHpFill = BitmapFactory.decodeResource(getResources(), R.drawable.hp_bar_fill);
        unitHpFrame = BitmapFactory.decodeResource(getResources(), R.drawable.unit_hp_bar_frame);
        unitHpFill = BitmapFactory.decodeResource(getResources(), R.drawable.unit_hp_bar_fill);

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

    public int[] convertIndiciesToPixels(double xIndex, double yIndex) {
        int x = (int) (yIndex * tileWidth * 0.74) - Math.round(originX) ;
        int y = (int) (yIndex * tileHeight * 0.29 + xIndex * tileHeight * 0.577) - Math.round(originY);
        int[] tup = {x,y};
        return tup;
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

        tileProgressFrame = getResizedBitmap(tileProgressFrame, (int)(getWidth() / zoomFactor), (int)((getWidth()  / zoomFactor) * 1.5));
        tileProgressFill = getResizedBitmap(tileProgressFill, (int)(getWidth() / zoomFactor), (int)((getWidth()  / zoomFactor) * 1.5));

        // build unit dimensions off adjusted tile dimensions
        tileWidth = scaledBitmaps.get("grassTile").getWidth();
        tileHeight = scaledBitmaps.get("grassTile").getHeight();

        worker1Bitmap = getResizedBitmap(worker1Bitmap, (int)tileWidth / 2, (int)tileHeight / 3);
        worker2Bitmap = getResizedBitmap(worker2Bitmap, (int)tileWidth / 2, (int)tileHeight / 3);

        troopLeft1Bitmap = getResizedBitmap(troopLeft1Bitmap, (int)tileWidth / 2, (int)tileHeight / 3);

        tileHpFrame = getResizedBitmap(tileHpFrame, (int)tileWidth / 2, (int)tileHeight / 3);
        tileHpFill = getResizedBitmap(tileHpFill, (int)tileWidth / 2, (int)tileHeight / 3);
        unitHpFrame = getResizedBitmap(unitHpFrame, (int)tileWidth / 2, (int)tileHeight / 3);
        unitHpFill = getResizedBitmap(unitHpFill, (int)tileWidth / 2, (int)tileHeight / 3);

    }

    public void setCamera(int xIndex, int yIndex) {
        int[] newOrigin = convertIndiciesToPixels(xIndex, yIndex);
        originX = newOrigin[0];
        originY = newOrigin[1];
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
        
        tile[][] gridArray = myGrid.getTiles();

        // draw land tiles themselves
        for (int i = 0; i < gridArray.length; i++) {
            for (int j = 0; j < gridArray[i].length; j++) {
                //Log.d("SIZE DEBUG***:", "width: " + grassTileBitmap.getWidth());
                //Log.d("SIZE DEBUG***:", "height: " + grassTileBitmap.getHeight());

                int[] pix = convertIndiciesToPixels(i,j);
                int x = pix[0];
                int y = pix[1];

                // out of "draw distance"
                if (x < -tileWidth || y < -tileHeight || x > getWidth() || y > getHeight()) {
                    continue;
                }

                if (gridArray[i][j].getType() == TILETYPE.NONE) {
                    // let space be "empty"
                    continue;
                }

                if (!myPlayer.isTileVisible(i, j)) {
                    //** no fog of war for debugging
                    canvas.drawBitmap(scaledBitmaps.get("fogTile"), x, y, null);
                    continue;
                }

                if (gridArray[i][j].getType() == TILETYPE.GRASS) {
                    canvas.drawBitmap(scaledBitmaps.get("grassTile"), x, y, null);
                } else if (gridArray[i][j].getType() == TILETYPE.GRAIN) {
                    canvas.drawBitmap(scaledBitmaps.get("grainTile"), x, y, null);
                } else if (gridArray[i][j].getType() == TILETYPE.WOOD) {
                    canvas.drawBitmap(scaledBitmaps.get("woodsLightTile"), x, y, null);
                } else if (gridArray[i][j].getType() == TILETYPE.WATER) {
                    canvas.drawBitmap(scaledBitmaps.get("waterTile"), x, y, null);
                } else if (gridArray[i][j].getType() == TILETYPE.TOWN) {
                    canvas.drawBitmap(scaledBitmaps.get("townTile"), x, y, null);
                } else if (gridArray[i][j].getType() == TILETYPE.MOUNTAIN) {
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

        //draw units
        for (player p : myGrid.getPlayers()) {
            for (unit u : p.myUnits) {
                int[] pix = convertIndiciesToPixels(u.get_location_x(), u.get_location_y());
                Bitmap unitBitmap;
                if (u instanceof worker) {
                    unitBitmap = worker1Bitmap;
                } else if (u instanceof troop) {
                    unitBitmap = troopLeft1Bitmap;
                } else {
                    unitBitmap = troopLeft1Bitmap;
                }
                canvas.drawBitmap(unitBitmap,
                        pix[0] + tileWidth/2 - worker1Bitmap.getWidth()/2,
                        pix[1] + tileHeight*2/3 - worker1Bitmap.getHeight()/2,
                        null);
                canvas.drawBitmap(unitHpFrame,
                        pix[0] + tileWidth/2 - worker1Bitmap.getWidth()/2,
                        pix[1] + tileHeight*2/3 - worker1Bitmap.getHeight()/2,
                        null);
                canvas.drawBitmap(unitHpFill,
                        pix[0] + tileWidth/2 - worker1Bitmap.getWidth()/2,
                        pix[1] + tileHeight*2/3 - worker1Bitmap.getHeight()/2,
                        null);
            }
        }

        // draw player overlay information
        // **This is not UI which is outside of tileView
        {
            //int x = (int) (myPlayer.getSelectY() * tileWidth * 0.7143) - Math.round(originX);
            //int y = (int) (myPlayer.getSelectY() * tileHeight * 0.2976 + myPlayer.getSelectX() * tileHeight * 0.5813) - Math.round(originY);
            //canvas.drawBitmap(scaledBitmaps.get("selectedTile"), x, y, null);
            if (myPlayer.getUnitProgress() > 0) {
                int[] homeCoords = convertIndiciesToPixels(myPlayer.getHomeCoord()[0], myPlayer.getHomeCoord()[1]);
                canvas.drawBitmap(tileProgressFrame, homeCoords[0], homeCoords[1], null);
                // can resize bitmap on the fly for dynamic size?
                //canvas.drawBitmap(getResizedBitmap(tileProgressFill, (int)(tileWidth * ((float)myPlayer.getUnitProgress() / myPlayer.getUnitProgressMax()))+1, (int)tileHeight),
                //        homeCoords[0], homeCoords[1], null);
            }
        }

        // loop through player resourceInventory and print key: values
        String text = "";
        for (RESOURCES r : myPlayer.getResourceInventory().keySet()) {
            text = text.concat(r.toString() + ": " + myPlayer.getResourceInventory().get(r).toString() + "\n ");
        }
        canvas.drawText(text, 20, 50, xPaintSquare);
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
