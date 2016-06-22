package com.osh.apps.movies.widget.recyclerview;

import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Created by oshri-n on 11/05/2016.
 */
public class SelectionManager implements ActionMode.Callback
{
private CustomRecyclerView.MultiChoiceModeListener multiChoiceModeListener;
private SparseBooleanArray selectedItems;
private ActionMode mode;


    public SelectionManager()
    {
    selectedItems=new SparseBooleanArray(0);

    multiChoiceModeListener=null;
    mode=null;
    }


    public void setMultiChoiceModeListener(CustomRecyclerView.MultiChoiceModeListener multiChoiceModeListener)
    {
    this.multiChoiceModeListener = multiChoiceModeListener;
    }


    public void onItemSelected(CustomRecyclerView.ViewHolder viewHolder)
    {
    int itemPosition;
    boolean isItemSelected;

    itemPosition = viewHolder.getLayoutPosition();

    isItemSelected = selectedItems.get(itemPosition, false);

    setItemSelected(viewHolder, itemPosition, !isItemSelected);
    }


    public void setItemSelected(CustomRecyclerView.ViewHolder viewHolder, int itemPosition, boolean isItemSelected)
    {

    if(isItemSelected)
        {
        selectedItems.put(itemPosition, isItemSelected);
        }else
            {
            selectedItems.delete(itemPosition);
            }

    onItemSelectedStateChanged(viewHolder,itemPosition,viewHolder.getItemViewType(),isItemSelected);
    }


    public void onItemSelectedStateChanged(CustomRecyclerView.ViewHolder viewHolder, int position, int viewType, boolean isSelected)
    {
    multiChoiceModeListener.onItemSelectedStateChanged(mode, viewHolder, position, viewType, isSelected);

    if(selectedItems.size() <= 0)
        {
        mode.finish();
        }

    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
    this.mode=mode;
    
    return multiChoiceModeListener.onCreateActionMode(mode, menu);
    }


    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
    {
    return multiChoiceModeListener.onPrepareActionMode(mode, menu);
    }


    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
    {
    return multiChoiceModeListener.onActionItemClicked(mode, item);
    }


    @Override
    public void onDestroyActionMode(ActionMode mode)
    {
    multiChoiceModeListener.onDestroyActionMode(mode);
    
    this.mode=null;
    selectedItems.clear();
    }


    public SparseBooleanArray getSelectedItemPositions()
    {
    return selectedItems;
    }


    public int getSelectedItemCount()
    {
    return selectedItems.size();
    }


    public boolean isSelectionMode()
    {
    return mode!=null;
    }


    public boolean isSelectionEnable()
    {
    return multiChoiceModeListener!=null;
    }
}
