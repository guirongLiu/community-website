package com.example.springblog.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> targets;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer totalPageNo;

    private Integer currentPage;
    private List<Integer> pages = new ArrayList<>();

    public void setPagination(Integer totalPage, Integer page) {
        currentPage = page;
        totalPageNo = totalPage;

        for(int i=page-3;i<=page+3;i++){
            if (i>0 && i<=totalPage)
                pages.add(i);
        }

        if(page == 1){
            showPrevious=false;
        } else{
            showPrevious = true;
        }

        if(page == totalPage){
            showNext=false;
        } else{
            showNext = true;
        }

        if(pages.contains(1)){
            showFirstPage=false;
        } else{
            showFirstPage=true;
        }

        if (pages.contains(totalPage)){
            showEndPage=false;
        } else{
            showEndPage=true;
        }

    }
}
