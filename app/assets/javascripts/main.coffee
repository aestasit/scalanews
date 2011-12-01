$('.story a.upvote').live "click",(e) ->
   e.preventDefault()
   $.ajax $(e.target).attr("href"),
      type: 'PUT',
      error:(jqXHR,textStatus,errorThrown) ->
         alert("error")
      success:(data,textStatus,jqXHR) ->
         alert("done")
