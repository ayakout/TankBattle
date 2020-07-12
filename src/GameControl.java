class GameControl implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int NEW_PLAYER_ENTERED_THE_GAME = 500;
	public static final int LOAD_IN_GAME_PLAYERS = 501;
	public static final int NEW_PLAYER_APPROVAL = 502;
	public static final int PLAYER_HAS_LEFT = 503;
	public static final int PLAYER_NAME_INUSE = 504;

	public int code;
	public String name;
	public int tankLives;
	public int tankPosition;
	public int tankX;
	public int tankY;

	GameControl(final int code, final Player p) {
		this.code = code;
		this.name = p.name;
		this.tankX = p.tank.anchorX;
		this.tankY = p.tank.anchorY;
		this.tankLives = p.tank.tankLives;
		this.tankPosition = p.tank.tankPosition;
	}
}