package com.averagecoder.wargame.objects;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.WargameCampaignMain;
import com.averagecoder.wargame.engine.InputController;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.sql.Wrapper;
import java.util.Comparator;
import java.util.HashMap;

public class Map extends GameObject{

    static Array<Tile> openNodes = new Array<Tile>();
    static Array<Tile> closedNodes = new Array<Tile>();
    static HashMap<Tile, Tile> cameFrom = new HashMap<Tile, Tile>();
    static Tile dest;
    float frontierAnimTimer = 0.001f;

    Vector2[] directions = {new Vector2(+1,  0), new Vector2(+1, -1), new Vector2( 0, -1), new Vector2(-1,  0), new Vector2(-1, +1), new Vector2( 0, +1)};

    public static boolean animatePath = false;
    public static int tileFrames = 7;
    public static HashMap<Vector2, Tile> hashMap = new HashMap<Vector2, Tile>();
    public static Array<Tile> activeTiles = new Array<Tile>();
    public static Comparator<Tile> tileComparator = new Comparator<Tile>() {
        @Override
        public int compare(Tile o1, Tile o2) {
            return o1.cost > o2.cost ? 1 : o1.cost < o2.cost ? -1 : 0;
        }
    };

    //visibility calculations
    public static Array<Tile> visibleTiles = new Array<Tile>();
    private static Array<Tile> openVisTiles = new Array<Tile>();
    private static Array<Tile> closeVisTiles = new Array<Tile>();

    //deployable calculations
    public static Array<Tile> deployableTiles = new Array<Tile>();
    private static Array<Tile> openDepTiles = new Array<Tile>();
    private static Array<Tile> closeDepTiles = new Array<Tile>();

    private static WargameCampaign gameHandler;

    public Map(WargameCampaign wargameCampaign, String ss, String keyPath){
        super(wargameCampaign);

        gameHandler = game;

        //get tile key
        FileHandle tileKeyFile = Gdx.files.internal(keyPath);
        String tileKeyStr = tileKeyFile.readString().replaceAll("\\s+", "");
        JsonValue mapKey = new JsonReader().parse(tileKeyStr);
        JsonValue tileKey = mapKey.get("tiles");
        String mapStr = mapKey.get("map").asString();

        // convert to axiel coordinates and store in hashtable
        int startCol = 0;
        for(int y = 0; y < game.TILE_HEIGHT; y++){
            int q = startCol;
            for(int x = 0; x < game.TILE_WIDTH; x++){
                int index = (y * game.TILE_WIDTH) + x;

                int frame = Integer.parseInt("" + mapStr.charAt(index));

                Vector2 tilePos = new Vector2(q, y);

                JsonValue thisTileKey = tileKey.get(Integer.toString(frame));

                boolean passable = thisTileKey.get("passable").asString().equals("yes");
                int moves = Integer.parseInt(thisTileKey.get("moves").asString());

                Tile thisTile = new Tile(game, tilePos, frame, passable, moves);
                thisTile.init(game.spritesheetAtlas.findRegion(ss));
                hashMap.put(tilePos, thisTile);

                q++;
            }
            startCol -= y % 2 == 0 ? 1 : 0;
        }

        width = game.TILE_WIDTH * (float)Math.round(Tile.sideSize * (float) Math.sqrt(3));
        height = game.TILE_HEIGHT * (float)Math.round((Tile.sideSize * 3)/2);
    }

    @Override
    public void update(float delta) {
        for(Vector2 key: hashMap.keySet()){
            Tile tile = hashMap.get(key);
            if(checkTileBound(tile))
                tile.update(delta);
        }

        if(dest != null){
            animateFrontier(delta);
        }
    }

    @Override
    public void draw(){
        for(Vector2 key: hashMap.keySet()){
            Tile tile = hashMap.get(key);
            if(checkTileBound(tile))
                tile.draw();
        }
    }

    public static Tile getTile(Vector2 key){
        return hashMap.get(key);
    }

    public static Vector2 cubeToHex(Vector3 c){
        return new Vector2(c.x, c.y);
    }

    public static Vector3 hexToCube(Vector2 h){
        return new Vector3(h.x, h.y, -h.x-h.y);
    }

    public static Vector2 pixelToHex(float touchX, float touchY){
        float q = (float)Math.floor((touchX * ((float)Math.sqrt(3)/3) - (touchY / 3)) / Tile.sideSize);
        float r = (float)Math.floor((touchY * (2.0f/3.0f)) / Tile.sideSize);

        return hexRound(new Vector2(q, r));
    }

    public static Vector2 hexRound(Vector2 hex){
        return cubeToHex(cubeRound(hexToCube(hex)));
    }

    public static Vector3 cubeRound(Vector3 cube){
        float rx = Math.round(cube.x);
        float ry = Math.round(cube.y);
        float rz = Math.round(cube.z);

        float x_diff = Math.abs(rx - cube.x);
        float y_diff = Math.abs(ry - cube.y);
        float z_diff = Math.abs(rz - cube.z);

        if(x_diff > y_diff && x_diff > z_diff)
            rx = -ry-rz;
        else if(y_diff > z_diff)
            ry = -rx-rz;
        else
            rz = -rx-ry;

        return new Vector3(rx, ry, rz);
    }

