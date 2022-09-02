package de.bushnaq.abdalla.family;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class Context {
	public Map<Integer, Integer>	generationToMaxWidthMap	= new HashMap<>();
	private ParameterOptions		ParameterOptions		= new ParameterOptions();

	public ParameterOptions getParameterOptions() {
		return ParameterOptions;
	}
}
