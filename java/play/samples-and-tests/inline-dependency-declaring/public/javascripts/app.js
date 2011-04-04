onDocReady(function(){
  var sampleBG = getCss('sample', 'background-color');
  var sampleFG = getCss('sample', 'color');
  
  if ((sampleBG == "rgb(0, 0, 255)") && (sampleFG == "rgb(255, 255, 0)")) {
    $('#result').addClass('success').text('Hello World is yello color in blue background, test result: success!');
  } else {
    $('#result').addClass('fail').text('Hello World should be yello color in blue background, test result: fail');
  }
});