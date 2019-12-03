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
	Natural_Convection,
	
	/**
	 * Use two electrical resistances and fan 
	 */
	Fan_Assisted,
	
	/**
	 * Use only the top electrical resistance
	 */
	Grill,
	
	/**
	 * Use the top electrical resistance and fan
	 */
	Air_forced_grill,
	
	/**
	 * Use the bottom electrical resistance 
	 */
	Heat_Sole
	;
	

}
