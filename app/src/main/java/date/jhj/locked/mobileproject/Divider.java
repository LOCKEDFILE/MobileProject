package date.jhj.locked.mobileproject;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class Divider extends RecyclerView.ItemDecoration {
    private int verticalSpaceHeight=24;
    public Divider(Context context) {
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 마지막 아이템이 아닌 경우, 공백 추가
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount()) {
            if(parent.getChildAdapterPosition(view)==0) {
                outRect.top = verticalSpaceHeight;
            }
            outRect.bottom= verticalSpaceHeight;
        }
    }

}