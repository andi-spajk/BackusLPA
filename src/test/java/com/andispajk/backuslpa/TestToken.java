package com.andispajk.backuslpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestToken {
    @Test
    public void testGetters() {
        Token tk = new Token("term", Token.IDENT, 0);
        assertEquals(tk.lexeme(), "term");
        assertEquals(tk.type(), Token.IDENT);
        assertEquals(tk.startPos(), 0);
        Token tk2 = new Token("}", Token.RCURLY, 17);
        assertEquals(tk2.lexeme(), "}");
        assertEquals(tk2.type(), Token.RCURLY);
        assertEquals(tk2.startPos(), 17);
    }
}
