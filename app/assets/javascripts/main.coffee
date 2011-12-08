$('.story a.upvote img').live "click",(e) ->
   e.preventDefault()
   that = $(this);
   p = that.parentsUntil('.row').slice(-1);
   $.ajax $(e.target).parent().attr("href"),
      type: 'PUT',
      error:(jqXHR,textStatus,errorThrown) ->
         alert("error")
      success:(data,textStatus,jqXHR) ->
         that.remove()
         p.addClass('voted')
         alert("done")

$('.login').live "click",(e) -> 
	e.preventDefault()
	window.location.href='/login';