    public static Vector2 getPixelPos(Vector3 tilePos){
        float y = ((Tile.sideSize * 3) / 2) * tilePos.y;
        float x = Tile.sideSize * (float)Math.sqrt(3) * (tilePos.x + (tilePos.y/2));
        return Map.hexRound(new Vector2(x, y));
    }

    public static Array<Tile> getNeighbors(Tile tile){
        Array<Tile> neighbors = new Array<Tile>();
        Vector2 mapPos = new Vector2();
        Tile n;

        mapPos.set(tile.tilePos.x + 1, tile.tilePos.y + 0);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        mapPos.set(tile.tilePos.x + 1, tile.tilePos.y - 1);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        mapPos.set(tile.tilePos.x - 1, tile.tilePos.y - 0);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        mapPos.set(tile.tilePos.x - 1, tile.tilePos.y + 1);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        mapPos.set(tile.tilePos.x + 0, tile.tilePos.y - 1);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        mapPos.set(tile.tilePos.x + 0, tile.tilePos.y + 1);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        return neighbors;
    }

    public static Array<Tile> getNeighbors(Vector2 tilePos){
        Array<Tile> neighbors = new Array<Tile>();
        Vector2 mapPos = new Vector2();
        Tile n;

        mapPos.set(tilePos.x + 1, tilePos.y + 0);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        mapPos.set(tilePos.x + 1, tilePos.y - 1);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        mapPos.set(tilePos.x - 1, tilePos.y - 0);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        mapPos.set(tilePos.x - 1, tilePos.y + 1);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        mapPos.set(tilePos.x + 0, tilePos.y - 1);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        mapPos.set(tilePos.x + 0, tilePos.y + 1);
        n = hashMap.get(mapPos);
        if(n != null && n.passable)
            neighbors.add(n);

        return neighbors;
    }

    public Vector2 direction(int dir){
        return directions[dir];
    }

    public Tile hexNeighbor(Vector2 tilePos, int dir){
        Vector2 thisDir = direction(dir);
        return getTile(new Vector2(tilePos.x + thisDir.x, tilePos.y + thisDir.y));
    }

    public static void clearActiveTiles(){
        for(Tile t: activeTiles)
            t.active = false;

        activeTiles.clear();
    }

    public boolean checkTileBound(Tile tile){
        boolean xBound = tile.pos.x > game.camera.position.x - (game.WIDTH / 2) - tile.width && tile.pos.x < game.camera.position.x + (game.WIDTH / 2);
        boolean yBound = tile.pos.y > game.camera.position.y - (game.HEIGHT / 2) - tile.height && tile.pos.y < game.camera.position.y + (game.HEIGHT / 2);

        return xBound && yBound;
    }

    public static Tile getTileAtPos(float x, float y){
        Tile tile = hashMap.get(pixelToHex(x, y));
        if(tile == null){
            for(Tile t: getNeighbors(pixelToHex(x, y))){
                if(t.collisionPoly.contains(x, y)){
                    tile = t;
                    break;
                }
            }
        }else if(!tile.collisionPoly.contains(x, y)){
            for(Tile t: getNeighbors(tile)){
                if(t.collisionPoly.contains(x, y)){
                    tile = t;
                    break;
                }
            }
        }

        return tile;
    }

    private void highlightTile(Tile tile){
        for(Tile t: activeTiles)
            t.active = false;

        activeTiles.clear();

        tile.active = true;
        activeTiles.add(tile);
    }

    private void highlightNeighbors(Tile tile){
        for(Tile t: activeTiles)
            t.active = false;

        activeTiles.clear();

        tile.active = true;
        activeTiles.add(tile);
        for(Tile t: getNeighbors(tile)){
            t.active = true;
            activeTiles.add(t);
        }
    }

    private void highlightFrontier(){
        for(Tile t: activeTiles)
            t.active = false;

        activeTiles.clear();

        for(Tile t: closedNodes){
            if(t != null) {
                t.active = true;
                activeTiles.add(t);
            }
        }
    }

    private void animateFrontier(float delta){
        frontierAnimTimer -= delta;
        if(frontierAnimTimer > 0)
            return;

        if(openNodes.size == 0){
            dest = null;
            return;
        }

        openNodes.sort(tileComparator);

        Tile t = openNodes.get(0);
        openNodes.removeValue(t, false);
        closedNodes.add(t);
        t.highlight(true);

        if (t.equals(dest)) {
            game.activeUnit.path = calculatePath(dest, getTile(game.activeUnit.tilePos));
            dest = null;
            return;
        }

        for (Tile tn : getNeighbors(t)) {
            tn.cost = getCost(tn, getTile(game.activeUnit.tilePos), dest);

            if(tn.ID.equals(dest.ID))
                tn.cost = 0;

            if(!closedNodes.contains(tn, false) && !openNodes.contains(tn, false)){
                openNodes.add(tn);
                cameFrom.put(tn, t);
            }
        }

        frontierAnimTimer = 0.05f;
    }

