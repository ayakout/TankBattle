class GameData implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Player information
	public String name;
	public int key;
	public int tankX;
	public int tankY;

	GameData(final Player p, final int key) {
		this.name = p.name;
		this.key = key;
		this.tankX = p.tank.anchorX;
		this.tankY = p.tank.anchorY;
	}
}