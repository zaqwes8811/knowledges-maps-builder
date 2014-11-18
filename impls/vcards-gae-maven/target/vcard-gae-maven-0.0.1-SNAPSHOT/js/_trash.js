// http://hayageek.com/ajax-file-upload-jquery/
  // https://github.com/blueimp/jQuery-File-Upload/wiki/Basic-plugin - еще вариант
  var options = { 
    beforeSend: function() {
        $("#progress").show();
        //clear everything
        $("#bar").width('0%');
        $("#message").html("");
        $("#percent").html("0%");
    },
    uploadProgress: function(event, position, total, percentComplete) {
        $("#bar").width(percentComplete+'%');
        $("#percent").html(percentComplete+'%');
    },
    success: function() {
        $("#bar").width('100%');
        $("#percent").html('100%');
 
    },
    complete: function(response) {
        // FIXME: можно же в принципе ничего не выводить?
        //$("#message").html("<font color='green'>"+response.responseText+"</font>");

        // FIXME: обновить бы данные страницы - списки страниц и генераторов
    },
    error: function() {
        $("#message").html("<font color='red'> ERROR: unable to upload files</font>");
    }
  }; 
 
  $("#myForm").ajaxForm(options);