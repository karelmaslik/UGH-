package com.game.ugh.drawables;

import androidx.constraintlayout.solver.widgets.Rectangle;

public interface IEnemy extends IDrawable
{
    void move();
    boolean isOutOfBounds();
    Rectangle getHitbox();
}
