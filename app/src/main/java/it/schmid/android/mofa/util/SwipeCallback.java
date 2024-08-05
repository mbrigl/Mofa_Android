package it.schmid.android.mofa.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

abstract public class SwipeCallback extends ItemTouchHelper.Callback {

    private final Paint mClearPaint;
    private final ColorDrawable mBackground;
    private final int backgroundColor;
    private final Drawable mDrawable;
    private final int intrinsicWidth;
    private final int intrinsicHeight;


    public SwipeCallback(Drawable drawable, String color) {
        mBackground = new ColorDrawable();
        backgroundColor = Color.parseColor(color);
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mDrawable = drawable;
        intrinsicWidth = drawable.getIntrinsicWidth();
        intrinsicHeight = drawable.getIntrinsicHeight();
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;
        if (isCancelled) {
            c.drawRect(itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), mClearPaint);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        mBackground.setColor(backgroundColor);
        mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        mBackground.draw(c);

        int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int iconMargin = (itemHeight - intrinsicHeight) / 2;
        int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
        int iconRight = itemView.getRight() - iconMargin;
        int iconBottom = iconTop + intrinsicHeight;

        mDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        mDrawable.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }
}