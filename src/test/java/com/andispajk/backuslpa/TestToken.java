package com.andispajk.backuslpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestToken {
    @Test
    public void testGetters() {
        Token tk = new Token("term", TkType.IDENT, 0);
        assertEquals(tk.lexeme(), "term");
        assertEquals(tk.type(), TkType.IDENT);
        assertEquals(tk.startPos(), 0);
        Token tk2 = new Token("}", TkType.RCURLY, 17);
        assertEquals(tk2.lexeme(), "}");
        assertEquals(tk2.type(), TkType.RCURLY);
        assertEquals(tk2.startPos(), 17);
    }
}
