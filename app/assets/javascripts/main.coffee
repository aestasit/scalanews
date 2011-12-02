$('.story a.upvote img').live "click",(e) ->
   e.preventDefault()
   $.ajax $(e.target).parent().attr("href"),
      type: 'PUT',
      error:(jqXHR,textStatus,errorThrown) ->
         alert("error")
      success:(data,textStatus,jqXHR) ->
         alert("done")

$('.login').live "click",(e) -> 
	e.preventDefault()
	window.location.href='/login';
