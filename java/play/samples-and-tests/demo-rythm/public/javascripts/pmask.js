/* ************************************************************************************* *\
 * The MIT License
 * Copyright (c) 2010 Luo Gelin - greenlaw110@gmail.com
 * Copyright (c) 2007 Fabio Zendhi Nagao - http://zend.lojcomm.com.br
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
\* ************************************************************************************* */

var pMask = Class.create({
    defNumberOptions: {
        groupSymbol: ',',
        groupDigits: 3,
        decSymbol: '.',
        decDigits: 2,
        stripMask: false,
        stripMaskOnSubmit: true
    },
    defOptions: {
        targetClass: "p-mask",
        maskEmptyChr: '_',

        validNumbers: "1234567890-.",
        validAlphas: "abcdefghijklmnopqrstuvwxyz",
        validAlphaNums: "abcdefghijklmnopqrstuvwxyz1234567890",
        
        errorClass: "p-mask-error",
        errorDisplayDuration: 1,

        onFocus: Prototype.emptyFunction,
        onBlur: Prototype.emptyFunction,
        onValid: Prototype.emptyFunction,
        onInvalid: Prototype.emptyFunction,
        onKeyDown: Prototype.emptyFunction
    },
    
    maskObject: function(obj) {
        obj = $(obj);
        if (!obj) return;
        if (obj.pMasked) return;
        obj.pMasked = true;
        /* try html5 data attribute first */
        var maskAtt = obj.getAttribute("data-pMask");
        if (maskAtt)
            obj.options = maskAtt.evalJSON();
        else
            obj.options = (obj.alt).evalJSON();
        if (!obj.options) return;
        if(obj.options.type && obj.options.type == "number") {
            //set default options
            obj.options = Object.extend(this.defNumberOptions, obj.options);
        }
        if (obj.options.stripMask == false) {
            obj.value = this._wearMask(obj, obj.value)
        }
        obj.observe("mousedown", function(event) {
            event.stop();
        });
        obj.observe("mouseup", function(event) {
            event.stop();
            this._onMouseUp(event, obj);
        }.bind(this));
        obj.observe("click", function(event) {
            event.stop();
        });
        obj.observe("keydown", function(event) {
            this._onKeyDown(event, obj);
            this.options.onKeyDown.defer(obj);
        }.bind(this));
        obj.observe("keypress", function(event) {
            this._onKeyPress(event, obj);
        }.bind(this));
        obj.observe("focus", function(event) {
            event.stop();
            this._onFocus(event, obj);
            this.options.onFocus.defer(obj);
        }.bind(this));
        obj.observe("blur", function(event) {
            event.stop();
            this._onBlur(event, obj);
            this.options.onBlur.defer(obj);
        }.bind(this));
    },

    initialize: function(options) {
        this.options = Object.extend(Object.extend({ }, this.defOptions), options || { });
        
        var clz = "." + this.options.targetClass;
        $$(clz).each(function(obj) {
            this.maskObject(obj);
        }.bind(this));
        
        var e = $$(clz).first();
        if (e) {
            var f = this._getObjForm(e);
            if (f && !f.pMasked) {
                f.observe('submit', function(){this._stripMaskOnSubmit();}.bind(this));
                f.pMasked = true;
            }
        }
    },

    _onMouseUp: function(event, obj) {
        if(obj.options.type == "fixed") {
            var p = this._getSelectionStart(obj);
            this._setSelection(obj, p, (p + 1));
        } else if(obj.options.type == "number") {
            this._setEnd(obj);
        }
    },

    _onKeyDown: function(event, obj) {
        if(event.keyCode == Event.KEY_RETURN) { // enter
            event.keyCode == Event.KEY_TAB;
            //TODO simulate TAB key
            //this._onKeyDown(event, obj);
            //obj.blur();
            //this._submitForm(obj);
        } else if(event.keyCode != Event.KEY_TAB) {
            event.stop();
            var chr = 0;
            if(obj.options.type == "fixed") {
                var p = this._getSelectionStart(obj);
                switch(event.keyCode) {
                    case Event.KEY_BACKSPACE: 
                        this._selectPrevious(obj);
                        break;
                    case Event.KEY_HOME: 
                        this._selectFirst(obj);
                        break;
                    case Event.KEY_END: 
                        this._selectLast(obj);
                        break;
                    case Event.KEY_LEFT:
                    case Event.KEY_UP:
                        this._selectPrevious(obj);
                        break;
                    case Event.KEY_RIGHT:
                    case Event.KEY_DOWN:
                        this._selectNext(obj);
                        break;
                    case Event.KEY_DELETE:
                        this._updSelection(obj, p, this.options.maskEmptyChr);
                        break;
                    default:
                        chr = this._chrFromEvent(event);
                        if(this._isViableInput(obj, p, chr)) {
                            if(event.shiftKey)
                                {this._updSelection(obj, p, chr.toUpperCase());}
                            else
                                {this._updSelection(obj, p, chr);}
                            this._onValid.bind(this).defer(event, obj);
                            this._selectNext(obj);
                        } else {
                            if (!this._isIrrelevantKeyCode(event.keyCode)) this._onInvalid.bind(this).defer(event, obj);
                        }
                        break;
                }
            } else if(obj.options.type == "number") {
                switch(event.keyCode) {
                    case Event.KEY_BACKSPACE: 
                    case Event.KEY_DELETE: 
                        this._popNumber(obj);
                        break;
                    default:
                        chr = this._chrFromEvent(event);
                        if(this.options.validNumbers.indexOf(chr) >= 0) {
                            this._pushNumber(obj, chr);
                            this._onValid.bind(this).defer(event, obj);
                        } else {
                            if (!this._isIrrelevantKeyCode(event.keyCode)) this._onInvalid.bind(this).defer(event, obj);
                        }
                        break;
                }
            }
        }
    },
    
    _isIrrelevantKeyCode: function(keyCode) {
        /* donot trigger warning for the following keys */
        // shift, ctrl, alt, left-win, right-win, win-menu, apple
        // see http://unixpapa.com/js/key.html
        return [16, 17, 18, 91, 92, 93, 224].indexOf(keyCode) != -1;
    },

    _onKeyPress: function(event, obj) {
        if((event.keyCode != Event.KEY_TAB)
            && !(event.shiftKey && event.keyCode == Event.KEY_TAB) 
            && (event.keyCode != Event.KEY_RETURN) 
            && !(event.ctrlKey && event.keyCode == 67) // ctrl + c
            && !(event.ctrlKey && event.keyCode == 86) // ctrl + v
            && !(event.ctrlKey && event.keyCode == 88) // ctrl + x
        ) {
            event.stop();
        }
    },

    _onFocus: function(event, obj) {
        if(obj.options.stripMask) obj.value = this._wearMask(obj, obj.value);
        if(obj.options.type == "fixed") 
            {this._selectFirst.bind(this, obj).defer();}
        else
            {this._setEnd.bind(this, obj).defer();}
    },

    _onBlur: function(event, obj) {
        if(obj.options.stripMask)
            obj.value = this._stripMask(obj);
    },
    
    _onInvalid: function(event, obj) {
        obj.addClassName(this.options.errorClass);
        this.options.onInvalid(event, obj);
        var f = function(obj){
            obj.removeClassName(this.options.errorClass);
        };
        var d = this.options.errorDisplayDuration;
        if (d > 0)
            f.bind(this).delay(d, obj);
    },
    
    _onValid: function(event, obj) {
        obj.removeClassName(this.options.errorClass);
        this.options.onValid(event, obj);
    },

    _selectAll: function(obj) {
        this._setSelection(obj, 0, obj.value.length);
    },

    _selectFirst: function(obj) {
        for(var i = 0, len = obj.options.mask.length; i < len; i++) {
            if(this._isInputPosition(obj, i)) {
                this._setSelection(obj, i, (i + 1));
                return;
            }
        }
    },

    _selectLast: function(obj) {
        for(var i = (obj.options.mask.length - 1); i >= 0; i--) {
            if(this._isInputPosition(obj, i)) {
                this._setSelection(obj, i, (i + 1));
                return;
            }
        }
    },

    _selectPrevious: function(obj, p) {
        if(!$(p))p = this._getSelectionStart(obj);
        if(p <= 0) {
            this._selectFirst(obj);
        } else {
            if(this._isInputPosition(obj, (p - 1))) {
                this._setSelection(obj, (p - 1), p);
            } else {
                this._selectPrevious(obj, (p - 1));
            }
        }
    },

    _selectNext: function(obj, p) {
        if(!$(p))p = this._getSelectionEnd(obj);
        if(p >= obj.options.mask.length) {
            this._selectLast(obj);
        } else {
            if(this._isInputPosition(obj, p)) {
                this._setSelection(obj, p, (p + 1));
            } else {
                this._selectNext(obj, (p + 1));
            }
        }
    },

    _setSelection: function(obj, a, b) {
        if(obj.setSelectionRange) {
            obj.focus();
            obj.setSelectionRange(a, b);
        } else if(obj.createTextRange) {
            var r = obj.createTextRange();
            r.collapse();
            r.moveStart("character", a);
            r.moveEnd("character", (b - a));
            r.select();
        }
    },

    _updSelection: function(obj, p, chr) {
        var value = obj.value;
        var output = "";
        output += value.substring(0, p);
        output += chr;
        output += value.substr(p + 1);
        obj.value = output;
        this._setSelection(obj, p, (p + 1));
    },

    _setEnd: function(obj) {
        var len = obj.value.length;
        this._setSelection(obj, len, len);
    },

    _getSelectionStart: function(obj) {
        var p = 0;
        if(obj.selectionStart) {
            if(Object.isNumber(obj.selectionStart)) p = obj.selectionStart;
        } else if(document.selection) {
            var r = document.selection.createRange().duplicate();
            r.moveEnd("character", obj.value.length);
            p = obj.value.lastIndexOf(r.text);
            if(r.text == "") p = obj.value.length;
        }
        return p;
    },

    _getSelectionEnd: function(obj) {
        var p = 0;
        if(obj.selectionEnd) {
            if(Object.isNumber(obj.selectionEnd))
                {p = obj.selectionEnd;}
        } else if(document.selection) {
            var r = document.selection.createRange().duplicate();
            r.moveStart("character", -obj.value.length);
            p = r.text.length;
        }
        return p;
    },

    _isInputPosition: function(obj, p) {
        var mask = obj.options.mask.toLowerCase();
        var chr = mask.charAt(p);
        if("9ax".indexOf(chr) >= 0)
            return true;
        return false;
    },

    _isViableInput: function(obj, p, chr) {
        var mask = obj.options.mask.toLowerCase();
        var chMask = mask.charAt(p);
        switch(chMask) {
            case '9':
                if(this.options.validNumbers.indexOf(chr) >= 0) return true;
                break;
            case 'a':
                if(this.options.validAlphas.indexOf(chr) >= 0) return true;
                break;
            case 'x':
                if(this.options.validAlphaNums.indexOf(chr) >= 0) return true;
                break;
            default:
                return false;
        }
    },

    _wearMask: function(obj, str) {
        if (!obj.options.mask) {
            if (obj.options.type == "number") {
                this._formatNumber(obj);
                return obj.value;
            } else {return str;}
        }
        var mask = obj.options.mask.toLowerCase();
        var output = "";
        for(var i = 0, u = 0, len = mask.length; i < len; i++) {
            switch(mask.charAt(i)) {
                case '9':
                    if(this.options.validNumbers.indexOf(str.charAt(u).toLowerCase()) >= 0) {
                        if(str.charAt(u) == "") {output += this.options.maskEmptyChr;}
                        else {output += str.charAt(u++);}
                    } else {
                        output += this.options.maskEmptyChr;
                    }
                    break;
                case 'a':
                    if(this.options.validAlphas.indexOf(str.charAt(u).toLowerCase()) >= 0) {
                        if(str.charAt(u) == "") {output += this.options.maskEmptyChr;}
                        else {output += str.charAt(u++);}
                    } else {
                        output += this.options.maskEmptyChr;
                    }
                    break;
                case 'x':
                    if(this.options.validAlphaNums.indexOf(str.charAt(u).toLowerCase()) >= 0) {
                        if(str.charAt(u) == "") {output += this.options.maskEmptyChr;}
                        else {output += str.charAt(u++);}
                    } else {
                        output += this.options.maskEmptyChr;
                    }
                    break;
                default:
                    output += mask.charAt(i);
                    break;
            }
        }
        return output;
    },

    _stripMask: function(obj) {
        var value = obj.value;
        if("" == value) return "";
        var output = "";
        if(obj.options.type == "fixed") {
            for(var i = 0, len = value.length; i < len; i++) {
                if((value.charAt(i) != this.options.maskEmptyChr) && (this._isInputPosition(obj, i)))
                    {output += value.charAt(i);}
            }
        } else if(obj.options.type == "number") {
            for(var i = 0, len = value.length; i < len; i++) {
                if(this.options.validNumbers.indexOf(value.charAt(i)) >= 0)
                    {output += value.charAt(i);}
            }
        }
        return output;
    },

    _chrFromEvent: function(event) {
        var chr = '';
        switch(event.keyCode) {
            case 48: case 96: // 0 and numpad 0
                chr = '0';
                break;
            case 49: case 97: // 1 and numpad 1
                chr = '1';
                break;
            case 50: case 98: // 2 and numpad 2
                chr = '2';
                break;
            case 51: case 99: // 3 and numpad 3
                chr = '3';
                break;
            case 52: case 100: // 4 and numpad 4
                chr = '4';
                break;
            case 53: case 101: // 5 and numpad 5
                chr = '5';
                break;
            case 54: case 102: // 6 and numpad 6
                chr = '6';
                break;
            case 55: case 103: // 7 and numpad 7
                chr = '7';
                break;
            case 56: case 104: // 8 and numpad 8
                chr = '8';
                break;
            case 57: case 105: // 9 and numpad 9
                chr = '9';
                break;
            case 189: case 109: // - and numpad -
                chr = '-';
                break;
            case 190: case 110: // . and numpad .
                chr = '.';
                break;
            default:
                chr = event.key(); // key pressed as a lowercase string //TODO
                break;
        }
        return chr;
    },

    _pushNumber: function(obj, chr) {
        var negative = (chr == '-');
        if (chr != '-' && chr != '.') obj.value = obj.value + chr;
        this._formatNumber(obj, negative);
    },

    _popNumber: function(obj) {
        obj.value = obj.value.substring(0, (obj.value.length - 1));
        this._formatNumber(obj);
    },

    _formatNumber: function(obj, negative) {
        
        var str2 = this._stripMask(obj);
        if (!str2.include('.') && obj.options.decDigits > 0) {
            str2 = str2 + '.' + '0'.times(obj.options.decDigits);
        }
        
        // additional processing for "."
        str2 = str2.sub('.', '');
        var str1 = "";
        // negative operation
        if (negative) {
            var isNeg = (str2.charAt(i) == '-')
            if (isNeg) {
                str2 = str2.sub('-', '');
            } else {
                str2 = '-' + str2;
            }
        }
        
        // stripLeadingZeros
        for(var i = 0, len = str2.length; i < len; i++) {
            if('0' != str2.charAt(i)) {
                str1 = str2.substr(i);
                break;
            }
        }

        // wearLeadingZeros
        str2 = str1;
        str1 = "";
        for(var len = str2.length, i = obj.options.decDigits; len <= i; len++) {
            str1 += "0";
        }
        str1 += str2;

        // decimalSymbol
        str2 = str1.substr(str1.length - obj.options.decDigits);
        str1 = str1.substring(0, (str1.length - obj.options.decDigits));

        // groupSymbols
        var re = new RegExp("(\\d+)(\\d{"+ obj.options.groupDigits +"})");
        while(re.test(str1)) {
            str1 = str1.replace(re, "$1"+ obj.options.groupSymbol +"$2");
        }

        // currencySymbol
        if (obj.options.currencySymbol) {
            str1 = obj.options.currencySymbol + str1;
        }

        obj.value = str1 + obj.options.decSymbol + str2;
    },

    _getObjForm: function(obj) {
        var n;
        for (; n = obj.tagName.toLowerCase(), n != 'body' && n != 'form'; obj = obj.up());
        return (obj.tagName.toLowerCase() == 'form') ? obj : null;
    },

    _stripMaskOnSubmit: function() {
        $$("." + this.options.targetClass).each(function(obj) {
            //alert(obj.tagName);
            if (obj.options.stripMaskOnSubmit && !obj.options.stripMask) {
                if (obj.value) {
                    obj.value = this._stripMask(obj);
                }
            }
        }.bind(this));
    },
    
    stripMask: function (obj) {
        obj = $(obj);
        if (!obj || !obj.value) return;
        obj.value = this._stripMask(obj);
    }

});