package com.osh.apps.movies.network;

/**
 * Created by oshri-n on 29/05/2016.
 */
public class SearchRequest
{
private static final int RESULTS_PER_PAGE=10;

private int currentPage,pageCount;
private boolean isCanceled;
private String query;


    public SearchRequest(String query)
    {
    this.query = query;

    currentPage=1;
    pageCount =1;

    isCanceled =false;
    }


    public boolean nextPage()
    {
    boolean hasNext=false;

    if(currentPage < pageCount)
        {
        currentPage++;
        hasNext=true;
        }

    return hasNext;
    }


    public int getCurrentPage()
    {
    return currentPage;
    }


    public void setTotalResults(int totalResults)
    {
    pageCount= totalResults / RESULTS_PER_PAGE;

    if(totalResults % RESULTS_PER_PAGE != 0)
        {
        pageCount++;
        }
    }


    public boolean isCanceled()
    {
    return isCanceled;
    }


    public void cancel()
    {
    isCanceled = true;
    }


    public String getQuery()
    {
    return query;
    }


    public boolean hasNext()
    {
    return currentPage < pageCount;
    }
}
