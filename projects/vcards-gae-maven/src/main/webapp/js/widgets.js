 var widgets = widgets || {};

 // Top object
widgets.StoreControllerWidget = function () {
  
  // Obj. graph
  this.log = gErrorActor;
  this.dal = gDataAccessLayer;
}

widgets.StoreControllerWidget.prototype.ResetFullStore = function () {
  gDataAccessLayer.resetFullStore();
}

widgets.StoreControllerWidget.prototype.sendPage = function(page) {
  var self = this;

  var onError = function(e) {
    try {
      var m = gMessageBuilder.buildError('Error occure, Master');
      gMessageBuilder.RemoveAfter(m, 2);
      self.log.push(m);
    } catch (ex) {
      // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/try...catch
      //if (e if e instanceof RangeError)
    }
  };

  var onSuccess = function(data) {
    var m = gMessageBuilder.buildInfo('Done, Master');
    gMessageBuilder.RemoveAfter(m, 2);
    self.log.push(m);
    gView.UpdateUserInfo();
  };

  this.dal.putPage(page, onSuccess, onError);
}

widgets.StoreControllerWidget.prototype.FillCurrentFilename = function () {
  var fileInput = $("#fileInput");
  var file = fileInput[0].files[0];

  this.currentTextFilename = file;
}


widgets.StoreControllerWidget.prototype.onUploadTextFile = function () {
  var self = this;
  var file = this.currentTextFilename;
  var pageName = file.name;  // FIXME: не очень, но пока так
  if (!file) {
    return;    
  }

  // FIXME: if not match
  if (false) {  }
  
  // FIXME: http://stackoverflow.com/questions/166221/how-can-i-upload-files-asynchronously-with-jquery
  // Вроде бы трудно на голом jQ and Ajax - "Doing this kind of uploading hacks is not an enjoyable experience"
  // http://malsup.com/jquery/form/#ajaxForm

  // FIXME: to html5 
  // http://www.matlus.com/html5-file-upload-with-progress/
  // http://blog.teamtreehouse.com/reading-files-using-the-html5-filereader-api
  var reader = new FileReader();

  reader.onload = function(e) {
    var text = reader.result;
    var page = new protocols.TextPackage(pageName, text);
    self.sendPage(page);
  };

  reader.readAsText(file);
}