/**
 * Copyright Tyto Software Pvt. Ltd.
 */
__sahiDebug__("user_ext.js: start");

// Uncomment the line below if pages with WebSockets do not work properly.
// window.WebSocket = null;

Sahi.prototype._getAttribute = function(el, attr) {
	return el[attr] || el.getAttribute(attr);
};

Sahi.prototype.areEqual2 = function(el, param, value) {
	if (param == "sahiText") {
		var str = this._getTextNoTrim(el);
		if ( value instanceof RegExp) {
			str = this.trim(str);
			return str != null && str.match(value) != null;
		}
		if (str.length - value.length > 1000)
			return false;
		return (this.trim(str) == this.trim(value));
	} else {
		return this.areEqualParams(this._getAttribute(el, param), value);
	}
};

Sahi.prototype.getAttribute = function(el, attr) {
	if ( typeof attr == "function") {
		return attr(el);
	}
	if (attr.indexOf("|") != -1) {
		var attrs = attr.split("|");
		for (var i = 0; i < attrs.length; i++) {
			var v = this.getAttribute(el, attrs[i]);
			if (v != null && v != "")
				return v;
		}
	} else {
		if (attr == "sahiText") {
			return this._getText(el);
		}
		return this._getAttribute(el, attr);
	}
};

Sahi.prototype.delAD = function(tagName) {
	var len = this.ADs.length;
	for ( i = 0; i < len; i++) {
		var a = this.ADs[i];
		if (a.tag == tagName) {
			this.ADs.splice(i, 1);
			break;
		}
	}
};

Sahi.prototype.updateAD = function(tagName, newAttrs) {
	var a;
	for (var i = 0; i < this.ADs.length; i++) {
		var d = this.ADs[i];
		if (d.tag == tagName) {
			a = d;
		}
	}
	if (!a) {
		console.error("The data for [" + tagName + "] is not found.");
	}
	this.delAD(tagName);
	newAttrs.forEach(function(newAttr) {
		a.attributes.push(newAttr);
	});
	this.addAD(a);
};

_sahi.updateAD("A", ["data-dojo-attach-point"]);
_sahi.updateAD("DIV", ["title", "data-dojo-attach-point", "dojoattachpoint", "idref"]);
_sahi.updateAD("SPAN", ["title", "data-dojo-attach-point", "dojoattachpoint"]);


/**
 * Traverse the dom node tree to get widget error messages
 * @param {Object} node
 */
var getErrMsgs = function(node) {
	var finalmsg;
	if (node.className.indexOf('hidden') != -1) {
		return;
	}
	if (node.getAttribute('widgetid')) {
		var w = dijit.getEnclosingWidget(node);
		if (w.isValid && !w.isValid()) {
			var errmsg;
			if (w.message) {
				errmsg = w.message + " [" + (w.value || w._label) + "]";
			} else if (w.required) {
				errmsg = 'This value is required.';
			}

			if (errmsg) {
				finalmsg = w.dojoAttachPoint + '(' + w.id + '): ' + errmsg;
			}
		};
	}
	var children = node.children;
	if (children.length > 0) {
		for (var i = 0; i < children.length; i++) {
			var msg = getErrMsgs(children[i]);
			if (msg) {
				if (finalmsg) {
					finalmsg = finalmsg + '\n' + msg;
				} else {
					finalmsg = msg;
				}
			}
		}
	}
	return finalmsg;
};

/** -- WaterBear Recorder Start -- * */

Sahi.prototype.sendIdentifierInfo = function(accessors, escapedAccessor, escapedValue, popupName, assertions) {
	var controlWin = this.getController();

	if (_sahi.controllerMode == "waterbear") {
		var origAccessors = accessors;
		accessors = _waterbear.getAccessors(escapedAccessor);

		if (accessors.length == 0) {
			accessors = _waterbear.getStdAccessors(origAccessors);
		}

		if (accessors.length > 0) {
			escapedAccessor = accessors[0];
		} else {
			escapedAccessor = undefined;
		}
	}

	controlWin.displayInfo(accessors, escapedAccessor, escapedValue, popupName, assertions);
};

var WBCodeLine = function(mName, mParams, invoker) {
	this.id//used to store the element id attribute value
	this.varType// generated from other attributes
	this.varName// generated from other attributes
	this.invoker = invoker;
	// a WBCodeLine object
	this.mName = mName;
	this.mParams = mParams;
	if (this.mName && this.mParams) {
		this.reBuild();
	}
}

