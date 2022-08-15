package de.bushnaq.abdalla.songmaster;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.family.tree.TreeMaster;

public class TestCase02 {
	TreeMaster treeMaster = new TreeMaster();

	@Test
	public void generateFraomExcel() throws Exception {
		treeMaster.readExcel("test-case-02.xlsx");
		treeMaster.generate("test-case-02");
	}

}
