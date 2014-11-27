// String formatter: http://stackoverflow.com/questions/1038746/equivalent-of-string-format-in-jquery
//
// http://callbackhell.com/
// https://blog.domenic.me/youre-missing-the-point-of-promises/
//
// Promises
//   http://www.html5rocks.com/en/tutorials/es6/promises/?redirect_from_locale=ru

// Class
// http://www.electrictoolbox.com/jquery-add-option-select-jquery/
// http://stackoverflow.com/questions/47824/how-do-you-remove-all-the-options-of-a-select-box-and-then-add-one-option-and-se
function View(dal) {
  var self = this;
  this.dal = dal;
  this.currentWordData = new CurrentWordData();
  this.userSummary = new UserSummary([]);
  this.currentTextFilename = "";

  this.log = gMessagesQueue;
}

View.prototype.setCurrentTextFilename = function (/*value*/) {
  var fileInput = $("#fileInput");
  var file = fileInput[0].files[0];

  this.currentTextFilename = file;
}

View.prototype.sendPage = function(page) {
  var self = this;

  var errorHandler = function(e) {
    try {
      alert(JSON.parse(e));
    } catch (ex) {
      // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/try...catch
      //if (e if e instanceof RangeError)

    }
  };

  var successHandler = function(data) {

  };

  this.dal.putPage(page);//, successHandler, errorHandler);
}

View.prototype.onUploadTextFile = function () {
  var self = this;
  var file = this.currentTextFilename;
  var pageName = file.name;  // FIXME: не очень, но пока так
  if (!file) {
    return;    
  }

  // FIXME:
  // if not match
  if (false) {

  }

  
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

  // FIXME: page is reload here

  reader.readAsText(file);
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

View.prototype._drawPageSummary = function() {
  var currentPageName = this.getCurrentPageName();
  $('#pageNameView').text(currentPageName);
}

View.prototype.drawNGramStatistic = function (imp) {
  $("#count_occurance").text(imp);
}

View.prototype.redrawSentences = function (sentences, word) {
  var sent = [];

  _.each(sentences, function(e) {
    var one = e;

    // FIXME: need smart replace! A in smArt also change
    // FIXME: may be not one occurence in sentence
    var one = one.replace(word, '<b>' + word + '</b>')
    sent.push(one);
  });

  var dom = $('#sentences');
  dom.empty();
  _.each(sent, function(e) { dom.append('<li>'+ e + '</li>')});
}

View.prototype.toggleSettings = function() {
  $('#settings_id').toggleClass("add-information-hidded");
}

View.prototype.togglePageInfo = function() {
  $('#pageInfoID').toggleClass("add-information-hidded");
}

View.prototype.markIsKnowIt = function() {
  // тоже вариант, Но разбить на части все равно нельзя 
  //   - операция послед. для одного клиента, но если клиентов много, то гонки данных
  // http://stackoverflow.com/questions/133310/how-can-i-get-jquery-to-perform-a-synchronous-rather-than-asynchronous-ajax-re
  var self = this;

  var errorHandler = function(data) { self.getWordError(data) };
  var successHandler = function(data) { 
    onGetData();  
    self.getWordSuccess(data); 
  }

  var point = this.makePoint();
  this.dal.markIsDone(point, successHandler, errorHandler);
  
}

View.prototype.makePoint = function () {
  var page = this.getCurrentPageName();
  var gen = this.getCurrentGenName();
  var pointPos = this.currentWordData.getPos();
  return new protocols.PathValue(page, gen, pointPos);
}

View.prototype.getUserSummary__ = function() {
  // Get user data
  var self = this;

  var waitMessage = gMessageBuilder.buildInfo('Loading user information. Wait please...');
  this.log.push(waitMessage);

  var errorHandler = function(data) { 
    waitMessage.selfDelete();
    var tmp = data.statusText;
    self.log.push(gMessageBuilder.buildError(tmp)); 
  };

  var successHandler = function(data) {
    waitMessage.selfDelete();
    self.userSummary.reset(data);
    var pages = self.userSummary.getPageNames();
    self.resetPagesOptions(pages);

    self._drawPageSummary();
  };

  this.dal.getUserSummary(successHandler, errorHandler);
}

// Actions
View.prototype.reload = function() {
  var self = this;
  this.getUserSummary__();

  // don't work in constructor
  $('#pages').change(function() {
    self.resetGenNames();
    self._drawPageSummary();
  })
}

View.prototype.buildPage = function() {
  // Build page by settings
}

View.prototype.getWordSuccess = function(data) {
  this.currentWordData.set(data);
  this.drawWordValue(data.word);
  this.redrawSentences(data.sentences, data.word);
  this.drawNGramStatistic(data.importance + " from " + data.maxImportance);
};

View.prototype.getWordError = function(data) {
  this.log.push(gMessageBuilder.buildError(data.statusText)); 
};

View.prototype.onGetWordPackage = function () { 
  var self = this;

  // Нужны еще данные - страница и имя генератора
  var point = this.makePoint();
  
  // FIXME: blinking now - need to think
  var errorHandler = function(data) { self.getWordError(data) };
  var successHandler = function(data) { self.getWordSuccess(data); }

  // делаем запрос
  this.dal.getWordPkgAsync(successHandler, errorHandler, point);
}

// State
// создаются до загрузки DOM?
var gMessagesQueue = new message_subsystem.MessagesQueue();
var gMessageBuilder = new message_subsystem.MessageBuilder();

var gDataAccessLayer = new DataAccessLayer();

var gView = new View(gDataAccessLayer);
var gPlotView = new PlotView(gDataAccessLayer);

function onGetData() {
  var point = gView.makePoint();
  gPlotView.onGetData(point);
}


$(function() {
  // Handler for .ready() called.
  gView.reload();
  gPlotView.reset();

  $("#fileInput").change(function(e) {
    gView.setCurrentTextFilename();
  })

  gMessagesQueue.push(
    gMessageBuilder.buildWarning('<b>Warning:</b> Project under development. One user for everyone. \
      All data can be removed in any time.'));
});

function test() {

}