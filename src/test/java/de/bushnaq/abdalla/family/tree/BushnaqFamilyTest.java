package de.bushnaq.abdalla.family.tree;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.family.tree.util.Base;

public class BushnaqFamilyTest extends Base {

	@Test
	public void generateFraomExcel() throws Exception {
		generate("bushnaq");
	}

}
