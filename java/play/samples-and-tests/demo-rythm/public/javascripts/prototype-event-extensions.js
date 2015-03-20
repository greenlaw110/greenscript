Event.SpecialKeys = $H({
	'enter': Event.KEY_RETURN,
	'up': Event.KEY_UP,
	'down': Event.KEY_DOWN,
	'left': Event.KEY_LEFT,
	'right': Event.KEY_RIGHT,
	'esc': Event.KEY_ESC,
	'space': 32,
	'backspace': Event.KEY_BACKSPACE,
	'tab': Event.KEY_TAB,
	'delete': Event.KEY_DELETE
});

Event.prototype.key = function() {
	if (this.type.include('key')) {
		var code = this.which || this.keyCode;
		var key = Event.SpecialKeys.index(code);
		if (this.type == 'keydown'){
			var fKey = code - 111;
			if (fKey > 0 && fKey < 13) key = 'f' + fKey;
		}
		return key || String.fromCharCode(code).toLowerCase();
	}
};
