package de.bushnaq.abdalla.family;

import java.util.HashMap;
import java.util.Map;

public class Context {
	public boolean					followMale				= true;				// children will be shown under the father if both parents are member of the family
	public Map<Integer, Integer>	generationToMaxWidthMap	= new HashMap<>();
	public boolean					horizontal				= true;				// draw a horizontal tree if true, otherwise a vertical one
	public boolean					includeSpouse			= true;				// included the spouse in teh tree if true otherwise do nto include them
}
