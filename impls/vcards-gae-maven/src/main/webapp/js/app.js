function UserException(message) {
   this.message = message;
   this.name = "UserException";
}

function Logger() {

}

Logger.prototype.putWarning = function(message) {

}

// Class
// http://www.electrictoolbox.com/jquery-add-option-select-jquery/
// http://stackoverflow.com/questions/47824/how-do-you-remove-all-the-options-of-a-select-box-and-then-add-one-option-and-se
function View(dal) {
  var self = this;
  this.dal = dal;
  this.currentWordData = new CurrentWordData();
  this.userSummary = new UserSummary([]);
  this.currentTextFilename = "";

}

View.prototype.setCurrentTextFilename = function (/*value*/) {
  var fileInput = $("#fileInput");
  var file = fileInput.files[0];

  this.currentTextFilename = file;
}

View.prototype.onUploadTextFile = function () {
  var self = this;
  // FIXME: http://stackoverflow.com/questions/166221/how-can-i-upload-files-asynchronously-with-jquery
  // Вроде бы трудно на голом jQ and Ajax - "Doing this kind of uploading hacks is not an enjoyable experience"
  // http://malsup.com/jquery/form/#ajaxForm

  // FIXME: to html5 
  // http://www.matlus.com/html5-file-upload-with-progress/
  // http://blog.teamtreehouse.com/reading-files-using-the-html5-filereader-api
  var reader = new FileReader();

  reader.onload = function(e) {
    //fileDisplayArea.innerText = reader.result;
  }

  var file = this.currentTextFilename;
  if (file) {
    reader.readAsText(file);

    // match
  }
  
}

View.prototype.onUploadFilterFile = function() {
  // FIXME: 
}

View.prototype.getCurrentPageName = function () {
  return $('#pages > option:selected').text();
}

View.prototype.getCurrentGenName = function() {
  return $('#pageGenerators > option:selected').text();
};

View.prototype.resetPagesOptions = function(newNames) {
  var pageSelect = $('#pages');
  pageSelect.empty();
  _.each(newNames, function(e) { pageSelect.append(new Option(e, e, true, true)); });  
  
  this.resetGenNames();
}

View.prototype.resetGenNames = function() {
  var currentPageName = this.getCurrentPageName();
  var genNames = this.userSummary.getGenNames(currentPageName);
  var pageGens = $('#pageGenerators');
  pageGens.empty();
  _.each(genNames, function(e) { pageGens.append(new Option(e, e, true, true)); });  
}

View.prototype.drawWordValue = function (word) {
  $("#word_holder_id").text(word);
}

View.prototype.draNGramStatistic = function (imp) {
  $("#count_occurance").text(imp);
}

View.prototype.redrawSentences = function (sentences) {
  var dom = $('#sentences');
  var sent = [sentences[0]];
  dom.empty();
  _.each(sent, function(e) { dom.append('<li>'+ e + '</li>')});
}

View.prototype._markIsKnowIt = function() {
  // думается лучше выполнить синхронно, хотя если здесь, то все равно
  // http://stackoverflow.com/questions/133310/how-can-i-get-jquery-to-perform-a-synchronous-rather-than-asynchronous-ajax-re
  
  var point = this._makePoint();
  this.dal.markIsDone(point);
  gPlotView.onGetData();
}

View.prototype._makePoint = function () {
  var page = this.getCurrentPageName();
  var gen = this.getCurrentGenName();
  var pointPos = this.currentWordData.getPos();
  return new protocols.PathValue(page, gen, pointPos);
}

// Actions
View.prototype.reload = function() {
  // Get user data
  var self = this;
  this.dal.getUserSummary(function(data) {
      self.userSummary.reset(JSON.parse(data));
      var pages = self.userSummary.getPageNames();
      self.resetPagesOptions(pages);
    });

  // don't work in constructor
  $('#know_it').change(function() {
    if (self.currentWordData.isActive()) {
      var $this = $(this);
      if ($this.is(':checked')) {
        self._markIsKnowIt();
      }
    }
  });

  // don't work in constructor
  $('#pages').change(function() {
    self.resetGenNames();
  })
}

View.prototype.onGetWordPackage = function () { 
  var self = this;

  // Нужны еще данные - страница и имя генератора
  var point = this._makePoint();

  // делаем запрос
  this.dal.getWordPkgAsync(function(data) {
      var v = JSON.parse(data);
      self.currentWordData.set(v);
      self.drawWordValue(v.word);
      self.redrawSentences(v.sentences);
      self.draNGramStatistic(v.importance + " from " + v.maxImportance);

      // сбрасываем флаг "i know"
      $('#know_it').prop('checked', false);
    }, point);
}

// State
// создаются до загрузки DOM?
var gDataAccessLayer = new DataAccessLayer();
var gView = new View(gDataAccessLayer);
var gPlotView = new PlotView(gDataAccessLayer);

$(function() {
  // Handler for .ready() called.
  gView.reload();
  gPlotView.reset();

  $("#fileInput").change(function(e) {
    gView.setCurrentTextFilename();
  })

});