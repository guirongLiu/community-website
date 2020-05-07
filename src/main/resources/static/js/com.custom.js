/*
submit comments
*/
function comment(){
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    console.log(content);
    post(questionId, 1, content);
}

function secComment(e){
    var commentId = $(e).data("id");
    var content = $("#reply-"+commentId).val();
    console.log(commentId);
    console.log(content);
    post(commentId, 2, content);
}

function post(parentId, type, content){

    if(!content){
            alert("comment cannot be empty.");
            return;
    }
    $.ajax({
        type:"POST",
        url:"/comment",
        data:JSON.stringify({
            "parentId":parentId,
            "content":content,
            "type":type
        }),
        success:function(response){
            if(response.code == 200){
                window.location.reload();
                //$("#comment-section").hide();
            }
            else{
                if(response.code == 2003){
                    var isAccept = confirm(response.message);
                    if(isAccept){
                        window.open("http://github.com/login/oauth/authorize?client_id=afe40b7c500ff0cf2a57&scope=user&state=1");
                        window.localStorage.setItem("closable",true);

                    }
                }else{
                    alert(response.message);
                }

            }
        },
        dataType:"json",
        contentType:"application/json"

    });

}

/*
display second level comment
*/

function collapseComments(e){
    var id = $(e).data("id");


    var comment = $("#secComments-"+id);
    if (comment.hasClass("show")){

        comment.removeClass("show");
        e.classList.remove("active");
        //$("#secComments-display-"+id).remove();
    }
    else{
        var subCommentContainer = $("#secComments-display-"+id);
        console.log(subCommentContainer.children().length);
        if(subCommentContainer.children().length < 1){
            $.getJSON( "/comment/"+id, function( data ) {
                console.log(data);
                $.each(data.data,function(index,commentDTO){


                    var mediaElement = $("<div/>",{
                        "class": "media"
                    }).append(
                        $("<div/>",{
                            "class":"media-left"
                        }).append(
                            $("<img/>",{
                            "class": "media-object img-rounded user-img",
                            "src": commentDTO.user.avatarUrl
                        }))
                    )
                    .append(
                        $("<div/>",{
                            "class":"media-body"
                        }).append(
                            $("<h5/>",{
                            "class":"media-heading"
                            }).append(
                            $("<div/>",{
                                "html":commentDTO.user.name})
                            )
                        ).append(
                            $("<div/>",{
                                "html":commentDTO.content
                            })
                        ).append(
                            $("<div/>",{
                                "class":"menu"
                            }).append(
                                $("<span/>",{
                                    "class": "float-right",
                                    "html": moment(commentDTO.gmtCreate).format("YYYY-MM-DD")
                                })
                            )
                        )
                        );



                    var commentDTOElement = $("<div>",{
                        "class" : "col-lg-12 col-md-12 col-sm-12 col-xs-12",
                        //html: commentDTO.content
                        }).append(mediaElement);
                    subCommentContainer.append(commentDTOElement);
                });


            });
        }
        comment.addClass("show");
        e.classList.add("active");

    }
}

function showSelectTag(){
    $("select-tag").show();
}

function selectTag(e){
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    if(previous.indexOf(value) == -1){
        if(previous){
            $("#tag").val(previous+','+value);
        }
        else{
            $("#tag").val(value);
        }
    }
}