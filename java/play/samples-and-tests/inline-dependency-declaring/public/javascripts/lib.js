var onDocReady = function(fn) {
  $(function(){
    window.setTimeout(fn, 300);
  });
}

var getCss = function(id, attr) {
  return $('#' + id).css(attr);
}