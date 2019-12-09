package utils.oven;
/**
 * Enumeration providing the different cooking modes of an oven
 * @author Laurie Rakotoarisoa
 *
 */
public enum OvenCookingMode {
	/**
	 * Using two electrical resistances
	 */
	Natural_Convection(180),
	
	/**
	 * Use two electrical resistances and fan 
	 */
	Fan_Assisted(160),
	
	/**
	 * Use only the top electrical resistance
	 */
	Grill(260),
	
	/**
	 * Use the top electrical resistance and fan
	 */
	Air_forced_grill(260),
	
	/**
	 * Use the bottom electrical resistance 
	 */
	Heat_Sole(265)
	;
	
	public final int temperature;
	
	private OvenCookingMode(int temperature) {
		this.temperature = temperature;
	}
	

}
