package de.bushnaq.abdalla.songmaster;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.family.tree.TreeMaster;

public class BushnaqFamilyTest {
	TreeMaster treeMaster = new TreeMaster();

	@Test
	public void generateFraomExcel() throws Exception {
		treeMaster.readExcel("bushnaq-family.xlsx");
		treeMaster.generate("bushnaq");
	}

}
