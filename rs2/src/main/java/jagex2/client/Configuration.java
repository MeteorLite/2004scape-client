package jagex2.client;

/**
 * set up the server connection details
 * <a href="https://2004scape.org/api/v1/worldlist">2004Scape World List</a>
 */
public class Configuration {
    public static final String URL = "https://w2.225.2004scape.org";
    public static final int PORT_OFFSET = 3;
    public static final int PORT = 43594;
    public static final boolean PLAY_ALL_SOUNDS = false;

    /**
     * These are useful for now to make sure that stuff is drawn fully.
     */
    public static final boolean ALWAYS_REDRAW_TITLE = !Client.vanilla;
    public static final boolean ALWAYS_REDRAW_GAME_FRAME = !Client.vanilla;
    public static final boolean ALWAYS_REDRAW_SIDE_ICONS = !Client.vanilla;

    public static final boolean ALWAYS_REDRAW_CHAT_BACK = !Client.vanilla;

    public static final boolean ALWAYS_REDRAW_MINIMAP = !Client.vanilla;

    public static final boolean ALWAYS_REDRAW_PRIVACY_SETTINGS = !Client.vanilla;
}
