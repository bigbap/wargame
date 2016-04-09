package com.averagecoder.wargame;

import com.averagecoder.wargame.engine.InputController;
import com.averagecoder.wargame.engine.NetworkController;
import com.averagecoder.wargame.engine.NetworkRequest;
import com.averagecoder.wargame.engine.Packets;
import com.averagecoder.wargame.engine.data_types.LoadedGameData;
import com.averagecoder.wargame.engine.data_types.MapData;
import com.averagecoder.wargame.engine.data_types.Player;
import com.averagecoder.wargame.engine.data_types.TileInfo;
import com.averagecoder.wargame.engine.data_types.UnitType;
import com.averagecoder.wargame.objects.Map;
import com.averagecoder.wargame.objects.ObjectFactory;
import com.averagecoder.wargame.objects.Unit;
import com.averagecoder.wargame.scenes.Hud;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;

public class WargameCampaign extends Game {

    public enum Menus {
        LOGIN_MENU,
        MAIN_MENU,
        NEW_GAME_MENU,
        LOAD_GAME_MENU,
        FRIEND_MENU,
        ADD_BATTLE_GROUPS_MENU
    }

	public final float WIDTH;
    public final float HEIGHT;
    public final int TILE_WIDTH = 90;
    public final int TILE_HEIGHT = 90;
    public final int TILE_SIZE = 64;

    public ObjectFactory objFactory;
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport viewport;
    public FPSLogger fpsLogger;
    public AssetManager manager;
    public TextureAtlas spritesheetAtlas;
    public String spritesheetStr = "spritesheet64.pack";
    public Unit activeUnit;
    public Hud hud;
    public Map map;

    public static ShapeRenderer shapeRenderer;
    public static BitmapFont font;
    public static InputMultiplexer multiplexer;
    public static GestureDetector inputController;
    public static NetworkController networkController;
    public static Preferences prefs;
    public static boolean refreshFriends = false;
    public static boolean refreshGames = false;
    public static Skin skin;
    public static BitmapFont font12;

    //generic game data to be cached
    public static HashMap<Integer, String> mapList = null;
    public static Array<TileInfo> tileData = new Array<TileInfo>();

    //player data
    public static int userid = 0;
    public static String username = null;
    public static String password = null;
    public static HashMap<Integer, String[]> gameList = null;
    public static HashMap<Integer, String[]> pendingGameList = null;
    public static Array<String> friendList = null;
    public static Array<String> friendRequestList = null;
    //public static HashMap<String, HashMap> playerList = new HashMap<String, HashMap>();

    //currently loaded game
    public static LoadedGameData loadedGameData = new LoadedGameData();
    public static Player thisPlayer = null;

    public WargameCampaign(float w, float h){
        WIDTH = w;
        HEIGHT = h;

        fpsLogger = new FPSLogger();
        camera = new OrthographicCamera();
        camera.position.set(WIDTH / 2, HEIGHT / 2, 0);
        viewport = new ExtendViewport(WIDTH, HEIGHT, camera);
    }
	
	@Override
	public void create () {

        batch = new SpriteBatch();

        //asset manager
        manager = new AssetManager();
        manager.load(spritesheetStr, TextureAtlas.class);
        manager.finishLoading();

        //spritesheet
        spritesheetAtlas = manager.get(spritesheetStr, TextureAtlas.class);

        //network
        networkController = new NetworkController(this);

        //font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Voltaire-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 10;
        font = generator.generateFont(parameter);
        generator.dispose();

        //shape renderer
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        prefs = Gdx.app.getPreferences("playerData");

        //load generic game data
        getUserData("getMapList");
        getUserData("getTileList");

        //skin
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Lato-Medium.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font12 = generator.generateFont(parameter);
        generator.dispose();

        skin = new Skin();
        skin.add("default-font", font12, BitmapFont.class);
        FileHandle fileHandle = Gdx.files.internal("skins/uiskin.json");
        FileHandle atlasFile = fileHandle.sibling("uiskin.atlas");

        if (atlasFile.exists()) {
            skin.addRegions(new TextureAtlas(atlasFile));
        }

        skin.load(fileHandle);

        setScreen(new WargameCampaignStart(this));
	}

	@Override
	public void render () {
		//fpsLogger.log();

        super.render();
	}

    @Override
    public void resize(int width, int height){
        super.resize(width, height);
    }

    @Override
    public void dispose(){
        manager.dispose();
        batch.dispose();
        font.dispose();
        spritesheetAtlas.dispose();
        shapeRenderer.dispose();
    }

    public void checkForOutOfBounds(){
        if(camera.position.x < (WIDTH / 2) - TILE_SIZE)
            camera.position.x = (WIDTH / 2) - TILE_SIZE;
        if(camera.position.y < (HEIGHT / 2) - TILE_SIZE)
            camera.position.y = (HEIGHT / 2) - TILE_SIZE;
        if(camera.position.x > map.width + TILE_SIZE - (WIDTH / 2))
            camera.position.x = map.width + TILE_SIZE - (WIDTH / 2);
        if(camera.position.y > map.height + TILE_SIZE - (HEIGHT / 2))
            camera.position.y = map.height + TILE_SIZE - (HEIGHT / 2);
    }

    private void getUserData(String task){
        Packets.PacketUserData userDataRequest = new Packets.PacketUserData();
        userDataRequest.userid = WargameCampaign.userid;
        userDataRequest.task = task;
        userDataRequest.message = "New user data request: task = " + task;
        NetworkController.newRequest(userDataRequest);
    }
}
