
var raphael_node = function(rset) {
    return { label: '', render : rset };	
}

// ----------------------------------------------------------------------------

var node_concept = function(node) {
	return raphael_node(function(r, n) {
		var set = r.set().push(  
	    	r.rect(n.point[0]+10, n.point[1]+10, 62, 86)
	        	.attr({"fill": "#fa8", r : "9px"})
	    );
        return set;
	});
}

var node_relation = function(node) {
	return raphael_node(function(r, n) {
		var set = r.set().push(  
	    	r.rect(n.point[0]+10, n.point[1]+10, 62, 86)
	        	.attr({"fill": "#fa8", r : "9px"})
	    );
        return set;
	});
}

var node_event = function(node) {
	return raphael_node(function(r, n) {
		var set = r.set().push(  
	    	r.rect(n.point[0]-30, n.point[1]-13, 62, 86)
	        	.attr({"fill": "#fa8", r : "9px"})
	    );
        return set;
	});
}

var node_repository = function(node) {
	return raphael_node(function(r, n) {
		var set = r.set().push(  
	    	r.rect(n.point[0]-30, n.point[1]-13, 62, 86)
	        	.attr({"fill": "#fa8", r : "9px"})
	    );
        return set;
	});
}

var node_handler = function(node) {
	return raphael_node(function(r, n) {
		var set = r.set().push(  
	    	r.rect(n.point[0]-30, n.point[1]-13, 62, 86)
	        	.attr({"fill": "#fa8", r : "9px"})
	    );
        return set;
	});
}

var node_listener = function(node) {
	return raphael_node(function(r, n) {
		var set = r.set().push(  
	    	r.rect(n.point[0]-30, n.point[1]-13, 62, 86)
	        	.attr({"fill": "#fa8", r : "9px"})
	    );
        return set;
	});
}

var node_command = function(node) {
	return raphael_node(function(r, n) {
		var set = r.set().push(  
	    	r.rect(n.point[0]-30, n.point[1]-13, 62, 86)
	        	.attr({"fill": "#fa8", r : "9px"})
	    );
        return set;
	});
}