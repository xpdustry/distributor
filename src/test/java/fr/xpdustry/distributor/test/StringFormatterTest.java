package fr.xpdustry.distributor.test;

import arc.struct.*;

import fr.xpdustry.distributor.util.*;

import org.junit.jupiter.api.*;


public class StringFormatterTest{
    @Test
    public void formatterTest(){
        StringFormatter f = new StringFormatter(ObjectMap.of(
            "c1",   "[royal]",  // Color 1
            "c2",   "[sky]",    // Color 2
            "d",    "[white]",  // Default color
            "w",    "[red]",    // Warning color
            "i",    "[yellow]", // Info color
            "pre",  "M.D.N >",  // Prefix
            "r", "red"
        ));;

        String s1 = "Bob is @ years old.";
        String s2 = "Bob has a {r} hair.";
        String s3 = "Bob bought a {r} ps4 for @ bucks and {b} cents. for @.";
        String s4 = "{pre} $c1[\\[\\[]" + "@" + "$c1[\\]]" + "$c2[:] @ c2[]";

        // TODO Make meaningful tests for StringFormatter

        System.out.println(s3);
        System.out.println(f.format(s3, 16, 12));

        System.out.println(s4);
        System.out.println(f.format(s4, "Bob", 12553));

        System.out.println(f.format("Bob is $r[[red]]"));
    }
}
