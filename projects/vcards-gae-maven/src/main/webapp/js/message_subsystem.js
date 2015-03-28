var message_subsystem = message_subsystem || {};


message_subsystem.MessagesQueue = function (space) {
  this.space = space;
  this.selector = '#log';
  //this.space = $('#log');  // don't work - not exist in time
}

message_subsystem.MessagesQueue.prototype.push = function(message) {
  message.appendTo(this.selector);
}

// Class
message_subsystem.MessageBuilder = function MessageBuilder() { }

message_subsystem.MessageBuilder.prototype.buildInfo = function(text) {
  return new message_subsystem.Message(text, 'info');
}

message_subsystem.MessageBuilder.prototype.RemoveAfter = function (m, sec) {
  setTimeout(function() { m.selfDelete(); }, sec * 1000);
}


message_subsystem.MessageBuilder.prototype.buildWarning = function(text) {
  return new message_subsystem.Message(text, 'warning');
}

message_subsystem.MessageBuilder.prototype.buildError = function(text) {
  return new message_subsystem.Message(text, 'danger');
}

// type - 'success', 'info', warning, danger
// http://www.tutorialspoint.com/bootstrap/bootstrap_alerts.htm
message_subsystem.Message = function(text, type) {
  var self = this;

  var value = 
    ['<div class="alert alert-' + type + ' alert-dismissable">',
       '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>',
       text,
    '</div>'].join('\n');

  var r = $.parseHTML(value);

  // connect closer
  $(r).first().click(function(e) { 
    self.selfDelete();
  });

  // State
  self.r = r;
}

message_subsystem.Message.prototype.appendTo = function(domParent) {
  $(domParent).append(this.r);
}

message_subsystem.Message.prototype.selfDelete = function () {
  // delete with childs
  // http://api.jquery.com/remove/
  $(this.r).remove();
}
