package com.game.ugh.drawables;

public interface IEnemy extends IDrawable
{
    void move();
    boolean isOutOfBounds();
}
