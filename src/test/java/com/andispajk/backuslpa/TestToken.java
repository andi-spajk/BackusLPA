package com.andispajk.backuslpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestToken {
    @Test
    public void testGetters() {
        Token tk = new Token("term", TkType.EBNF_IDENT, 0);
        assertEquals("term", tk.lexeme());
        assertEquals(TkType.EBNF_IDENT, tk.type());
        assertEquals(0, tk.startPos());
        Token tk2 = new Token("}", TkType.RCURLY, 17);
        assertEquals("}", tk2.lexeme());
        assertEquals(TkType.RCURLY, tk2.type());
        assertEquals(17, tk2.startPos());
    }
}
