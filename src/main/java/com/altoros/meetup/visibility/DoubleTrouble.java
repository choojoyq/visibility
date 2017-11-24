package com.altoros.meetup.visibility;

/**
 * @author Nikita Gorbachevski
 */
public class DoubleTrouble {

    public static class Alien {
    }

    private Alien alien;

    public void setAlien(Alien alien) {
        this.alien = alien;
    }

    public void assertSanity() {
        if (alien != alien)
            throw new AssertionError("Whoops");
    }
}
