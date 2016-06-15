function searchToggle(obj, evt){
    var container = $(obj).closest('.search-wrapper');

    if(!container.hasClass('active')){

          container.addClass('active');
          evt.preventDefault();
    }
    else if(container.hasClass('active') && $(obj).closest('.input-holder').length == 0){
          container.removeClass('active');
          // clear input
          container.find('.search-input').val('');
          // clear and hide result container when we press close
          container.find('.result-container').fadeOut(100, function(){$(this).empty();});
    }
}

function submitFn(obj, evt){
    value = $(obj).find('.search-input').val().trim();

    // _html = "Yup yup! Your search text sounds like this: ";
    // if(!value.length){
    //     _html = "Yup yup! Add some text friend :D";
    // }
    // else{
    //     _html += "<b>" + value + "</b>";
    // }

    $(obj).find('.result-container').html('<span>' + _html + '</span>');
    $(obj).find('.result-container').fadeIn(100);

    evt.preventDefault();
}

function startDictation(obj,evt) {
    if (window.hasOwnProperty('webkitSpeechRecognition')) 
    {
      var recognition = new webkitSpeechRecognition();

      recognition.continuous = true;
      recognition.interimResults = true;
      // recognition.lang = "en-US";
      document.getElementById('transcript').placeholder = "Recording..."
      recognition.start();
      recognition.onresult = function (e) 
      {
        document.getElementById('transcript').value = e.results[0][0].transcript;
        recognition.stop();
        // document.getElementById('labnol').submit();
      };

      recognition.onerror = function(e) {
        recognition.stop();
      }
      
      event.preventDefault();
    }

}