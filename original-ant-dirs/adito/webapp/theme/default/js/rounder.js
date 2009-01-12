function roundCorner(control, name) {
    var original = control;
    /* Make it the inner div of the four */
    /* Now create the outer-most div */
    var tr = document.createElement('div');
    tr.className = 'rounded_dialog_tr';
    /* Swap out the original (we'll put it back later) */
    original.parentNode.replaceChild(tr, original);
    /* Create the two other inner nodes */
    var tl = document.createElement('div');
    tl.className = 'rounded_dialog_tl';
    var br = document.createElement('div');
    br.className = 'rounded_dialog_br';
    var bl = document.createElement('div');
    bl.className = 'rounded_dialog_bl';
    /* Now glue the nodes back in to the document */
    tr.appendChild(tl);
    tl.appendChild(br);
    br.appendChild(bl);
    bl.appendChild(original);
}

function roundCornersById(id, name) {
    var original = document.getElementById(id);
    if(original) {
	    /* Make it the inner div of the four */
	    original.className = 'rounded_dialog_bl'; 
	    /* Now create the outer-most div */
	    var tr = document.createElement('div');
	    tr.id = original.id;
	    original.id = '';
	    tr.className = 'rounded_dialog';
	    /* Swap out the original (we'll put it back later) */
	    original.parentNode.replaceChild(tr, original);
	    /* Create the two other inner nodes */
	    var tl = document.createElement('div');
	    tl.className = 'rounded_dialog_tl';
	    var br = document.createElement('div');
	    br.className = 'rounded_dialog_br';
	    /* Now glue the nodes back in to the document */
	    tr.appendChild(tl);
	    tl.appendChild(br);
	    br.appendChild(original);
   }
}

function getElementsByClass(searchClass,node,tag) {
	var classElements = new Array();
	if ( node == null )
		node = document;
	if ( tag == null )
		tag = '*';
	var els = node.getElementsByTagName(tag);
	var elsLen = els.length;
	var pattern = new RegExp("(^|\\s)"+searchClass+"(\\s|$)");
	for (i = 0, j = 0; i < elsLen; i++) {
		if ( pattern.test(els[i].className) ) {
			classElements[j] = els[i];
			j++;
		}
	}
	return classElements;
}

function roundCornersByClass(className) {
  var divs = getElementsByClass(className, document, '*');
  for (var i = 0; i < divs.length; i++) {
	  roundCorner(divs[i], className);
  }
}

function roundCorners() {
  var divs = document.getElementsByTagName('div');
  var rounded_divs = [];
  for (var i = 0; i < divs.length; i++) {
    if (/\bdialog_content\b/.exec(divs[i].className)) {
      rounded_divs[rounded_divs.length] = divs[i];
    }
  }
  for (var i = 0; i < rounded_divs.length; i++) {
	  roundCorner(rounded_divs[i], 'dialog_content');
  }