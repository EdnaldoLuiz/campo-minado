package br.com.campominado;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Teste {

	@Test
	void test() {
		int a = 1 + 1;
		assertEquals(2, a);
	}
	
	@Test
	void testar() {
		int x = 2 + 10 - 7;
		assertEquals(5, x);
	}
}
