package de.bushnaq.abdalla.family.tree;

import org.junit.jupiter.api.Test;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.tree.util.Base;

public class TestCase02 extends Base {

	@Test
	public void generate() throws Exception {
		Context context = new Context();
		generate(context, "test-case-02", "test-case-02");
	}

}
