// FIXME: Sign what page processed
// FIXME: on clear store send update user info
// FIXME: Make Apply worked

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

/**
  \brief Work with summary information of user.
*/
function SummaryWidget(dal) {
  var self = this;
  this.m_dal = dal;
  this.m_userSummary = new UserSummary([]);
  this.m_log = gErrorActor;
}

SummaryWidget.prototype.UpdateUserInfo = function() {
  var self = this;
  setTimeout(function() { self.reload(); }, 5000);
}

SummaryWidget.prototype.GetCurrentPageName = function () {
  return $('#pages > option:selected').text();
}

SummaryWidget.prototype.GetCurrentGeneratorName = function() {
  return $('#pageGenerators > option:selected').text();
};

SummaryWidget.prototype.ResetPageOptions = function(updatedNames) 
{
  // Need store, and active
  var currentPageName = this.GetCurrentPageName();

  // Old pages
  var pageSelect = $('#pages');
  pageSelect.empty();

  _.each(updatedNames, function(e) { 
    pageSelect.append(new Option(e, e, true, true)); 
  });

  if (currentPageName) {
    if ($("#pages option[value='" + currentPageName + "']").length > 0) {
      $("#pages").val(currentPageName)  
    }
  }
  
  var pageName = this.GetCurrentPageName();
  this.ResetGeneratorOptions(pageName);
}

SummaryWidget.prototype.ResetGeneratorOptions = function(pageName) {
  var pageGens = $('#pageGenerators');
  pageGens.empty();

  _.each(this.m_userSummary.getGenNames(pageName), 
    function(e) { pageGens.append(new Option(e, e, true, true)); });  
}

SummaryWidget.prototype.toggleSettings = function() {
  $('#settings_id').toggleClass("add-information-hidded");
}

SummaryWidget.prototype.togglePageInfo = function() {
  $('#pageInfoID').toggleClass("add-information-hidded");
}

SummaryWidget.prototype.makePoint = function (pointPos) {
  var page = this.GetCurrentPageName();
  var gen = this.GetCurrentGeneratorName();
  return new protocols.PathValue(page, gen, pointPos);
}

SummaryWidget.prototype._GetUserSummary = function() {
  // Get user data
  var self = this;

  var waitMessage = gMessageBuilder.buildInfo('Loading user information. Wait please...');
  this.m_log.push(waitMessage);

  var onError = function(data) { 
    waitMessage.selfDelete();
    var tmp = data.statusText;
    self.log.push(gMessageBuilder.buildError(tmp)); 
  };

  var onSuccess = function(data) {
    waitMessage.selfDelete();

    self.m_userSummary.reset(data);

    var pages = self.m_userSummary.getPageNames();
    self.ResetPageOptions(pages);

    self.drawPageSummary_();

    // Need it. Usability
    self.activatePipeline();
  };

  this.m_dal.getUserSummary(onSuccess, onError);
}

SummaryWidget.prototype.reload = function() {
  var self = this;
  this._GetUserSummary();

  // don't work in constructor
  $('#pages').change(function() {
    self.ResetGeneratorOptions(self.GetCurrentPageName());
    self.drawPageSummary_();
  })
}

SummaryWidget.prototype.activatePipeline = function () {
  // Build page by settings
  var pointPos = 0 + 1;
  var point = this.makePoint(pointPos);

  // FIXME: remove from here - cyclic dependency
  gCardWidget.onGetWordPackage(point);
}

SummaryWidget.prototype.drawPageSummary_ = function() {
  var currentPageName = this.GetCurrentPageName();
  $('#pageNameView').text(currentPageName);
}

/// /// ///

/**
  \fixme Generate DOM

  \fixme Expose dependencies
  
*/
function CardWidget(summary) {
  this.m_currentWordData = new CurrentWordData();
  this.m_dal = gDataAccessLayer;
  this.m_log = gErrorActor;
  this.m_summary = summary;
}

CardWidget.prototype.getWordError = function(data) {
  this.m_log.push(gMessageBuilder.buildError(data.statusText)); 
};

CardWidget.prototype.Do = function () { 
  var self = this;
  var pointPos = this.m_currentWordData.getPos();
  var point = this.m_summary.makePoint(pointPos);
  this.onGetWordPackage(point);
}

CardWidget.prototype.getWordSuccess = function(data) {
  this.m_currentWordData.set(data);
  this.drawWordValue(data.word);
  this.redrawSentences(data.sentences, data.word);
  this.drawNGramStatistic(data.importance + " from " + data.maxImportance);
};

CardWidget.prototype.drawWordValue = function (word) {
  $("#word_holder_id").text(word);
}

CardWidget.prototype.drawNGramStatistic = function (imp) {
  $("#count_occurance").text(imp);
}

CardWidget.prototype.redrawSentences = function (sentences, word) {
  var sent = [];

  _.each(sentences, function(e) {
    var one = e;

    // FIXME: need smart replace! A in smArt also change
    // FIXME: may be not one occurence in sentence
    // FIXME: Bugs live here
    var one = one.replace(word, '<b>' + word + '</b>')
    sent.push(one);
  });

  var dom = $('#sentences');
  dom.empty();
  _.each(sent, function(e) { dom.append('<li>'+ e + '</li>')});
}

CardWidget.prototype.onGetWordPackage = function (point) { 
  var self = this;
  
  // FIXME: blinking now - need to think
  var onError = function(data) { self.getWordError(data) };
  var onSuccess = function(data) { self.getWordSuccess(data); }

  // делаем запрос
  this.m_dal.getWordPkgAsync(onSuccess, onError, point);
}


CardWidget.prototype.markIsKnowIt = function() {
  // тоже вариант, Но разбить на части все равно нельзя 
  //   - операция послед. для одного клиента, но если клиентов много, то гонки данных
  // http://stackoverflow.com/questions/133310/how-can-i-get-jquery-to-perform-a-synchronous-rather-than-asynchronous-ajax-re
  var self = this;

  var onError = function(data) { self.getWordError(data) };
  var onSuccess = function(data) { self.getWordSuccess(data); };

  var pointPos = this.m_currentWordData.getPos();
  var point = this.m_summary.makePoint(pointPos);
  this.m_dal.markIsDone(point, 
      onSuccess, 
      onError);
}

/// /// ///

// Level > 1
var gErrorActor = new message_subsystem.MessagesQueue();
var gMessageBuilder = new message_subsystem.MessageBuilder();
var gDataAccessLayer = new DataAccessLayer();

// Level 1
var gView = new SummaryWidget(gDataAccessLayer);

// Level 0
var gStoreController = new widgets.StoreControllerWidget();
var gCardWidget = new CardWidget(gView);

$(function() {
  // Handler for .ready() called.
  Q.fcall(function () { gView.reload(); });
  //.then(plus1);  // Don't know how connect
  

  $("#fileInput").change(function(e) { gStoreControllerWidget.FillCurrentFilename(); })

  var m = gMessageBuilder.buildWarning(
      '<b>Warning:</b> Project under development. ' 
      + 'One user for everyone. All data can be removed in any time.');
  gMessageBuilder.RemoveAfter(m, 5);
  gErrorActor.push(m);

  var m1 = gMessageBuilder.buildWarning("<b>Warning:</b> Subtitles and plain text files only");
  gMessageBuilder.RemoveAfter(m1, 5);
  gErrorActor.push(m1);
});