WBCodeLine.prototype.reBuild = function() {
	if (this.mName == "byAttribute") {
		this.varName = this.mParams[0];
		this.varType = this.mParams[1].split(".")[0];
	} else if (this.mName == "byLabel") {
		var labelText = this.mParams[0];
		this.varName = labelText.replace(/[^A-Za-z0-9]+/g, '').trim();
		this.varName = this.varName.substring(0, 1).toLowerCase() + this.varName.substring(1, this.varName.length);
		this.varType = this.mParams[1].split(".")[0];

		var rbStat = $Messages.rbStat(labelText);
		this.mParams[0] = rbStat;
	} else if (this.mName == "byIndex") {
		this.varType = this.mParams[1].split(".")[0];
		var idxValue = this.mParams[0];
		this.varName = this.varType.substring(0, 1).toLowerCase() + this.varType.substring(1, this.varType.length) + idxValue;
	} else if (this.mName == "webElem") {
		var attrValue = this.mParams[0];
		attrValue = attrValue.replace(/[^A-Za-z0-9]+/g, '').trim();
		var elemType = this.mParams[1].split(".")[1].toLowerCase();

		this.varName = elemType + attrValue;
		this.varType = "WebElement";
	}

	if (this.mParams.length == 3) {
		var parentVarName = this.mParams[2].varName;
		this.varName = this.varName + "_" + parentVarName;
	}

};

WBCodeLine.prototype.isFor = function(javaClassName) {
	return this.mParams && this.mParams[1] && _sahi.contains(this.mParams[1], javaClassName);
};

WBCodeLine.prototype.hasParent = function() {
	return ["byLabel", "byIndex", "byAttribute", "webElem"].indexOf(this.mName) != -1 && this.mParams[2];
};

