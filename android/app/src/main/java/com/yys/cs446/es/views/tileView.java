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
    private double zoomFactor;
    
    // maybe view only needs to know about myGrid.getTiles()
    // if object is copied it will need strict updates (but avoids race conditions?)
    private grid myGrid = null;

    private player myPlayer = null;

    // bitmap dictionaries for tiles
    private HashMap<String, Bitmap> originalTileBitmaps;
    private HashMap<String, Bitmap> scaledTileBitmaps;

    // bitmap dictionaries for units
    private HashMap<String, Bitmap> originalUnitBitmaps;
    private HashMap<String, Bitmap> scaledUnitBitmaps;

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
        originalTileBitmaps = new HashMap<String, Bitmap>();
        originalTileBitmaps.put("grassTile", BitmapFactory.decodeResource(getResources(), R.drawable.grass));
        originalTileBitmaps.put("grainTile", BitmapFactory.decodeResource(getResources(), R.drawable.grain));
        originalTileBitmaps.put("woodsLightTile", BitmapFactory.decodeResource(getResources(), R.drawable.woods_light));
        originalTileBitmaps.put("waterTile", BitmapFactory.decodeResource(getResources(), R.drawable.water));
        originalTileBitmaps.put("townTile", BitmapFactory.decodeResource(getResources(), R.drawable.town));
        originalTileBitmaps.put("ownedTile", BitmapFactory.decodeResource(getResources(), R.drawable.tile_owned));
        originalTileBitmaps.put("selectedTile", BitmapFactory.decodeResource(getResources(), R.drawable.tile_selected));
        originalTileBitmaps.put("fogTile", BitmapFactory.decodeResource(getResources(), R.drawable.fog));
        originalTileBitmaps.put("mountainTile", BitmapFactory.decodeResource(getResources(), R.drawable.mountain));
        originalTileBitmaps.put("tileHPFrame", BitmapFactory.decodeResource(getResources(), R.drawable.hp_bar_frame));
        originalTileBitmaps.put("tileHPFill", BitmapFactory.decodeResource(getResources(), R.drawable.hp_bar_fill));
        originalTileBitmaps.put("tileProgressFrame", BitmapFactory.decodeResource(getResources(), R.drawable.progress_bar_frame));
        originalTileBitmaps.put("tileProgressFill", BitmapFactory.decodeResource(getResources(), R.drawable.progress_bar_fill));

        scaledTileBitmaps = new HashMap<String, Bitmap>(originalTileBitmaps);

        // load original unit bitmaps
        originalUnitBitmaps = new HashMap<String, Bitmap>();
        originalUnitBitmaps.put("workerWork1", BitmapFactory.decodeResource(getResources(), R.drawable.worker1));
        originalUnitBitmaps.put("workerWork2", BitmapFactory.decodeResource(getResources(), R.drawable.worker2));
        originalUnitBitmaps.put("workerWalkLeft1", BitmapFactory.decodeResource(getResources(), R.drawable.worker_left1));
        originalUnitBitmaps.put("workerWalkLeft2", BitmapFactory.decodeResource(getResources(), R.drawable.worker_left2));
        originalUnitBitmaps.put("workerWalkRight1", BitmapFactory.decodeResource(getResources(), R.drawable.worker_right1));
        originalUnitBitmaps.put("workerWalkRight2", BitmapFactory.decodeResource(getResources(), R.drawable.worker_right2));
        originalUnitBitmaps.put("workerStay1", BitmapFactory.decodeResource(getResources(), R.drawable.worker_stay1));
        originalUnitBitmaps.put("workerStay2", BitmapFactory.decodeResource(getResources(), R.drawable.worker_stay2));
        originalUnitBitmaps.put("troopWalkLeft1", BitmapFactory.decodeResource(getResources(), R.drawable.troop_left1));
        originalUnitBitmaps.put("troopWalkLeft2", BitmapFactory.decodeResource(getResources(), R.drawable.troop_left2));
        originalUnitBitmaps.put("troopWalkRight1", BitmapFactory.decodeResource(getResources(), R.drawable.troop_right1));
        originalUnitBitmaps.put("troopWalkRight2", BitmapFactory.decodeResource(getResources(), R.drawable.troop_right2));
        originalUnitBitmaps.put("troopWalkUp1", BitmapFactory.decodeResource(getResources(), R.drawable.troop_up1));
        originalUnitBitmaps.put("troopWalkUp2", BitmapFactory.decodeResource(getResources(), R.drawable.troop_up2));
        originalUnitBitmaps.put("troopWalkDown1", BitmapFactory.decodeResource(getResources(), R.drawable.troop_down1));
        originalUnitBitmaps.put("troopWalkDown2", BitmapFactory.decodeResource(getResources(), R.drawable.troop_down2));
        originalUnitBitmaps.put("unitHPFrame", BitmapFactory.decodeResource(getResources(), R.drawable.unit_hp_bar_frame));
        originalUnitBitmaps.put("unitHPFill", BitmapFactory.decodeResource(getResources(), R.drawable.unit_hp_bar_fill));

        scaledUnitBitmaps = new HashMap<String, Bitmap>(originalUnitBitmaps);

        // when ui is built get dimensions
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    zoomFactor = 4;
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
        Log.d("DEBUG", "updateBitmapScale: " + zoomFactor);
        Iterator it = originalTileBitmaps.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            // use pair.getKey(), pair.getValue()
            scaledTileBitmaps.put(pair.getKey().toString(), getResizedBitmap(originalTileBitmaps.get(pair.getKey()), (int)(getWidth() / zoomFactor), (int)((getWidth()  / zoomFactor) * 1.5)));
            it.remove();
        }

        // build unit dimensions off adjusted tile dimensions
        tileWidth = scaledTileBitmaps.get("grassTile").getWidth();
        tileHeight = scaledTileBitmaps.get("grassTile").getHeight();

        Iterator ut = originalUnitBitmaps.entrySet().iterator();
        while (ut.hasNext()) {
            Map.Entry pair = (Map.Entry)ut.next();
            // use pair.getKey(), pair.getValue()
            scaledUnitBitmaps.put(pair.getKey().toString(), getResizedBitmap(originalUnitBitmaps.get(pair.getKey()), (int)tileWidth/2, (int)tileHeight/3));
            ut.remove();
        }

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
            //canvas.drawBitmap(originalTileBitmaps.get("townTile"), 0, 0, null);
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
                    canvas.drawBitmap(scaledTileBitmaps.get("fogTile"), x, y, null);
                    continue;
                }

                if (gridArray[i][j].getType() == TILETYPE.GRASS) {
                    canvas.drawBitmap(scaledTileBitmaps.get("grassTile"), x, y, null);
                } else if (gridArray[i][j].getType() == TILETYPE.GRAIN) {
                    canvas.drawBitmap(scaledTileBitmaps.get("grainTile"), x, y, null);
                } else if (gridArray[i][j].getType() == TILETYPE.WOOD) {
                    canvas.drawBitmap(scaledTileBitmaps.get("woodsLightTile"), x, y, null);
                } else if (gridArray[i][j].getType() == TILETYPE.WATER) {
                    canvas.drawBitmap(scaledTileBitmaps.get("waterTile"), x, y, null);
                } else if (gridArray[i][j].getType() == TILETYPE.TOWN) {
                    canvas.drawBitmap(scaledTileBitmaps.get("townTile"), x, y, null);
                } else if (gridArray[i][j].getType() == TILETYPE.MOUNTAIN) {
                    canvas.drawBitmap(scaledTileBitmaps.get("mountainTile"), x, y, null);
                }

                // draw player ile overlay info
                // may need testing for Zbuffer (draw order)
                // but should be good as long ad elements stay in the 2:3 width:length ratio
                if (myPlayer.isTileOwned(i,j)) {
                    canvas.drawBitmap(scaledTileBitmaps.get("ownedTile"), x, y, null);
                }
            }
        }

        //draw units
        // animation frames stored in???
        for (player p : myGrid.getPlayers()) {
            for (unit u : p.myUnits) {
                // select correct bitmap to draw
                // select based on unit type/movement direction?/animation State?
                // units could store correct bitmap but would couple model and view
                Bitmap unitBitmap = determineBitmap(u);

                // draw selected bitmap
                int[] pix = convertIndiciesToPixels(u.get_location_x(), u.get_location_y());
                canvas.drawBitmap(unitBitmap,
                        pix[0] + tileWidth/2 - unitBitmap.getWidth()/2,
                        pix[1] + tileHeight*2/3 - unitBitmap.getHeight()/2,
                        null);
                if (u.getHP() < u.getHPMax()) {
                    canvas.drawBitmap(scaledUnitBitmaps.get("unitHPFrame"),
                            pix[0] + tileWidth / 2 - unitBitmap.getWidth() / 2,
                            pix[1] + tileHeight * 2 / 3 - unitBitmap.getHeight() / 2,
                            null);
                    int dynamicWidth = (int) (scaledUnitBitmaps.get("unitHPFrame").getWidth() * (u.getHP() / u.getHPMax()));
                    canvas.drawBitmap(getResizedBitmap(scaledUnitBitmaps.get("unitHPFill"), dynamicWidth, scaledUnitBitmaps.get("unitHPFrame").getHeight()),
                            pix[0] + tileWidth / 2 - unitBitmap.getWidth() / 2,
                            pix[1] + tileHeight * 2 / 3 - unitBitmap.getHeight() / 2,
                            null);
                }
            }
        }

        // draw player overlay information
        // **This is not UI which is outside of tileView
        {
            //int x = (int) (myPlayer.getSelectY() * tileWidth * 0.7143) - Math.round(originX);
            //int y = (int) (myPlayer.getSelectY() * tileHeight * 0.2976 + myPlayer.getSelectX() * tileHeight * 0.5813) - Math.round(originY);
            //canvas.drawBitmap(scaledTileBitmaps.get("selectedTile"), x, y, null);
            int[] homeCoords = convertIndiciesToPixels(myPlayer.getHomeCoord()[0], myPlayer.getHomeCoord()[1]);
            if (myPlayer.getUnitProgress() > 0) {
                canvas.drawBitmap(scaledTileBitmaps.get("tileProgressFrame"), homeCoords[0], homeCoords[1], null);
                // can resize bitmap on the fly for dynamic size?
                int dynamicWidth = (int)(tileWidth * (myPlayer.getUnitProgress() / myPlayer.getUnitProgressMax()));
                if (dynamicWidth <= 0) dynamicWidth = 1;
                //Log.d("Divide by zero debug", "onDraw: " + Integer.toString(dynamicWidth));
                //Log.d("unchained ratio debug", "onDraw: " + Float.toString(tileHeight));
                canvas.drawBitmap(getResizedBitmap(scaledTileBitmaps.get("tileProgressFill"), dynamicWidth, (int)tileHeight),
                        homeCoords[0], homeCoords[1], null);
            }
            if (myPlayer.getHP() < myPlayer.getHPMax()) {
                canvas.drawBitmap(scaledTileBitmaps.get("tileHPFrame"), homeCoords[0], homeCoords[1], null);
                int dynamicWidth = (int)(tileWidth * (myPlayer.getHP() / myPlayer.getHPMax()));
                if (dynamicWidth <= 0) dynamicWidth = 1;
                canvas.drawBitmap(getResizedBitmap(scaledTileBitmaps.get("tileHPFill"), dynamicWidth, (int)tileHeight),
                        homeCoords[0], homeCoords[1], null);
            }
        }

        // loop through player resourceInventory and print key: values
        String text = "";
        for (RESOURCES r : myPlayer.getResourceInventory().keySet()) {
            if (r == RESOURCES.NONE) continue;
            text = text.concat(r.toString() + ": " + Math.floor(myPlayer.getResourceInventory().get(r)) + "\n ");
        }
        canvas.drawText(text, 20, 50, xPaintSquare);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
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
            case MotionEvent.ACTION_MOVE:

                if (event.getPointerCount() == 2) {
                    float xDist = event.getX(0) - event.getX(1);
                    float yDist = event.getY(0) - event.getY(1);
                    double squareDist = Math.pow(xDist, 2) + Math.pow(yDist, 2);
                    float trueDist = (float)Math.sqrt(squareDist);
                    // Adjust zoomfactor and origin relatively based on trueDist
                    //zoomFactor += (trueDist - lastPinchDistance) / 100;
                    //updateBitmapScale();
                    postInvalidate();
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
            default:
                return true;
        }
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int reqW, int reqH) {
        Matrix matrix = new Matrix();

        RectF src = new RectF(0,0, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(0,0, reqW, reqH);

        // Matrix.ScaleToFit.FILL resizes to EXACTLY reqW and reqH
        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.FILL);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap determineBitmap(unit u) {
        Bitmap unitBitmap = null;
        if (u instanceof worker) {
            if (u.status() == unit.Command.STAY) {
                if (u.animState <= 1) {
                    unitBitmap = scaledUnitBitmaps.get("workerStay1");
                } else {
                    unitBitmap = scaledUnitBitmaps.get("workerStay2");
                }
                // 4 Stay frames
                u.animState += 1;
                if (u.animState > 3) u.animState = 0;
            } else if (u.status() == unit.Command.WORK) {
                if (u.animState <= 1) {
                    unitBitmap = scaledUnitBitmaps.get("workerWork1");
                } else {
                    unitBitmap = scaledUnitBitmaps.get("workerWork2");
                }
                // 4 working frames
                u.animState += 1;
                if (u.animState > 3) u.animState = 0;
            } else { // else covers combat, move, and any other future states
                // worker only has 2 move directions
                if (u.moveDirection <= 1) {
                    if (u.animState == 0) {
                        unitBitmap = scaledUnitBitmaps.get("workerWalkRight1");
                    } else {
                        unitBitmap = scaledUnitBitmaps.get("workerWalkRight2");
                    }
                } else {
                    if (u.animState == 0) {
                        unitBitmap = scaledUnitBitmaps.get("workerWalkLeft1");
                    } else {
                        unitBitmap = scaledUnitBitmaps.get("workerWalkLeft2");
                    }
                }
                // 2 walking frames
                u.animState += 1;
                if (u.animState > 1) u.animState = 0;
            }
        } else if (u instanceof troop) {
            // troop should have combat/no combat sprites?
            if (false && u.status() == unit.Command.STAY) {
                if (u.animState <= 1) {
                    unitBitmap = scaledUnitBitmaps.get("workerStay1");
                } else {
                    unitBitmap = scaledUnitBitmaps.get("workerStay2");
                }
                // 4 Stay frames
                u.animState += 1;
                if (u.animState > 3) u.animState = 0;
            } else { // else covers combat, move, and any other future states
                // troop has all 4 directions
                switch (u.moveDirection) {
                    case 0:
                        if (u.animState == 0) {
                            unitBitmap = scaledUnitBitmaps.get("troopWalkUp1");
                        } else {
                            unitBitmap = scaledUnitBitmaps.get("troopWalkUp2");
                        }
                        break;
                    case 1:
                        if (u.animState == 0) {
                            unitBitmap = scaledUnitBitmaps.get("troopWalkRight1");
                        } else {
                            unitBitmap = scaledUnitBitmaps.get("troopWalkRight2");
                        }
                        break;
                    case 2:
                        if (u.animState == 0) {
                            unitBitmap = scaledUnitBitmaps.get("troopWalkDown1");
                        } else {
                            unitBitmap = scaledUnitBitmaps.get("troopWalkDown2");
                        }
                        break;
                    default: //case 3
                        if (u.animState == 0) {
                            unitBitmap = scaledUnitBitmaps.get("troopWalkLeft1");
                        } else {
                            unitBitmap = scaledUnitBitmaps.get("troopWalkLeft2");
                        }
                        break;
                }
                // 2 waling frames
                u.animState += 1;
                if (u.animState > 1) u.animState = 0;
            }
        } else if (u instanceof settler) {
            if (u.animState <= 1) {
                unitBitmap = scaledUnitBitmaps.get("workerStay1");
            } else {
                unitBitmap = scaledUnitBitmaps.get("workerStay2");
            }
            // 4 stay frames
            u.animState += 1;
            if (u.animState > 3) u.animState = 0;
        } else {
            if (u.animState == 0) {
                unitBitmap = scaledUnitBitmaps.get("workerStay1");
            } else {
                unitBitmap = scaledUnitBitmaps.get("workerStay2");
            }
        }

        return unitBitmap;
    }
}
