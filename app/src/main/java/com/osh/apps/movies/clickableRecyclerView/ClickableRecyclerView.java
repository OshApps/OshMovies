package com.osh.apps.movies.clickableRecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.View;


/**
 * Created by oshri-n on 11/05/2016.
 */
public class ClickableRecyclerView extends RecyclerView implements RecyclerView.OnChildAttachStateChangeListener
{
private SelectionManager selectionManager;
private OnItemEventListener itemEventListener;
private OnItemClickListener itemClickListener;
private OnItemLongClickListener itemLongClickListener;


	public ClickableRecyclerView(Context context, AttributeSet attrs)
	{
	super(context, attrs);

	selectionManager=new SelectionManager();
    itemEventListener=new OnItemEventListener();

	itemClickListener=null;
	itemLongClickListener=null;

	addOnChildAttachStateChangeListener(this);
	}


	@Override
    public void onChildViewAttachedToWindow(View view)
    {
    view.setOnClickListener(itemEventListener);

    view.setOnLongClickListener(itemEventListener);
    }


    @Override
    public void onChildViewDetachedFromWindow(View view){}


    /**
     * this method will return null when the position is out of view for more information look at {@link RecyclerView#findViewHolderForAdapterPosition(int)}
     *
     *
     */
    @Override
    public ViewHolder findViewHolderForAdapterPosition(int position)
    {
    return super.findViewHolderForAdapterPosition(position);
    }


    private void postAfterAdapterUpdates(Runnable action)
    {

    if(hasPendingAdapterUpdates())
        {
        post(action);
        }else
            {
            action.run();
            }
    }


	public int getSelectedItemCount()
    {
    return selectionManager.getSelectedItemCount();
    }


	public SparseBooleanArray getSelectedItemPositions()
	{
	return selectionManager.getSelectedItemPositions();
	}

    
	private void onItemClick(final int itemPosition)
	{

    postAfterAdapterUpdates(new Runnable()
        {
            @Override
            public void run()
            {
            ViewHolder holder=findViewHolderForAdapterPosition(itemPosition);

            if(selectionManager.isSelectionMode())
                {
                selectionManager.onItemSelected(holder);

                }else if(itemClickListener!=null)
                    {
                    itemClickListener.onItemClick(holder, holder.getAdapterPosition(), holder.getItemViewType());
                    }
            }
        });
	}


	private void onItemLongClick(final int itemPosition)
	{
    postAfterAdapterUpdates(new Runnable()
        {
            @Override
            public void run()
            {
            ViewHolder holder=findViewHolderForAdapterPosition(itemPosition);

            if(selectionManager.isSelectionEnable())
                {
                if(!selectionManager.isSelectionMode())
                    {
                    startActionMode(selectionManager);

                    selectionManager.onItemSelected(holder);
                    }

                }else if(itemLongClickListener!=null)
                    {
                    itemLongClickListener.onItemLongClick(holder, holder.getAdapterPosition(), holder.getItemViewType());
                    }
            }
        });
	}


	public void startSelectionMode()
	{

	if(selectionManager.isSelectionEnable() && !selectionManager.isSelectionMode())
		{
		startActionMode(selectionManager);
		}
	}


    public void setItemSelected(final int position, final boolean isSelected)
	{

	if(selectionManager.isSelectionEnable() && ( selectionManager.isSelectionMode() || isSelected ) )
		{

		if(!selectionManager.isSelectionMode())
			{
			startActionMode(selectionManager);
			}

        postAfterAdapterUpdates(new Runnable()
             {
                 @Override
                 public void run()
                 {
                 selectionManager.setItemSelected(findViewHolderForAdapterPosition(position) , position, isSelected);
                 }
             });
		}
	}


    public void setMultiChoiceModeListener(MultiChoiceModeListener multiChoiceModeListener)
	{
	selectionManager.setMultiChoiceModeListener(multiChoiceModeListener);
	}


	public void setOnItemClickListener(OnItemClickListener itemClickListener)
	{
	this.itemClickListener = itemClickListener;
	}


    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener)
    {
    this.itemLongClickListener = itemLongClickListener;
    }


    public interface MultiChoiceModeListener extends ActionMode.Callback
	{
		void onItemSelectedStateChanged(ActionMode mode, ViewHolder viewHolder, int position, int viewType, boolean isSelected);
	}


	public interface OnItemClickListener
	{
		void onItemClick(ViewHolder viewHolder, int position, int viewType);
	}


    public interface OnItemLongClickListener
	{
		void onItemLongClick(ViewHolder viewHolder, int position, int viewType);
	}


    private class OnItemEventListener implements OnClickListener,OnLongClickListener
    {

        @Override
        public void onClick(View itemView)
        {

        onItemClick(getChildAdapterPosition(itemView));
        }


        @Override
        public boolean onLongClick(View itemView)
        {
        onItemLongClick(getChildAdapterPosition(itemView));

        return true;
        }
    }

}