WBCodeLine.prototype.fromStr = function(str) {
	var parts = str.split("=");
	this.varType = parts[0].split(" ")[0];
	this.varName = parts[0].split(" ")[1];

	str = parts[1].replace("(" + this.varType + ")", "");

	this.mName = str.substr(0, str.indexOf("("));
	var paramsStr = str.substring(str.indexOf("(") + 1, str.lastIndexOf(")"));
	var posComma = paramsStr.lastIndexOf(",");
	var param3 = paramsStr.substring(posComma + 1);
	paramsStr = paramsStr.substring(0, posComma);
	posComma = paramsStr.lastIndexOf(",");
	var param2 = paramsStr.substring(posComma + 1);
	var param1 = paramsStr.substring(0, posComma).replace(/"/g, '');
	this.mParams = [param1, param2, param3];
};

WBCodeLine.prototype.toLineCode = function() {
	var stat = "";

	if (this.mName) {
		stat = this.mName + '(';
		if (this.mParams) {
			for (var i = 0; i < this.mParams.length; i++) {
				var param = this.mParams[i];
				if ( param instanceof WBCodeLine) {
					param = param.varName;
				} else if (isNaN(param) && (_sahi.contains(param, ".class") || _sahi.contains(param, "getString(") || _sahi.contains(param, "ET."))) {
					// do nothing, keep it as is
				} else {
					param = isNaN(param) ? '"' + param + '"' : param;
				}
				stat = stat + param;
				if (i != this.mParams.length - 1) {
					stat = stat + ",";
				}
			}
		}
		stat = stat + ')';
	}

	if (this.invoker) {
		stat = this.invoker.varName + '.' + stat;
	}

	if (this.varName && this.varType) {
		stat = this.varType + " " + this.varName + "=(" + this.varType + ")" + stat;
	}

	return stat;
}

WBCodeLine.prototype.genAccessorPart = function() {
	var s = this.genFullCode();
	return s + (this.varName ? ((s == "" ? "" : ";") + this.varName) : "");
};

WBCodeLine.prototype.getParent = function() {
	return this.mParams[2];
};

WBCodeLine.prototype.setParent = function(p) {
	this.mParams[2] = p;
};

WBCodeLine.prototype.genFullCode = function() {
	var s = "";
	if (this.invoker) {
		var invCode = this.invoker.genFullCode();
		s = s + (invCode != "" ? invCode + ";" : "");
	}
	if (this.hasParent()) {
		var pCode = this.getParent().genFullCode();
		s = s + (pCode != "" ? pCode + ";" : "");
	}
	if (!_waterbear.findVar(this)) {
		s = s + this.toLineCode();
		_waterbear.regNewVar(this);
	}
	return s;
};

var WaterBear = function() {
	this.reset();
	this.wdefs
	this.ignoredWidgets = ["dijit_layout_BorderContainer", "dojox_grid_DataGrid"];
};

WaterBear.prototype.reset = function() {
	this.declaredVars = [];
	this.lastCmd = "";
	this.currParentVar
};

WaterBear.prototype.createCLFromStr = function(str) {
	var cl = new WBCodeLine();
	cl.fromStr(str);
	cl.mParams[2] = this.currParentVar;
	return cl;
};

WaterBear.prototype.ajaxGet = function(url) {
	var http = _sahi.createRequestObject();
	http.open("GET", url, false);
	http.send();
	return http.responseText;
};

WaterBear.prototype.init = function() {
	if (!this.wdefs || this.wdefs.length == 0) {
		this.wdefs = {};

		var wdef_data = "";
		var wdefFiles = this.getWDEFFilePaths();
		for (var i = 0; i < wdefFiles.length; i++) {
			var tmpData = this.ajaxGet(wdefFiles[i]);
			wdef_data = wdef_data + tmpData;
			if (i != (wdefFiles.length - 1)) {
				wdef_data = wdef_data + "\n";
			}
		}

		var lines = wdef_data.split("\n");
		for (var idx in lines) {
			var line = lines[idx];
			if (line.trim().length == 0 || line.trim().substr(0, 1) == "#") {
				continue;
			}
			var parts = line.split("=");
			var javaClassName = parts[0];
			javaClassName = javaClassName.substring(javaClassName.lastIndexOf(".") + 1);
			var values = parts[1].split("|");
			var prefixStr = values[values.length - 1];
			var elemType = values[0];
			if (this.wdefs[javaClassName]) {
				this.wdefs[javaClassName]["prefixes"] = this.wdefs[javaClassName]["prefixes"].concat(prefixStr.split(","));
			} else {
				this.wdefs[javaClassName] = {
					"type" : elemType,
					"prefixes" : prefixStr.split(",")
				};
			}
		}
		console.log(this.wdefs);
	}

	// load resource bundles
	if (_waterbear.gEnabled()) {
		$Messages.load(this.getBundleSetting());
	}
};

WaterBear.prototype.getStdAccessors = function(accessors) {
	var accessor0 = _sahi._eval("_sahi." + accessors[0]);
	var parent = this._findWParent(accessor0);
	var newAccessors = [];
	for (var i = 0; i < accessors.length; i++) {
		var acc = accessors[i];
		var wbMethod = this._formCLFromAccessor(acc, parent);
		newAccessors.push(wbMethod.toLineCode());
	}
	this.currParentVar = parent;
	return newAccessors;
};

WaterBear.prototype.getAccessors = function(escapedAccessor) {
	var elem = _sahi._eval("_sahi." + escapedAccessor);
	var w = this.findEnclosingWidget(elem);
	if (!w) {
		return [];
	}
	var parent = this._findWParent(w);
	var javaClassName = this._getJavaClassName(w);

	var accessors = [];
	var stat = this._genByAttrStat(w, parent, javaClassName);
	if (stat) {
		accessors.push(stat.toLineCode());
	}
	stat = this._genByLabelStat(w, parent, javaClassName);
	if (stat) {
		accessors.push(stat.toLineCode());
	}
	stat = this._genByIdxStat(w, parent, javaClassName);
	if (stat) {
		accessors.push(stat.toLineCode());
	}

	this.currParentVar = parent;

	return accessors;
};

WaterBear.prototype._formWebElemMethod = function(identifier, elemType, parent) {
	if (parent) {
		return new WBCodeLine("webElem", [identifier, "ET." + elemType, parent]);
	} else {
		return new WBCodeLine("webElem", [identifier, "ET." + elemType]);
	}
};

WaterBear.prototype.monthTextToInt = function(monthText) {
	var months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
	return months.indexOf(monthText) + 1;
};

WaterBear.prototype.toGString = function(text) {
	var gText = $Messages.rbStat(text);
	if (gText == text) {
		return '"' + gText + '"';
	} else {
		return gText;
	}
}

WaterBear.prototype.getScript = function(infoAr, el, evType, e) {
	var info = infoAr[0];
	var accessor = _sahi.escapeDollar(_sahi.getAccessor1(info));
	if (accessor == null)
		return null;
	var ev = info.event;
	var value = info.value;
	var type = info.type;
	if (evType != null)
		evType = evType.toLowerCase();

	var cl = this._genFindMethod(accessor);
	if (!cl) {
		return null;
	}

	var cmd = null;
	if (value == null)
		value = "";

	var actionPart;

	// handle F12 and contextmenu
	if (evType == "keydown" || evType == "contextmenu") {
		if (evType == "keydown") {
			// this._alert(this.isRecordabeKeyDown(el, e));
			if (_sahi.isRecordabeKeyDown(el, e)) {
				cmd = "_keyPress(" + cl + ", [" + e.keyCode + "," + 0 + "]);";
				// this._alert(cmd);
			}
		} else {
			actionPart = "rightClick()";
		}
		if (!actionPart)
			return null;
	} else {
		if (ev == "_click") {
			if (cl.isFor("DateTextBox.class")) {
				eval("var el=_sahi." + accessor);
				if (_waterbear._getAttr(el, "class") == "dijitCalendarDateLabel") {
					var w = _waterbear.findEnclosingWidget(el);
					var dropdown = _sahi._byId(_waterbear._getAttr(w, "widgetid") + "_popup");
					var monthText = _sahi._getText(_sahi._div("dijitCalendarMonthLabel dijitCalendarCurrentMonthLabel", _sahi._in(dropdown)));
					var monthValue = _waterbear.monthTextToInt(monthText);
					var yearValue = parseInt(_sahi._getText(_sahi._span("dijitInline dijitCalendarSelectedYear", _sahi._in(dropdown))));
					var dayValue = _sahi._getText(el).valueOf();
					var dayParentClass = _waterbear._getAttr(_sahi._parentNode(el), "class");
					if (_sahi.contains(dayParentClass, "dijitCalendarNextMonth")) {
						monthValue += 1;
					} else if (_sahi.contains(dayParentClass, "dijitCalendarPreviousMonth")) {
						monthValue -= 1;
					}
					if (monthValue == 13) {
						monthValue = 1;
						yearValue += 1;
					} else if (monthValue == 0) {
						monthValue = 12;
						yearValue -= 1;
					}
					actionPart = 'select("' + (yearValue) + '","' + monthValue + '","' + dayValue + '")';
				}
			} else if (cl.isFor("AccordionContainer.class")) {
				eval("var sectionText=_sahi._getText(_sahi." + accessor + ")");
				actionPart = 'selectSection(' + _waterbear.toGString(sectionText) + ')';
			} else if (cl.isFor("EvoPresetChooser.class")) {
				eval("var presetText=_sahi._getText(_sahi._div('presetTitle',_sahi._near(_sahi." + accessor + ")))");
				actionPart = 'choose(' + _waterbear.toGString(presetText) + ')';
			} else if (_sahi.contains(cl.mName, "findActiveMenu")) {
				eval("var menuItemText=_sahi._getText(_sahi." + accessor + ")");
				actionPart = 'clickAction(' + _waterbear.toGString(menuItemText) + ')';
			} else if (cl.invoker && (cl.invoker.varName.indexOf("prgsDlg") != -1 || cl.invoker.varName == "navTasks" || _sahi.contains(cl.invoker.varName, "tabContainer"))) {
				//do nothing
			} else if (cl.isFor("TimeTextbox.class")) {
				eval("var timeStr=_sahi._getText(_sahi." + accessor + ")");
				if (timeStr && timeStr.trim() != "") {
					actionPart = 'select("' + timeStr + '")';
				}
			} else if (cl.isFor("Select.class")) {
				eval("var optText=_sahi._getText(_sahi." + accessor + ")");
				actionPart = 'select(' + _waterbear.toGString(optText) + ')';
			} else if (cl.isFor("Checkbox.class")) {
				if (accessor.indexOf("_checkbox") == -1) {
					accessor = "_checkbox(0,_sahi._near(_sahi." + accessor + "))";
				}
				eval("var checked=_sahi." + accessor + ".checked");
				actionPart = 'easyCheck(' + checked + ')';
			} else if (cl.isFor("SonasCommonList.class")) {
				eval("var rowText=_sahi._getText(_sahi." + accessor + ")");
				actionPart = 'selectRowByText("' + rowText + '")';
			} else {
				actionPart = "click()";
			}
		} else if (ev == "_setValue") {
			actionPart = "setValue(" + _sahi.quotedEscapeValue(value) + ")";
		} else if (ev == "_setSelected") {
			actionPart = "choose(" + _sahi.quotedEscapeValue(value) + ")";
		} else if (ev == "wait") {
			cmd = "_wait(" + value + ");";
		} else if (ev == "mark") {
			cmd = "//MARK: " + value;
		} else if (ev == "_setFile") {
			if (!this.isBlankOrNull(value)) {
				actionPart = "setFile2(" + _sahi.quotedEscapeValue(value) + ")";
			} else {
				return null;
			}
		}
	}

	var accessorPart = cl.genAccessorPart();

	if (accessorPart) {
		cmd = accessorPart + ( actionPart ? "." + actionPart : "") + ";";
	}

	// Sometimes Sahi generate an event twice, this code is to remove
	// the
	// duplicated one.
	if (cmd) {
		if (this.lastCmd.indexOf(cmd) != -1) {
			cmd = "";
		} else {
			this.lastCmd = cmd;
		}
	}

	return cmd;
};

WaterBear.prototype.addRBStat = function(stat) {
	if (stat.indexOf("byLabel") != -1) {
		var labelText = stat.split('"')[1];
		var rbStat = $Messages.rbStat(labelText);
		if (_sahi.contains(rbStat, "getString(")) {
			stat = stat.replace('"' + labelText + '"', rbStat);
		}
	}
	return stat;
};

WaterBear.prototype.findVarByLineCodeStr = function(clstr) {
	for (var i = 0; i < this.declaredVars.length; i++) {
		var declaredVar = this.declaredVars[i];
		if (declaredVar.toLineCode() == clstr) {
			return declaredVar;
		}
	}
};

WaterBear.prototype.findVar = function(cl) {
	for (var i = 0; i < this.declaredVars.length; i++) {
		var declaredVar = this.declaredVars[i];
		if (declaredVar.toLineCode() == cl.toLineCode()) {
			return declaredVar;
		}
	}
};

WaterBear.prototype.regNewVar = function(cl) {
	var declaredVar = this.findVar(cl);
	if (!declaredVar) {
		this.declaredVars.push(cl);
	}
};

WaterBear.prototype._getAttr = function(elem, attrName) {
	if (elem) {
		return _sahi._getAttribute(elem, attrName);
	}
};

WaterBear.prototype._findDialogs = function() {
	return this._collectWidgets("_div", /evo_dialog_Dialog|evo_common_Dialog|evo_dialog_PresetDialog/);
};

WaterBear.prototype._collectWidgets = function(elemType, re, inElem) {
	var divs = [];
	if (inElem) {
		divs = _sahi._collect(elemType, re, _sahi._in(inElem));
	} else {
		divs = _sahi._collect(elemType, re);
	}

	return divs;
};

WaterBear.prototype._getPageDivId = function(w) {
	var $regexp = this.getPageRegExp();
	var $divs = this._collectWidgets("_div", $regexp);
	var inDivs = [];
	// find all the divs containing this element
	for (var i = 0; i < $divs.length; i++) {
		var $div = $divs[i];
		if (_sahi._isVisible($div) && _sahi._contains($div, w)) {
			inDivs.push($div);
		}
	}
	// find the nearest the div containing this element
	for (var i = 0; i < inDivs.length; i++) {
		var $div = inDivs[i];
		var $divId = this._getAttr($div, "id");
		var $found = false;

		for (var j = 0; j < inDivs.length; j++) {
			var anotherDiv = inDivs[j];
			var anotherDivId = this._getAttr(anotherDiv, "id");
			if (anotherDivId != $divId && _sahi._contains($div, anotherDiv)) {
				$found = true;
				break;
			}
		}

		if (!$found) {
			return $divId;
		}
	}
};

WaterBear.prototype._findWParent = function(w) {
	var parentVarName;
	var parentCL;

	var dlgs = this._findDialogs();
	for (dlgIdx in dlgs) {
		var dlg = dlgs[dlgIdx];
		if (_sahi._contains(dlg, w)) {
			var dlgId = this._getAttr(dlg, "id");

			if (_sahi._exists(_sahi._div(/dijit_ProgressBar_.*/, _sahi._in(dlg)))) {
				var dlgNamePrefix = "prgsDlg";
				var dlgJavaClassName = "EvoProgressDialog";
				var dlgMethodName = "progressDlg";
				var dlgTitle = _sahi._getText(_sahi._div("h1", _sahi._in(dlg)));
			} else {
				var dlgNamePrefix = "dlg";
				var dlgJavaClassName = "EvoDialog";
				var dlgMethodName = "dlg";
				var dlgTitle = this._getAttr(_sahi._div("dijitDialogTitleBar", _sahi._in(dlg)), "title");
			}

			if (dlgTitle) {
				parentVarName = dlgNamePrefix + dlgTitle.replace(/[^A-Za-z]+/g, '').trim();
			} else {
				parentVarName = dlgNamePrefix + dlgId.substring(dlgId.lastIndexOf("_") + 1);
			}

			var rbDlgTitle = $Messages.rbStat(dlgTitle);

			parentCL = new WBCodeLine(dlgMethodName, [rbDlgTitle]);
			parentCL.varName = parentVarName;
			parentCL.varType = dlgJavaClassName;
			parentCL.id = dlgId;
			break;
		}
	}

	if (!parentCL) {
		var pageId = this._getPageDivId(w);
		if (pageId) {
			var parts = pageId.split("_");
			parentCL = this._formWebElemMethod(pageId, "DIV");
			parentCL.varName = "page" + parts[parts.length - 2];
			parentCL.id = pageId;
		}
	}

	if (parentCL) {

		//check if the widget is in a TabContainer widget
		var tabContainers = this._collectWidgets("_div", /^dijit_layout_TabContainer_\d*$/, _sahi._byId(parentCL.id));
		for (var i = 0; i < tabContainers.length; i++) {
			var tabContainer = tabContainers[i];
			var tab = this._collectWidgets("_div","dijitTab dijitTabChecked dijitChecked",tabContainer)[0];
			var tabText = _sahi._getText(_sahi._span("tabLabel", _sahi._in(tab)));

			if (_sahi._contains(tabContainer, w)) {
				var parentCL = new WBCodeLine("byIndex", [i, "TabContainer.class", parentCL]);
				parentCL.id = this._getAttr(tabContainer, "id");

				var tabPanes = this._collectWidgets("_div", /.*dijitTabPane dijitTabContainerTop-child.*/, tabContainer);
				for (var j = 0; j < tabPanes.length; j++) {
					var tabPane = tabPanes[j];
					if (_sahi._contains(tabPane, w)) {
						var varName = "tabPane" + j + "_" + parentCL.varName;
						var parentCL = new WBCodeLine("getTabPane", [tabText], parentCL);
						parentCL.varName = varName;
						parentCL.varType = "WebElement";
						parentCL.id = this._getAttr(tabPane, "id");
						break;
					}
				}
			}
		}

		//check if the widget is in a DataGrid widget
		var dataGrids = this._collectWidgets("_div", /dojox_grid_DataGrid/, _sahi._div(parentCL.id));
		for (var i = 0; i < dataGrids.length; i++) {
			var dataGrid = dataGrids[i];
			if (_sahi._contains(dataGrid, w)) {
				var parentCL = new WBCodeLine("byIndex", [i, "DataGrid.class", parentCL]);
				parentCL.id = this._getAttr(dataGrid, "id");
				break;
			}
		}

		return parentCL;
	}
};

WaterBear.prototype._genByAttrStat = function(w, parent, javaClassName) {
	var stat;

	if (javaClassName.indexOf("RadioButton") != -1) {
		var valueValue = this._findValueValue(w);
		stat = valueValue ? new WBCodeLine("byAttribute", [valueValue, javaClassName + '.class', parent]) : null;
	} else {
		var nameValue = this._findNameValue(w);
		stat = nameValue ? new WBCodeLine("byAttribute", [nameValue, javaClassName + '.class', parent]) : null;
	}
	return stat;
};

WaterBear.prototype._genByLabelStat = function(w, parent, javaClassName) {
	var labelValue = this._findLabelValue(w);
	var stat;
	if (labelValue) {
		stat = new WBCodeLine("byLabel", [labelValue, javaClassName + '.class', parent]);
	}
	return stat;
};

WaterBear.prototype._genByIdxStat = function(w, parent, javaClassName) {
	var stat;
	var idxValue = this._findIdxValue(w, parent.id, javaClassName);
	if (idxValue != undefined && idxValue != null) {
		stat = new WBCodeLine("byIndex", [idxValue, javaClassName + '.class', parent]);
	}
	return stat;
};

WaterBear.prototype._formCLFromAccessor = function(acc, parent) {
	var elemType = acc.substr(1, acc.indexOf("(") - 1).toUpperCase();
	var identifer = acc.substring(acc.indexOf("(") + 1, acc.lastIndexOf(")")).replace(/"/g, '');

	var cl = this._formWebElemMethod(identifer, elemType, parent);
	return cl;
};

WaterBear.prototype._genFindMethod = function(accessor) {
	var elem = _sahi._eval("_sahi." + accessor);
	var w = this.findEnclosingWidget(elem);

	if (w) {
		var javaClassName = this._getJavaClassName(w);
		var navTasksVar = new WBCodeLine();
		navTasksVar.varName = "navTasks";

		if (javaClassName == "EvoFisheyeItem") {
			var pageName = _sahi._getText(elem).split(" ").join("");
			return new WBCodeLine("gotoPage" + pageName, null, navTasksVar);
		} else if (javaClassName == "IconNavRepeaterItem") {
			var subPageName = _sahi._getText(elem).split(" ").join("");
			return new WBCodeLine("gotoSubPage" + subPageName, null, navTasksVar);
		} else if (javaClassName == "MenuItem") {
			return new WBCodeLine("findActiveMenu");
		} else {
			var parentCL = this._findWParent(w);

			if (_sahi.contains(parentCL.varName, "prgsDlg") && javaClassName == "Button") {
				var labelValue = this._findLabelValue(w);
				if (labelValue != "Cancel") {
					return new WBCodeLine("waitforTaskdone", [0], parentCL);
				}
			} else {
				var stat = this._genAccessorStat(w, parentCL, javaClassName);
				return stat;
			}
		}
	} else {
		var parentCL = this._findWParent(elem);
		if (parentCL && parentCL.varType == "TabContainer") {
			var tabListWrapper = _sahi._div(/dijitTabListWrapper.*/, _sahi._byId(parentCL.id));
			if (_sahi._contains(tabListWrapper, elem)) {
				var className = this._getAttr(elem, "className");
				var tabText;
				if (className == "tabLabel") {
					tabText = _sahi._getText(elem);
				} else {
					tabText = _sahi._getText(_sahi._span("tabLabel", elem));
				}
				var rbStat = $Messages.rbStat(tabText);
				var cl = new WBCodeLine("select", [rbStat]);
				cl.invoker = parentCL;
				parentCL.varType = "TabContainer";
				return cl;
			}
		} else {
			return this._formCLFromAccessor(accessor, parentCL);
		}
	}
};

WaterBear.prototype._genAccessorStat = function(w, parent, javaClassName) {
	if (w) {
		var stat = this._genByAttrStat(w, parent, javaClassName);
		if (!stat) {
			stat = this._genByLabelStat(w, parent, javaClassName);
			if (!stat) {
				stat = this._genByIdxStat(w, parent, javaClassName);
			}
		}
		return stat;
	}
};

WaterBear.prototype._findIdxValue = function(w, pDivId, javaClassName) {
	var p = _sahi._byId(pDivId);
	var widgetid = this._getAttr(w, "widgetid");

	var prefixes = this.wdefs[javaClassName]["prefixes"];
	var re = "^" + prefixes.join("_\\d*|^") + "_\\d*";

	var wElemType = this._getWidgetElemType(w);
	var allSameTypeWidgets = this._collectWidgets("_" + wElemType, new RegExp(re), p);
	for (var i = 0; i < allSameTypeWidgets.length; i++) {
		var temp = allSameTypeWidgets[i];
		if (widgetid == this._getAttr(temp, "widgetid")) {
			return i;
		}
	}
};

// this method is designed to provide the label locating logic for project specific widget
WaterBear.prototype.hookFindLabelValue = function(w) {

}

WaterBear.prototype._findLabelValue = function(w) {
	var javaClassName = this._getJavaClassName(w);
	var widgetid = this._getAttr(w, "widgetid");
	var labelValue = this.hookFindLabelValue(w);

	if (!labelValue) {
		if (javaClassName == "Button" || javaClassName == "DropDownButton") {
			labelValue = _sahi._getText(_sahi._byId(widgetid + "_label"));
		} else {
			var labelDiv = _sahi._div("/.*label.*/", _sahi._near(w));
			if (_sahi._exists(labelDiv)) {
				var labelText = _sahi._getText(labelDiv);
				if (labelText && labelText != "") {
					labelValue = labelText;
				}
			}
		}
	}

	if (labelValue) {
		labelValue = labelValue.indexOf(":") == -1 ? labelValue : labelValue
		.split(":")[0] + ":";
	}
	return labelValue;
};

WaterBear.prototype._getWidgetElemType = function(w) {
	var widgetid = this._getAttr(w, "widgetid");
	var widPrefix = widgetid.substring(0, widgetid.lastIndexOf("_"))

	var keys = Object.getOwnPropertyNames(this.wdefs);
	for (var i = 0; i < keys.length; i++) {
		var key = keys[i];
		var value = this.wdefs[key]["prefixes"];
		if (value.indexOf(widPrefix) != -1) {
			return this.wdefs[key]["type"];
		}
	}
};

WaterBear.prototype._getJavaClassName = function(w) {
	var widgetid = this._getAttr(w, "widgetid");
	if (widgetid) {
		var widPrefix = widgetid.substring(0, widgetid.lastIndexOf("_"));

		var keys = Object.getOwnPropertyNames(this.wdefs);
		for (var i = 0; i < keys.length; i++) {
			var key = keys[i];
			var value = this.wdefs[key]["prefixes"];
			if (value.indexOf(widPrefix) != -1) {
				if ((key == "Textbox" && _sahi._exists(_sahi._password(0, _sahi._in(w)))) || (key == "Password" && !_sahi._exists(_sahi._password(0, _sahi._in(w))))) {
					continue;
				}
				return key;
			}
		}
	}
};

WaterBear.prototype.findEnclosingWidget = function(elem) {
	if (!elem) {
		return null;
	}

	var widgetid = this._getAttr(elem, "widgetid");
	if (widgetid && this.ignoredWidgets.indexOf(widgetid.substring(0, widgetid.lastIndexOf("_"))) == -1) {
		if (_sahi.contains(widgetid, "dijit_MenuItem")) {
			var selectMenu = _sahi._parentNode(elem, "table");
			var selectMenuWid = this._getAttr(selectMenu, "widgetid");
			if (selectMenuWid.indexOf("evo_widget_Menu_") != -1) {
				return elem;
			} else if (_sahi.contains(selectMenuWid, "dijit_form_Select")) {
				var selectWid = selectMenuWid.substring(0, selectMenuWid.lastIndexOf("_"));
				return _sahi._byId(selectWid);
			}
		} else if (/^dijit_form_TimeTextBox_\d+_popup$/.test(widgetid)) {
			var timeTextBoxWid = "widget_" + widgetid.substring(0, widgetid.lastIndexOf("_"));
			return _sahi._byId(timeTextBoxWid);
		} else if (/^dijit_form_DateTextBox_\d+_popup.*$/.test(widgetid)) {
			var dateTextBoxWid = "widget_" + widgetid.replace(/(_popup.*)/, "");
			return _sahi._byId(dateTextBoxWid);
		} else {
			if (this._getWidgetElemType(elem)) {
				return elem;
			}
		}
	} else {
		var parentElem = _sahi._parentNode(elem);
		return this.findEnclosingWidget(parentElem);
	}
};

WaterBear.prototype._findNameValue = function(w) {
	var widgetid = this._getAttr(w, "widgetid");
	var coreElem = _sahi._byId(widgetid);
	var nameValue = this._getAttr(coreElem, "name");
	if (nameValue) {
		return nameValue;
	}
};

WaterBear.prototype._findValueValue = function(w) {
	var widgetid = this._getAttr(w, "widgetid");
	var coreElem = _sahi._byId(widgetid);
	var valueValue = this._getAttr(coreElem, "value");
	if (valueValue) {
		return valueValue;
	}
};

WaterBear.prototype.gEnabled = function() {
	return true;
};

var $Messages = function() {
	var $msgCache = new Array();

	return {
		load : function($bundles) {
			for (var j = 0; j < $bundles.length; j++) {
				var $bundle = $bundles[j];
				var $bName = $bundle["bundleName"];
				var $bPath = $bundle["bundlePath"];
				var $bFiles = $bundle["bundleFiles"];
				for (var i = 0; i < $bFiles.length; i++) {
					var $fileName = $bFiles[i];
					var $filePath = $bPath + "/" + $fileName + ".properties";
					var $content = WaterBear.prototype.ajaxGet($filePath);
					if ($content == null) {
						console.log("Cannot load content from " + $filePath);
					}
					$msgCache.push([$bName, $fileName, $content]);
				}
			}
		},

		rbStat : function(propValue) {
			var rt;
			if (_waterbear.gEnabled()) {
				var res = this.findMessageKey(propValue);
				if (res) {
					rt = 'getString(' + res.bundleName + '.' + res.resName + ', "' + res.propKey + '")';
				}
			}
			return rt ? rt : propValue;
		},

		findMessageKey : function($propValue) {
			if ($propValue) {
				$propValue = $propValue.trim();
			} else {
				return;
			}
			if ($propValue.length == 0) {
				return null;
			}
			$propValue = $propValue.split("?")[0];
			$propValue = $propValue.replace(/\(/gm, "\\(");
			$propValue = $propValue.replace(/\)/gm, "\\)");
			$propValue = $propValue.trim();
			var $re = new RegExp("(.*)=" + $propValue + "$", "gm");
			var $key = "";
			var $resname = "";
			var $bundleName = "";
			for (var i = 0; i < $msgCache.length; i++) {
				$bundleName = $msgCache[i][0];
				var $fileName = $msgCache[i][1];
				var $content = $msgCache[i][2];
				var $m = $re.exec($content);
				if ($m != null) {
					$key = $m[1];
					$resname = $fileName;
					break;
				}
			}
			if ($key == "") {
				console.log("Cannot find the property key for [" + $propValue + "]");
				// throw ERROR_PROPERTY_KEY_NOT_FOUND;
			} else {
				return {
					"bundleName" : $bundleName,
					"resName" : $resname,
					"propKey" : $key
				};
			}
		}
	}
}();

var initWBController = function() {
	_waterbear = new WaterBear();

	_sahi.controllerURL = "/_s_/spr/controllerwb.htm";
	_sahi.controllerHeight = 250;
	_sahi.controllerWidth = 420;
	_sahi.recorderClass = "StepWiseRecorder";
	Sahi.prototype.getExpectPromptScript = function(s, retVal) {
		return "browser." + this.getPopupDomainPrefixes() + "expectPrompt(" + this.quotedEscapeValue(s) + ", " + this.quotedEscapeValue(retVal) + ")";
	};
	Sahi.prototype.getExpectConfirmScript = function(s, retVal) {
		return "browser." + this.getPopupDomainPrefixes() + "expectConfirm(" + this.quotedEscapeValue(s) + ", " + retVal + ")";
	};
	Sahi.prototype.getNavigateToScript = function(url) {
		return "browser." + this.getPopupDomainPrefixes() + "navigateTo(" + this.quotedEscapeValue(url) + ");";
	};
	Sahi.prototype.getScript = function(infoAr, el, evType, e) {

		return _waterbear.getScript(infoAr, el, evType, e);
	};
	Sahi.prototype.escapeDollar = function(s) {
		return s;
		if (s == null)
			return null;
		return s.replace(/[$]/g, "\\$");
	};
	Sahi.prototype.getAccessor1 = function(info) {
		if (info == null)
			return null;
		if ("" == ("" + info.shortHand) || info.shortHand == null)
			return null;
		var accessor = info.type + "(" + this.escapeForScript(info.shortHand) + ")";
		// if (accessor.indexOf("_") == 0) accessor = accessor.substring(1);
		return accessor;
	};
	_sahi.language = {
		ASSERT_ENABLED : "assertTrue(<accessor>.isEnabled());",
		ASSERT_NOT_ENABLED : "assertFalse(<accessor>.isEnabled());",
		ASSERT_READONLY : "assertTrue(<accessor>.isReadonly());",
		ASSERT_NOT_READONLY : "assertFalse(<accessor>.isReadonly());",
		ASSERT_EXISTS : "assertTrue(<accessor>.exists());",
		ASSERT_VISIBLE : "assertTrue(<accessor>.isVisible());",
		ASSERT_EQUAL_TEXT : "assertEquals(<value>, <accessor>.getText());",
		ASSERT_CONTAINS_TEXT : "assertTrue(<accessor>.getText().indexOf(<value>)!=-1);",
		ASSERT_EQUAL_VALUE : "assertEquals(<value>, <accessor>.getValue());",
		ASSERT_SELECTION : "assertEquals(<value>, <accessor>.getSelected());",
		ASSERT_CHECKED : "assertTrue(<accessor>.checked());",
		ASSERT_NOT_CHECKED : "assertFalse(<accessor>.checked());",
		POPUP : "popup(<window_name>).",
		DOMAIN : "domain(<domain>)."
	};

	var oHead = document.getElementsByTagName('HEAD').item(0);
	var oScript = document.createElement("script");
	oScript.type = "text/javascript";
	oScript.src = "/_s_/spr/wbrecorder_initdata.js";
	oHead.appendChild(oScript);
};

if (_sahi.controllerMode == "waterbear") {
	initWBController();
}

Sahi.prototype.setMode = function(mode) {
	if (mode != "waterbear" && mode != "sahi") {
		console.error("Unknown controller mode [" + mode + "]!!");
	} else {
		_sahi.controllerMode = mode;
		if (mode == "waterbear") {
			initWBController();
		} else if (mode == "sahi") {
			_sahi.controllerURL = "/_s_/spr/controller7.htm";
			_sahi.controllerHeight = 550;
			_sahi.controllerWidth = 485;
			_sahi.recorderClass = "Recorder";
		}
		console.log("The controller mode was set to '" + mode + "' successfully.");
	}
};

/** -- WaterBear Recorder End -- * */

__sahiDebug__("user_ext.js: end");