    private static void calculateFrontier(Tile source, Tile dest){
        clearFrontier(source);

        while(openNodes.size > 0){
            openNodes.sort(tileComparator);

            Tile t = openNodes.get(0);
            openNodes.removeValue(t, false);
            closedNodes.add(t);

            if (t.equals(dest))
                return;

            for (Tile tn : getNeighbors(t)) {
                tn.cost = getCost(tn, getTile(source.tilePos), dest);
                if(!closedNodes.contains(tn, false) && !openNodes.contains(tn, false)){
                    openNodes.add(tn);
                    cameFrom.put(tn, t);
                }
            }
        }
    }

    private static void clearFrontier(Tile source){
        clearActiveTiles();
        cameFrom.clear();
        openNodes.clear();
        closedNodes.clear();
        openNodes.add(source);
    }

    private static Array<Tile> calculatePath(Tile dest, Tile source){
        Array<Tile> tilePath = new Array<Tile>();

        if(dest == source)
            return tilePath;

        Tile current = dest;
        tilePath.add(current);
        int maxIter = gameHandler.TILE_WIDTH * gameHandler.TILE_HEIGHT;
        while(current != source){
            current = cameFrom.get(current);
            if(current != source)
                tilePath.add(current);
            maxIter--;
            if(maxIter < 0)
                break;
        }

        tilePath.reverse();

        return tilePath;
    }

    public static void findPath(Tile source, Tile destination){
        if(animatePath){
            clearFrontier(source);
            dest = destination;
        }else{
            calculateFrontier(source, destination);
            InputController.path = calculatePath(destination, source);
        }
    }

    public static float hexDistance(Vector2 h1, Vector2 h2){
        return cubeDistance(hexToCube(h1), hexToCube(h2));
    }

    public static float cubeDistance(Vector3 c1, Vector3 c2){
        return (Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y) + Math.abs(c1.z - c2.z)) / 2;
    }

    public static float getCost(Tile node, Tile source, Tile dest){
        return (getGCost(source, node) + getHCost(node, dest)) + node.moves;
    }

    public static float getGCost(Tile source, Tile node){
        return hexDistance(source.tilePos, node.tilePos);
    }

    public static float getHCost(Tile node, Tile dest){
        return hexDistance(node.tilePos, dest.tilePos);
    }

    private static void clearVisibility(){
        for(Tile vt: visibleTiles)
            vt.visible = false;
        visibleTiles.clear();
    }

    public static void prepareVisibility(Tile source){
        openVisTiles.clear();
        closeVisTiles.clear();
        openVisTiles.add(source);
    }

    public static void calculateVisibleTiles(){
        clearVisibility();
        for(Unit u: WargameCampaignMain.units.values()) {
            if(u.playerID == WargameCampaign.userid) {
                prepareVisibility(u.unitTile);

                while (openVisTiles.size > 0) {
                    Tile t = openVisTiles.get(0);
                    if (Map.hexDistance(u.tilePos, t.tilePos) > u.visibilityRange) {
                        break;
                    }

                    openVisTiles.removeValue(t, false);
                    closeVisTiles.add(t);
                    if (!visibleTiles.contains(t, false)) {
                        visibleTiles.add(t);
                        t.visible = true;
                    }

                    for (Tile tn : Map.getNeighbors(t)) {
                        if (!closeVisTiles.contains(tn, false) && !openVisTiles.contains(tn, false))
                            openVisTiles.add(tn);
                    }
                }
            }
        }
    }

    public static void calculateUnitVisibility(){
        for(Unit u: WargameCampaignMain.units.values()) {
            u.visible = visibleTiles.contains(u.unitTile, false);
        }
    }

    private static void clearDeployable(){
        for(Tile vt: deployableTiles)
            vt.deployable = false;
        deployableTiles.clear();
    }

    public static void prepareDeployable(Tile source){
        openDepTiles.clear();
        closeDepTiles.clear();
        openDepTiles.add(source);
    }
    public static void calculateDeployableTiles(Vector2 origin){
        Tile originTile = hashMap.get(origin);
        clearDeployable();
        prepareDeployable(originTile);

        while (openDepTiles.size > 0) {
            Tile t = openDepTiles.get(0);
            if (Map.hexDistance(originTile.tilePos, t.tilePos) > 6) {
                break;
            }

            openDepTiles.removeValue(t, false);
            closeDepTiles.add(t);
            if(!deployableTiles.contains(t, false)) {
                deployableTiles.add(t);
                t.deployable = true;
            }

            for (Tile tn : Map.getNeighbors(t)) {
                if (!closeDepTiles.contains(tn, false) && !openDepTiles.contains(tn, false))
                    openDepTiles.add(tn);
            }
        }
    }

    public static void clearDeployableTiles(){
        for(Tile t: deployableTiles)
            t.deployable = false;
    }
}
